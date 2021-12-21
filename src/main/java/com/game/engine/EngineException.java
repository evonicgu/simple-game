package com.game.engine;

public class EngineException extends Exception {
    public EngineException(String error) {
        super(error);
    }

    public EngineException(String error, Throwable previous) {
        super(error, previous);
    }
}
