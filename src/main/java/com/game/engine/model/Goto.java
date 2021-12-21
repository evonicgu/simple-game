package com.game.engine.model;

import com.game.engine.instruction.Instruction;

import java.util.ArrayList;

public class Goto {
    private String action, option;

    private ArrayList<Instruction> instructions = new ArrayList<Instruction>();

    public Goto(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public String getOption() {
        return option;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }
}
