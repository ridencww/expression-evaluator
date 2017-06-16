package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.Date;


public class Token {
    private final TokenType type;
    private final Value value;
    private final Value shadowValue;

    private final int row;
    private final int column;
    private int argc;
    private String text;

    public Token() { this(null, ""); }

    public Token(TokenType type, String text) {
        this(type, text, 0, 0);
    }

    public Token(TokenType type, String text, int row, int column) {
        this.type = type == null ? TokenType.NOMATCH : type;
        this.text = text;
        this.row = row;
        this.column = column;
        this.value = new Value();
        switch (this.type) {
            case NUMBER:
                value.setValue(new BigDecimal(text));
                break;

            case STRING:
                value.setValue(text);
                break;

            default:
                break;
        }

        this.shadowValue = new Value(value);
    }

    /*---------------------------------------------------------------------------------*/

    /**
     * This ensure that any changes made in RPNtoValue will not impacted cached tokens
     */
    public void restoreValue() {
        value.set(shadowValue);
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
     * LPAREN and RPAREN are not considered operators, but the regex parses them as such. This
     * routine must not return true for those two types
     * @return true if token is an operator and not an open or close parenthesis
     *
     * Instead of having to pass in caseSensitivity, departing from the other isXXX methods, the operator
     * type is searched in case sensitive and case insensitive mode.
     */
    public boolean isOperator() {
        Operator op = Operator.find(this, false);
        if (op == null) {
            op = Operator.find(this, true);
        }
        return TokenType.OPERATOR.equals(type) && !Operator.LPAREN.equals(op) && !Operator.RPAREN.equals(op);
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

    public boolean opEquals(Operator operator) {
       return text != null && operator != null && text.equals(operator.getText());
   }

   /*----------------------------------------------------------------------------*/

    @Override
    public String toString() {
        String rc = getRow() + "," + getColumn();
        String args = TokenType.FUNCTION.equals(getType())  ? " (args=" +  argc + ")" : "";
        return String.format("%-6s %-10s %s %s", rc, getType().name(), getText(), args);
    }

}
