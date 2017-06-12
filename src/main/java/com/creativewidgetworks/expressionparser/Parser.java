package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

public class Parser {
    // Internal VO class to hold a function's argument count
    public class ArgCount { public boolean haveArgs; public int count; }

    // Default numeric precision (number of decimal places)
    public static final int DEFAULT_PRECISION = 5;

    private static final String DEFAULT_SPLIT_CHARACTER = ";";
    private static final String SPLIT_REGEX = "(?=([^\\\"\\']*[\\\"\\'][^\\\"\\']*[\\\"\\'])*[^\\\"\\']*$)";

    // Number of digits of precision for math operations
    private int precision = DEFAULT_PRECISION;

    // RegEx tokenizer - package level for testing
    private boolean caseSensitive;
    private final String expressionDelimiter;
    final Map<String,List<Token>> tokenizedExpressions = new HashMap<>();

    // Status
    private ParserException  lastException;
    private String lastExpression;

    // Holds working variables, scope exists over multiple calls to eval()
    private final Map<String, Value> variables = new TreeMap<>();

    public Parser() {
        caseSensitive = false;
        expressionDelimiter = DEFAULT_SPLIT_CHARACTER;
    }

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
        return
            (isType(token, Operator.LEFT_ASSOCIATIVE, caseSensitive) && compareTokens(token, topOfStack, caseSensitive) >= 0) ||
            (isType(token, Operator.RIGHT_ASSOCIATIVE, caseSensitive) && compareTokens(token, topOfStack, caseSensitive) > 0);
    }

    /*----------------------------------------------------------------------------*/

    public void clearCache() {
        tokenizedExpressions.clear();
    }

    public void clearVariables() {
        variables.clear();
    }

    public ParserException getLastException() {
        return lastException;
    }

    public String getLastExpression() {
        return lastExpression;
    }

    public Map<String, Value> getVariables() {
        return variables;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /*----------------------------------------------------------------------------*/

    private BigDecimal getConstant(String name) {
        return Constant.get(name, caseSensitive);
    }

    /*----------------------------------------------------------------------------*/

    private Object getProperty(String name) {
        Object obj = System.getenv(name);
        return obj == null ? System.getProperty(name) : obj;
    }

    /*----------------------------------------------------------------------------*/

    public boolean setCaseSensitive(boolean caseSensitive) {
        boolean oldValue = this.caseSensitive;
        this.caseSensitive = caseSensitive;
        return oldValue;
    }

    /*----------------------------------------------------------------------------*/

    private void setStatusAndFail(String message, Object... parameters) throws ParserException {
        setStatusAndFail(null, message, parameters);
    }

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
        lastExpression = source;

        try {
            // See if this expression has been previously parsed
            List<Token> tokens = tokenizedExpressions.get(source);

            // If expression isn't in the map then tokenize, generate RPN stack, and store the tokens
            if (tokens == null) {
                tokens = new ArrayList<>();
                String[] expressions = source.split(expressionDelimiter + SPLIT_REGEX);
                for (String expression : expressions) {
                    if (expression.trim().length() > 0) {
                        List<Token> list = Tokenizer.tokenize(expression, caseSensitive, false);
                        if (list.size() > 0) {
                            tokens.addAll(infixToRPN(list));
                        }
                    }
                }

                // Save the parsed tokens in the cache
                if (tokens.size() > 0) {
                    tokenizedExpressions.put(source, tokens);
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

    /**
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

        // To simplify processing later, a check for matching parenthesis is performed now
        int count = 0;
        for (Token token : inputTokens) {
            if (token.opEquals(Operator.LPAREN)) {
                count++;
            } else if (token.opEquals(Operator.RPAREN)) {
                count--;
                if (count < 0) {
                    setStatusAndFail(token, "error.missing_parens", Operator.LPAREN.getText());
                }
            }
        }
        if (count != 0) {
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

            lastToken = token;

            if (token.isNumber() || token.isString() || token.isConstant() || token.isIdentifer() || token.isProperty()) {
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
                // Copy tokens to output and bump argument count for function
                while (!stack.empty() && !stack.peek().opEquals(Operator.LPAREN)) {
                    outputTokens.add(stack.pop());
                }
                if (argStack.peek().haveArgs) {
                    argStack.peek().count++;
                }

            } else if (token.opEquals(Operator.LPAREN)) {
                stack.push(token);

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
            setStatusAndFail("error.both_must_be_numeric");
        }
    }

    private void assertSufficientStack(Token Token, Stack<Token> stack, int requiredSize) throws ParserException {
        if (stack.size() < requiredSize) {
            setStatusAndFail(Token, "error.syntax");
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
                setStatusAndFail("error.expected_telse", Operator.TELSE.getText());
            }

            Token falseValue = stack.pop();
            Token trueValue = stack.pop();
            Token booleanValue =  stack.pop();
            if (booleanValue.getValue().getType() != ValueType.BOOLEAN) {
                setStatusAndFail(booleanValue, "error.boolean_expected");
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
                    result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
                }
            } else if (op.equals(Operator.MINUS)) {
                // Subtraction
                BigDecimal bd = lhs.asNumber().subtract(rhs.asNumber());
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
            } else if (op.equals(Operator.PERCENT)) {
                BigDecimal bd = lhs.asNumber().divide(new BigDecimal("100"), RoundingMode.UP);
                result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
            } else if (op.equals(Operator.EXP)) {
                // Exponentiation x^y
                MathContext mc = rhs.asNumber().compareTo(BigDecimal.ZERO) < 0 ? MathContext.DECIMAL128 : MathContext.UNLIMITED;
                BigDecimal bd = lhs.asNumber().pow(rhs.asNumber().intValue(), mc);
                result = new Token(TokenType.NUMBER, bd.toPlainString(), token.getRow(), token.getColumn());
            } else if (op.equals(Operator.ASSIGNMENT)) {
                // Assignment
                if (lhs.isIdentifer()) {
                    // Trying to assign an uninitialized variable -- could also get here
                    // if user is trying to call a function that doesn't exist.
                    if (rhs.getValue().getType().equals(ValueType.UNDEFINED)) {
                        setStatusAndFail(rhs, "error.expected_initialized", rhs.getText());
                    }

                    // Identifier should always be found as it would have been created when parsing the RPN stack
                    variables.get(lhs.getText().toUpperCase()).set(rhs.getValue());
                    result = new Token(rhs.getType(), rhs.getText(), token.getRow(), token.getColumn());
                    result.getValue().set(rhs.getValue());
                } else {
                    setStatusAndFail(lhs, "error.expected_identifier", lhs.getText());
                }
            } else {
                result = processRelationalOperators(lhs, op, rhs);
            }
        } catch (ArithmeticException ex) {
            throw new ParserException(ex.getMessage(), ex);
        }

        return result;
    }

    private Token processRelationalOperators(Token lhs, Operator op, Token rhs) throws ParserException {
        boolean isTrue = false;

        if (lhs.getValue().getType() == ValueType.BOOLEAN) {
            if (op.inSet(Operator.EQU, Operator.NEQ, Operator.AND, Operator.OR)) {
                isTrue = performComparison(lhs.getValue().asBoolean(), rhs.getValue().asBoolean(), op);
            } else {
                setStatusAndFail(rhs, "error.invalid_operator_boolean", op.getText());
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

        Token token = new Token();
        token.getValue().setValue(isTrue ? Boolean.TRUE : Boolean.FALSE);
        return token;
    }

    @SuppressWarnings("unchecked")
    private boolean performComparison(Comparable o1, Comparable o2, Operator op) throws ParserException {
        boolean isTrue;
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
        } else {
            isTrue = false;
            setStatusAndFail("error.invalid_operator", op);
        }
        return isTrue;
    }

    private Token processFunction(Token function, Stack<Token> stack) throws ParserException {
        Value value = null;
        String name = function.getText();

        Function f = Function.get(name, caseSensitive);
        if (f != null) {
            try {
                value = f.execute(function, stack);
            } catch (ParserException ex) {
                setStatusAndFail(function, ex.getMessage());
            }
        } else {
            setStatusAndFail(function, "error.no_handler", name);
        }

        return new Token().setValue(value);
    }

    protected Value RPNtoValue(List<Token> tokens) throws ParserException {
        int tcount = 0;

        Stack<Token> stack = new Stack<>();

        for (Token token : tokens) {
            token.restoreValue();

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
                            break;
                        case BOOLEAN:
                            value.setValue(value.asBoolean() ? Boolean.FALSE : Boolean.TRUE);
                            break;
                        default:
                            setStatusAndFail(token, "error.type_mismatch", value.getType().name());
                    }
                    stack.push(new Token(TokenType.NUMBER, value.asString(), token.getRow(), token.getColumn()));
                    continue;
                } else if (op.equals(Operator.TIF)) {
                    tcount--;
                } else if (op.equals(Operator.TELSE)) {
                    tcount++;
                    stack.push(token);
                    continue;
                } else if (op.equals(Operator.UNARY_PLUS)) {
                    continue;
                }

                // Test for TIF without corresponding TELSE
                if (tcount % 2 != 0) {
                    setStatusAndFail(token, "error.missing_telse", Operator.TIF.getText(), Operator.TELSE.getText());
                }

                stack.push(processOperators(token, stack));
            } else {
                stack.push(token);
            }
        }

        // Test for uncaught TELSE without corresponding TIF
        if (tcount != 0) {
            setStatusAndFail("error.missing_tif", Operator.TELSE.getText(), Operator.TIF.getText());
        }

        return stack.pop().getValue();
    }

}
