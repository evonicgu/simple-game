package com.game.engine.execution;

import com.game.engine.EngineException;

public class TypeError extends EngineException {
    public TypeError(String error) {
        this(error, null);
    }

    public TypeError(String error, Throwable previous) {
        super(error, previous);
    }
}
