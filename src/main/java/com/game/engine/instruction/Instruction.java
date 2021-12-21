package com.game.engine.instruction;

import com.game.engine.EngineException;
import com.game.engine.Game;
import com.game.engine.execution.Executor;
import com.game.engine.execution.ast.Node;
import com.game.engine.execution.parser.Parser;
import com.game.engine.execution.parser.ParserException;
import com.game.engine.model.Action;
import com.game.engine.model.Variable;

public abstract class Instruction {
    protected Node ast;
    protected Executor executor;

    public Instruction(String expr) throws ParserException {
        Parser parser = new Parser(expr);

        this.ast = parser.parse();
        this.executor = new Executor(this.ast);
    }

    public abstract Variable execute(Action currentAction, Game game) throws EngineException;
}
