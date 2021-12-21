package com.game.engine.execution;

import com.game.engine.EngineException;

public class BadMethodCallException extends EngineException {
    public BadMethodCallException(String error) {
        this(error, null);
    }

    public BadMethodCallException(String error, Throwable previous) {
        super(error, previous);
    }
}
