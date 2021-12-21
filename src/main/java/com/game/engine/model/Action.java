package com.game.engine.model;

import com.game.engine.instruction.Instruction;

import java.util.ArrayList;
import java.util.Hashtable;

public class Action {
    private Hashtable<String, Variable> variables = new Hashtable<String, Variable>();

    private ArrayList<Instruction> instructions = new ArrayList<Instruction>();
    private ArrayList<Goto> transitions = new ArrayList<Goto>();

    private Instruction choiceInstruction;
    private String choiceText;

    public void addVariable(String name, Variable variable) throws RedeclarationException {
        if (variables.containsKey(name)) {
            throw new RedeclarationException(name);
        }

        variables.put(name, variable);
    }

    public void addTransition(Goto transition) {
        transitions.add(transition);
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void setInstructions(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
    }

    public void setTransitions(ArrayList<Goto> transitions) {
        this.transitions = transitions;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public void setChoiceInstruction(Instruction instruction) {
        this.choiceInstruction = instruction;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public Hashtable<String, Variable> getVariables() {
        return variables;
    }

    public ArrayList<Goto> getTransitions() {
        return transitions;
    }

    public Instruction getChoiceInstruction() {
        return choiceInstruction;
    }

    public String getChoiceText() {
        return choiceText;
    }
}
