package com.game.engine.execution.parser;

import com.game.engine.execution.parser.token.*;

public class Lexer {
    private final String expr;
    private int pos = 0;

    public Lexer(String expr) {
        this.expr = expr;
    }

    public Token lex() throws ParserException {
        if (pos >= expr.length()) {
            return new Token(Token.Type.T_END);
        }

        char c = expr.charAt(pos++);

        while (Character.isWhitespace(c) && pos < expr.length()) {
            c = expr.charAt(pos++);
        }

        switch (c) {
            case '.':
                return new Token(Token.Type.T_DOT);
            case ',':
                return new Token(Token.Type.T_COMMA);
            case '=':
                if (pos < expr.length() && expr.charAt(pos) == '=') {
                    ++pos;
                    return new Token(Token.Type.T_COMPARE);
                }

                return new Token(Token.Type.T_EQUALS);
            case '%':
                return new Token(Token.Type.T_MOD);
            case '<':
                if (pos < expr.length() && expr.charAt(pos) == '=') {
                    ++pos;
                    return new Token(Token.Type.T_LE);
                }

                return new Token(Token.Type.T_LT);
            case '>':
                if (pos < expr.length() && expr.charAt(pos) == '=') {
                    ++pos;
                    return new Token(Token.Type.T_GE);
                }

                return new Token(Token.Type.T_GT);
            case '+':
                return new Token(Token.Type.T_PLUS);
            case '-':
                return new Token(Token.Type.T_MINUS);
            case '*':
                return new Token(Token.Type.T_MUL);
            case '/':
                return new Token(Token.Type.T_DIV);
            case '(':
                return new Token(Token.Type.T_PAREN_OPEN);
            case ')':
                return new Token(Token.Type.T_PAREN_CLOSE);
            case '!':
                if (pos < expr.length() && expr.charAt(pos) == '=') {
                    return new Token(Token.Type.T_COMPARE_NOT);
                }

                throw new ParserException("unable to tokenize expression");
            case '&':
                if (pos < expr.length() && expr.charAt(pos) == '&') {
                    return new Token(Token.Type.T_AND);
                }

                throw new ParserException("unable to tokenize expression");
            case '|':
                if (pos < expr.length() && expr.charAt(pos) == '|') {
                    return new Token(Token.Type.T_OR);
                }

                throw new ParserException("unable to tokenize expression");
            case '\'': {
                StringBuilder stringBuilder = new StringBuilder();

                while (pos < expr.length()) {
                    c = expr.charAt(pos);

                    if (c == '\'') {
                        break;
                    }

                    if (c == '\\') {
                        if (pos + 1 >= expr.length()) {
                            throw new ParserException("escape literal at the end of the string");
                        }

                        c = expr.charAt(++pos);
                    }

                    stringBuilder.append(c);
                    ++pos;
                }
                ++pos;

                return new StrToken(stringBuilder.toString());
            }
            default:
                if (Character.isDigit(c)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(c);

                    boolean isFloat = false;

                    while (pos < expr.length()) {
                        c = expr.charAt(pos);

                        if (Character.isDigit(c)) {
                            stringBuilder.append(c);
                        } else if (c == '.') {
                            stringBuilder.append(c);
                            isFloat = true;
                        } else {
                            break;
                        }

                        ++pos;
                    }

                    if (isFloat) {
                        return new FloatToken(Double.parseDouble(stringBuilder.toString()));
                    } else {
                        return new IntToken(Integer.parseInt(stringBuilder.toString()));
                    }
                }

                if (Character.isLetterOrDigit(c) || c == '_' || c == '$') {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(c);

                    while (pos < expr.length()) {
                        c = expr.charAt(pos);

                        if (Character.isLetterOrDigit(c) || c == '_' || c == '$') {
                            stringBuilder.append(c);
                        } else {
                            break;
                        }

                        ++pos;
                    }

                    String name = stringBuilder.toString();

                    if (name.equals("true") || name.equals("false")) {
                        Boolean value = Boolean.parseBoolean(name);

                        return new BoolToken(value);
                    }

                    return new IdentifierToken(name);
                }

                throw new ParserException("unable to tokenize expression");
        }
    }
}
