package com.game.engine.execution.ast;

import java.util.ArrayList;

public class MethodCallNode implements Node {
    private final String identifierName, methodName;

    private final ArrayList<Node> parameters;

    public MethodCallNode(String identifierName, String methodName, ArrayList<Node> parameters) {
        this.identifierName = identifierName;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    @Override
    public Type getNodeType() {
        return Type.METHOD;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public String getMethodName() {
        return methodName;
    }

    public ArrayList<Node> getParameters() {
        return parameters;
    }
}
