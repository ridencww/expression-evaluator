package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.creativewidgetworks.expressionparser.enums.ValueType;

/**
 * Defines the grammar tokens for a simple expression parser comparable
 * to a four function calculator
 */
public class GrammarBasicCalc extends Grammar {
    
    private Map<String, BigDecimal>   CONSTANTS;
    private Map<String, int[]>        OPERATORS;
    private Map<String, Function>     FUNCTIONS;
    
    /*----------------------------------------------------------------------------*/
    
    public String getPattern_CONSTANT() { 
        return "pi|null"; 
    }
    
    public String getPattern_FUNCTION() { 
        return "abs|now|sqrt|sqr"; 
    }
    
    public String getPattern_NUMBER() { 
        return "(?:\\b[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+\\b)(?:[eE][-+]?[0-9]+\\b)?"; 
    }
    
    public String getPattern_STRING() { 
        return "(\"[^\"\\\\\\r\\n]*(?:\\\\.[^\"\\\\\\r\\n]*)*\")|('[^'\\\\\\r\\n]*(?:\\\\.[^'\\\\\\r\\n]*)*')"; 
    }
    
    public String getPattern_OPERATOR() {
        return "(\\?|:|\\*|\\/|%|\\+|\\-|\\^|==|!=|<=|<|>=|>|and|xor|or|not|div|mod|\\\\|!+|=)";
    }
    
    public String getPattern_SEPARATOR() {
        return "[\\(\\){}\\[\\],]";
    }
    
    public String getPattern_IDENTIFIER() {
        return "[_A-Za-z][_A-Za-z0-9]*";
    }

    public String getPattern_PROPERTY() {
        return "\\$\\{(.*)}";  //  ${...}
    }
    
    public String getPattern_EOS() {
        return "\\z";
    }
    
    public String getPattern_NOMATCH() {
        return ".*";
    }
    
    public String getPattern_WHITESPACE() {
        return "\\G\\s+";
    }    
    
    /*----------------------------------------------------------------------------*/
    
    // Separator terminals
    public String getCOMMA()       { return ","; }  
    public String getLPAREN()      { return "("; }
    public String getRPAREN()      { return ")"; }

    // Assignment
    public String getASSIGN()      { return "="; }
    
    // Ternary
    public String getTIF()         { return "?"; }
    public String getTELSE()       { return ":"; }

    // Basic math operators
    public String getPLUS()        { return "+"; }
    public String getMINUS()       { return "-"; }
    public String getMULT()        { return "*"; }
    public String getDIV()         { return "/"; }
    public String getIDIV()        { return "\\"; }
    public String getIDIV2()       { return "DIV"; }
    public String getMODULUS()     { return "MOD"; }
    public String getUNARY_MINUS() { return "!"; }
    public String getUNARY_PLUS()  { return "!!"; }
    public String getEXP()         { return "^"; }
    public String getPERCENT()     { return "%"; }
 
    // Comparison operators
    public String getEQU()         { return "=="; }    
    public String getNEQ()         { return "!="; }
    public String getLT()          { return "<"; } 
    public String getLTE()         { return "<="; } 
    public String getGT()          { return ">"; } 
    public String getGTE()         { return ">="; }

    // Logical operators
    public String getAND()         { return "AND"; } 
    public String getOR()          { return "OR"; }
    public String getNOT()         { return "NOT"; }
    public String getLSHIFT()      { return "<<"; }
    public String getRSHIFT()      { return ">>"; }
    
    /*----------------------------------------------------------------------------*/
    
    public BigDecimal getConstant(String constant, boolean caseSensitive) {
        if (CONSTANTS == null) {
            CONSTANTS = new HashMap<String, BigDecimal>();
            CONSTANTS.put("NULL", null);
            CONSTANTS.put("PI", new BigDecimal(Math.PI).setScale(15, BigDecimal.ROUND_HALF_UP));        
        }
    
        return constant == null ? null : CONSTANTS.get(caseSensitive ? constant : constant.toUpperCase());
    }
    
    /*----------------------------------------------------------------------------*/
    
