package com.game.engine.execution.ast;

public class IntegerNode extends PrimitiveNode {
    private final int value;

    public IntegerNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return PrimitiveType.INT;
    }
}
