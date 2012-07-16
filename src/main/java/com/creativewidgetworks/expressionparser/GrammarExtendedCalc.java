package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.creativewidgetworks.expressionparser.enums.ValueType;
import com.creativewidgetworks.expressionparser.functions.NameCase;

/**
 * Defines the grammar tokens for a expression parser comparable
 * to a scientific calculator with string and date functions
 */
public class GrammarExtendedCalc extends GrammarBasicCalc {
    
    // Used for isNumber
    private final Pattern pattern_NUMBER = Pattern.compile(getPattern_NUMBER(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    
    // Used by MatchByLen
    private final char MATCHBYLEN_VARIATIONS_SEPARATOR_CHARACTER = ':';
    private Parser tmpParser = null;
    
    @Override    
    public String getPattern_FUNCTION() { 
        String baseFunctions = super.getPattern_FUNCTION();
        String moreFunctions = 
            "arccos|arcsin|arctan|arraylen|average|ceiling|containsall|containsany|contains|cos|endswith|" +
            "exp|factorial|find|floor|hex|isblank|isboolean|isdate|isnumber|isnull|left|len|log10|log|lower|" +
            "makeboolean|matchbylen|match|max|min|mid|namecase|random|replaceall|replacefirst|replace|right|" +
            "sin|split|startswith|string|str|tan|trimleft|trimright|trim|upper|val";
        return baseFunctions.endsWith("|") ? baseFunctions + moreFunctions : baseFunctions + "|" + moreFunctions; 
    }

    /*----------------------------------------------------------------------------*/

    @Override
    protected Map<String, Function> getFunctions() {
        Map<String, Function> functions = super.getFunctions();
        if (functions.get("ARCCOS") == null) {
            functions.put("ARCCOS", new Function(this, "_ARCCOS", 1, 1, ValueType.NUMBER));
            functions.put("ARCSIN", new Function(this, "_ARCSIN", 1, 1, ValueType.NUMBER));
            functions.put("ARCTAN", new Function(this, "_ARCTAN", 1, 1, ValueType.NUMBER));
            functions.put("ARRAYLEN", new Function(this, "_ARRAYLEN", 1, 1));
            functions.put("AVERAGE", new Function(this, "_AVERAGE", 1, Integer.MAX_VALUE, ValueType.NUMBER));
            functions.put("CEILING", new Function(this, "_CEILING", 1, 1, ValueType.NUMBER));
            functions.put("CONTAINS", new Function(this, "_CONTAINS", 2, 2, ValueType.STRING, ValueType.STRING));
            functions.put("CONTAINSALL", new Function(this, "_CONTAINSALL", 2, 2, ValueType.STRING, ValueType.STRING));
            functions.put("CONTAINSANY", new Function(this, "_CONTAINSANY", 2, 2, ValueType.STRING, ValueType.STRING));
            functions.put("COS", new Function(this, "_COS", 1, 1, ValueType.NUMBER));
            functions.put("ENDSWITH", new Function(this, "_ENDSWITH", 2, 2, ValueType.STRING, ValueType.STRING));
            functions.put("EXP", new Function(this, "_EXP", 1, 1, ValueType.NUMBER));
            functions.put("FACTORIAL", new Function(this, "_FACTORIAL", 1, 1, ValueType.NUMBER));
            functions.put("FIND", new Function(this, "_FIND", 2, 3, ValueType.STRING, ValueType.STRING, ValueType.NUMBER));
            functions.put("FLOOR", new Function(this, "_FLOOR", 1, 1, ValueType.NUMBER));
            functions.put("HEX", new Function(this, "_HEX", 1, 1, ValueType.NUMBER));
            functions.put("ISBLANK", new Function(this, "_ISBLANK", 1, 1));
            functions.put("ISBOOLEAN", new Function(this, "_ISBOOLEAN", 1, 1));
            functions.put("ISDATE", new Function(this, "_ISDATE", 1, 1));
            functions.put("ISNULL", new Function(this, "_ISNULL", 1, 1));
            functions.put("ISNUMBER", new Function(this, "_ISNUMBER", 1, 1));
            functions.put("LEFT", new Function(this, "_LEFT", 2, 2, ValueType.STRING, ValueType.NUMBER));
            functions.put("LEN", new Function(this, "_LEN", 1, 1, ValueType.STRING));
            functions.put("LOG", new Function(this, "_LOG", 1, 1, ValueType.NUMBER));
            functions.put("LOG10", new Function(this, "_LOG10", 1, 1, ValueType.NUMBER));
            functions.put("LOWER", new Function(this, "_LOWER", 1, 1, ValueType.STRING));
            functions.put("MAKEBOOLEAN", new Function(this, "_MAKEBOOLEAN", 1, 1));
            functions.put("MATCH", new Function(this, "_MATCH", 2, 2, ValueType.STRING, ValueType.STRING));
            functions.put("MATCHBYLEN", new Function(this, "_MATCHBYLEN", 3, 3, ValueType.STRING, ValueType.STRING, ValueType.STRING));
            functions.put("MAX", new Function(this, "_MAX", 2, 2, ValueType.NUMBER, ValueType.NUMBER));
            functions.put("MID", new Function(this, "_MID", 2, 3, ValueType.STRING, ValueType.NUMBER, ValueType.NUMBER));
            functions.put("MIN", new Function(this, "_MIN", 2, 2, ValueType.NUMBER, ValueType.NUMBER));
            functions.put("NAMECASE", new Function(new NameCase(), "_NAMECASE", 1, 1, ValueType.STRING));
            functions.put("RANDOM", new Function(this, "_RANDOM", 0, 2, ValueType.NUMBER, ValueType.NUMBER));
            functions.put("REPLACE", new Function(this, "_REPLACE", 3, 3, ValueType.STRING, ValueType.STRING, ValueType.STRING));
            functions.put("REPLACEALL", new Function(this, "_REPLACEALL", 3, 3, ValueType.STRING, ValueType.STRING, ValueType.STRING));
            functions.put("REPLACEFIRST", new Function(this, "_REPLACEFIRST", 3, 3, ValueType.STRING, ValueType.STRING, ValueType.STRING));
            functions.put("RIGHT", new Function(this, "_RIGHT", 2, 2, ValueType.STRING, ValueType.NUMBER));
            functions.put("SIN", new Function(this, "_SIN", 1, 1, ValueType.NUMBER));
            functions.put("SPLIT", new Function(this, "_SPLIT", 1, 3, ValueType.STRING, ValueType.STRING, ValueType.NUMBER));
            functions.put("STARTSWITH", new Function(this, "_STARTSWITH", 2, 2, ValueType.STRING, ValueType.STRING));
            functions.put("STR", new Function(this, "_STR", 1, 3, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER));
            functions.put("STRING", new Function(this, "_STRING", 2, 2, ValueType.STRING, ValueType.NUMBER));
            functions.put("TAN", new Function(this, "_TAN", 1, 1, ValueType.NUMBER));
            functions.put("TRIM", new Function(this, "_TRIM", 1, 2, ValueType.STRING, ValueType.STRING));
            functions.put("TRIMLEFT", new Function(this, "_TRIMLEFT", 1, 2, ValueType.STRING, ValueType.STRING));
            functions.put("TRIMRIGHT", new Function(this, "_TRIMRIGHT", 1, 2, ValueType.STRING, ValueType.STRING));
            functions.put("UPPER", new Function(this, "_UPPER", 1, 1, ValueType.STRING));
            functions.put("VAL", new Function(this, "_VAL", 1, 1, ValueType.STRING));
        }
        
        return functions;
    }
    
    
    /**
       DATE", "DATEWITHIN", "DATEBETWEEN"
       
       ISDATE MAKETIMESTAMP
       //  CAL_DAYS UNITS_HOURS ,MONTHS,WEEKS  CAL_SECS CAL_MS
       //  CAL_BOM EOM BOY EOY
       DATECALC(date, amount, units)
       DATECALC(date, CAL_EOM)                 
       DATEDIFF(date1,date2,units) 
   
       NAMECASE
     */
    
    private boolean isTrimableCharacter(char toTest, char testChar) {
        return toTest == testChar || testChar == ' ' && Character.isWhitespace(toTest);
    }

    private String makeString(char c, int len) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < len) {
            sb.append(c);
        }
        return sb.toString();
    }
    
