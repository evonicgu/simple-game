package com.game.engine.execution.parser.token;

public class StrToken extends Token {
    private final String value;

    public StrToken(String value) {
        super(Type.T_STR);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
