package com.game.engine.execution.parser.token;

public class IntToken extends Token {
    private final int value;

    public IntToken(int value) {
        super(Type.T_INT);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
