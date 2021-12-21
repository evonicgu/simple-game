package com.game;

import com.game.engine.EngineException;
import com.game.engine.Game;

public class Main {
    public static void main(String[] args) {
        Game game = new Game("game.xml");

        try {
            game.initialize();

            game.run();
        } catch (EngineException e) {
            System.out.println("Text game error:");
            System.err.println(e);
        }
    }
}
