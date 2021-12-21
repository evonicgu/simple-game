package com.game.engine.execution.ast;

public abstract class PrimitiveNode implements Node {
    public enum PrimitiveType {
        STR,
        BOOL,
        INT,
        FLOAT
    }

    @Override
    public Type getNodeType() {
        return Type.PRIMITIVE;
    }

    public abstract PrimitiveType getPrimitiveType();
}
