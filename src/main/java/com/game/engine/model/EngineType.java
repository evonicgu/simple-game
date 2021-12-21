package com.game.engine.model;

import java.util.Objects;

public class EngineType {
    private String baseType;
    private boolean isArray = false;

    public EngineType(String type) {
        if (type.endsWith("[]")) {
            isArray = true;
            type = type.substring(0, type.length() - 2);
        }

        baseType = type;
    }

    public String getBaseType() {
        return baseType;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EngineType)) return false;
        EngineType that = (EngineType) o;
        return isArray == that.isArray && baseType.equals(that.baseType);
    }
}
