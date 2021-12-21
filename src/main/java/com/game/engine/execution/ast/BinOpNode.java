package com.game.engine.execution.ast;

public class BinOpNode implements Node {
    private final Node lhs, rhs;

    public Node getLhs() {
        return lhs;
    }

    public Node getRhs() {
        return rhs;
    }

    public OpType getOpType() {
        return opType;
    }

    public enum OpType {
        EQUALS,
        OR,
        AND,
        COMPARE,
        COMPARE_NOT,
        GT,
        GE,
        LT,
        LE,
        PLUS,
        MINUS,
        MUL,
        DIV,
        MOD
    }

    private final OpType opType;

    public BinOpNode(Node lhs, Node rhs, OpType opType) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.opType = opType;
    }

    @Override
    public Type getNodeType() {
        return Type.BIN_OP;
    }
}