    private double getNumber(long minimum, long maximum, int precision) {
        double result = (minimum - 1);
        double multiplier = 0;

        // Test for reversal of min/max values
        if (maximum < minimum) {
            long temp = minimum;
            minimum = maximum;
            maximum = temp;
        }
        
        precision = Math.abs(precision);
        
        // Get a whole number greater than or equal to the minimum value and less than the maximum value
        result = Math.min(minimum, maximum) + Math.floor((Math.random() * Math.abs(maximum - minimum)));

        // Take a random number with precision digits then divide by the multiplier to turn it into a decimal value.
        multiplier = Math.pow(10.0, precision);  // 10 ^ precision
        int decimal = (int)(Math.random() * multiplier);
        result += (decimal / multiplier);

        return result;
    }
    

    private String trimLeft(String str, char characterToRemove) {
        StringBuilder sb = new StringBuilder(str);
        if (str.length() > 0) {
            while ((sb.length() > 0) && isTrimableCharacter(sb.charAt(0), characterToRemove)) {
              sb.deleteCharAt( 0 );
            }
        }
        return sb.toString();
    }

    private String trimRight(String str, char characterToRemove) {
        StringBuilder sb = new StringBuilder(str);
        int length = sb.length();
        while ((length > 0) && isTrimableCharacter(sb.charAt(length - 1), characterToRemove)) {
            sb.deleteCharAt(length - 1);
            length = sb.length();
        }
        return sb.toString();
    }

    private String trim(String str, char characterToRemove) {
        String strToTrim = trimLeft(str, characterToRemove);
        return trimRight(strToTrim, characterToRemove);
    }    
    
    /*----------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------*/

    /*
     * Returns the arc cosine of the number; number in radians
     * arccos(0.70710) -> 45
     * arccos(null) -> null
     */
    public Value _ARCCOS(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.acos(number.doubleValue());
            value.setValue(scale(BigDecimal.valueOf(Math.toDegrees(d))));
        }

