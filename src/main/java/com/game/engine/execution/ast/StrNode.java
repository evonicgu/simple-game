package com.game.engine.execution.ast;

public class StrNode extends PrimitiveNode {
    private final String value;

    public StrNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return PrimitiveType.STR;
    }
}
