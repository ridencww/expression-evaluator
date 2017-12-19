package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;

public class Parser {
    // Internal VO class to hold a function's argument count
    public class ArgCount { public boolean haveArgs; public int count; }

    // Default numeric precision (number of decimal places)
    public static final int DEFAULT_PRECISION = 5;

    // Maximum size of arrays that can be created by DIM
    public static int MAX_DIM_ROWS = 10000;
    public static int MAX_DIM_COLS = 256;

    // By default, disable access to system and environment properties
    private boolean allowProperties = false;

    // By default, use the JVM's timezone
    private TimeZone localTimeZone = TimeZone.getDefault();

    private static final String DEFAULT_SPLIT_CHARACTER = ";";
    private static final String SPLIT_REGEX = "(?=([^\\\"\\']*[\\\"\\'][^\\\"\\']*[\\\"\\'])*[^\\\"\\']*$)";

    // Number of digits of precision for math operations
    private int precision = DEFAULT_PRECISION;

    // RegEx tokenizer - package level for testing
    private boolean caseSensitive;
    private final String expressionDelimiter;
    final Map<String,List<Token>> tokenizedExpressions = new HashMap<>();

    // Status
    private ParserException lastException;
    private String lastExpression;

    // Containers for constants, functions, and variables
    private final Map<String, BigDecimal> constants = new HashMap<>();
    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, Value> globals = new TreeMap<>();
    private final Map<String, Value> variables = new TreeMap<>();

    private FieldInterface fieldInterface;

    public Parser() {
        caseSensitive = false;
        expressionDelimiter = DEFAULT_SPLIT_CHARACTER;
        clearConstants();
        clearFunctions();
    }

    /*----------------------------------------------------------------------------*/

    public boolean setAllowProperties(boolean allowProperties) {
        boolean orgAllowProperties = this.allowProperties;
        this.allowProperties = allowProperties;
        return orgAllowProperties;
    }

    /*----------------------------------------------------------------------------*/

    public TimeZone getTimeZone() {
        return localTimeZone;
    }

    public TimeZone setTimeZone(TimeZone timezone) {
        TimeZone orgTimeZone = this.localTimeZone;
        this.localTimeZone = timezone;
        return orgTimeZone;
    }

    /*----------------------------------------------------------------------------*/

    /**
     * Examine the stack (list of function parameters) looking for any whose value
     * is null. Returns null if all parameters are non-null or a comma-delimited
     * list of parameter numbers that are null.
     */
    public String listOfNullParameters(Stack<Token> stack) {
        return listOfNullParameters(stack, 0);
    }

    public String listOfNullParameters(Stack<Token> stack, int offset) {
        StringBuilder sb = new StringBuilder();

        if (stack != null) {
            for (int i = offset; i < stack.size(); i++) {
                if (stack.elementAt(i).getValue().asObject() == null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(i);
                }
            }
        }

        return sb.length() == 0 ? null : sb.toString();
    }

    /*----------------------------------------------------------------------------*/

    public void addConstant(String name, BigDecimal value) {
        if (name != null) {
            constants.put(caseSensitive ? name : name.toUpperCase(), value);
            TokenType.invalidatePattern();
        }
    }

    public void clearConstant(String name) {
        constants.remove(caseSensitive ? name : name.toUpperCase());
        TokenType.invalidatePattern();
    }

    public void clearConstants() {
        constants.clear();
        addConstant("null", null);
        addConstant("pi", BigDecimal.valueOf(Math.PI));
        TokenType.invalidatePattern();
    }

    public BigDecimal getConstant(String name) {
        return name == null ? null : constants.get(caseSensitive ? name : name.toUpperCase());
    }

    public Map<String, BigDecimal> getConstants() {
        return constants;
    }

    public String getConstantRegex() {
        List<String> names = new ArrayList<>();
        names.addAll(constants.keySet());

        // Sort in descending order to insure proper matching
        Collections.sort(names, Collections.<String>reverseOrder());

        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append(name);
        }

        if (sb.length() == 0) {
            sb.append("~~no-constants-defined~~");
        }