        return value;
    }      

    /*
     * Returns the arc sine of the number; number in radians
     * arcsin(0.70710) -> 45
     * arcsin(null) -> null
     */
    public Value _ARCSIN(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.asin(number.doubleValue());
            value.setValue(scale(BigDecimal.valueOf(Math.toDegrees(d))));
        }
        
        return value;
    }      

    /*
     * Returns the arc tangent of the number; number in radians
     * arctan(1) -> 45
     * arctan(null) -> null
     */
    public Value _ARCTAN(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.atan(number.doubleValue());
            value.setValue(scale(BigDecimal.valueOf(Math.toDegrees(d))));
        }
        
        return value;
    }      
    
    /**
     * Returns the number of elements in an array variable
     * arraylen(split("00,10,11", ",")) -> 3
     */    
    public Value _ARRAYLEN(Symbol function, Stack<Symbol> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue(BigDecimal.ZERO);
        
        Symbol symbol = stack.pop();
        Value theValue = symbol.getValue();
        if (theValue.asObject() != null) {
            if (theValue.getArray() == null) {
                String msg = ParserException.formatMessage("error.expected_array", symbol.getText());
                throw new ParserException(msg, symbol.getRow(), symbol.getColumn());
            }
            value.setValue(BigDecimal.valueOf(theValue.getArray().size()));
        } else {
            value.setValue((BigDecimal)null);
        }

        return value;
    }    
    
    /*
     * Returns the average for a list of NUMBER values
     * average(2, 4, 6, 8) -> 5
     * average(null) -> null
     */
    public Value _AVERAGE(Symbol function, Stack<Symbol> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);

        Symbol[] args = popArguments(function, stack); 
        
        BigDecimal number = args[0].asNumber();
        if (number != null) {
            int count = 1;
            double d = number.doubleValue();
            for (int i = 1; i < function.getArgc(); i++) {
                count++;
                if (args[i].getValue().getType() == ValueType.NUMBER) {
                    number = args[i].asNumber();
                    d += number.doubleValue();
                } else {
                    String msg = ParserException.formatMessage("error.expected_number", args[i].getValue().getType().name());
                    throw new ParserException(msg, args[i].getRow(), args[i].getColumn());
                }
            }
            
            value.setValue(scale(BigDecimal.valueOf(d / count)));
        }

        return value;
    }     
    
    /*
     * Returns the next integer greater than the number
     * ceiling(0.01) -> 1
     * ceiling(2.022) -> 3
     * ceiling(null) -> null
     */
    public Value _CEILING(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.ceil(number.doubleValue());
            value.setValue(BigDecimal.valueOf(d));
        }

        return value;
    }     
    
    /*
     * Determines if source string contains target string
     * contains("Ralph", "") -> false
     * contains("", "lp") -> false
     * contains("Ralph", "lp") -> true
     * contains("Ralph", "LP") -> false
     */
    public Value _CONTAINS(Symbol function, Stack<Symbol> stack) {
        String matchStr = stack.pop().asString();
        String str = stack.pop().asString();
        boolean b = false;
        if (str != null  && matchStr != null) {
            b = str.contains(matchStr);
        }
        return new Value(function.getText()).setValue(b ? Boolean.TRUE : Boolean.FALSE);
    }    

    /*
     * Determines if source string contains ALL of the CHARACTERS in the target. The
     * characters can appear anywhere in the source string
     * containsAll("Ralph", "xyz") -> false
     * containsAll("", "lp") -> false
     * containsAll("Ralph", "hR") -> true
     */
    public Value _CONTAINSALL(Symbol function, Stack<Symbol> stack) {
        String matchStr = stack.pop().asString();
        String str = stack.pop().asString();
        boolean b = false;
        if (str != null  && matchStr != null) {
            b = str.length() > 0 && matchStr.length() > 0;
            for (int i = 0; i < matchStr.length(); i++) {
                if (str.indexOf(matchStr.charAt(i)) == -1) {
                    b = false;
                    break;
                }
            }
        }
        return new Value(function.getText()).setValue(b ? Boolean.TRUE : Boolean.FALSE);
    }    

    /*
     * Determines if source string contains ANY of the CHARACTERS in the target. The
     * characters can appear anywhere in the source string
     * containsAny("Ralph", "xyz") -> false
     * containsAny("", "lp") -> false
     * containsAny("Ralph", "12a") -> true
     */
    public Value _CONTAINSANY(Symbol function, Stack<Symbol> stack) {
        String matchStr = stack.pop().asString();
        String str = stack.pop().asString();
        boolean b = false;
        if (str != null  && matchStr != null) {
            for (int i = 0; i < matchStr.length(); i++) {
                if (str.indexOf(matchStr.charAt(i)) != -1) {
                    b = true;
                    break;
                }
            }
        }
        return new Value(function.getText()).setValue(b ? Boolean.TRUE : Boolean.FALSE);        
    }    
    
    /*
     * Returns the cosine of the number; number in degrees
     * cos(45) -> 0.70710
     * cos(null) -> null
     */
    public Value _COS(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double radians = Math.toRadians(number.doubleValue());
            value.setValue(scale(BigDecimal.valueOf(Math.cos(radians))));
        }

        return value;
    }      
    
    /*
     * Tests to see if a string ends with a given string
     * endswith("Ralph", "I") -> false
     * endswith("Ralph", "ph") -> true
     * endswith("Ralph", "Ph") -> false
     */
    public Value _ENDSWITH(Symbol function, Stack<Symbol> stack) {
        String match = stack.pop().asString();
        String str = stack.pop().asString();
        boolean b = str != null && match != null && str.endsWith(match);
        return new Value(function.getText()).setValue(b ? Boolean.TRUE : Boolean.FALSE);
    }

    /*
     * Returns e (the base of natural logarithms) raised to a power
     * exp(1) -> 2.71828
     * exp(null) -> null
     */    
    public Value _EXP(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.exp(number.doubleValue());
            value.setValue(BigDecimal.valueOf(d));
        }

        return value;
    }     
    
    /*
     * FACTORIAL(number)
     *  5! = 1 x 2 x 3 x 4 x 5 = 120
     *
     * FACTORIAL(5) = 120
     */
    public Value _FACTORIAL(Symbol function, Stack<Symbol> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);     
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            if (number.intValue() < 0) {
                throw new ParserException(ParserException.formatMessage("error.function_value_negative", number));                
            }

            // Start with 1
            BigDecimal bd = BigDecimal.ONE;
            
            // and perform the multiplication
            int count = number.intValue();
            for (int i = 1; i <= count; i++) {
                bd = bd.multiply(BigDecimal.valueOf(i));
            }
            
            value.setValue(bd);
        }
        
        return value;
    }     
    
    /*
     * Returns a 1 based index of search within target string
     * find("Ralph", "") -> 0
     * find("", "lp") -> 0
     * find("Ralph", "lp") -> 3
     * find("RalphRalph", "lp", 5) -> 8
     */
    public Value _FIND(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue(BigDecimal.ZERO);     

        Symbol[] args = popArguments(function, stack); 
        
        String str = args[0].asString();
        String searchFor = args[1].asString();
        BigDecimal start = function.getArgc() == 3 ? args[2].asNumber() : BigDecimal.ONE;
        if (str != null && searchFor != null &&
            str.length() > 0 && searchFor.length() > 0) {
            int startAt = start.intValue();
            if (startAt < str.length()) {
                BigDecimal bd = new BigDecimal(str.indexOf(searchFor, --startAt));
                value.setValue(bd.add(BigDecimal.ONE));
            }
        }
        
        return value;
    }    

    /*
     * Returns the lowest integer less than the number
     * floor(0.01) -> 0
     * floor(2.022) -> 2
     * floor(null) -> null
     */
    public Value _FLOOR(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.floor(number.doubleValue());
            value.setValue(BigDecimal.valueOf(d));
        }

        return value;
    }     
    
    /*
     * Converts a number to a hex string
     * hex(0) -> "0"
     * hex(123.45) -> "123.45"
     * hex("kdkdkd") -> not a number exception 
     */
    public Value _HEX(Symbol function, Stack<Symbol> stack) {
        final int byteMax = 256;
        final int wordMax = 65536;
        final int hexByteLen = 2;
        final int hexWordLen = 4;
        final int hexLongLen = 8;

        Value value = new Value(function.getText()).setValue((String)null);     
        
        int l = 0;
        String str = "";
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            long d = number.longValue();
            if (Math.abs(d) < wordMax) {
                l = Math.abs(d) < byteMax ? hexByteLen : hexWordLen;
                str = Integer.toHexString((int)d);
            } else {
                l = hexLongLen;
                str = Long.toHexString(d);
            }

            if (d < 0) {
                // for negative values, truncate extra FFs
                str = str.substring(str.length() - l);
            } else {
                // for positive values, left pad with zeros
                while (str.length() < l) {
                    str = "0" + str;
                }
            }
            
            value.setValue(str.toUpperCase());
        }
        
        return value;
    }

    /*
     * Returns whether or not the string is null or empty
     * isBlank("") -> true
     * isBlank(null) -> true
     * isBlank(NULL) -> true
     * isBlank("raLph") -> false
     */
    public Value _ISBLANK(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(str == null || str.trim().length() == 0 ? Boolean.TRUE : Boolean.FALSE);
    }       

    /*
     * Returns whether or not the string can be parsed into a number into a date.  Note that this
     * method calls MakeBoolean and then tests the result
     * isBoolean("1") -> true
     * makeBoolean("1.0") -> true
     * makeBoolean("0") -> false
     * makeBoolean("0.0") -> false
     * makeBoolean("true") -> true
     * makeBoolean("yes") -> true
     * makeBoolean("on") -> true
     * makeBoolean("X") -> false
     * makeBoolean("<null>") -> false
     * makeBoolean("2.0") -> false
     */
    public Value _ISBOOLEAN(Symbol function, Stack<Symbol> stack) {
        Value value = _MAKEBOOLEAN(function, stack);
        value.setValue(value.asObject() != null && value.asBoolean().booleanValue() ? Boolean.TRUE : Boolean.FALSE);
        return value;
    }       
    
    /*
     * Returns whether or not the string can be parsed into a valid DATE value.  Note the this method 
     * calls MakeDate() and then tests the result
     */
    public Value _ISDATE(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(str == null || str.trim().length() == 0 ? Boolean.TRUE : Boolean.FALSE);
    }       
    
    /*
     * Returns whether or not the string is null
     * isNull("") -> false
     * isNull(null) -> true
     * isNull(NULL) -> true
     * isNull("raLph") -> false
     */
    public Value _ISNULL(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(str == null ? Boolean.TRUE : Boolean.FALSE);
    }      

    /*
     * Returns whether or not the string represents a number
     * isNumber("0") -> true
     * isNumber("X") -> false
     * isNumber("<null>") -> false
     */
    public Value _ISNUMBER(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        boolean b = str != null && pattern_NUMBER.matcher(str).find();
        return new Value(function.getText()).setValue(Boolean.valueOf(b));
    }
        
    /*
     * Returns the leftmost n characters
     * left("", 3) -> ""
     * left("Ralph", 3) -> "Ral"
     * left("Ra", 3) -> "Ra"
     */
    public Value _LEFT(Symbol function, Stack<Symbol> stack) {    
        Value value = new Value(function.getText()).setValue((String)null);        
        
        BigDecimal bdCount = stack.pop().asNumber();
        String str = stack.pop().asString();
        
        if (str != null) {
            if (bdCount != null) {
                int count = bdCount.intValue();
                if (str.length() > 0 && count > 0) {
                    int sourceLen = str.length();
                    int targetLen = (sourceLen > count) ? count : sourceLen;
                    char[] buffer = new char[targetLen];
                    str.getChars(0, targetLen, buffer, 0);
                    value.setValue(new String(buffer));
                } else {
                    value.setValue("");
                }   
            } else {
                value.setValue(str);
            }
        }
        
        return value;
    }    
    
    /*
     * Returns the length of a string
     * len("raLph") -> 5
     * len(null) -> 0
     */
    public Value _LEN(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(BigDecimal.valueOf(str == null ? 0 : str.length()));
    }

    
    /*
     * Returns the natural log of the number
     * log(2) -> 0.69314
     * log(null) -> null
     */
    public Value _LOG(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.log(number.doubleValue());
            value.setValue(BigDecimal.valueOf(d));
        }

        return value;
    }

    /*
     * Returns the log of the number
     * log10(2) -> 0.30102
     * log10(null) -> null
     */
    public Value _LOG10(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.log10(number.doubleValue());
            value.setValue(BigDecimal.valueOf(d));
        }

        return value;    }    
    
    
    /*
     * Lower cases a string
     * lower("raLph iDen") -> "ralph iden"
     */
    public Value _LOWER(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(str == null ? null : str.toLowerCase());
    }
    
    /*
     * Create a BOOLEAN value from an input string
     * makeBoolean("1") -> true
     * makeBoolean("1.0") -> true
     * makeBoolean("0") -> false
     * makeBoolean("0.0") -> false
     * makeBoolean("true") -> true
     * makeBoolean("yes") -> true
     * makeBoolean("on") -> true
     * makeBoolean("X") -> null
     * makeBoolean("<null>") -> null
     * makeBoolean("2.0") -> null
     */    
    public Value _MAKEBOOLEAN(Symbol function, Stack<Symbol> stack) {    
        Value value = new Value(function.getText()).setValue((Boolean)null);        
    
        Symbol token = stack.pop();
        if (token.asString() != null && token.getValue().getType() != ValueType.DATE) {
            // Try conversion of boolean-like strings
            String str = token.asString() + "~";
            if (str.length() > 1 && ("1~true~yes~on~").indexOf(str.toLowerCase()) != -1) {
                value.setValue(Boolean.TRUE);
            } else if (str.length() > 1 && ("0~false~no~off~").indexOf(str.toLowerCase()) != -1) {
                value.setValue(Boolean.FALSE);
            } else {
                // probe for variations of 0 and 1
                str = token.asString();
                if (pattern_NUMBER.matcher(str).find()) {
                    BigDecimal bd = new BigDecimal(str);
                    if (bd.compareTo(BigDecimal.ZERO) == 0 ) {
                        value.setValue(Boolean.FALSE);
                    } else if (bd.compareTo(BigDecimal.ONE) == 0 ) {
                        value.setValue(Boolean.TRUE);
                    }
                }
            }
        }        
        
        return value;
    }
    
    /*
     * Performs a match of the regular expression and stores the groups in an array variable
     * match("(815) 555-1212 x100","(\d{3})\D*(\d{3})\D*(\d{4})\D*(\d*)$") -> [815][555][1212][100]
     */    
    public Value _MATCH(Symbol function, Stack<Symbol> stack) throws ParserException {    
        Value value = new Value(function.getText()).setValue((String)null);
        
        String pattern = stack.pop().asString();
        String str = stack.pop().asString();
        
        if (str != null && pattern != null) {
            Pattern p = null;
            try {
                p = Pattern.compile(pattern);
            } catch (PatternSyntaxException ex) {
                throw new ParserException(ParserException.formatMessage("error.invalid_regex_pattern", pattern));
            }
            
            Matcher m1 = p.matcher(str);
            if (m1.find()) {
                for (int i = 0; i <= m1.groupCount(); i++) {
                    String matchGroup = m1.group(i) == null ? "" : m1.group(i);
                    if (i == 0) {
                        // Set the string value to match group 0 (the whole match)
                        value.setValue(matchGroup);
                    }
                    // Add all match groups (including the whole match group 0) as array values
                    Value aValue = new Value("match" + i).setValue(matchGroup);
                    value.addValueToArray(aValue);
                }
            } else {
                // No matches, which isn't necessarily an error. Return empty string
                value.setValue("");
            }
        }   
        
        return value;
    }

    /*
     * Performs a match of the regular expression and returns a formatted string
     * based on the matched pattern. This is a funky scheme that I devised to 
     * format strings, phone numbers in my example, based on the length of the 
     * matched pattern.
     * 
     * matchByLen("8155551212", "[0-9]*", "?='invalid':0=:7=      ###-####:10=(###) ###-####")
     *     -> "(815) 555-1212"
     * 
     * matchByLen("5551212", "[0-9]*", "?='invalid':0=:7=      ###-####:10=(###) ###-####")
     *     -> "      555-1212"
     */    
    public Value _MATCHBYLEN(Symbol function, Stack<Symbol> stack) throws ParserException {    
        Value value = new Value(function.getText()).setValue((String)null);
        
        // Create temp parser if one hasn't already been created
        if (tmpParser == null) {
            tmpParser = new Parser(this);
        }
        
        String variations = stack.pop().asString();
        String pattern = stack.pop().asString();
        String str = stack.pop().asString();
        
        if (str != null && pattern != null && variations != null) {
            Pattern p = null;
            try {
                p = Pattern.compile(pattern);
            } catch (PatternSyntaxException ex) {
                throw new ParserException(ParserException.formatMessage("error.invalid_regex_pattern", pattern));
            }            
           
            Matcher m1 = p.matcher(str);
            if (m1.find()) {
                // Look for template variations based on number of chars matched
                // e.g., 0=:4=####:7=###-####:?='(N/A)'
                String key = m1.group(0).length() + "=";
                int start = variations.indexOf(key);
                int end = start;
                if (start != -1) {
                    end = variations.indexOf(MATCHBYLEN_VARIATIONS_SEPARATOR_CHARACTER, start);
                    if (end == -1) {
                        // Case where this is the last choice in the string
                        // and there isn't any trailing semicolon.
                        end = variations.length();
                    }

                    String exp = variations.substring(start + key.length(), end);
                    if (key.equals("0=") && exp.trim().length() > 0) {
                        // If the match length is zero, run the substitution string
                        // through an evaluator to allow more flexibility in what
                        // to load for empty matches
                        Value tmp = tmpParser.eval(exp.replace('\'', '"'));
                        exp = tmp.asString();
                    }

                    // Replace '#' placeholders with group data
                    int o = 0;
                    StringBuilder sb = new StringBuilder(exp);
                    for (int i = 0; i < sb.length() && o < m1.group(0).length(); i++) {
                        if (sb.charAt(i) == '#') {
                            // Substitute the actual matched data
                            sb.setCharAt(i, m1.group(0).charAt(o++));
                        }
                    }

                    value.setValue(sb.toString());
                } else {
                    // check for ?= and evaluate
                    key = "?=";
                    start = variations.indexOf(key);
                    end = start;
                    if (start != -1) {
                        end = variations.indexOf(MATCHBYLEN_VARIATIONS_SEPARATOR_CHARACTER, start);
                        if (end == -1) {
                            // Case where this is the last choice in the string
                            // and there isn't any trailing semicolon.
                            end = variations.length();
                        }

                        // Pull off default, unmatched expression and evaluate it
                        String exp = variations.substring(start + key.length(), end);
                        Value tmp = tmpParser.eval(exp.replace('\'', '"'));
                        value.setValue(tmp.asString());
                    } else {
                        // no match and no default expression
                        value.setValue("");
                    }
                }
            } else {
                // No matches, which isn't necessarily an error. Return empty string
                value.setValue("");
            }
        }   
        
        return value;
    }
    
    /*
     * Returns the larger of the two values
     * min(5, 3) -> 3
     * min(null, null) -> null
     * min(null, 1) -> null
     * min(1, null) -> null
     */
    public Value _MAX(Symbol function, Stack<Symbol> stack) {    
        Value value = new Value(function.getText()).setValue((BigDecimal)null);        
        
        BigDecimal rhs = stack.pop().asNumber();
        BigDecimal lhs = stack.pop().asNumber();
        if (lhs != null && rhs != null) {
            value.setValue(lhs.max(rhs));
        }
        
        return value;
    }      
    
    /*
     * Returns a substring of a set of characters
     * mid("Ralph",2,1) -> "a"
     * mid("Ralph",2) -> "alph"
     * mid("Ralph",2,100) -> "alph"
     */
    public Value _MID(Symbol function, Stack<Symbol> stack) {    
        Value value = new Value(function.getText()).setValue((String)null);        

        Symbol[] args = popArguments(function, stack); 
        
        String str = args[0].asString(); // source string
        BigDecimal index = args[1].asNumber(); // starting index (1 based)
        BigDecimal length = args.length == 2 ? null : args[2].asNumber();  // length to copy
        
        if (str != null && index != null && (args.length < 3 || length != null)) {
            if (str.length() > 0) {
                int end;
                int start = index.intValue();
                int count = function.getArgc() == 2 ? str.length() : length.intValue();
                    
                 if (start < 1 || count <= 0 || start > str.length()) {
                        value.setValue("");
                 } else {
                     int len = str.length();
                     if (start + count > len) {
                         end = len;
                     } else {
                         end = (start - 1) + count;
                     }
                        
                     value.setValue(str.substring(start - 1, end));
                }
            } else {
                value.setValue("");
            }
        }
        
        return value;
    }      
    
    /*
     * Returns the smaller of the two values
     * min(5, 3) -> 3
     * min(null, null) -> null
     * min(null, 1) -> null
     * min(1, null) -> null
     */
    public Value _MIN(Symbol function, Stack<Symbol> stack) {    
        Value value = new Value(function.getText()).setValue((BigDecimal)null);        
        
        BigDecimal rhs = stack.pop().asNumber();
        BigDecimal lhs = stack.pop().asNumber();
        if (lhs != null && rhs != null) {
            value.setValue(lhs.min(rhs));
        }
        
        return value;
    }    

    /*
     * Returns the random number between two values
     * random() -> 0.00322  (range 0..1)
     * random(3) -> 3.2323  (range 0..5)
     * random(10, 15) -> 13.2323  (range 10..15)
     */
    public Value _RANDOM(Symbol function, Stack<Symbol> stack) {    
        Symbol[] args = popArguments(function, stack);
        double d = 0;
        if (args.length == 0) {
            d = getNumber(0, 1, getPrecision());
        } else if (args.length == 1) {
            d = getNumber(0, args[0].asNumber().longValue(), getPrecision());
        } else if (args.length == 2) {
            d = getNumber(args[0].asNumber().longValue(), args[1].asNumber().longValue(), getPrecision());
        }
        return new Value(function.getText()).setValue(BigDecimal.valueOf(d));
    }  
    
    /*
     * Replaces the occurrences of the first string with the second
     * string. Does not support regular expressions for the search.
     * replace("Ralph,Iden,Dev", ",", "/") -> Ralph/Iden/Dev
     * replace("Ralph", null, null) -> Ralph
     */
    public Value _REPLACE(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);
        
        String replaceWith = stack.pop().asString();
        String searchFor = stack.pop().asString();
        String str = stack.pop().asString();
        if (str != null) {
            if (searchFor == null || replaceWith == null) {
                value.setValue(str);
            } else {
                value.setValue(str.replace(searchFor, replaceWith));
            }
        }
        
        return value;
    }

    /*
     * Replaces all the occurrences of the first string with the second
     * string. Supports regular expressions for the search.
     * replaceAll("Ralph,Iden", ",", "/") -> Ralph/Iden
     * replaceAll("abbbcabc-000-abc", "ab+c", "123") -> 123123-000-123
     * replaceAll("acabc-000-abc", "ab+c", "123") -> ac123-000-123
     * replaceAll("Ralph", null, null) -> Ralph
     */
    public Value _REPLACEALL(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);
        
        String replaceWith = stack.pop().asString();
        String searchFor = stack.pop().asString();
        String str = stack.pop().asString();
        if (str != null) {
            if (searchFor == null || replaceWith == null) {
                value.setValue(str);
            } else {
                value.setValue(str.replaceAll(searchFor, replaceWith));
            }
        }
        
        return value;
    }

    /*
     * Replaces the first occurrence of the first string with the second
     * string. Supports regular expressions for the search.
     * replaceFirst("Ralph,Iden,Dev", ",", "/") -> Ralph/Iden,Dev
     * replaceFirst("abbbcabc-000-abc", "ab+c", "123") -> 123abc-000-abc
     * replaceFirst("acabc-000-abc", "ab+c", "123") -> ac123-000-abc
     * replaceFirst("Ralph", null, null) -> Ralph
     */
    public Value _REPLACEFIRST(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);
        
        String replaceWith = stack.pop().asString();
        String searchFor = stack.pop().asString();
        String str = stack.pop().asString();
        if (str != null) {
            if (searchFor == null || replaceWith == null) {
                value.setValue(str);
            } else {
                value.setValue(str.replaceFirst(searchFor, replaceWith));
            }
        }
        
        return value;
    }
    
    /*
     * Returns the rightmost n characters
     * right("", 3) -> ""
     * right("Ralph", 3) -> "lph"
     * right("Ra", 3) -> "Ra"
     */
    public Value _RIGHT(Symbol function, Stack<Symbol> stack) {    
        Value value = new Value(function.getText()).setValue((String)null);        
        
        BigDecimal bdCount = stack.pop().asNumber();
        String str = stack.pop().asString();
        
        if (str != null) {
            if (bdCount != null) {
                int count = bdCount.intValue();
                if (str.length() > 0 && count > 0) {
                    int sourceLen = str.length();
                    int targetLen = (sourceLen > count) ? count : sourceLen;
                    char[] buffer = new char[targetLen];
                    str.getChars(sourceLen - targetLen, sourceLen, buffer, 0);
                    value.setValue(new String(buffer));
                } else {
                    value.setValue("");
                }                
            } else {
                value.setValue(str);
            }
        }
        
        return value;
    }    
    
    /*
     * Returns the sine of the number; number in degrees
     * sin(45) -> 0.70710
     * sin(null) -> null
     */
    public Value _SIN(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double radians = Math.toRadians(number.doubleValue());
            value.setValue(scale(BigDecimal.valueOf(Math.sin(radians))));
        }

        return value;
    }     
    
    /*
     * Splits a string on delimiter boundaries into a String array
     * split("Ralph,Iden") -> [Ralph][Iden]
     * split("Ralph,Iden", ",") -> [Ralph][Iden]
     * split("Ralph,Iden", ",", 1) -> [Ralph,Iden]
     * See the javadoc for String.split for an explaination of the limit (third)
     * parameter.
     */
    public Value _SPLIT(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);
        
        Symbol[] args = popArguments(function, stack);
        if (args[0].asString() != null) {
            String delimiter = (args.length > 1 && args[1].asString() != null) ? args[1].asString() : ",";
            int limit = (args.length == 3 && args[2].asNumber() != null) ? args[2].asNumber().intValue() : -1;
            
            String[] fields = args[0].asString().split(delimiter, limit);
            if (fields.length > 0) {
                value.setValue(fields[0]);
                for (int i = 0; i < fields.length; i++) {
                    Value aValue = new Value(function.getText() + i).setValue(fields[i]);
                    value.addValueToArray(aValue);
                }
            }
        }
        
        return value;
    }    
    
    /*
     * Tests to see if a string starts with a given string
     * startswith("Ralph", "I") -> false
     * startswith("Ralph", "Ra") -> true
     * startswith("Ralph", "ra") -> false
     */
    public Value _STARTSWITH(Symbol function, Stack<Symbol> stack) {
        String match = stack.pop().asString();
        String str = stack.pop().asString();
        boolean b = str != null && match != null && str.startsWith(match);
        return new Value(function.getText()).setValue(b ? Boolean.TRUE : Boolean.FALSE);
    }

    /*
     * Converts a number to a string value - Note that Java's formatter will
     * not truncate the string; for example str(123456,3) = 123456 and NOT
     * "123", "456", or error
     * str(0) -> "0"
     * str(123.45) -> "123.45"
     * str(123.45, 3) -> "123"
     * str(123.45, 7, 3) -> "123.450"
     * str("kdkdkd") -> 0 or not a number exception if "ignoreErrors" is true
     */    
    public Value _STR(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);        
        
        Symbol[] args = popArguments(function, stack);
        
        String number = args[0].asString();
        if (number != null) {
            boolean done = false;
            BigDecimal precision = null;
            BigDecimal width = null;
            
            if (function.getArgc() > 1) {
                width = args[1].asNumber();
                if (width == null) {
                    done = true;
                } else if (function.getArgc() > 2) {
                    precision = args[2].asNumber();
                    if (precision == null) {
                        done = true;
                    }
                }
            }

            if (!done) {
                if (function.getArgc() > 1) {
                    StringBuilder sb = new StringBuilder();

                    int tl = width.intValue();
                    int dp = precision == null ? 0 : precision.intValue();

                    int len = tl - dp - 1;
                    if (len > 0) {
                        sb.append(makeString('#', len - 1));
                        sb.append('0');
                    }

                    if (dp > 0) {
                        sb.append(".");
                        sb.append(makeString('0', dp));
                    }

                    DecimalFormat df = new DecimalFormat(sb.toString());
                    BigDecimal bd = new BigDecimal(number).setScale(dp, BigDecimal.ROUND_HALF_UP);
                    value.setValue(df.format(bd));
                } else {
                    value.setValue(number);
                }
            }
        }        
        
        return value;
    }
    
    /*
     * Replicates a string n number of times
     * replicate("*", 2) -> "**"
     * replicate("RI", 2) -> "RIRI"
     */    
    public Value _STRING(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);        

        BigDecimal count = stack.pop().asNumber();  
        String str = stack.pop().asString();
        if (str != null && count != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count.intValue(); i++) {
                sb.append(str);
            }
            value.setValue(sb.toString());
        }        
        
        return value;
    }
    
    /*
     * Returns the tangent of the number; number in degrees
     * tan(45) -> 1
     * tan(null) -> null
     */    
    public Value _TAN(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);
        
        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double radians = Math.toRadians(number.doubleValue());
            value.setValue(scale(BigDecimal.valueOf(Math.tan(radians))));
        }

        return value;
    }     
    
    /*
     * Removes the specified character (or WHITESPACE) from both ends of a string
     * trim(" Ralph \r ") -> "Ralph"
     * trim("**Ralph**", "*") -> "Ralph"
     */    
    public Value _TRIM(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);  
        
        Symbol[] args = popArguments(function, stack);
        
        String str = args[0].asString();
        if (str != null) {
            if (function.getArgc() == 2 && args[1].asString().length() > 0) {
                value.setValue(trim(str, args[1].asString().charAt(0)));
            } else {
                value.setValue(str.trim());
            }
        }
        
        return value;
    }

    /*
     * Removes the specified character (or WHITESPACE) from the left side of a string
     * trim(" Ralph \r ") -> "Ralph \r "
     * trim("**Ralph**", "*") -> "Ralph**"
     */    
    public Value _TRIMLEFT(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);  
        
        Symbol[] args = popArguments(function, stack);
        
        String str = args[0].asString();
        if (str != null) {
            if (function.getArgc() == 2 && args[1].asString().length() > 0) {
                value.setValue(trimLeft(str, args[1].asString().charAt(0)));
            } else {
                value.setValue(trimLeft(str, ' '));
            }
        }
        
        return value;
    }

    /*
     * Removes the specified character (or WHITESPACE) from the right side of a string
     * trim(" Ralph \r ") -> " Ralph"
     * trim("**Ralph**", "*") -> "**Ralph"
     */    
    public Value _TRIMRIGHT(Symbol function, Stack<Symbol> stack) {
        Value value = new Value(function.getText()).setValue((String)null);  
        
        Symbol[] args = popArguments(function, stack);
        
        String str = args[0].asString();
        if (str != null) {
            if (function.getArgc() == 2 && args[1].asString().length() > 0) {
                value.setValue(trimRight(str, args[1].asString().charAt(0)));
            } else {
                value.setValue(trimRight(str, ' '));
            }
        }
        
        return value;
    }    
    
    /*
     * Upper cases a string
     * upper("raLph iDen") -> "RALPH IDEN"
     */    
    public Value _UPPER(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(str == null ? null : str.toUpperCase());
    }

    /*
     * Converts a string to a numeric value
     * val("") -> 0
     * val("123.45") -> 123.45
     * val("kdkdkd") -> arithmetic exception
     */    
    public Value _VAL(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        BigDecimal bd = (str == null) ? null : str.length() == 0 ? BigDecimal.ZERO : new BigDecimal(str);
        return new Value(function.getText()).setValue(bd);
    }
    
}
