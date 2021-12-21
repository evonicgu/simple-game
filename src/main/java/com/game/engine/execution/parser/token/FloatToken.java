package com.game.engine.execution.parser.token;

public class FloatToken extends Token {
    private final Double value;

    public FloatToken(Double value) {
        super(Type.T_FLOAT);
        this.value = value;
    }

    public Double getValue() {
        return value;
    }
}
