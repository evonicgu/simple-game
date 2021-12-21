package com.game.engine.execution.ast;

public class FloatNode extends PrimitiveNode {
    private final double value;

    public FloatNode(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return PrimitiveType.FLOAT;
    }
}
