package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.Date;

import com.creativewidgetworks.expressionparser.enums.SymbolType;

public class Symbol {
    private int argc;
    private int row;
    private int column;
    private String text;
    private Value value;
    private SymbolType type;

    public Symbol() {
        this(SymbolType.UNDEFINED, null, -1, -1);
    }
    
    public Symbol(SymbolType type, String text, int row, int column) {
        this.argc = 0;
        this.row = row;
        this.column = column;
        this.type = type;
        this.text = text;
        this.value = new Value();
        switch (type) {
            case NUMBER:
                value.setValue(new BigDecimal(text));
                break;
                
            case STRING:
                value.setValue(stripQuotes(text));
                break;
                
            default:
                break;
        }
    }

    public int getArgc() {
        return argc;
    }

    public void setArgc(int argc) {
        this.argc = argc;
    }
    
    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public SymbolType getType() {
        return type;
    }
    
    public Value getValue() {
        return value;
    }
    
    public Symbol setValue(Value value) {
        getValue().set(value);
        return this;
    }
    
    public Boolean asBoolean() {
        return value != null ? value.asBoolean() : null;
    }

    public Date asDate() {
        return value != null ? value.asDate() : null;
    }

    public BigDecimal asNumber() {
        return value != null ? value.asNumber() : null;
    }

    public String asString() {
        return value != null ? value.asString() : null;
    }

    public boolean isConstant() {
        return type == SymbolType.CONSTANT;
    }

    public boolean isFunction() {
        return type == SymbolType.FUNCTION;
    }

    public boolean isIdentifer() {
        return type == SymbolType.IDENTIFIER;
    }

    public boolean isNumber() {
        return type == SymbolType.NUMBER;
    }

    public boolean isString() {
        return type == SymbolType.STRING;
    }

    public boolean isOperator() {
        return type == SymbolType.OPERATOR;
    }

    public boolean isProperty() {
        return type == SymbolType.PROPERTY;
    }

    public boolean isSeparator() {
        return type == SymbolType.SEPARATOR;
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Override
    public boolean equals(Object obj) {
        return getText().equals(obj);
    }

    /*----------------------------------------------------------------------------*/
    
    /* 
     * Remove the quote characters (if present) from the beginning and end of a string
     * @param String to process
     * @return text with quotes removed
     */
    private String stripQuotes(String tokenText) {
        if (tokenText != null && tokenText.length() > 1 && (
            tokenText.startsWith("\"") && tokenText.endsWith("\"") ||
            tokenText.startsWith("'") && tokenText.endsWith("'"))) {
            tokenText = tokenText.substring(1, tokenText.length() - 1);
        }
        return tokenText;
    }    
    
    /*----------------------------------------------------------------------------*/
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        switch (type) {
            case CONSTANT   : sb.append("CONSTANT"); break; 
            case FUNCTION   : sb.append("FUNCTION"); break;
            case EOS        : sb.append("EOS"); break;
            case IDENTIFIER : sb.append("IDENTIFER"); break; 
            case NOMATCH    : sb.append("NOMATCH"); break;
            case NUMBER     : sb.append("NUMBER"); break;
            case OPERATOR   : sb.append("OPERATOR"); break;
            case PROPERTY   : sb.append("PROPERTY"); break;
            case SEPARATOR  : sb.append("SEPARATOR"); break;
            case STRING     : sb.append("STRING"); break;
        }

        while (sb.length() < 15) sb.append(".");
        sb.append(" ").append(getText());
        if (type == SymbolType.FUNCTION) {
            sb.append(" (args=").append(argc).append(")");
        }
        
        return sb.toString();
    }

}    
