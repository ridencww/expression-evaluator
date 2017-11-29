package com.creativewidgetworks.expressionparser;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ParserException extends Exception {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/creativewidgetworks/expressionparser/parser");

    private int errorAtRow;
    private int errorAtCol;

    public ParserException(String msg) {
        super(msg);
        initialize(0, 0);
    }

    public ParserException(String msg, Throwable th) {
        super(msg, th);
        initialize(0, 0);
    }

    public ParserException(String msg, int row, int column) {
        super(msg);
        initialize(row, column);
    }

    public ParserException(String msg, Throwable th, int row, int column) {
        super(msg, th);
        initialize(row, column);
    }

    private void initialize(int row, int column) {
        this.errorAtRow = row;
        this.errorAtCol = column;
    }

    public int getErrorRow() {
        return errorAtRow;
    }

    public int getErrorColumn() {
        return errorAtCol;
    }


    /**
     * Format a message with the given parameters. If the bundle is not null then the message is
     * treated as a key in that bundle; the found resource will be used as the message to format.
     *
     * This method provides a convenient wrapper for java.util.ResourceBundle and
     * java.text.MessageFormat operations because all exceptions are caught and a simple
     * concatenation of the original message and the parameters is returned.
     *
     * @param message the message key to translate
     * @param parameters optional values for substitution
     *
     * @return String (with the resource message or the original message + parameters if no resource found)
     */
     public static String formatMessage( String message, Object... parameters) {
        String formattedMessage = message;

        if (message != null) {
            // Lookup message in resource bundle, if applicable
            if (bundle != null) {
                try {
                    formattedMessage = bundle.getString(message);
                } catch (MissingResourceException mre) {
                    // Do nothing, we'll just use the message itself
                }
            }

            // Format message with parameters, if applicable
            if (parameters != null) {
                try {
                    // Parameters provided, but template doesn't have any place holders. This
                    // can happen with a badly formed or missing template value
                    if (!formattedMessage.contains("{")) {
                        throw new IllegalArgumentException();
                    }
                    formattedMessage = MessageFormat.format(formattedMessage, parameters);
                } catch(IllegalArgumentException iae) {
                    StringBuilder sb = new StringBuilder();
                    for (Object parameter : parameters) {
                        sb.append(";");
                        sb.append(parameter);
                    }
                    formattedMessage += sb.toString();
                }
            }
        }

        return formattedMessage;
    }

}
