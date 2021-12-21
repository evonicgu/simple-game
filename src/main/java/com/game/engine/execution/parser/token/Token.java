package com.game.engine.execution.parser.token;

public class Token {
    public enum Type {
        T_DOT,
        T_COMMA,
        T_AND,
        T_OR,
        T_COMPARE,
        T_COMPARE_NOT,
        T_LT,
        T_LE,
        T_GT,
        T_GE,
        T_EQUALS,
        T_PLUS,
        T_MINUS,
        T_MUL,
        T_DIV,
        T_MOD,
        T_IDENTIFIER,
        T_STR,
        T_BOOL,
        T_INT,
        T_FLOAT,
        T_PAREN_OPEN,
        T_PAREN_CLOSE,
        T_END
    }

    private final Type type;

    public Token(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
