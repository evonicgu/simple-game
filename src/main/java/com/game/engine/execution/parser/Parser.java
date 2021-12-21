package com.game.engine.execution.parser;

import com.game.engine.execution.ast.*;
import com.game.engine.execution.parser.token.*;

import java.util.ArrayList;

public class Parser {
    private final Lexer lexer;
    private Token lookahead;

    public Parser(String expr) throws ParserException {
        lexer = new Lexer(expr);
        lookahead = lexer.lex();
    }

    public Node parse() throws ParserException {
        Node lhs = T(), rhs;

        if (lookahead.getType() == Token.Type.T_EQUALS) {
            match(Token.Type.T_EQUALS);
            rhs = parse();
            return new BinOpNode(lhs, rhs, BinOpNode.OpType.EQUALS);
        }

        return lhs;
    }

    private Node T() throws ParserException {
        Node lhs = K();

        return _T(lhs);
    }

    private Node _T(Node lhs) throws ParserException {
        if (lookahead.getType() == Token.Type.T_OR) {
            match(Token.Type.T_OR);
            Node rhs = K();

            return _T(new BinOpNode(lhs, rhs, BinOpNode.OpType.OR));
        }

        return lhs;
    }

    private Node K() throws ParserException {
        Node lhs = F();

        return _K(lhs);
    }

    private Node _K(Node lhs) throws ParserException {
        if (lookahead.getType() == Token.Type.T_AND) {
            match(Token.Type.T_AND);
            Node rhs = F();

            return _T(new BinOpNode(lhs, rhs, BinOpNode.OpType.AND));
        }

        return lhs;
    }

    private Node F() throws ParserException {
        Node lhs = S();

        if (lookahead.getType() == Token.Type.T_COMPARE) {
            match(Token.Type.T_COMPARE);
            Node rhs = S();

            return new BinOpNode(lhs, rhs, BinOpNode.OpType.COMPARE);
        } else if (lookahead.getType() == Token.Type.T_COMPARE_NOT) {
            match(Token.Type.T_COMPARE_NOT);
            Node rhs = S();

            return new BinOpNode(lhs, rhs, BinOpNode.OpType.COMPARE_NOT);
        }

        return lhs;
    }

    private Node S() throws ParserException {
        Node lhs = M();

        switch (lookahead.getType()) {
            case T_GT -> {
                match(Token.Type.T_GT);
                Node rhs = M();

                return new BinOpNode(lhs, rhs, BinOpNode.OpType.GT);
            }
            case T_GE -> {
                match(Token.Type.T_GE);
                Node rhs = M();

                return new BinOpNode(lhs, rhs, BinOpNode.OpType.GE);
            }
            case T_LT -> {
                match(Token.Type.T_LT);
                Node rhs = M();

                return new BinOpNode(lhs, rhs, BinOpNode.OpType.LT);
            }
            case T_LE -> {
                match(Token.Type.T_LE);
                Node rhs = M();

                return new BinOpNode(lhs, rhs, BinOpNode.OpType.LE);
            }
        }

        return lhs;
    }

    private Node M() throws ParserException {
        Node lhs = G();

        return _M(lhs);
    }

    private Node _M(Node lhs) throws ParserException {
        if (lookahead.getType() == Token.Type.T_PLUS) {
            match(Token.Type.T_PLUS);
            Node rhs = G();

            return _M(new BinOpNode(lhs, rhs, BinOpNode.OpType.PLUS));
        } else if (lookahead.getType() == Token.Type.T_MINUS) {
            match(Token.Type.T_MINUS);
            Node rhs = G();

            return _M(new BinOpNode(lhs, rhs, BinOpNode.OpType.MINUS));
        }

        return lhs;
    }

    private Node G() throws ParserException {
        Node lhs = C();

        return _G(lhs);
    }

    private Node _G(Node lhs) throws ParserException {
        if (lookahead.getType() == Token.Type.T_MUL) {
            match(Token.Type.T_MUL);
            Node rhs = C();

            return _G(new BinOpNode(lhs, rhs, BinOpNode.OpType.MUL));
        } else if (lookahead.getType() == Token.Type.T_DIV) {
            match(Token.Type.T_DIV);
            Node rhs = C();

            return _G(new BinOpNode(lhs, rhs, BinOpNode.OpType.DIV));
        } else if (lookahead.getType() == Token.Type.T_MOD) {
            match(Token.Type.T_MOD);
            Node rhs = C();

            return _G(new BinOpNode(lhs, rhs, BinOpNode.OpType.MOD));
        }

        return lhs;
    }

    private Node C() throws ParserException {
        Node tree;

        switch (lookahead.getType()) {
            case T_IDENTIFIER -> {
                String name = ((IdentifierToken) lookahead).getName();
                match(Token.Type.T_IDENTIFIER);

                if (lookahead.getType() != Token.Type.T_DOT) {
                    tree = new IdentifierNode(name);
                    break;
                }

                match(Token.Type.T_DOT);

                String methodName = ((IdentifierToken) lookahead).getName();
                ArrayList<Node> parameters = new ArrayList<Node>();

                match(Token.Type.T_IDENTIFIER);
                match(Token.Type.T_PAREN_OPEN);

                if (lookahead.getType() != Token.Type.T_PAREN_CLOSE) {
                    parameters.add(parse());

                    while (lookahead.getType() != Token.Type.T_PAREN_CLOSE) {
                        match(Token.Type.T_COMMA);
                        parameters.add(parse());
                    }
                }

                match(Token.Type.T_PAREN_CLOSE);

                tree = new MethodCallNode(name, methodName, parameters);
            }
            case T_INT -> {
                tree = new IntegerNode(((IntToken) lookahead).getValue());
                match(Token.Type.T_INT);
            }
            case T_FLOAT -> {
                tree = new FloatNode(((FloatToken) lookahead).getValue());
                match(Token.Type.T_FLOAT);
            }
            case T_STR -> {
                tree = new StrNode(((StrToken) lookahead).getValue());
                match(Token.Type.T_STR);
            }
            case T_BOOL -> {
                tree = new BoolNode(((BoolToken) lookahead).getValue());
                match(Token.Type.T_BOOL);
            }
            case T_PAREN_OPEN -> {
                match(Token.Type.T_PAREN_OPEN);
                tree = parse();
                match(Token.Type.T_PAREN_CLOSE);
            }
            default -> throw new ParserException("Error: unexpected token " + lookahead.getType().name());
        }

        return tree;
    }

    private void match(Token.Type type) throws ParserException {
        if (lookahead.getType() == type) {
            lookahead = lexer.lex();
            return;
        }

        throw new ParserException("Error: unexpected token " + type.name());
    }
}
