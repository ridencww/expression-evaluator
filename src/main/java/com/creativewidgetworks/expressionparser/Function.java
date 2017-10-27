package com.creativewidgetworks.expressionparser;

import java.lang.reflect.Method;
import java.util.*;

public class Function {
    private final String functionName;
    private final int minArgs;
    private final int maxArgs;

    private final Object javaInstance;
    private final Method javaMethod;
    private final ValueType[] parameters;

    public Function(String functionName, Object instance, String methodName, int minArgs, int maxArgs, ValueType... types) {
        this.functionName = functionName;

        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.parameters = types;

        this.javaInstance = instance;

        String className = instance.getClass().getName();

        try {
            javaMethod = instance.getClass().getMethod(methodName, Token.class, Stack.class);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Init " + className + " NoSuchMethodException " + methodName, ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException("Init " + className + " " + methodName, ex);
        }
    }

    /*---------------------------------------------------------------------------------*/

    public String getName() {
        return functionName;
    }

    /*---------------------------------------------------------------------------------*/

    public Value execute(Token function, Stack<Token> stack) throws ParserException {
        Value value;

        try {
            validateParameters(function, stack);
            value = (Value) javaMethod.invoke(javaInstance, function, stack);
        } catch (ParserException pex) {
            throw pex;
        } catch (Exception ex) {
            if (ex.getCause() instanceof ParserException) {
                throw (ParserException)ex.getCause();
            } else {
                String msg = ex.getMessage();
                if (msg == null) {
                    msg = ex.getCause().getMessage();
                    if (msg == null) {
                        msg = ex.getCause().toString();
                    }
                }
                throw new ParserException(msg, ex, function.getRow(), function.getColumn());
            }
        }

        return value;
    }

    /*----------------------------------------------------------------------------*/

    private void validateParameters(Token function, Stack<Token> stack) throws ParserException {
        if (stack != null && function != null) {
            // Make sure the number of arguments parsed is within the accepted range
            if (function.getArgc() < minArgs || function.getArgc() > maxArgs) {
                String strMaxArgs = maxArgs != Integer.MAX_VALUE ? String.valueOf(maxArgs) : "n";
                String str = (minArgs == maxArgs) ? String.valueOf(minArgs) : (minArgs + ".." + strMaxArgs);
                String msg = ParserException.formatMessage("error.function_parameter_count", function.getText(), str, function.getArgc());
                throw new ParserException(msg, function.getRow(), function.getColumn() + function.getText().length());
            }

            // Validate parameter types that were passed to the function
            if (parameters != null && parameters.length > 0) {
                for (int i = 0; i < function.getArgc(); i++) {
                    // If number of arguments is within range, but fewer parameter types
                    // were specified for testing, exit.  This supports having functions
                    // that pass in an open ended number of parameters without having to
                    // specify the type of each and every parameter
                    if (i >= parameters.length) {
                        break;
                    }

                    // Get next parameter to test
                    Token token = stack.get(stack.size() - function.getArgc() + i);

                    // Any token whose value is null will cause the testing of that
                    // parameter to be skipped. This supports passing a NULL value
                    // into a function.
                    if (token.getValue().asObject() == null) {
                        continue;
                    }

                    // Make sure parameter type agrees with what is expected
                    if (parameters[i] != token.getValue().getType() && parameters[i] != ValueType.UNDEFINED) {
                        String msg = ParserException.formatMessage("error.function_type_mismatch",
                                function.getText(), String.valueOf(i + 1), parameters[i].name(), token.getValue().getType().name());
                        throw new ParserException(msg, function.getRow(), function.getColumn() + function.getText().length());
                    }
                }
            }
        }
    }

}
