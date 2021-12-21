package com.game.engine.model;

import com.game.engine.EngineException;

public class RedeclarationException extends EngineException {
    public RedeclarationException(String name) {
        this(name, null);
    }

    public RedeclarationException(String name, Throwable previous) {
        super("Error: symbol " + name + " was redeclared", previous);
    }
}
