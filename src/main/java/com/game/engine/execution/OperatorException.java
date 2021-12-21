package com.game.engine.execution;

import com.game.engine.EngineException;

public class OperatorException extends EngineException {
    public OperatorException(String error) {
        this(error, null);
    }

    public OperatorException(String error, Throwable previous) {
        super(error, previous);
    }
}
