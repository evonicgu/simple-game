package com.game.engine.execution;

import com.game.engine.EngineException;

public class UndefinedVariableException extends EngineException {
    public UndefinedVariableException(String name) {
        this(name, null);
    }

    public UndefinedVariableException(String name, Throwable previous) {
        super("Error: undefined variable " + name, previous);
    }
}
