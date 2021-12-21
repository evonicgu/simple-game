package com.game.engine.execution.parser.token;

public class IdentifierToken extends Token {
    private final String name;

    public IdentifierToken(String name) {
        super(Type.T_IDENTIFIER);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