    public int[] getOperatorData(String operator, boolean caseSensitive) {
        if (OPERATORS == null) {
            // Operators in lowest to highest precedence
            // -- Logical
            OPERATORS = new HashMap<String, int[]>();
            OPERATORS.put(getAND(),         new int[] {1, LEFT_ASSOCIATIVE});
            OPERATORS.put(getOR(),          new int[] {2, LEFT_ASSOCIATIVE});
            // -- Assignment
            OPERATORS.put(getASSIGN(),      new int[] {3, RIGHT_ASSOCIATIVE});
            // -- Ternary
            OPERATORS.put(getTIF(),         new int[] {4, RIGHT_ASSOCIATIVE});
            OPERATORS.put(getTELSE(),       new int[] {4, RIGHT_ASSOCIATIVE});
            // -- Comparators
            OPERATORS.put(getEQU(),         new int[] {5, NON_ASSOCIATIVE});
            OPERATORS.put(getNEQ(),         new int[] {5, NON_ASSOCIATIVE});
            OPERATORS.put(getLT(),          new int[] {5, NON_ASSOCIATIVE});
            OPERATORS.put(getLTE(),         new int[] {5, NON_ASSOCIATIVE});
            OPERATORS.put(getGT(),          new int[] {5, NON_ASSOCIATIVE});
            OPERATORS.put(getGTE(),         new int[] {5, NON_ASSOCIATIVE});
            // -- Bitwise
            OPERATORS.put(getLSHIFT(),      new int[] {6, LEFT_ASSOCIATIVE});
            OPERATORS.put(getRSHIFT(),      new int[] {6, LEFT_ASSOCIATIVE});
            // -- Math operators
            OPERATORS.put(getPLUS(),        new int[] {7, LEFT_ASSOCIATIVE});
            OPERATORS.put(getMINUS(),       new int[] {7, LEFT_ASSOCIATIVE});
            OPERATORS.put(getMULT(),        new int[] {8, LEFT_ASSOCIATIVE});
            OPERATORS.put(getDIV(),         new int[] {8, LEFT_ASSOCIATIVE});
            OPERATORS.put(getIDIV(),        new int[] {8, LEFT_ASSOCIATIVE});
            OPERATORS.put(getIDIV2(),       new int[] {8, LEFT_ASSOCIATIVE});
            OPERATORS.put(getMODULUS(),     new int[] {8, LEFT_ASSOCIATIVE});
            // -- Exponentiation
            OPERATORS.put(getEXP(),         new int[] {9, RIGHT_ASSOCIATIVE});
            // -- Unary
            OPERATORS.put(getPERCENT(),     new int[] {10, RIGHT_ASSOCIATIVE}); 
            OPERATORS.put(getUNARY_MINUS(), new int[] {11, RIGHT_ASSOCIATIVE});   
            OPERATORS.put(getUNARY_PLUS(),  new int[] {11, RIGHT_ASSOCIATIVE});    
        }
    
        return operator == null ? null : OPERATORS.get(caseSensitive ? operator : operator.toUpperCase());
    }
    
    /*----------------------------------------------------------------------------*/
    
    protected Map<String, Function> getFunctions() {
        if (FUNCTIONS == null) {
            FUNCTIONS = new HashMap<String, Function>();
            FUNCTIONS.put("ABS", new Function(this, "_ABS", 1, 1, ValueType.NUMBER));
            FUNCTIONS.put("NOW", new Function(this, "_NOW", 0, 1, ValueType.NUMBER));
            FUNCTIONS.put("SQR", new Function(this, "_SQR", 1, 1, ValueType.NUMBER));
            FUNCTIONS.put("SQRT", new Function(this, "_SQRT", 1, 1, ValueType.NUMBER));
        }
        return FUNCTIONS;
    }

    /*----------------------------------------------------------------------------*/
    
    public Function getFunction(String function, boolean caseSensitive) {
        return function == null ? null : getFunctions().get(caseSensitive ? function : function.toUpperCase());
    }
    
    /*----------------------------------------------------------------------------*/
    
    public Object getProperty(String property) {
        Object obj = System.getProperty(property);
        if (obj == null) {
            obj = System.getenv(property);
        }
        return obj;
    }
    
    /*----------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------*/
   
    /*
     * Returns the absolute value of a number
     * abs(0) -> 0
     * abs(123.45) -> 123.45
     * abs(-123.45) -> 123.45
     * abs("kdkdkd") -> expected number exception
     */    
    public Value _ABS(Symbol function, Stack<Symbol> stack) {
        BigDecimal bd = stack.pop().asNumber();
        return new Value(function.getText()).setValue(bd == null ? null : bd.abs());
    }

    /*
     * Returns the current date and time
     * parameter_1: (no parameter = actual time)
     *              0 = actual time
     *              1 = beginning of today
     *              2 = end of today
     * date() ->   2009-08-31 13:32:02
     * date(1) ->  2009-08-31 00:00:00
     * date(2) ->  2009-08-31 23:59:59
     * date("kdkdkd") -> expected number exception
     * 
     * This function exists in the basic calc grammar because the ParserTest needs 
     * to be sure that it can test Values that are dates
     * 
     */    
    public Value _NOW(Symbol function, Stack<Symbol> stack) throws ParserException {
        Calendar calendar = Calendar.getInstance(); 
        if (function.getArgc() > 0) {
            int mode = stack.pop().asNumber().intValue();
            switch (mode) {
                case 0:
                    break; // current time
               
                case 1:    // beginning of day
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    break;
                    
                case 2:    // end of day
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 0);
                    break;
                    
                default:
                    String msg = ParserException.formatMessage("error.function_value_out_of_range", 
                        function.getText(), "1", "0", "2", String.valueOf(mode));
                    throw new ParserException(msg);
            }
        }
        
        return new Value(function.getText()).setValue(new Date(calendar.getTimeInMillis()));
    } 
    
    /*
     * Squares a number
     * sqr(2) -> 4
     * sqr("dd") -> expected number exception
     */    
    public Value _SQR(Symbol function, Stack<Symbol> stack) {
        BigDecimal bd = stack.pop().asNumber();
        return new Value(function.getText()).setValue(bd == null ? null : bd.multiply(bd));
    }    

    /*
     * Returns the square root of a number
     * sqrt(25) -> 5
     * sqrt(-2) -> Infinity or NAN exception
     * sqrt("dd") -> expected number exception
     */    
    public Value _SQRT(Symbol function, Stack<Symbol> stack) {
        BigDecimal bd = stack.pop().asNumber();
        bd = bd == null ? null : new BigDecimal(Math.sqrt(bd.doubleValue()));
        return new Value(function.getText()).setValue(bd);
    }    
    
}
