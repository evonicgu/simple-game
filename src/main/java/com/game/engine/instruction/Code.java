package com.game.engine.instruction;

import com.game.engine.EngineException;
import com.game.engine.Game;
import com.game.engine.execution.parser.ParserException;
import com.game.engine.model.Action;
import com.game.engine.model.Variable;

public class Code extends Instruction {
    public Code(String expr) throws ParserException {
        super(expr);
    }

    @Override
    public Variable execute(Action currentAction, Game game) throws EngineException {
        return executor.execute(currentAction, game);
    }
}
