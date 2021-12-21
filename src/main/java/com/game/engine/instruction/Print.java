package com.game.engine.instruction;

import com.game.engine.EngineException;
import com.game.engine.Game;
import com.game.engine.execution.parser.ParserException;
import com.game.engine.model.Action;
import com.game.engine.model.EngineType;
import com.game.engine.model.Variable;

import java.util.ArrayList;

public class Print extends Instruction {
    public Print(String expr) throws ParserException {
        super(expr);
    }

    @Override
    public Variable execute(Action currentAction, Game game) throws EngineException {
        Variable result = executor.execute(currentAction, game);

        if (result.getEngineType().isArray()) {
            System.out.print('[');
            boolean first = true;

            for (Variable o : result.<ArrayList<Variable>>get()) {
                if (!first) {
                    System.out.print(", ");
                }

                first = false;

                printOne(o);
            }

            System.out.print(']');
        } else {
            printOne(result);
        }

        System.out.println();

        return new Variable(null, "void");
    }

    private void printOne(Variable result) throws EngineException {
        EngineType type = result.getEngineType();

        System.out.print(switch (type.getBaseType()) {
            case "int" -> result.<Integer>get();
            case "string" -> result.<String>get();
            case "bool" -> result.<Boolean>get();
            case "float" -> result.<Double>get();
            default -> throw new EngineException("Error: cannot print value of type " + type.getBaseType());
        });
    }
}
