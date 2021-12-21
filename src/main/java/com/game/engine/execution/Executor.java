package com.game.engine.execution;

import com.game.engine.EngineException;
import com.game.engine.Game;
import com.game.engine.execution.ast.*;
import com.game.engine.model.Action;
import com.game.engine.model.EngineType;
import com.game.engine.model.Variable;

import java.util.ArrayList;

public class Executor {
    private final Node ast;

    public Executor(Node ast) {
        this.ast = ast;
    }

    public Variable execute(Action currentAction, Game game) throws EngineException {
        return execute(ast, currentAction, game);
    }

    private Variable execute(Node ast, Action currentAction, Game game) throws EngineException {
        switch (ast.getNodeType()) {
            case BIN_OP -> {
                BinOpNode node = (BinOpNode) ast;

                Variable lhs = execute(node.getLhs(), currentAction, game);
                Variable rhs = execute(node.getRhs(), currentAction, game);

                EngineType lhsType = lhs.getEngineType(), rhsType = rhs.getEngineType();

                if (lhsType.getBaseType().equals("void") || rhsType.getBaseType().equals("void")) {
                    throw new TypeError("Error: cannot use void as an operand");
                }

                if (lhsType.isArray() || rhsType.isArray()) {
                    throw new OperatorException("Error: cannot use operators with non-primitive types");
                }

                return executeOperator(lhs, rhs, node.getOpType());
            }
            case METHOD -> {
                MethodCallNode node = (MethodCallNode) ast;

                Variable variable = getVariable(node.getIdentifierName(), currentAction, game);

                if (!variable.getEngineType().isArray()) {
                    throw new PrimitiveMethodCallException(node.getMethodName(), variable.getEngineType().getBaseType());
                }

                ArrayList<Variable> executedParameters = new ArrayList<Variable>();

                for (var parameter : node.getParameters()) {
                    Variable executedParameter = execute(parameter, currentAction, game);

                    if (executedParameter.getEngineType().getBaseType().equals("void")) {
                        throw new TypeError("Error: cannot use void as method argument");
                    }

                    executedParameters.add(executedParameter);
                }

                return executeMethodCall(variable, node.getMethodName(), executedParameters);
            }
            case VARIABLE -> {
                IdentifierNode node = (IdentifierNode) ast;

                return getVariable(node.getName(), currentAction, game);
            }
            case PRIMITIVE -> {
                PrimitiveNode node = (PrimitiveNode) ast;

                return switch (node.getPrimitiveType()) {
                    case INT -> new Variable(((IntegerNode) node).getValue(), "int");
                    case STR -> new Variable(((StrNode) node).getValue(), "string");
                    case BOOL -> new Variable(((BoolNode) node).getValue(), "bool");
                    case FLOAT -> new Variable(((FloatNode) node).getValue(), "float");
                };
            }
        }

        throw new EngineException("Error: unexpected ast node type");
    }

    private Variable executeMethodCall(Variable arr,
                                       String methodName,
                                       ArrayList<Variable> executedParameters) throws EngineException {
        int parameterCount = executedParameters.size();
        String listType = arr.getEngineType().getBaseType();

        switch (methodName) {
            case "add" -> {
                if (parameterCount != 1 && parameterCount != 2) {
                    throw new BadMethodCallException("Error: expected add() method to have 1 or 2 arguments");
                }

                if (parameterCount == 1) {
                    Variable parameter = executedParameters.get(0);

                    assertParameter(parameter, listType);

                    arr.<ArrayList<Variable>>get().add(parameter);
                } else {
                    Variable index = executedParameters.get(0);
                    Variable parameter = executedParameters.get(1);

                    assertParameter(index, "int");
                    assertParameter(parameter, listType);

                    arr.<ArrayList<Variable>>get().add(index.<Integer>get(), parameter);
                }

                return new Variable(null, "void");
            }
            case "clear" -> {
                if (parameterCount != 0) {
                    throw new BadMethodCallException("Error: expected clear() method to have no arguments");
                }

                arr.<ArrayList<Variable>>get().clear();
                return new Variable(null, "void");
            }
            case "contains" -> {
                if (parameterCount != 1) {
                    throw new BadMethodCallException("Error: expected contains() method to have 1 argument");
                }

                Variable parameter = executedParameters.get(0);

                assertParameter(parameter, listType);

                return new Variable(arr.<ArrayList<Variable>>get().contains(parameter), "bool");
            }
            case "get" -> {
                if (parameterCount != 1) {
                    throw new BadMethodCallException("Error: expected get() method to have 1 argument");
                }

                Variable parameter = executedParameters.get(0);

                assertParameter(parameter, "int");

                return arr.<ArrayList<Variable>>get().get(parameter.<Integer>get());
            }
            case "indexOf" -> {
                if (parameterCount != 1) {
                    throw new BadMethodCallException("Error: expected indexOf() method to have 1 argument");
                }

                Variable parameter = executedParameters.get(0);

                assertParameter(parameter, listType);

                return new Variable(arr.<ArrayList<Variable>>get().indexOf(parameter), "int");
            }
            case "isEmpty" -> {
                if (parameterCount != 0) {
                    throw new BadMethodCallException("Error: expected isEmpty() method to have no arguments");
                }

                return new Variable(arr.<ArrayList<Variable>>get().isEmpty(), "bool");
            }
            case "remove" -> {
                if (parameterCount != 1) {
                    throw new BadMethodCallException("Error: expected remove() method to have 1 arguments");
                }

                Variable parameter = executedParameters.get(0);

                assertParameter(parameter, "int");

                arr.<ArrayList<Variable>>get().remove((int) parameter.<Integer>get());

                return new Variable(null, "void");
            }
            case "set" -> {
                if (parameterCount != 2) {
                    throw new BadMethodCallException("Error: expected set() method to have 2 arguments");
                }

                Variable index = executedParameters.get(0);
                Variable element = executedParameters.get(1);

                assertParameter(index, "int");
                assertParameter(element, listType);

                arr.<ArrayList<Variable>>get().set(index.<Integer>get(), element);

                return element;
            }
            case "size" -> {
                if (parameterCount != 0) {
                    throw new BadMethodCallException("Error: expected size() to have no arguments");
                }

                return new Variable(arr.<ArrayList<Variable>>get().size(), "int");
            }
        }

        throw new BadMethodCallException("Error: undefined method " + methodName);
    }

