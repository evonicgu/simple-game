package com.game.engine.execution.parser;

import com.game.engine.EngineException;

public class ParserException extends EngineException {
    public ParserException(String error) {
        this(error, null);
    }

    public ParserException(String error, Throwable previous) {
        super("Parse error: " + error, previous);
    }
}
