package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Abstract class that allows the expression parser to define the
 * grammar used when parsing expressions.  The comments to the
 * right of the terminals represent the most common values that
 * are returned by the method call.
 */
public abstract class Grammar {
    // Default numeric precision (number of decimal places)
    public static final int DEFAULT_PRECISION = 5;
    
    // Associative rule constants
    public final int NON_ASSOCIATIVE   = 0;
    public final int LEFT_ASSOCIATIVE  = 1;
    public final int RIGHT_ASSOCIATIVE = 2;
    
    // RegEx pattern strings for token types
    public abstract String getPattern_CONSTANT();
    public abstract String getPattern_FUNCTION();
    public abstract String getPattern_NUMBER();
    public abstract String getPattern_STRING();
    public abstract String getPattern_OPERATOR();
    public abstract String getPattern_SEPARATOR();
    public abstract String getPattern_IDENTIFIER();
    public abstract String getPattern_PROPERTY();
    public abstract String getPattern_EOS();
    public abstract String getPattern_NOMATCH();
    public abstract String getPattern_WHITESPACE();
    
    // Separator terminals
    public abstract String getCOMMA();           //  , 
    public abstract String getLPAREN();          //  (
    public abstract String getRPAREN();          //  )

    // Assignment
    public abstract String getASSIGN();          //  =   assignment

    // Ternary
    public abstract String getTIF();             //  ?
    public abstract String getTELSE();           //  :    
    
    // Basic math operators
    public abstract String getPLUS();            //  +
    public abstract String getMINUS();           //  -
    public abstract String getMULT();            //  *
    public abstract String getDIV();             //  /
    public abstract String getIDIV();            //  \   integer divide
    public abstract String getIDIV2();           // DIV  alternative integer divide, terminal (can return same value as IDIV)
    public abstract String getMODULUS();         // MOD  modulus (remainder after integer division)
    public abstract String getUNARY_MINUS();     //  !   negation (also used internally by the parser)
    public abstract String getUNARY_PLUS();      //  !!  (used internally by the parser)
    public abstract String getEXP();             //  ^   exponentiation e.g., x^y
    public abstract String getPERCENT();         //  %   multiply left symbol by 0.01 e.g., 10% -> .1
 
    // Comparison operators
    public abstract String getEQU();             //  ==  equal    
    public abstract String getNEQ();             //  !=  not equal
    public abstract String getLT();              //  <   less than
    public abstract String getLTE();             //  <=  less than or equal 
    public abstract String getGT();              //  >   greater than
    public abstract String getGTE();             //  >=  greater than or equal

    // Logical operators
    public abstract String getAND();             //  AND  logical AND 
    public abstract String getOR();              //  OR   logical OR
    public abstract String getNOT();             //  NOT  logical NOT
    public abstract String getLSHIFT();          //  <<   left shift
    public abstract String getRSHIFT();          //  >>   right shift
   
    /*----------------------------------------------------------------------------*/

    // These methods are how the parser interacts with a specific grammar
    public abstract BigDecimal getConstant(String constant, boolean caseSensitive);
    public abstract Function getFunction(String function, boolean caseSensitive);
    public abstract int[] getOperatorData(String operator, boolean caseSensitive);
    public abstract Object getProperty(String property);
    
    /*----------------------------------------------------------------------------*/

    // Number of digits of precision for math operations
    private int precision = DEFAULT_PRECISION;
    
    public int getPrecision() {
        return precision;
    }
    
    public void setPrecision(int precision) {
        this.precision = precision;
    }
    
    protected BigDecimal scale(BigDecimal number) {
        BigDecimal result = number;
        if (number != null) {
            if (number.doubleValue() == 0.0) {
                result = BigDecimal.ZERO;
            } else {
                result = number.setScale(getPrecision(), BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
            }
        }
        return result;
    }
    
    /*----------------------------------------------------------------------------*/
    
    private int compareTokens(Symbol token1, Symbol token2, boolean caseSensitive) throws ParserException {
        if (!token1.isOperator()) {
            throw new ParserException(ParserException.formatMessage("error.operator_expected", token1.getType().name()));
        } else if (!token2.isOperator()) {
            throw new ParserException(ParserException.formatMessage("error.operator_expected_at_top", token1.getType().name()));
        }
        
        int[] data1 = getOperatorData(token1.getText(), caseSensitive);
        if (data1 == null) {
            throw new ParserException(ParserException.formatMessage("error.operator_not_found", token1.getText()));
        }
        
        int[] data2 = getOperatorData(token2.getText(), caseSensitive);
        if (data2 == null) {
            throw new ParserException(ParserException.formatMessage("error.operator_not_found", token2.getText()));
        }
        
        return data1[0] - data2[0];
    }

    private boolean isType(Symbol token, int type, boolean caseSensitive) throws ParserException {
        if (!token.isOperator()) {
            throw new ParserException(ParserException.formatMessage("error.expected_operator_token", token.getText(), token.getType().name()));
        }
        
        int[] data = getOperatorData(token.getText(), caseSensitive);
        if (data == null) {
            throw new ParserException(ParserException.formatMessage("error.operator_not_found", token.getText()));
        }
        
        return data[1] == type;
    }    
    
    public boolean shouldPopToken(Symbol token, Symbol topOfStack, boolean caseSensitive) throws ParserException {
        return (isType(token, LEFT_ASSOCIATIVE, caseSensitive) && compareTokens(token, topOfStack, caseSensitive) <= 0) || 
               (isType(token, RIGHT_ASSOCIATIVE, caseSensitive) && compareTokens(token, topOfStack, caseSensitive) < 0);
    }
    
    /*----------------------------------------------------------------------------*/
    
    /**
     * Pops the arguments off of the stack and returns an array in reverse order
     * (e.g., 1, 2, "RI" -> "RI", 2, 1).  This ensures optional arguments in
     * function calls appear at the end of the array and not the beginning.
     */
    protected Symbol[] popArguments(Symbol function, Stack<Symbol> stack) {
        Symbol[] args = null;
        if (function.getArgc() > 0 && stack.size() > 0) {
            args = new Symbol[function.getArgc()];
            for (int i = function.getArgc() - 1; i >= 0; i--) {
                args[i] = stack.pop();
            }
        } else {
            args = new Symbol[0];
        }
        return args;
    }
    
}
