package com.game.engine.model;

import com.game.engine.EngineException;

import java.util.Objects;

public class Variable {
    private Object content;
    private EngineType engineType;

    public <T> Variable(T content, String engineType) {
        this.content = content;
        this.engineType = new EngineType(engineType);
    }

    public <V> V get() throws EngineException {
        try {
            return (V) content;
        } catch (ClassCastException e) {
            throw new EngineException("Unexpected variable type: " + engineType, e);
        }
    }

    public <T> void set(T content, String engineType) {
        this.content = content;
        this.engineType = new EngineType(engineType);
    }

    public EngineType getEngineType() {
        return engineType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable variable = (Variable) o;
        return engineType.equals(variable.engineType) && content.equals(variable.content);
    }
}
