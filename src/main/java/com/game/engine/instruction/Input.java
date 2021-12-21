package com.game.engine.instruction;

import com.game.engine.EngineException;
import com.game.engine.Game;
import com.game.engine.execution.TypeError;
import com.game.engine.execution.parser.ParserException;
import com.game.engine.model.Action;
import com.game.engine.model.EngineType;
import com.game.engine.model.Variable;

import java.util.Scanner;

public class Input extends Instruction {
    Scanner scanner = new Scanner(System.in);

    public Input(String expr) throws ParserException {
        super(expr);
    }

    @Override
    public Variable execute(Action currentAction, Game game) throws EngineException {
        Variable result = executor.execute(currentAction, game);

        EngineType type = result.getEngineType();

        if (type.isArray()) {
            throw new TypeError("Error: cannot use input for arrays");
        } else {
            switch (type.getBaseType()) {
                case "int" -> result.set(scanner.nextInt(), "int");
                case "bool" -> result.set(scanner.nextBoolean(), "bool");
                case "float" -> result.set(scanner.nextDouble(), "float");
                case "string" -> result.set(scanner.next(), "string");
                default -> throw new EngineException("Error: cannot use input for type " + type.getBaseType());
            }
        }

        return result;
    }
}
