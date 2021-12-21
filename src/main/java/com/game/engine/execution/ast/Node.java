package com.game.engine.execution.ast;

public interface Node {
    enum Type {
        BIN_OP,
        PRIMITIVE,
        METHOD,
        VARIABLE
    }

    Type getNodeType();
}