    private void assertParameter(Variable variable, String type) throws TypeError {
        if (variable.getEngineType().isArray() || !variable.getEngineType().getBaseType().equals(type)) {
            throw new TypeError("Error: unexpected method argument type");
        }
    }

    private Variable executeOperator(Variable lhs, Variable rhs, BinOpNode.OpType opType) throws EngineException {
        String lhsType = lhs.getEngineType().getBaseType(), rhsType = rhs.getEngineType().getBaseType();

        switch (opType) {
            case EQUALS -> {
                if (!lhsType.equals(rhsType)) {
                    throw new TypeError("Error: cannot assign value to a variable of different type");
                }

                lhs.set(rhs.get(), rhs.getEngineType().getBaseType());
                return new Variable(null, "void");
            }
            case OR -> {
                if (!lhsType.equals("bool") || !rhsType.equals("bool")) {
                    throw new TypeError("Error: expected all operands to be boolean for '||' operator");
                }

                return new Variable(lhs.<Boolean>get() || rhs.<Boolean>get(), "bool");
            }
            case AND -> {
                if (!lhsType.equals("bool") || !rhsType.equals("bool")) {
                    throw new TypeError("Error: expected all operands to be boolean for '&&' operator");
                }

                return new Variable(lhs.<Boolean>get() && rhs.<Boolean>get(), "bool");
            }
            case COMPARE -> {
                return new Variable(lhs.get().equals(rhs.get()), "bool");
            }
            case COMPARE_NOT -> {
                return new Variable(!lhs.get().equals(rhs.get()), "bool");
            }
            case GT -> {
                return new Variable(lhs.<Comparable<?>>get().compareTo(rhs.get()) > 0, "bool");
            }
            case GE -> {
                return new Variable(lhs.<Comparable<?>>get().compareTo(rhs.get()) >= 0, "bool");
            }
            case LT -> {
                return new Variable(lhs.<Comparable<?>>get().compareTo(rhs.get()) < 0, "bool");
            }
            case LE -> {
                return new Variable(lhs.<Comparable<?>>get().compareTo(rhs.get()) <= 0, "bool");
            }
            case MINUS -> {
                if (!lhsType.equals(rhsType)) {
                    throw new TypeError("Error: expected all operands to have the same type for '-' operator");
                }

                if (lhsType.equals("bool") || lhsType.equals("string")) {
                    throw new TypeError("Error: cannot use '-' operator on booleans and strings");
                }

                if (lhsType.equals("int")) {
                    return new Variable(lhs.<Integer>get() - rhs.<Integer>get(), "int");
                } else {
                    return new Variable(lhs.<Double>get() - rhs.<Double>get(), "float");
                }
            }
            case PLUS -> {
                if (lhsType.equals("string") || rhsType.equals("string")) {
                    return new Variable(lhs.get().toString() + rhs.get().toString(), "string");
                }

                if (!lhsType.equals(rhsType)) {
                    throw new TypeError("Error: expected all operands to have the same type for '+' operator");
                }

                if (lhsType.equals("bool")) {
                    throw new TypeError("Error: cannot use '+' operator on booleans");
                }

                if (lhsType.equals("int")) {
                    return new Variable(lhs.<Integer>get() + rhs.<Integer>get(), "int");
                } else {
                    return new Variable(lhs.<Double>get() + rhs.<Double>get(), "float");
                }
            }
            case MUL -> {
                if (!lhsType.equals(rhsType)) {
                    throw new TypeError("Error: expected all operands to have the same type for '*' operator");
                }

                if (lhsType.equals("bool") || lhsType.equals("string")) {
                    throw new TypeError("Error: cannot use '*' operator on booleans and strings");
                }

                if (lhsType.equals("int")) {
                    return new Variable(lhs.<Integer>get() * rhs.<Integer>get(), "int");
                } else {
                    return new Variable(lhs.<Double>get() * rhs.<Double>get(), "float");
                }
            }
            case DIV -> {
                if (!lhsType.equals(rhsType)) {
                    throw new TypeError("Error: expected all operands to have the same type for '/' operator");
                }

                if (lhsType.equals("bool") || lhsType.equals("string")) {
                    throw new TypeError("Error: cannot use '/' operator on booleans and strings");
                }

                if (lhsType.equals("int")) {
                    return new Variable(lhs.<Integer>get() / rhs.<Integer>get(), "int");
                } else {
                    return new Variable(lhs.<Double>get() / rhs.<Double>get(), "float");
                }
            }
            case MOD -> {
                if (!lhsType.equals("int") || !rhsType.equals("int")) {
                    throw new TypeError("Error: expected all operands to be integers for '%' operator");
                }

                return new Variable(lhs.<Integer>get() % rhs.<Integer>get(), "int");
            }
        }

        throw new EngineException("Error: unexpected operator type");
    }

    private Variable getVariable(String name, Action currentAction, Game game) throws UndefinedVariableException {
        if (currentAction.getVariables().containsKey(name)) {
            return currentAction.getVariables().get(name);
        }

        if (game.getVariables().containsKey(name)) {
            return game.getVariables().get(name);
        }

        throw new UndefinedVariableException(name);
    }
}
