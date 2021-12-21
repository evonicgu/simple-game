package com.game.engine.execution;

import com.game.engine.EngineException;

public class PrimitiveMethodCallException extends EngineException {
    public PrimitiveMethodCallException(String method, String baseType) {
        this(method, baseType, null);
    }

    public PrimitiveMethodCallException(String method, String baseType, Throwable previous) {
        super("Tried to invoke method '" + method + "()' on primitive type " + baseType, previous);
    }
}
