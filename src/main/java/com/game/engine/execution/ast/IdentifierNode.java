package com.game.engine.execution.ast;

public class IdentifierNode implements Node {
    private final String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    @Override
    public Type getNodeType() {
        return Type.VARIABLE;
    }

    public String getName() {
        return name;
    }
}
