package com.game.engine.execution.ast;

public class BoolNode extends PrimitiveNode {
    private final boolean value;

    public BoolNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return PrimitiveType.BOOL;
    }
}
