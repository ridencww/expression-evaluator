package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FunctionToolbox {

    private Parser parser;

    // Used for isNumber
    private final Pattern pattern_NUMBER = Pattern.compile(TokenType.NUMBER.getRegex(new Parser()), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    // Used by MatchByLen
    private final char MATCHBYLEN_VARIATIONS_SEPARATOR_CHARACTER = ':';
    private Parser tmpParser = null;


    public static FunctionToolbox register(Parser parser) {
        FunctionToolbox toolbox = new FunctionToolbox();

        toolbox.parser = parser;

        parser.addFunction(new Function("ABS", toolbox, "_ABS", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("ARCCOS", toolbox, "_ARCCOS", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("ARCSIN", toolbox, "_ARCSIN", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("ARCTAN", toolbox, "_ARCTAN", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("ARRAYLEN", toolbox, "_ARRAYLEN", 1, 1));
        parser.addFunction(new Function("AVERAGE", toolbox, "_AVERAGE", 1, Integer.MAX_VALUE, ValueType.NUMBER));
        parser.addFunction(new Function("CEILING", toolbox, "_CEILING", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("CONTAINS", toolbox, "_CONTAINS", 2, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("CONTAINSALL", toolbox, "_CONTAINSALL", 2, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("CONTAINSANY", toolbox, "_CONTAINSANY", 2, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("COS", toolbox, "_COS", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("ENDSWITH", toolbox, "_ENDSWITH", 2, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("EXP", toolbox, "_EXP", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("FACTORIAL", toolbox, "_FACTORIAL", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("FIND", toolbox, "_FIND", 2, 3, ValueType.STRING, ValueType.STRING, ValueType.NUMBER));
        parser.addFunction(new Function("FLOOR", toolbox, "_FLOOR", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("HEX", toolbox, "_HEX", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("ISBLANK", toolbox, "_ISBLANK", 1, 1));
        parser.addFunction(new Function("ISBOOLEAN", toolbox, "_ISBOOLEAN", 1, 1));
        parser.addFunction(new Function("ISDATE", toolbox, "_ISDATE", 1, 1));
        parser.addFunction(new Function("ISNULL", toolbox, "_ISNULL", 1, 1));
        parser.addFunction(new Function("ISNUMBER", toolbox, "_ISNUMBER", 1, 1));
        parser.addFunction(new Function("LEFT", toolbox, "_LEFT", 2, 2, ValueType.STRING, ValueType.NUMBER));
        parser.addFunction(new Function("LEN", toolbox, "_LEN", 1, 1, ValueType.STRING));
        parser.addFunction(new Function("LOG", toolbox, "_LOG", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("LOG10", toolbox, "_LOG10", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("LOWER", toolbox, "_LOWER", 1, 1, ValueType.STRING));
        parser.addFunction(new Function("MAKEBOOLEAN", toolbox, "_MAKEBOOLEAN", 1, 1));
        parser.addFunction(new Function("MATCH", toolbox, "_MATCH", 2, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("MATCHBYLEN", toolbox, "_MATCHBYLEN", 3, 3, ValueType.STRING, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("MAX", toolbox, "_MAX", 2, 2, ValueType.NUMBER, ValueType.NUMBER));
        parser.addFunction(new Function("MID", toolbox, "_MID", 2, 3, ValueType.STRING, ValueType.NUMBER, ValueType.NUMBER));
        parser.addFunction(new Function("MIN", toolbox, "_MIN", 2, 2, ValueType.NUMBER, ValueType.NUMBER));
        parser.addFunction(new Function("NAMECASE", toolbox, "_NAMECASE", 1, 1, ValueType.STRING));
        parser.addFunction(new Function("RANDOM", toolbox, "_RANDOM", 0, 2, ValueType.NUMBER, ValueType.NUMBER));
        parser.addFunction(new Function("REPLACE", toolbox, "_REPLACE", 3, 3, ValueType.STRING, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("REPLACEALL", toolbox, "_REPLACEALL", 3, 3, ValueType.STRING, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("REPLACEFIRST", toolbox, "_REPLACEFIRST", 3, 3, ValueType.STRING, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("RIGHT", toolbox, "_RIGHT", 2, 2, ValueType.STRING, ValueType.NUMBER));
        parser.addFunction(new Function("SIN", toolbox, "_SIN", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("SPLIT", toolbox, "_SPLIT", 1, 3, ValueType.STRING, ValueType.STRING, ValueType.NUMBER));
        parser.addFunction(new Function("SQR", toolbox, "_SQR", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("SQRT", toolbox, "_SQRT", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("STARTSWITH", toolbox, "_STARTSWITH", 2, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("STR", toolbox, "_STR", 1, 3, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER));
        parser.addFunction(new Function("STRING", toolbox, "_STRING", 2, 2, ValueType.STRING, ValueType.NUMBER));
        parser.addFunction(new Function("TAN", toolbox, "_TAN", 1, 1, ValueType.NUMBER));
        parser.addFunction(new Function("TRIM", toolbox, "_TRIM", 1, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("TRIMLEFT", toolbox, "_TRIMLEFT", 1, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("TRIMRIGHT", toolbox, "_TRIMRIGHT", 1, 2, ValueType.STRING, ValueType.STRING));
        parser.addFunction(new Function("UPPER", toolbox, "_UPPER", 1, 1, ValueType.STRING));
        parser.addFunction(new Function("VAL", toolbox, "_VAL", 1, 1, ValueType.STRING));

        return toolbox;
    }

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

    protected BigDecimal scale(BigDecimal number) {
        BigDecimal result = number;
        if (number != null) {
            if (number.doubleValue() == 0.0) {
                result = BigDecimal.ZERO;
            } else {
                result = number.setScale(parser.getPrecision(), BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
            }
        }
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
     * Returns the absolute value of the number
     * abs(-1) -> 1
     */
    public Value _ABS(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);

        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = Math.abs(number.doubleValue());
            value.setValue(scale(BigDecimal.valueOf(d)));
        }

        return value;
    }

    /*
     * Returns the arc cosine of the number; number in radians
     * arccos(0.70710) -> 45
     * arccos(null) -> null
     */
    public Value _ARCCOS(Token function, Stack<Token> stack) {
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
    public Value _ARCSIN(Token function, Stack<Token> stack) {
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
    public Value _ARCTAN(Token function, Stack<Token> stack) {
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
    public Value _ARRAYLEN(Token function, Stack<Token> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue(BigDecimal.ZERO);

        Token token = stack.pop();
        Value theValue = token.getValue();
        if (theValue.asObject() != null) {
            if (theValue.getArray() == null) {
                String msg = ParserException.formatMessage("error.expected_array", token.getText());
                throw new ParserException(msg, token.getRow(), token.getColumn());
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
    public Value _AVERAGE(Token function, Stack<Token> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);

        Token[] args = parser.popArguments(function, stack);

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
    public Value _CEILING(Token function, Stack<Token> stack) {
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
    public Value _CONTAINS(Token function, Stack<Token> stack) {
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
    public Value _CONTAINSALL(Token function, Stack<Token> stack) {
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
    public Value _CONTAINSANY(Token function, Stack<Token> stack) {
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
    public Value _COS(Token function, Stack<Token> stack) {
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
    public Value _ENDSWITH(Token function, Stack<Token> stack) {
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
    public Value _EXP(Token function, Stack<Token> stack) {
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
    public Value _FACTORIAL(Token function, Stack<Token> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);

        Token numberToken = stack.pop();
        BigDecimal number = numberToken.asNumber();

        if (number != null) {
            if (number.intValue() < 0) {
                throw new ParserException(ParserException.formatMessage("error.function_value_negative", number), numberToken.getRow(), numberToken.getColumn());
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
    public Value _FIND(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue(BigDecimal.ZERO);

        Token[] args = parser.popArguments(function, stack);

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
    public Value _FLOOR(Token function, Stack<Token> stack) {
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
    public Value _HEX(Token function, Stack<Token> stack) {
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
    public Value _ISBLANK(Token function, Stack<Token> stack) {
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
    public Value _ISBOOLEAN(Token function, Stack<Token> stack) {
        Value value = _MAKEBOOLEAN(function, stack);
        value.setValue(value.asObject() != null && value.asBoolean().booleanValue() ? Boolean.TRUE : Boolean.FALSE);
        return value;
    }

    /*
     * Returns whether or not the string can be parsed into a valid DATE value.  Note the this method 
     * calls MakeDate() and then tests the result
     */
    public Value _ISDATE(Token function, Stack<Token> stack) {
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
    public Value _ISNULL(Token function, Stack<Token> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(str == null ? Boolean.TRUE : Boolean.FALSE);
    }

    /*
     * Returns whether or not the string represents a number
     * isNumber("0") -> true
     * isNumber("X") -> false
     * isNumber("<null>") -> false
     */
    public Value _ISNUMBER(Token function, Stack<Token> stack) {
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
    public Value _LEFT(Token function, Stack<Token> stack) {
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
    public Value _LEN(Token function, Stack<Token> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(BigDecimal.valueOf(str == null ? 0 : str.length()));
    }


    /*
     * Returns the natural log of the number
     * log(2) -> 0.69314
     * log(null) -> null
     */
    public Value _LOG(Token function, Stack<Token> stack) {
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
    public Value _LOG10(Token function, Stack<Token> stack) {
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
    public Value _LOWER(Token function, Stack<Token> stack) {
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
    public Value _MAKEBOOLEAN(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((Boolean)null);

        Token token = stack.pop();
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
    public Value _MATCH(Token function, Stack<Token> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue((String)null);

        Token patternToken = stack.pop();
        String pattern = patternToken.asString();

        String str = stack.pop().asString();

        if (str != null && pattern != null) {
            Pattern p = null;
            try {
                p = Pattern.compile(pattern);
            } catch (PatternSyntaxException ex) {
                throw new ParserException(ParserException.formatMessage("error.invalid_regex_pattern", pattern), patternToken.getRow(), patternToken.getColumn());
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
    public Value _MATCHBYLEN(Token function, Stack<Token> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue((String)null);

        // Create temp parser if one hasn't already been created
        if (tmpParser == null) {
            tmpParser = new Parser();
        }

        String variations = stack.pop().asString();

        Token patternToken = stack.pop();
        String pattern = patternToken.asString();

        String str = stack.pop().asString();

        if (str != null && pattern != null && variations != null) {
            Pattern p = null;
            try {
                p = Pattern.compile(pattern);
            } catch (PatternSyntaxException ex) {
                throw new ParserException(ParserException.formatMessage("error.invalid_regex_pattern", pattern), patternToken.getRow(), patternToken.getColumn());
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
    public Value _MAX(Token function, Stack<Token> stack) {
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
    public Value _MID(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((String)null);

        Token[] args = parser.popArguments(function, stack);

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
    public Value _MIN(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);

        BigDecimal rhs = stack.pop().asNumber();
        BigDecimal lhs = stack.pop().asNumber();
        if (lhs != null && rhs != null) {
            value.setValue(lhs.min(rhs));
        }

        return value;
    }

    /*
     * Uppercases the first character of each word
     * namecase("john smith") -> "John Smith"
     */
    public Value _NAMECASE(Token function, Stack<Token> stack) {
        String str = stack.pop().asString();
        if (str != null) {
            StringBuilder sb = new StringBuilder();
            boolean uc = true;
            for (int i = 0; i < str.length(); i++) {
                if (uc && !Character.isWhitespace(str.charAt(i))) {
                    sb.append(Character.toUpperCase(str.charAt(i)));
                    uc = false;
                } else {
                    sb.append(Character.toLowerCase(str.charAt(i)));
                    if (Character.isWhitespace(str.charAt(i))) {
                        uc = true;
                    }
                }
            }
            str = sb.toString();
        }

        return new Value(function.getText()).setValue(str);
    }

    /*
     * Returns the random number between two values
     * random() -> 0.00322  (range 0..1)
     * random(3) -> 3.2323  (range 0..5)
     * random(10, 15) -> 13.2323  (range 10..15)
     */
    public Value _RANDOM(Token function, Stack<Token> stack) {
        Token[] args = parser.popArguments(function, stack);
        double d = 0;
        if (args.length == 0) {
            d = getNumber(0, 1, parser.getPrecision());
        } else if (args.length == 1) {
            d = getNumber(0, args[0].asNumber().longValue(), parser.getPrecision());
        } else if (args.length == 2) {
            d = getNumber(args[0].asNumber().longValue(), args[1].asNumber().longValue(), parser.getPrecision());
        }
        return new Value(function.getText()).setValue(BigDecimal.valueOf(d));
    }

    /*
     * Replaces the occurrences of the first string with the second
     * string. Does not support regular expressions for the search.
     * replace("Ralph,Iden,Dev", ",", "/") -> Ralph/Iden/Dev
     * replace("Ralph", null, null) -> Ralph
     */
    public Value _REPLACE(Token function, Stack<Token> stack) {
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
    public Value _REPLACEALL(Token function, Stack<Token> stack) {
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
    public Value _REPLACEFIRST(Token function, Stack<Token> stack) {
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
    public Value _RIGHT(Token function, Stack<Token> stack) {
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
    public Value _SIN(Token function, Stack<Token> stack) {
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
    public Value _SPLIT(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((String)null);

        Token[] args = parser.popArguments(function, stack);
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
     * Returns the number squared
     * sqr(9) -> 81
     */
    public Value _SQR(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);

        BigDecimal number = stack.pop().asNumber();
        if (number != null) {
            double d = number.doubleValue() * number.doubleValue();
            value.setValue(scale(BigDecimal.valueOf(d)));
        }

        return value;
    }

    /*
     * Returns the square root of the number
     * sqrt(81) -> 9
     */
    public Value _SQRT(Token function, Stack<Token> stack) throws ParserException {
        Value value = new Value(function.getText()).setValue((BigDecimal)null);

        Token token = stack.pop();

        BigDecimal number = token.asNumber();
        if (number != null) {
            double d = Math.sqrt(number.doubleValue());
            if (Double.isNaN(d)) {
                String msg = ParserException.formatMessage("error.not_a_number");
                throw new ParserException(msg, token.getRow(), token.getColumn());
            }
            value.setValue(scale(BigDecimal.valueOf(d)));
        }

        return value;
    }

    /*
     * Tests to see if a string starts with a given string
     * startswith("Ralph", "I") -> false
     * startswith("Ralph", "Ra") -> true
     * startswith("Ralph", "ra") -> false
     */
    public Value _STARTSWITH(Token function, Stack<Token> stack) {
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
    public Value _STR(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((String)null);

        Token[] args = parser.popArguments(function, stack);

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
    public Value _STRING(Token function, Stack<Token> stack) {
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
    public Value _TAN(Token function, Stack<Token> stack) {
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
    public Value _TRIM(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((String)null);

        Token[] args = parser.popArguments(function, stack);

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
    public Value _TRIMLEFT(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((String)null);

        Token[] args = parser.popArguments(function, stack);

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
    public Value _TRIMRIGHT(Token function, Stack<Token> stack) {
        Value value = new Value(function.getText()).setValue((String)null);

        Token[] args = parser.popArguments(function, stack);

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
    public Value _UPPER(Token function, Stack<Token> stack) {
        String str = stack.pop().asString();
        return new Value(function.getText()).setValue(str == null ? null : str.toUpperCase());
    }

    /*
     * Converts a string to a numeric value
     * val("") -> 0
     * val("123.45") -> 123.45
     * val("kdkdkd") -> arithmetic exception
     */
    public Value _VAL(Token function, Stack<Token> stack) throws ParserException {
        Token token = stack.pop();
        try {
            String str = token.asString();
            BigDecimal bd = (str == null) ? null : str.length() == 0 ? BigDecimal.ZERO : new BigDecimal(str);
            return new Value(function.getText()).setValue(bd);
        } catch (NumberFormatException nfe) {
            throw new ParserException(ParserException.formatMessage("error.expected_numberformat", token.asString()), token.getRow(), token.getColumn());
        }
    }

}