        return sb.toString();
    }

    /*----------------------------------------------------------------------------*/

    public Value getField(String name) {
        if (fieldInterface != null) {
            return fieldInterface.getField(name, getCaseSensitive());
        } else {
            return null;
        }
    }

    public FieldInterface getFieldInterface() {
        return fieldInterface;
    }

    public FieldInterface setFieldInterface(FieldInterface fieldInterface) {
        FieldInterface oldValue = fieldInterface;
        this.fieldInterface = fieldInterface;
        return oldValue;
    }

    /*----------------------------------------------------------------------------*/

    public void addFunction(Function function) {
        if (function != null) {
            functions.put(caseSensitive ? function.getName() : function.getName().toUpperCase(), function);
            TokenType.invalidatePattern();
        }
    }

    public void clearFunction(String name) {
        functions.remove(caseSensitive ? name : name.toUpperCase());
        TokenType.invalidatePattern();
    }

    public void clearFunctions() {
        functions.clear();
        addFunction(new Function("clearGlobal", this, "_CLEARGLOBAL", 1, 1));
        addFunction(new Function("clearGlobals", this, "_CLEARGLOBALS", 0, 0));
        addFunction(new Function("dim", this, "_DIM", 2, 3, ValueType.UNDEFINED, ValueType.NUMBER, ValueType.NUMBER));
        addFunction(new Function("getGlobal", this, "_GETGLOBAL", 1, 1, ValueType.STRING));
        addFunction(new Function("setGlobal", this, "_SETGLOBAL", 2, 2, ValueType.STRING));
        addFunction(new Function("now", this, "_NOW", 0, 1));
        addFunction(new Function("precision", this, "_PRECISION", 1, 1, ValueType.NUMBER));
        TokenType.invalidatePattern();
    }

    public Function getFunction(String functionName) {
        return functionName == null ? null : functions.get(caseSensitive ? functionName : functionName.toUpperCase());
    }

    public Map<String, Function> getFunctions() {
        return functions;
    }

    public String getFunctionRegex() {
        List<String> regexs = new ArrayList<>();
        for (Function function : functions.values()) {
            regexs.add(function.getName());
        }

        // Sort in descending order to insure proper matching
        Collections.sort(regexs, Collections.<String>reverseOrder());

        StringBuilder sb = new StringBuilder();
        for (String regex : regexs) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append(regex);
        }

        if (sb.length() == 0) {
            sb.append("~~no-functions-defined~~");
        }

        return sb.toString();
    }

    /*----------------------------------------------------------------------------*/

    private Object getProperty(String name) {
        if (allowProperties) {
            Object obj = System.getenv(name);
            return obj == null ? System.getProperties().get(name) : obj;
        } else {
            return null;
        }
    }

    /*----------------------------------------------------------------------------*/

    public void addGlobalVariable(String name, Value value) {
        if (name != null && value != null) {
            globals.put(caseSensitive ? name : name.toUpperCase(), value);
        }
    }

    public void clearGlobalVariable(String name) {
        if (name != null) {
            globals.remove(caseSensitive ? name : name.toUpperCase());
        }
    }

    public void clearGlobalVariables() {
        globals.clear();
    }

    public Value getGlobalVariable(String name) {
        return name == null ? null : globals.get(caseSensitive ? name : name.toUpperCase());
    }

    public Map<String, Value> getGlobalVariables() {
        return globals;
    }

    /*----------------------------------------------------------------------------*/

    public void addVariable(String name, Value value) {
        if (name != null && value != null) {
            variables.put(caseSensitive ? name : name.toUpperCase(), value);
        }
    }

    public void clearVariable(String name) {
        variables.remove(caseSensitive ? name : name.toUpperCase());
    }

    public void clearVariables() {
        variables.clear();
    }

    public Value getVariable(String name) {
        return name == null ? null : variables.get(caseSensitive ? name : name.toUpperCase());
    }

    public Map<String, Value> getVariables() {
        return variables;
    }


    /*----------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------*/

    private int compareTokens(Token token1, Token token2, boolean caseSensitive) throws ParserException {
        if (!token1.isOperator()) {
            throw new ParserException(ParserException.formatMessage("error.operator_expected", token1.getType().name()));
        } else if (!token2.isOperator()) {
            throw new ParserException(ParserException.formatMessage("error.operator_expected_at_top", token1.getType().name()));
        }

        Operator o1 =  Operator.find(token1, caseSensitive);
        if (o1 == null) {
            throw new ParserException(ParserException.formatMessage("error.operator_not_found", token1.getText()));
        }

        Operator o2 =  Operator.find(token2, caseSensitive);
        if (o2 == null) {
            throw new ParserException(ParserException.formatMessage("error.operator_not_found", token2.getText()));
        }

        return  o1.getPrecedence() - o2.getPrecedence();
    }

    private boolean isType(Token token, int type, boolean caseSensitive) throws ParserException {
        if (!token.isOperator()) {
            throw new ParserException(ParserException.formatMessage("error.expected_operator_token", token.getText(), token.getType().name()));
        }

        Operator o = Operator.find(token, caseSensitive);
        if (o == null) {
            throw new ParserException(ParserException.formatMessage("error.operator_not_found", token.getText()));
        }

        return o.getAssociation() == type;
    }

    private boolean shouldPopToken(Token token, Token topOfStack, boolean caseSensitive) throws ParserException {
        // Unary minus/plus are handled differently if the top token is the exponentiation operator
        Operator op = Operator.find(token, caseSensitive);
        if (op.inSet(Operator.UNARY_MINUS, Operator.UNARY_PLUS) && Operator.EXP.getText().equals(topOfStack.getText())) {
            return false;
        } else {
            return
                    (isType(token, Operator.LEFT_ASSOCIATIVE, caseSensitive) && compareTokens(token, topOfStack, caseSensitive) >= 0) ||
                            (isType(token, Operator.RIGHT_ASSOCIATIVE, caseSensitive) && compareTokens(token, topOfStack, caseSensitive) > 0);
        }
    }

    /*----------------------------------------------------------------------------*/

    public void clearCache() {
        tokenizedExpressions.clear();
    }

    public ParserException getLastException() {
        return lastException;
    }

    public String getLastExpression() {
        return lastExpression;
    }

    /*----------------------------------------------------------------------------*/

    public boolean getCaseSensitive() {
        return caseSensitive;
    }

    public boolean setCaseSensitive(boolean caseSensitive) {
        boolean oldValue = this.caseSensitive;
        this.caseSensitive = caseSensitive;
        return oldValue;
    }

    /*----------------------------------------------------------------------------*/

    public int getPrecision() {
        return precision;
    }

    public int setPrecision(int decimals) {
        int oldValue = this.precision;
        this.precision = decimals;
        return oldValue;
    }

    /*----------------------------------------------------------------------------*/

    private void setStatusAndFail(Token currentToken, String message, Object... parameters) throws ParserException {
        int errorAtRow = currentToken == null ? -1 : currentToken.getRow();
        int errorAtCol = currentToken == null ? -1 : currentToken.getColumn();
        String errorMessage = ParserException.formatMessage(message, parameters);
        throw new ParserException(errorMessage, errorAtRow, errorAtCol);
    }

    /*----------------------------------------------------------------------------*/

    public Value eval(String source) {
        // Source statements cannot be null
        if (source == null) {
            source = "";
        }

        source += ";";

        // Clear results of last parse
        Value value;
        lastException = null;

        try {
            // See if this expression has been previously parsed
            List<Token> tokens = tokenizedExpressions.get(source);

            // If expression isn't in the map then tokenize, generate RPN stack, and store the result
            if (tokens == null) {
                tokens = new ArrayList<>();
                String[] expressions = source.split(expressionDelimiter + SPLIT_REGEX);
                for (String expression : expressions) {
                    if (expression.trim().length() > 0) {
                        lastExpression = expression;
                        List<Token> list = tokenize(expression, false);
                        if (list.size() > 0) {
                            tokens.addAll(infixToRPN(list));
                        }
                    }
                }

                // Save the parsed tokens in the cache
                if (tokens.size() > 0) {
                    tokenizedExpressions.put(source, tokens);
                }
            } else {
                // Restore any token values that may have been updated so cached expressions will continue to work
                for (Token token : tokens) {
                    token.restoreOrgValue();
                }
            }

            // Evaluate the expression
            value = (tokens.size() > 0) ? RPNtoValue(tokens) : new Value("ERROR: EMPTY EXPRESSION");
        } catch (ParserException ex) {
            lastException = ex;
            value = new Value().setValue(lastException);
        }

        return value;
    }

    /*----------------------------------------------------------------------------*/

    public List<Token> tokenize(String input, boolean wantWhitespace) {
        int offset = 0;
        int row = 1;

        List<Token> tokens = new ArrayList<>();

        Matcher matcher = TokenType.getPattern(this).matcher(input);
        while (matcher.find()) {
            if (wantWhitespace || matcher.group(TokenType.WHITESPACE.name()) == null) {
                for (TokenType tokenType : TokenType.values()) {
                    if (matcher.group(tokenType.name()) != null) {
                        String text = tokenType.resolve(matcher.group(tokenType.name()));
                        Token token = new Token(tokenType, text, row, matcher.start() + 1 - offset);
                        token.saveOrgValue();
                        tokens.add(token);
                        break;
                    }
                }
            }

            if (matcher.group(TokenType.NEWLINE.name()) != null) {
                offset = matcher.start() + 1;
                row++;
            }
        }

        // Remove the NOMATCH signifying end-of-expression
        if (tokens.size() > 1) {
            int last = tokens.size() - 1;
            Token token = tokens.get(last);
            if (TokenType.NOMATCH.equals(token.getType())) {
                tokens.remove(last);
            }
        }

        return tokens;
    }

    /*----------------------------------------------------------------------------*/

    /*
     * Convert a list of infix tokens to Reverse Polish Notation (RPN) form. Dijkstra's
     * shunting-yard algorithm is used to process the tokens.
     * @param inputTokens list of tokens to process
     * @return List<Token> outputTokens in RPN form
     */
    protected List<Token> infixToRPN(List<Token> inputTokens) throws ParserException {
        List<Token> outputTokens = new ArrayList<>();

        Token lastToken = null;
        Stack<Token> stack = new Stack<>();
        Stack<ArgCount> argStack = new Stack<>();

        // To simplify processing later, checks for matching brackets and parenthesis are performed now
        int bcount = 0;
        int pcount = 0;
        for (Token token : inputTokens) {
            if (token.opEquals(Operator.LBRACKET)) {
                bcount++;
            } else if (token.opEquals(Operator.RBRACKET)) {
                bcount--;
                if (bcount < 0) {
                    setStatusAndFail(token, "error.missing_bracket", Operator.LBRACKET.getText());
                }
            }

            if (token.opEquals(Operator.LPAREN)) {
                pcount++;
            } else if (token.opEquals(Operator.RPAREN)) {
                pcount--;
                if (pcount < 0) {
                    setStatusAndFail(token, "error.missing_parens", Operator.LPAREN.getText());
                }
            }
        }

        if (bcount != 0) {
            Token token = inputTokens.get(inputTokens.size() - 1);
            setStatusAndFail(token, "error.missing_bracket", Operator.RBRACKET.getText());
        }

        if (pcount != 0) {
            Token token = inputTokens.get(inputTokens.size() - 1);
            setStatusAndFail(token, "error.missing_parens", Operator.RPAREN.getText());
        }

        for (Token token : inputTokens) {
            // Touch up token if a unary minus or plus is encountered
            if ((token.opEquals(Operator.MINUS) || token.opEquals(Operator.PLUS))) {
                boolean isUnary = lastToken == null || lastToken.isOperator() || lastToken.isParen();
                if (isUnary) {
                    if (token.opEquals(Operator.MINUS)) {
                        token.setText(Operator.UNARY_MINUS.getText());  // unary minus, parsers 1 - -1.0
                    } else {
                        token.setText(Operator.UNARY_PLUS.getText()); // unary plus, parses  1 + +1.0
                    }
                }
            }

            // Addresses where a function name is mistyped and is recognized as an identifier
            if (lastToken != null && lastToken.isIdentifer() && token.getText().equalsIgnoreCase(Operator.LPAREN.getText())) {
                setStatusAndFail(lastToken, "error.expected_function", lastToken.getText());
            }

            lastToken = token;

            if (token.isNumber() || token.isString() || token.isConstant() || token.isField() || token.isIdentifer() || token.isProperty()) {
                outputTokens.add(token);
                if (!argStack.isEmpty()) {
                    argStack.peek().haveArgs = true;
                }

            } else if (token.isFunction()) {
                stack.push(token);
                if (!argStack.isEmpty()) {
                    argStack.peek().haveArgs = true;
                }
                argStack.push(new ArgCount());

            } else if (token.opEquals(Operator.COMMA)) {
                // Ignore commas if inside brackets
                if (bcount == 0) {
                    // Copy tokens to output and bump argument count for function
                    while (!stack.empty() && !stack.peek().opEquals(Operator.LPAREN)) {
                        outputTokens.add(stack.pop());
                    }
                    if (argStack.size() > 0 && argStack.peek().haveArgs) {
                        argStack.peek().count++;
                    }
                }

            } else if (token.opEquals(Operator.LBRACKET)) {
                bcount++;
                stack.push(token);
            } else if (token.opEquals(Operator.LPAREN)) {
                stack.push(token);
            } else if (token.opEquals(Operator.RBRACKET)) {
                bcount--;
                while (!stack.empty() && !stack.peek().opEquals(Operator.LBRACKET)) {
                    outputTokens.add(stack.pop());
                }
                outputTokens.add(stack.pop());
            } else if (token.opEquals(Operator.RPAREN)) {
                while (!stack.empty() && !stack.peek().opEquals(Operator.LPAREN)) {
                    outputTokens.add(stack.pop());
                }

                stack.pop();

                if (!stack.empty() && stack.peek().isFunction()) {
                    Token function = stack.pop();
                    ArgCount argc = argStack.pop();
                    function.setArgc(argc.haveArgs ? argc.count + 1 : argc.count);
                    outputTokens.add(function);
                }

            } else if (token.isOperator()) {
                // While stack not empty and stack top element is an operator add it to the output
                while (!stack.empty() && stack.peek().isOperator()) {
                    if (shouldPopToken(token, stack.peek(), caseSensitive)) {
                        outputTokens.add(stack.pop());
                        continue;
                    }
                    break;
                }
                stack.push(token);
            }
        }

        // Copy the rest of the stack to the output
        while (!stack.empty()) {
            outputTokens.add(stack.pop());
        }

        return outputTokens;
    }

    /*----------------------------------------------------------------------------*/

    private boolean haveString(Token lhs, Token rhs) {
        return lhs.getValue().getType() == ValueType.STRING || rhs.getValue().getType() == ValueType.STRING;
    }

    private void assertBothNumbers(Token lhs, Token rhs) throws ParserException {
        if (lhs.getValue().getType() != ValueType.NUMBER || rhs.getValue().getType() != ValueType.NUMBER ) {
            setStatusAndFail(rhs, "error.both_must_be_numeric", lhs.asString(), rhs.asString());
        }
    }

    private void assertSufficientStack(Token token, Stack<Token> stack, int requiredSize) throws ParserException {
        if (stack.size() < requiredSize) {
            setStatusAndFail(token, "error.syntax");
        }
    }

    private Token processOperators(Token token, Stack<Token> stack) throws ParserException {
        Token result = null;

        // Unary: percentage
        Operator op = Operator.find(token, caseSensitive);
        if (op.equals(Operator.PERCENT)) {
            assertSufficientStack(token, stack, 1);
            BigDecimal bd = stack.pop().asNumber().divide(new BigDecimal(100), getPrecision(), RoundingMode.HALF_UP).stripTrailingZeros();
            return new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
        }

        // Ternary
        if (op.equals(Operator.TIF)) {
            assertSufficientStack(token, stack, 4);
            if (!Operator.TELSE.equals(Operator.find(stack.pop(), caseSensitive))) {
                setStatusAndFail(token, "error.expected_telse", Operator.TELSE.getText());
            }

            Token falseValue = stack.pop();
            Token trueValue = stack.pop();
            Token booleanValue =  stack.pop();
            if (booleanValue.getValue().getType() != ValueType.BOOLEAN) {
                setStatusAndFail(booleanValue, "error.boolean_expected", booleanValue.getType());
            }

            return booleanValue.asBoolean() ? trueValue : falseValue;
        }

        assertSufficientStack(token, stack, 2);
        Token rhs = stack.pop();
        Token lhs = stack.pop();

        try {
            if (op.equals(Operator.PLUS)) {
                // Addition/concantenation
                if (haveString(lhs, rhs)) {
                    String strL = lhs.asString() == null ? "" : lhs.asString();
                    String strR = rhs.asString() == null ? "" : rhs.asString();
                    result = new Token(TokenType.STRING, strL + strR, token.getRow(), token.getColumn());
                } else {
                    assertBothNumbers(lhs, rhs);
                    BigDecimal bd = lhs.asNumber().add(rhs.asNumber());
                    bd = bd.setScale(getPrecision(), BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                    result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
                }
            } else if (op.equals(Operator.MINUS)) {
                // Subtraction
                BigDecimal bd = lhs.asNumber().subtract(rhs.asNumber());
                bd = bd.setScale(getPrecision(), BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
            } else if (op.equals(Operator.MULT)) {
                // Multiplication
                BigDecimal bd = lhs.asNumber().multiply(rhs.asNumber());
                bd = bd.setScale(getPrecision(), BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
            } else if (op.equals(Operator.DIV)) {
                // Division
                int divisorScale = rhs.asNumber().scale();
                int scale = lhs.asNumber().equals(BigDecimal.ZERO) ? divisorScale : getPrecision();
                BigDecimal bd = lhs.asNumber().divide(rhs.asNumber(), scale, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
            } else if (op.equals(Operator.IDIV)) {
                // Integer division
                BigDecimal bd = lhs.asNumber().divideToIntegralValue(rhs.asNumber());
                result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
            } else if (op.equals(Operator.MODULUS)) {
                // Modulus
                BigDecimal bd = lhs.asNumber().remainder(rhs.asNumber());
                result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
            } else if (op.equals(Operator.EXP)) {
                // Exponentiation x^y
                MathContext mc = rhs.asNumber().compareTo(BigDecimal.ZERO) < 0 ? MathContext.DECIMAL128 : MathContext.UNLIMITED;
                BigDecimal bd = lhs.asNumber().pow(rhs.asNumber().intValue(), mc);
                bd = bd.setScale(getPrecision(), BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
            } else if (op.equals(Operator.ASSIGNMENT)) {
                // Assignment
                if (lhs.isIdentifer()) {
                    // Trying to assign an uninitialized variable -- could also get here
                    // if user is trying to call a function that doesn't exist.
                    if (rhs.getValue().getType().equals(ValueType.UNDEFINED)) {
                        setStatusAndFail(rhs, "error.expected_initialized", rhs.getText());
                    }

                    // Identifier should always be found as it would have been created when parsing the RPN
                    // stack. Setting one and two dimensional array values is handled here as well.
                    String[] varName = lhs.getText().split("[\\[,\\]]");
                    Value val = variables.get(varName[0].toUpperCase());
                    if (varName.length > 1) {
                        val = val.getArray().get(Integer.valueOf(varName[1]).intValue());
                        if (varName.length > 2) {
                            val = val.getArray().get(Integer.valueOf(varName[2]).intValue());
                        }
                    }

                    val.set(rhs.getValue());
                } else {
                    setStatusAndFail(lhs, "error.expected_identifier", lhs.getText());
                }
            } else {
                result = processRelationalOperators(lhs, token, rhs);
            }
        } catch (ArithmeticException ex) {
            throw new ParserException(ex.getMessage(), ex, token.getRow(), token.getColumn());
        }

        return result;
    }

    private Token processRelationalOperators(Token lhs, Token operator, Token rhs) throws ParserException {
        boolean isTrue = false;

        Operator op = Operator.find(operator, caseSensitive);

        if (lhs.getValue().getType() == ValueType.BOOLEAN) {
            if (op.inSet(Operator.EQU, Operator.NEQ, Operator.AND, Operator.OR)) {
                isTrue = performComparison(lhs.getValue().asBoolean(), rhs.getValue().asBoolean(), op);
            } else {
                setStatusAndFail(operator, "error.invalid_operator_boolean", op.getText());
            }
        } else if (lhs.getValue().getType() == ValueType.NUMBER) {
            if (!op.inSet(Operator.AND, Operator.OR)) {
                isTrue = performComparison(lhs.getValue().asNumber(), rhs.getValue().asNumber(), op);
            } else {
                setStatusAndFail(rhs, "error.invalid_operator", op.getText());
            }
        } else if (lhs.getValue().getType() == ValueType.STRING) {
            if (!op.inSet(Operator.AND, Operator.OR)) {
                isTrue = performComparison(lhs.getValue().asString(), rhs.getValue().asString(), op);
            } else {
                setStatusAndFail(rhs, "error.invalid_operator", op.getText());
            }
        } else if (lhs.getValue().getType() == ValueType.DATE) {
            if (!op.inSet(Operator.AND, Operator.OR)) {
                isTrue = performComparison(lhs.getValue().asDate(), rhs.getValue().asDate(), op);
            } else {
                setStatusAndFail(rhs, "error.invalid_operator", op.getText());
            }
        }

        return new Token(TokenType.VALUE, new Value("VALUE", isTrue ? Boolean.TRUE : Boolean.FALSE), rhs.getRow(), rhs.getColumn() + 1);
    }

    @SuppressWarnings("unchecked")
    private boolean performComparison(Comparable o1, Comparable o2, Operator op) throws ParserException {
        boolean isTrue = false;
        boolean haveValues = o1 != null && o2 != null;
        if (Operator.AND.equals(op)) {
            isTrue = o1 instanceof Boolean && o2 instanceof Boolean && ((Boolean) o1 && (Boolean) o2);
        } else if (Operator.OR.equals(op)) {
            isTrue = o1 instanceof Boolean && o2 instanceof Boolean && ((Boolean)o1 || (Boolean)o2);
        } else if (Operator.LT.equals(op)) {
            isTrue = haveValues && o1.compareTo(o2) < 0;
        } else if (Operator.LTE.equals(op)) {
            isTrue = haveValues && o1.compareTo(o2) <= 0;
        } else if (Operator.EQU.equals(op)) {
            isTrue = haveValues && o1.compareTo(o2) == 0;
        } else if (Operator.NEQ.equals(op)) {
            isTrue = haveValues && o1.compareTo(o2) != 0;
        } else if (Operator.GTE.equals(op)) {
            isTrue = haveValues && o1.compareTo(o2) >= 0;
        } else if (Operator.GT.equals(op)) {
            isTrue = haveValues && o1.compareTo(o2) > 0;
        }
        return isTrue;
    }

    private Token processField(Token field, Stack<Token> stack) throws ParserException {
        Value value = getField(field.getText());
        return new Token(TokenType.VALUE, getField(field.getText()), field.getRow(), field.getColumn());
    }

    private Token processFunction(Token function, Stack<Token> stack) throws ParserException {
        Value value = null;
        String name = function.getText();

        Function f = getFunction(name);
        if (f != null) {
            value = f.execute(function, stack);
        } else {
            setStatusAndFail(function, "error.no_handler", name);
        }

        return new Token(TokenType.VALUE, value, function.getRow(), function.getColumn());
    }

    protected Value RPNtoValue(List<Token> tokens) throws ParserException {
        int tcount = 0;
        Token last_telse = null;

        Stack<Token> stack = new Stack<>();

        for (Token token : tokens) {

            if (token.isProperty()) {
                Object obj = getProperty(token.getText());
                Value value = new Value();
                if (obj instanceof Boolean) {
                    value.setValue((Boolean) obj);
                } else if (obj instanceof BigDecimal) {
                    value.setValue((BigDecimal) obj);
                } else if (obj instanceof Date) {
                    value.setValue((Date) obj);
                } else if (obj != null) {
                    value.setValue(obj.toString());
                }
                token.setValue(value);
                stack.push(token);
            } else if (token.isField()) {
                stack.push(processField(token, stack));
            } else if (token.isFunction()) {
                stack.push(processFunction(token, stack));
            } else if (token.isConstant()) {
                BigDecimal bd = getConstant(token.getText());
                token.getValue().setValue(bd);
                stack.push(token);
            } else if (token.isIdentifer()) {
                // Retrieve the value referenced by the identifier or create an empty placeholder
                Value value = variables.get(token.getText().toUpperCase());
                if (value == null) {
                    value = new Value();
                    variables.put(token.getText().toUpperCase(), value);
                }
                token.getValue().set(value);
                stack.push(token);
            } else if (token.isOperator()) {
                // Handle unary minus (negation) and plus
                Operator op = Operator.find(token, caseSensitive);
                if (Operator.UNARY_MINUS.equals(op) || Operator.NOT.equals(op)) {
                    Value value = stack.pop().getValue();
                    switch (value.getType()) {
                        case NUMBER:
                            value.setValue(value.asNumber().negate());
                            stack.push(new Token(TokenType.NUMBER, value.asString(), token.getRow(), token.getColumn()));
                            break;
                        case BOOLEAN:
                            value.setValue(value.asBoolean() ? Boolean.FALSE : Boolean.TRUE);
                            stack.push(new Token(TokenType.VALUE, value, token.getRow(), token.getColumn()));
                            break;
                        default:
                            setStatusAndFail(token, "error.type_mismatch", value.getType().name());
                    }
                    continue;
                } else if (op.equals(Operator.TIF)) {
                    tcount--;
                } else if (op.equals(Operator.TELSE)) {
                    tcount++;
                    last_telse = token;
                    stack.push(token);
                    continue;
                } else if (op.equals(Operator.UNARY_PLUS)) {
                    continue;
                }

                // Test for TIF without corresponding TELSE
                if (tcount % 2 != 0) {
                    setStatusAndFail(token, "error.missing_telse", Operator.TIF.getText(), Operator.TELSE.getText());
                }

                // If an assignment has occurred, the result should not be pushed on the stack
                Token result = processOperators(token, stack);
                if (result != null) {
                    stack.push(result);
                }
            } else if (token.getText().equals(Operator.LBRACKET.getText())) {
                Token index = null;
                Token subIndex = null;
                if (stack.peek().getType().equals(TokenType.NUMBER)) {
                    index = stack.pop();
                    if (stack.size() == 0) {
                        setStatusAndFail(index, "error.syntax");
                    }
                    if (stack.peek().getType().equals(TokenType.NUMBER)) {
                        subIndex = index;
                        index = stack.pop();
                        if (!stack.peek().getType().equals(TokenType.IDENTIFIER)) {
                            setStatusAndFail(subIndex, "Only one or two dimensional arrays are supported");
                        }
                    }
                }

                Token var = stack.pop();

                if (!ValueType.ARRAY.equals(var.getValue().getType())) {
                    setStatusAndFail(var, "error.expected_array", var.getValue().getType());
                }

                String strIdx = "";
                if (index != null) {
                    strIdx = subIndex == null ? index.asString() : index.asString() + "," + subIndex.asString();
                    strIdx = "[" + strIdx + "]";
                }
                String valName = var.getText() + strIdx;

                int idx = 0;
                Value val = null;
                if (index != null) {
                    List<Value> array = var.getValue().getArray();
                    int len = (array == null) ? 0 : array.size() - 1;
                    idx = index.getValue().asNumber().intValue();
                    if (idx < 0 || idx > len) {
                        setStatusAndFail(index, "error.index_out_of_range", String.valueOf(idx), String.valueOf(len));
                    }

                    val = new Value();
                    val.set(var.getValue().getArray().get(idx));
                    val.setName(valName);

                    if (subIndex != null) {
                        if (!ValueType.ARRAY.equals(val.getType())) {
                            setStatusAndFail(var, "error.expected_array", val.getType());
                        }

                        array = val.getArray();
                        len = (array == null) ? 0 : array.size() - 1;
                        idx = subIndex.getValue().asNumber().intValue();
                        if (idx < 0 || idx > len) {
                            setStatusAndFail(subIndex, "error.index_out_of_range", String.valueOf(idx), String.valueOf(len));
                        }
                        val = val.getArray().get(idx);
                    }
                }

                // V[] is the same as V[0]
                if (val == null) {
                    int len = var.getValue().getArray().size();
                    val = len > 0 ? var.getValue().getArray().get(0) : var.getValue();
                }

                stack.push(new Token(TokenType.IDENTIFIER, valName, val, var.getRow(), var.getColumn()));
           } else {
                stack.push(token);
            }
        }

        // Test for uncaught TELSE without corresponding TIF
        if (tcount != 0) {
            setStatusAndFail(last_telse, "error.missing_tif", Operator.TELSE.getText(), Operator.TIF.getText());
        }

        // Stack should have been consumed except for the final result
        if (stack.size() > 1) {
            setStatusAndFail(stack.get(0), "error.syntax");
        }

        // For variable assignment-only expressions, return Boolean.TRUE
        return stack.size() == 0 ? new Value("empty result", Boolean.TRUE) : stack.pop().getValue();
    }

    /*----------------------------------------------------------------------------*/

    /*
     * Pops the arguments off of the stack and returns an array in the order
     * that they were pushed. This ensures optional arguments in function calls
     * appear at the end of the array and not the beginning.
     */
    public Token[] popArguments(Token function, Stack<Token> stack) {
        Token[] args = null;
        if (function.getArgc() > 0 && stack.size() > 0) {
            args = new Token[function.getArgc()];
            for (int i = function.getArgc() - 1; i >= 0; i--) {
                args[i] = stack.pop();
            }
        } else {
            args = new Token[0];
        }
        return args;
    }

    /*----------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------*/

    /*
     * Clears a global variable
     *  clearGlobal("DOW") -> Boolean.TRUE and "DOW" is removed, if present
     */
    public Value _CLEARGLOBAL(Token function, Stack<Token> stack) throws ParserException {
        boolean haveParameters = listOfNullParameters(stack) == null;
        if (haveParameters) {
            clearGlobalVariable(stack.pop().asString());
        }
        return new Value(function.getText()).setValue(haveParameters ? Boolean.TRUE : Boolean.FALSE);
    }

    /*
     * Clears all global variables
     *  clearGlobals() -> Boolean.TRUE and global variables cleared
     */
    public Value _CLEARGLOBALS(Token function, Stack<Token> stack) throws ParserException {
        clearGlobalVariables();
        return new Value(function.getText()).setValue(Boolean.TRUE);
    }

    /*
    * Creates a one or two dimension array for use
    *  DIM(V, 10) -> One dimensional array of a size of 10 rows is assigned to V
    *  DIM(V, 10, 5) -> Two dimensional array of a size of 10 rows, each row containing 5 elements is assigned to V
    */
    public Value _DIM(Token function, Stack<Token> stack) throws ParserException {
        String nullParams = listOfNullParameters(stack, 1);
        if (nullParams != null) {
            setStatusAndFail(function, "error.null_parameters", nullParams);
        }

        Token[] args = popArguments(function, stack);

        if (!TokenType.IDENTIFIER.equals(args[0].getType())) {
            setStatusAndFail(args[0], "error.expected_identifier", args[0].getType().name());
        }

        int numRows = args[1].asNumber().intValue();
        if (numRows < 1 || numRows > MAX_DIM_ROWS) {
            setStatusAndFail(args[1], "error.function_value_out_of_range", "DIM", "numRows", "1", "10000", String.valueOf(numRows));
        }

        int numCols = 0;
        if (args.length > 2) {
            numCols = args[2].asNumber().intValue();
            if (numCols < 1 || numCols > MAX_DIM_COLS) {
                setStatusAndFail(args[2], "error.function_value_out_of_range", "DIM", "numCols", "1", "256", String.valueOf(numCols));
            }
        }

        Value value = new Value("ARRAY", ValueType.ARRAY);
        for (int i = 0; i < numRows; i++) {
            Value newRow = new Value();
            value.addValueToArray(newRow);
            for (int j = 0; j < numCols; j++) {
                newRow.addValueToArray(new Value());
            }
        }

        args[0].setValue(value);
        variables.put(args[0].getText().toUpperCase(), args[0].getValue());

        return value;
    }

    /*
     * Returns a global variable or null if not found
     * parser.eval("SetGlobal('DOW', 1)");
     * getGlobal("DOW") ->  1 (NUMBER)
     * getGlobal("NotFound") -> null
     */
    public Value _GETGLOBAL(Token function, Stack<Token> stack) throws ParserException {
        String name = stack.pop().asString();
        Value value = new Value(function.getText());
        value.set(globals.get(name == null ? "~nofind~" : name));
        return value;
    }

    /*
     * Sets a global variableif not found
     * setGlobal("DOW", "Wednesday") ->  Boolean.TRUE and global "DOW" is (created and) set to "Wednesday"
     */
    public Value _SETGLOBAL(Token function, Stack<Token> stack) throws ParserException {
        Value value = stack.pop().getValue();
        String name = stack.pop().asString();

        boolean haveValues = value.asString() != null && name != null;
        if (haveValues) {
            addGlobalVariable(name, value);
        }

        return new Value(function.getText()).setValue(haveValues ? Boolean.TRUE : Boolean.FALSE);
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
     */
    public Value _NOW(Token function, Stack<Token> stack) throws ParserException {
        String nullParams = listOfNullParameters(stack);
        if (nullParams != null) {
            setStatusAndFail(function, "error.null_parameters", nullParams);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(getTimeZone());
        if (function.getArgc() > 0) {
            Token token = stack.pop();
            int mode = token.asNumber().intValue();
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
                    throw new ParserException(msg, token.getRow(), token.getColumn() - 1);
            }
        }

        return new Value(function.getText()).setValue(new Date(calendar.getTimeInMillis()));
    }

    /*----------------------------------------------------------------------------*/

    /*
     * Sets the number of decimal places when working with numeric values
     * parameter_1: number of decimal places (0>)
     * returns previous precision value
     */
    public Value _PRECISION(Token function, Stack<Token> stack) throws ParserException {
        String nullParams = listOfNullParameters(stack);
        if (nullParams != null) {
            setStatusAndFail(function, "error.null_parameters", nullParams);
        }

        Value value = new Value(function.getText()).setValue(BigDecimal.valueOf(precision));

        if (listOfNullParameters(stack) == null) {
            Token token = stack.pop();
            int decimals = token.asNumber().intValue();
            if (decimals >= 0 && decimals <= 100) {
                precision = decimals;
            } else {
                String msg = ParserException.formatMessage("error.function_value_out_of_range",
                        function.getText(), "1", "0", "100", String.valueOf(decimals));
                throw new ParserException(msg, token.getRow(), token.getColumn() - 1);
            }
        }

        return value;
    }

}
