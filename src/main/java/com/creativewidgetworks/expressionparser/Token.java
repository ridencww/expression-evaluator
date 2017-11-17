package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.Date;


public class Token {
    private final TokenType type;

    private Value value;
    private Value orgValue;

    private final int row;
    private final int column;
    private String text;

    private int argc;

    public Token(TokenType type, String text, int row, int column) {
        this(type, text, null, row, column);
        Value value = new Value();
        if (type != null) {
            if (TokenType.NUMBER.equals(type)) {
                value = new Value("number", new BigDecimal(text));
            } else if (TokenType.STRING.equals(type)) {
                value = new Value("string", text);
            }
        }
        setValue(value);

    }

    public Token(TokenType type, Value value, int row, int column) {
        this(type, "VALUE", value, row, column);
    }

    public Token(TokenType type, String text, Value value, int row, int column) {
        this.type = type == null ? TokenType.NOMATCH : type;
        this.text = text;
        this.row = row;
        this.column = column;
        this.value = new Value(value);
    }

    /*---------------------------------------------------------------------------------*/

    void saveOrgValue() {
        orgValue = new Value(value);
    }

    void restoreOrgValue() {
        value.set(orgValue);
    }

    /*---------------------------------------------------------------------------------*/

    public Boolean asBoolean() {
        return getValue() != null ? getValue().asBoolean() : null;
    }

    public Date asDate() {
        return getValue() != null ? getValue().asDate() : null;
    }

    public BigDecimal asNumber() {
        return getValue() != null ? getValue().asNumber() : null;
    }

    public Object asObject() {
        return getValue() != null ? getValue().asObject() : null;
    }

    public String asString() {
        return getValue() != null ? getValue().asString() : null;
    }

   /*---------------------------------------------------------------------------------*/

    public boolean equals(Operator operator) {
        return operator != null && getText() != null && getText().equalsIgnoreCase(operator.getText());
    }

    /*---------------------------------------------------------------------------------*/

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getText() {
        return text;
    }

    public TokenType getType() {
        return type;
    }

    public Value getValue() {
        return value;
    }

    public int getArgc() {
        return argc;
    }

    public boolean isConstant() {
        return TokenType.CONSTANT.equals(type);
    }
    public boolean isField() {
        return TokenType.FIELD.equals(type);
    }

    public boolean isFunction() {
        return TokenType.FUNCTION.equals(type);
    }

    public boolean isIdentifer() {
        return TokenType.IDENTIFIER.equals(type);
    }

    public boolean isNumber() {
        return TokenType.NUMBER.equals(type);
    }

    /**
     * LPAREN, RPAREN, LBRACKET, and RBRACKET are not considered operators, but the regex parses them
     * as such. This routine must not return true for those four types
     * @return true if token is an operator and not an open or close parenthesis
     *
     * Instead of having to pass in caseSensitivity, departing from the other isXXX methods, the operator
     * type is searched in case sensitive and case insensitive mode. The searching only occurs for token
     * types that are functions.
     */
    public boolean isOperator() {
        if (TokenType.OPERATOR.equals(type)) {
            Operator op = Operator.find(this, true);
            if (op == null) {
                op = Operator.find(this, false);
            }
            return !Operator.LPAREN.equals(op) && !Operator.RPAREN.equals(op)  &&
                    !Operator.LBRACKET.equals(op) && !Operator.RBRACKET.equals(op);
        } else {
            return false;
        }
    }

    public boolean isParen() {
        Operator op = Operator.find(this, false);
        if (op == null) {
            op = Operator.find(this, true);
        }
        return TokenType.OPERATOR.equals(type) && Operator.LPAREN.equals(op);
    }

    public boolean isProperty() {
        return TokenType.PROPERTY.equals(type);
    }

    public boolean isString() {
        return TokenType.STRING.equals(type);
    }


    public Token setArgc(int argc) {
        this.argc = argc;
        return this;
    }

    public Token setText(String text) {
        this.text = text;
        return this;
    }

    public Token setValue(Value value) {
        this.value.set(value);
        return this;
    }

    /*----------------------------------------------------------------------------*/

    public boolean opEquals(Operator... operators) {
        boolean result = false;
        if (text != null && operators != null) {
            for (Operator operator : operators) {
                result = operator != null && text.equals(operator.getText());
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

   /*----------------------------------------------------------------------------*/

    @Override
    public String toString() {
        String rc = getRow() + "," + getColumn();
        String args = TokenType.FUNCTION.equals(getType())  ? " (args=" +  argc + ")" : "";
        return String.format("%-6s %-10s %s %s", rc, getType().name(), getText(), args);
    }

}
