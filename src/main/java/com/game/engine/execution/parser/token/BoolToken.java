package com.game.engine.execution.parser.token;

public class BoolToken extends Token {
    private final Boolean value;

    public BoolToken(Boolean value) {
        super(Type.T_BOOL);
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }
}
