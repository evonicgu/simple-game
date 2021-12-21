package com.game.engine;

import com.game.engine.execution.parser.ParserException;
import com.game.engine.model.*;
import com.game.engine.instruction.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Game {
    private final File file;
    private Document document;

    private String name, startAction;

    private final Hashtable<String, Variable> variables = new Hashtable<String, Variable>();
    private final Hashtable<String, Action> actions = new Hashtable<String, Action>();

    Action currentAction = null;

    static class XmlErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }

    public Game(String filename) throws NullPointerException {
        if (filename == null) {
            throw new NullPointerException();
        }

        file = new File(filename);
    }

    public void initialize() throws EngineException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);

        factory.setAttribute(
                "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                XMLConstants.W3C_XML_SCHEMA_NS_URI);

        factory.setAttribute(
                "http://java.sun.com/xml/jaxp/properties/schemaSource",
                "src/main/resources/game.xsd");

        try {
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(new XmlErrorHandler());

            document = documentBuilder.parse(file);
        } catch (ParserConfigurationException e) {
            throw new EngineException("Error: failed to configure the parser", e);
        } catch (IOException e) {
            throw new EngineException("Error: failed to read the file", e);
        } catch (SAXException e) {
            throw new EngineException("Error: failed to parse the file", e);
        }

        initializeVariables();

        initializeActions();
    }

    public void run() throws EngineException {
        System.out.println("Loading...");

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new EngineException("Error: loading was interrupted", e);
        }

        System.out.println("You are playing the '" + name + "' game!");

        String currentActionName = startAction;

        do {
            currentAction = cloneAction(actions.get(currentActionName));

            if (currentAction == null) {
                throw new EngineException("Error: unknown action " + currentActionName);
            }

            for (var instruction : currentAction.getInstructions()) {
                instruction.execute(currentAction, this);
            }

            var transitions = currentAction.getTransitions();

            if (transitions.isEmpty()) {
                continue;
            }

            System.out.println(currentAction.getChoiceText() + '\n');

            for (int i = 0; i < transitions.size(); ++i) {
                System.out.println(i + 1 + ") " + transitions.get(i).getOption());
            }

            var choiceInstruction = currentAction.getChoiceInstruction();

            int choice = choiceInstruction.execute(currentAction, this).<Integer>get() - 1;

            if (choice < 0 || choice >= transitions.size() ) {
                throw new EngineException("Error: choice number out of range");
            }

            var transition = transitions.get(choice);

            currentActionName = transition.getAction();

            for (var instruction : transition.getInstructions()) {
                instruction.execute(currentAction, this);
            }
        } while (!currentAction.getTransitions().isEmpty());
    }

    public Hashtable<String, Variable> getVariables() {
        return variables;
    }

    private void initializeVariables() throws EngineException {
        var root = document.getDocumentElement();

        name = root.getFirstChild().getTextContent();
        startAction = root.getElementsByTagName("start").item(0).getTextContent();

        var declarations = root.getElementsByTagName("var");

        for (int i = 0; i < declarations.getLength(); ++i) {
            var declaration = (Element) declarations.item(i);
            String name = declaration.getAttribute("name");
            String type = declaration.getAttribute("type");
            String value = declaration.getAttribute("value");

            if (variables.containsKey(name)) {
                throw new RedeclarationException(name);
            }

            variables.put(name, createVariable(type, value));
        }
    }

    private void initializeActions() throws EngineException {
        var root = document.getDocumentElement();

        var actionElements = root.getElementsByTagName("action");

        for (int i = 0; i < actionElements.getLength(); ++i) {
            var actionElement = (Element) actionElements.item(i);

            String name = actionElement.getElementsByTagName("name").item(0).getTextContent();

            if (actions.containsKey(name)) {
                throw new RedeclarationException(name);
            }

            actions.put(name, parseAction(actionElement));
        }
    }

    private Action parseAction(Element actionElement) throws EngineException {
        Action action = new Action();

        var children = actionElement.getChildNodes();

        for (int i = 0; i < children.getLength(); ++i) {
            if (!(children.item(i) instanceof Element)) {
                continue;
            }

            var child = (Element) children.item(i);
            String expr = child.getAttribute("expr");

            switch (child.getTagName()) {
                case "input" -> action.addInstruction(new Input(expr));
                case "print" -> action.addInstruction(new Print(expr));
                case "do" -> action.addInstruction(new Code(expr));
                case "var" -> action.addVariable(
                        child.getAttribute("name"),
                        createVariable(child.getAttribute("type"), child.getAttribute("value"))
                );
            }
        }

        parseChoice(action, (Element) actionElement.getElementsByTagName("choice").item(0));

        return action;
    }

    private void parseChoice(Action action, Element choiceElement) throws ParserException {
        action.setChoiceText(choiceElement.getElementsByTagName("text").item(0).getTextContent());
        action.setChoiceInstruction(new Code(choiceElement.getAttribute("expr")));

        var children = choiceElement.getElementsByTagName("goto");

        for (int i = 0; i < children.getLength(); ++i) {
            Element gotoElement = (Element) children.item(i);

            Goto transition = new Goto(gotoElement.getAttribute("action"));

            transition.setOption(gotoElement.getElementsByTagName("option").item(0).getTextContent());

            var gotoChildren = gotoElement.getChildNodes();

            for (int j = 1; j < children.getLength(); ++j) {
                if (!(gotoChildren.item(j) instanceof Element)) {
                    continue;
                }

                Element instruction = (Element) gotoChildren.item(j);

                String expr = instruction.getAttribute("expr");

                switch (instruction.getTagName()) {
                    case "input" -> transition.addInstruction(new Input(expr));
                    case "print" -> transition.addInstruction(new Print(expr));
                    case "do" -> transition.addInstruction(new Code(expr));
                }
            }

            action.addTransition(transition);
        }
    }

    private Variable createVariable(String type, String value) throws EngineException {
        if (type.endsWith("[]")) {
            if (!value.isEmpty()) {
                throw new EngineException("Error: array variables don't support default values");
            }

            return createArrayVariable(type);
        } else {
            return createPrimitiveVariable(type, value);
        }
    }

    private Variable createArrayVariable(String type) throws EngineException {
        return switch (type) {
            case "int[]", "string[]", "bool[]", "float[]" -> new Variable(new ArrayList<Variable>(), type);
            default -> throw new EngineException("Error: unexpected variable array type: " + type);
        };
    }

    private Variable createPrimitiveVariable(String type, String value) throws EngineException {
        switch (type) {
            case "int":
                if (value.isEmpty()) {
                    value = "0";
                }

                return new Variable(Integer.parseInt(value), type);
            case "float":
                if (value.isEmpty()) {
                    value = "0.0";
                }

                return new Variable(Double.parseDouble(value), type);
            case "string":
                return new Variable(value, type);
            case "bool":
                if (value.isEmpty()) {
                    value = "false";
                }

                return new Variable(Boolean.parseBoolean(value), type);
            default:
                throw new EngineException("Error: unexpected variable type: " + type);
        }
    }

    private Action cloneAction(Action action) throws EngineException {
        if (action == null) {
            return null;
        }

        Action newAction = new Action();

        newAction.setChoiceText(action.getChoiceText());
        newAction.setChoiceInstruction(action.getChoiceInstruction());
        newAction.setInstructions(action.getInstructions());
        newAction.setTransitions(action.getTransitions());

        for (var entry : action.getVariables().entrySet()) {
            Variable variable = entry.getValue();
            String name = entry.getKey();

            EngineType type = variable.getEngineType();

            if (type.isArray()) {
                newAction.getVariables().put(name, new Variable(new ArrayList<Variable>(), type.getBaseType() + "[]"));
            } else {
                newAction.getVariables().put(name, switch (type.getBaseType()) {
                    case "int" -> new Variable(variable.<Integer>get(), "int");
                    case "bool" -> new Variable(variable.<Boolean>get(), "bool");
                    case "string" -> new Variable(variable.<String>get(), "string");
                    case "float" -> new Variable(variable.<Double>get(), "float");
                    default -> throw new EngineException("Error: unable to copy the variable");
                });
            }
        }

        return action;
    }
}
