package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.creativewidgetworks.expressionparser.enums.SymbolType;
import com.creativewidgetworks.expressionparser.enums.ValueType;

public class Parser {
    // Internal VO class to hold a function's argument count 
    public class ArgCount { public boolean haveArgs; public int count; }
    
    public static final int COL_SYMBOL_TYPE = 0;
    public static final int COL_PATTERN = 1;
    
    public static final String DEFAULT_SPLIT_CHARACTER = ";";
    public static final String SPLIT_REGEX = "(?=([^\\\"\\']*[\\\"\\'][^\\\"\\']*[\\\"\\'])*[^\\\"\\']*$)";
    
    // RegEx matching patterns
    private Pattern pattern_CONSTANT;
    private Pattern pattern_FUNCTION;
    private Pattern pattern_NUMBER;
    private Pattern pattern_STRING;
    private Pattern pattern_OPERATOR;
    private Pattern pattern_PROPERTY;
    private Pattern pattern_SEPARATOR;
    private Pattern pattern_IDENTIFIER;
    private Pattern pattern_EOS;
    private Pattern pattern_NOMATCH;
    private Pattern pattern_WHITESPACE;
    
    private Object[][] patterns;
    
    // Terminals
    private String COMMA, LPAREN, RPAREN;
    private String ASSIGN, TIF, TELSE;
    private String PLUS, MINUS, MULT, DIV, IDIV, IDIV2, MOD, EXP, PERCENT, UNARY_PLUS, UNARY_MINUS;
    private String EQU, NEQ, GT, GTE, LTE, LT;
    private String AND, OR, NOT;
    
    // Defines the grammar used by parser instance
    private Grammar grammar;
    
    // RegEx tokenizer
    private boolean caseSensitive; 
    private Matcher matcher;
    private int lastMatch;
    private String expressionDelimiter;
    private Map<String,List<Symbol>> tokenizedExpressions = new HashMap<String, List<Symbol>>();

    // Status 
    private ParserException  lastException;
    private String lastExpression;

    // Holds working variables, scope exists over multiple calls to eval()
    private Map<String, Value> variables = new TreeMap<String, Value>();
    
    public Parser() {
        caseSensitive = false;
        expressionDelimiter = DEFAULT_SPLIT_CHARACTER;
        initialize(new GrammarBasicCalc());
    }

    public Parser(Grammar grammar) {
        caseSensitive = false;
        expressionDelimiter = DEFAULT_SPLIT_CHARACTER;
        initialize(grammar);
    }
    
    /*----------------------------------------------------------------------------*/
    
    // Bind a set of terminals and matching rules to the parser
    private void initialize(Grammar theGrammar) {
        grammar = theGrammar;
        
        // Bind matching patterns
        pattern_CONSTANT = Pattern.compile(grammar.getPattern_CONSTANT(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_FUNCTION = Pattern.compile(grammar.getPattern_FUNCTION(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_NUMBER = Pattern.compile(grammar.getPattern_NUMBER(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_STRING = Pattern.compile(grammar.getPattern_STRING(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_OPERATOR = Pattern.compile(grammar.getPattern_OPERATOR(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_PROPERTY = Pattern.compile(grammar.getPattern_PROPERTY(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_SEPARATOR = Pattern.compile(grammar.getPattern_SEPARATOR(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_IDENTIFIER = Pattern.compile(grammar.getPattern_IDENTIFIER(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_EOS = Pattern.compile(grammar.getPattern_EOS(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_NOMATCH = Pattern.compile(grammar.getPattern_NOMATCH(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        pattern_WHITESPACE = Pattern.compile(grammar.getPattern_WHITESPACE(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);        
        patterns = new Object[][] {
            {SymbolType.CONSTANT, pattern_CONSTANT}, 
            {SymbolType.FUNCTION, pattern_FUNCTION}, 
            {SymbolType.NUMBER, pattern_NUMBER}, 
            {SymbolType.STRING, pattern_STRING}, 
            {SymbolType.OPERATOR, pattern_OPERATOR},
            {SymbolType.PROPERTY, pattern_PROPERTY},
            {SymbolType.SEPARATOR, pattern_SEPARATOR}, 
            {SymbolType.IDENTIFIER, pattern_IDENTIFIER}, 
            {SymbolType.EOS, pattern_EOS}, 
            {SymbolType.NOMATCH, pattern_NOMATCH}, 
            {SymbolType.WHITESPACE, pattern_WHITESPACE} // EOS, NOMATCH, and WHITESPACE must be the last three items
        };
        
        // Bind terminals
        COMMA = grammar.getCOMMA();
        LPAREN = grammar.getLPAREN();
        RPAREN = grammar.getRPAREN();
        // --
        ASSIGN = grammar.getASSIGN();
        TIF = grammar.getTIF();
        TELSE = grammar.getTELSE();
        // --
        PLUS = grammar.getPLUS();
        MINUS = grammar.getMINUS();
        MULT = grammar.getMULT();
        DIV = grammar.getDIV();
        IDIV = grammar.getIDIV();
        IDIV2 = grammar.getIDIV2();
        MOD = grammar.getMODULUS();
        EXP = grammar.getEXP();
        PERCENT = grammar.getPERCENT();
        UNARY_MINUS = grammar.getUNARY_MINUS();
        UNARY_PLUS = grammar.getUNARY_PLUS();
        // --
        EQU = grammar.getEQU();
        NEQ = grammar.getNEQ();
        GT  = grammar.getGT();
        GTE = grammar.getGTE();
        LTE = grammar.getLTE();
        LT  = grammar.getLT();
        // --
        AND = grammar.getAND();
        OR  = grammar.getOR();
        NOT = grammar.getNOT();
    }
    
    /*----------------------------------------------------------------------------*/
    
    public void setStatusAndFail(String message, Object... parameters) throws ParserException { 
        setStatusAndFail(null, message, parameters);
    }
    
    public void setStatusAndFail(Symbol currentToken, String message, Object... parameters) throws ParserException { 
        int errorAtRow = currentToken == null ? -1 : currentToken.getRow();
        int errorAtCol = currentToken == null ? -1 : currentToken.getColumn();
        String errorMessage = ParserException.formatMessage(message, parameters);
        throw new ParserException(errorMessage, errorAtRow, errorAtCol);
    }
     
    /*----------------------------------------------------------------------------*/
    
    public void clearCache() {
        tokenizedExpressions.clear();
    }

    public void clearVariables() {
        variables.clear();
    }
    
    public Grammar getGrammar() {
        return grammar;
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
    


    /*----------------------------------------------------------------------------*/
    
    public Value eval(String source) {
        Value value = null;

        try {
            // Clear results of last parse
            lastException = null;
            lastExpression = source;

            // Source statements cannot be null
            if (source == null) {
                source = "";
            }

            // See if this expression has been previously parsed
            List<Symbol> tokens = tokenizedExpressions.get(source);

            // If expression isn't in the map then tokenize, generate RPN stack, and store the tokens
            if (tokens == null) {
                tokens = new ArrayList<Symbol>();
                String[] expressions = source.split(expressionDelimiter + SPLIT_REGEX);
                for (String expression : expressions) {
                    if (expression.trim().length() > 0) {
                        List<Symbol> list = tokenize(expression);
                        if (list.size() > 0) {
                            tokens.addAll(infixToRPN(list));
                        }
                    }
                }
            }

            // Save the parsed tokens in the cache
            if (tokens.size() > 0) {
                tokenizedExpressions.put(source, tokens);
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
     * Return the next token in the stream
     * @param eatWhitespace true if all whitespace before the next token should be ignored
     * @return Token representing the next token or TokenType.EOS if the stream has been exhausted
     */
    private Symbol getToken(boolean eatWhitespace) {
        if (eatWhitespace) {
            if (matcher.usePattern(pattern_WHITESPACE).find(lastMatch)) {
                lastMatch = matcher.end();
            }
        }
        
        Symbol token = null;
     
        for (Object[] row : patterns) {
            Pattern pattern = (Pattern)row[COL_PATTERN];
            SymbolType symbolType = (SymbolType)row[COL_SYMBOL_TYPE];
            
            if (matcher.usePattern(pattern).find(lastMatch)) {
                if (matcher.start() == lastMatch) {
                    lastMatch = matcher.end();
                    String text = symbolType == SymbolType.PROPERTY ? matcher.group(1) : matcher.group();
                    token = new Symbol(symbolType, text, 1, lastMatch);
                    break;
                }
            }
        }
        
        return token;
    }    

    /*----------------------------------------------------------------------------*/
    
    /**
     * Parse a source string into a series of tokens to be processed.
     * Note: Although this method could be incorporated as part of the infixToRPN
     * method, keeping a separate operation can make things more flexible as no
     * lexical analysis is being performed during the tokenization.
     * @param source statements
     * @return list of tokens
     */
    protected List<Symbol> tokenize(String source) {
        List<Symbol> tokens = new ArrayList<Symbol>();
        
        this.lastMatch = 0;
        this.matcher = pattern_NOMATCH.matcher(source == null ? "" : source);
        
        Symbol token = new Symbol(SymbolType.UNDEFINED, "", -1, -1);
        while (!token.getType().equals(SymbolType.EOS)) {
            token = getToken(true);
            if (!token.getType().equals(SymbolType.EOS)) {
                tokens.add(token);
            }
        }
        
        return tokens;
    }
    
    /*----------------------------------------------------------------------------*/
    
    /**
     * Convert a list of infix tokens to Reverse Polish Notation (RPN) form. Dijkstra's  
     * shunting-yard algorithm is used to process the tokens.
     * @param inputTokens
     * @return String[] outputTokens in RPN form
     */
    protected List<Symbol> infixToRPN(List<Symbol> inputTokens) throws ParserException {
        List<Symbol> outputTokens = new ArrayList<Symbol>();
        
        Symbol lastToken = null;
        Stack<Symbol> stack = new Stack<Symbol>();
        Stack<ArgCount> argStack = new Stack<ArgCount>();
        
        // To simplify processing later, a check for matching parenthesis is performed now
        int count = 0;
        for (Symbol token : inputTokens) {        
            if (token.equals(LPAREN)) {
                count++;
            } else if (token.equals(RPAREN)) {
                count--;
                if (count < 0) {
                    setStatusAndFail(token, "error.missing_parens", LPAREN);
                }
            }
        }
        if (count != 0) {
            Symbol token = inputTokens.get(inputTokens.size() - 1);
            setStatusAndFail(token, "error.missing_parens", RPAREN);
        }
        
        for (Symbol token : inputTokens) {
            // Touch up token if a unary minus or plus is encountered
            if ((token.equals(MINUS) || token.equals(PLUS))) {
                boolean isUnary = 
                    lastToken == null || 
                    lastToken.isOperator() || 
                    lastToken.getText().equals(LPAREN) ||
                    lastToken.getText().equals(COMMA);
                if (isUnary) {
                    if (token.equals(MINUS)) {
                        token.setText(UNARY_MINUS);  // unary minus, parsers 1 - -1.0
                    } else {
                        token.setText(UNARY_PLUS); // unary plus, parses  1 + +1.0
                    }
                }
            }
            
            lastToken = token;
            
            if (token.isNumber() || token.isString() || token.isConstant() || token.isIdentifer()) {
                outputTokens.add(token);
                if (!argStack.isEmpty()) {
                    argStack.peek().haveArgs = true;
                }
            } else if (token.isProperty()) {
                 Object obj = grammar.getProperty(token.getText());
                 Value value = new Value();
                 if (obj == null) {
                     // parameter not found -- will return UNDEFINED value
                 } else if (obj instanceof Boolean) {
                     value.setValue((Boolean)obj);
                 } else if (obj instanceof BigDecimal) {
                     value.setValue((BigDecimal)obj);
                 } else if (obj instanceof Date) {
                     value.setValue((Date)obj);
                 } else {
                     value.setValue(obj.toString());
                 }
                 stack.push(new Symbol().setValue(value));
            } else if (token.isFunction()) {
                stack.push(token);
                if (!argStack.isEmpty()) {
                    argStack.peek().haveArgs = true;
                }
                argStack.push(new ArgCount());
            } else if (token.equals(COMMA)) {
                // Copy tokens to output and bump argument count for function
                while (!stack.empty() && !stack.peek().equals(LPAREN)) {
                    outputTokens.add(stack.pop());
                }
                if (argStack.peek().haveArgs) {
                    argStack.peek().count++;
                }
            } else if (token.isOperator()) {
                // While stack not empty and stack top element is an operator add it to the output
                while (!stack.empty() && stack.peek().isOperator()) {
                    if (grammar.shouldPopToken(token, stack.peek(), caseSensitive)) {
                        outputTokens.add(stack.pop());
                        continue;
                    }
                    break;
                }
                stack.push(token);
            } else if (token.equals(LPAREN)) {
                stack.push(token); 
            } else if (token.equals(RPAREN)) {
                while (!stack.empty() && !stack.peek().equals(LPAREN)) {
                    outputTokens.add(stack.pop());
                }
                
                stack.pop();

                if (!stack.empty() && stack.peek().isFunction()) {
                    Symbol function = stack.pop();
                    ArgCount argc = argStack.pop();
                    function.setArgc(argc.haveArgs ? argc.count + 1 : argc.count);
                    outputTokens.add(function);
                }
            }
        }
        
        // Copy the rest of the stack to the output
        while (!stack.empty()) {
            outputTokens.add(stack.pop());
        }
        
        return outputTokens;
    }

    /*----------------------------------------------------------------------------*/

    private boolean haveString(Symbol lhs, Symbol rhs) {
        return lhs.getValue().getType() == ValueType.STRING || rhs.getValue().getType() == ValueType.STRING;
    }
   
    private void assertBothNumbers(Symbol lhs, Symbol rhs) throws ParserException {
        if (lhs.getValue().getType() != ValueType.NUMBER || rhs.getValue().getType() != ValueType.NUMBER ) {
            setStatusAndFail("error.both_must_be_numeric");
        }
    }
    
    private void assertSufficientStack(Symbol symbol, Stack<Symbol> stack, int requiredSize) throws ParserException {
        if (stack.size() < requiredSize) {
            setStatusAndFail(symbol, "error.syntax");
        }
    }
    
    private Symbol processOperators(Symbol operator, Stack<Symbol> stack) throws ParserException {
        Symbol result = null;

        // Unary: percentage
        String op = operator.getText();
        if (op.equals(PERCENT)) {
            assertSufficientStack(operator, stack, 1);
            BigDecimal bd = stack.pop().asNumber().divide(new BigDecimal(100));
            return new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());   
        }

        // Ternary 
        if (op.equals(TIF)) {
            assertSufficientStack(operator, stack, 4);
            if (!stack.pop().getText().equals(TELSE)) {
                setStatusAndFail("error.expected_telse", TELSE);
            }
            
            Symbol falseValue = stack.pop();
            Symbol trueValue = stack.pop();
            Symbol booleanValue =  stack.pop();
            if (booleanValue.getValue().getType() != ValueType.BOOLEAN) {
                setStatusAndFail(booleanValue, "error.boolean_expected");
            }

            return booleanValue.asBoolean().booleanValue() ? trueValue : falseValue;
        }
        
        assertSufficientStack(operator, stack, 2);
        Symbol rhs = stack.pop();
        Symbol lhs = stack.pop();

        try {
            if (op.equals(PLUS)) {
                // Addition
                if (haveString(lhs, rhs)) {
                    String strL = lhs.asString() == null ? "" : lhs.asString();
                    String strR = rhs.asString() == null ? "" : rhs.asString();
                    result = new Symbol(SymbolType.STRING, strL + strR, operator.getRow(), operator.getColumn());
                } else {
                    assertBothNumbers(lhs, rhs);
                    BigDecimal bd = lhs.asNumber().add(rhs.asNumber());
                    result = new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());
                }
            } else if (op.equals(MINUS)) {
                // Subtraction
                BigDecimal bd = lhs.asNumber().subtract(rhs.asNumber());
                result = new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());
            } else if (op.equals(MULT)) {
                // Multiplication
                BigDecimal bd = lhs.asNumber().multiply(rhs.asNumber());
                bd = bd.setScale(grammar.getPrecision(), BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                result = new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());
            } else if (op.equals(DIV)) {
                // Division
                int divisorScale = rhs.asNumber().scale();
                int scale = lhs.asNumber().equals(BigDecimal.ZERO) ? divisorScale : grammar.getPrecision();
                BigDecimal bd = lhs.asNumber().divide(rhs.asNumber(), scale, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                result = new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());
            } else if (op.equals(IDIV) || op.equalsIgnoreCase(IDIV2)) {
                // Integer division
                BigDecimal bd = lhs.asNumber().divideToIntegralValue(rhs.asNumber());
                result = new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());
            } else if (op.equalsIgnoreCase(MOD)) {
                // Modulus
                BigDecimal bd = lhs.asNumber().remainder(rhs.asNumber());
                result = new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());
            } else if (op.equals(PERCENT)) {
                BigDecimal bd = lhs.asNumber().divide(new BigDecimal("100"));
                result = new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());
            } else if (op.equals(EXP)) {
                // Exponentiation x^y
                MathContext mc = rhs.asNumber().compareTo(BigDecimal.ZERO) < 0 ? MathContext.DECIMAL128 : MathContext.UNLIMITED;
                BigDecimal bd = lhs.asNumber().pow(rhs.asNumber().intValue(), mc);
                result = new Symbol(SymbolType.NUMBER, bd.toPlainString(), operator.getRow(), operator.getColumn());
            } else if (op.equals(ASSIGN)) {
                // Assignment
                if (lhs.isIdentifer()) {
                    // Trying to assign an uninitialized variable -- could also get here
                    // if user is trying to call a function that doesn't exist.
                    if (rhs.getValue().getType().equals(ValueType.UNDEFINED)) {
                        setStatusAndFail(rhs, "error.expected_initialized", rhs.getText());
                    }

                    // Identifier should always be found as it would have been created when parsing the RPN stack
                    variables.get(lhs.getText().toUpperCase()).set(rhs.getValue());
                    result = new Symbol(rhs.getType(), rhs.getText(), operator.getRow(), operator.getColumn());
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
    
    private Symbol processRelationalOperators(Symbol lhs, String op, Symbol rhs) throws ParserException {
        boolean isTrue = false; 
        
        if (lhs.getValue().getType() == ValueType.BOOLEAN) {
            boolean b1 = lhs.asBoolean().booleanValue();
            boolean b2 = rhs.asBoolean().booleanValue();
            if (op.equals(EQU)) {
                isTrue = b1 == b2;
            } else if (op.equals(NEQ)) {
                isTrue = b1 != b2;
            } else if (op.equalsIgnoreCase(AND)){
                isTrue = b1 && b2;
            } else if (op.equalsIgnoreCase(OR)) {
                isTrue = b1 || b2;
            } else {
                setStatusAndFail("error.invalid_operator", op);
            }         
        } else if (lhs.getValue().getType() == ValueType.NUMBER) {
            BigDecimal bd1 = lhs.asNumber();
            BigDecimal bd2 = rhs.asNumber();
            if (op.equals(LT)) {
                isTrue = bd1.compareTo(bd2) < 0;
            } else if (op.equals(LTE)) {
                isTrue = bd1.compareTo(bd2) <= 0;
            } else if (op.equals(EQU)) {
                isTrue = bd1.compareTo(bd2) == 0;
            } else if (op.equals(NEQ)) {
                isTrue = bd1.compareTo(bd2) != 0;
            } else if (op.equals(GTE)) {
                isTrue = bd1.compareTo(bd2) >= 0;
            } else if (op.equals(GT)) {
                isTrue = bd1.compareTo(bd2) > 0;
            } else {
                setStatusAndFail("error.invalid_operator", op);
            }
        } else if (lhs.getValue().getType() == ValueType.STRING) {
            String s1 = lhs.asString();
            String s2 = rhs.asString();
            boolean haveValues = ((s1 != null) && (s2 != null));
            if (op.equals(LT)) {
                isTrue = haveValues && s1.compareTo(s2) < 0;
            } else if (op.equals(LTE)) {
                isTrue = haveValues && s1.compareTo(s2) <= 0;
            } else if (op.equals(EQU)) {
                isTrue = haveValues && s1.compareTo(s2) == 0;
            } else if (op.equals(NEQ)) {
                isTrue = haveValues && s1.compareTo(s2) != 0;
            } else if (op.equals(GTE)) {
                isTrue = haveValues && s1.compareTo(s2) >= 0;
            } else if (op.equals(GT)) {
                isTrue = haveValues && s1.compareTo(s2) > 0;
            } else {
                setStatusAndFail("error.invalid_operator", op);
            }            
        } else if (lhs.getValue().getType() == ValueType.DATE) {
            Date d1 = lhs.asDate();
            Date d2 = rhs.asDate();
            if (op.equals(LT)) {
                isTrue = d1.compareTo(d2) < 0;
            } else if (op.equals(LTE)) {
                isTrue = d1.compareTo(d2) <= 0;
            } else if (op.equals(EQU)) {
                isTrue = d1.compareTo(d2) == 0;
            } else if (op.equals(NEQ)) {
                isTrue = d1.compareTo(d2) != 0;
            } else if (op.equals(GTE)) {
                isTrue = d1.compareTo(d2) >= 0;
            } else if (op.equals(GT)) {
                isTrue = d1.compareTo(d2) > 0;
            } else {
                setStatusAndFail("error.invalid_operator", op);
            }
        }

        Symbol result = new Symbol();
        result.getValue().setValue(isTrue ? Boolean.TRUE : Boolean.FALSE);
        return result;
    }

    private Symbol processFunction(Symbol function, Stack<Symbol> stack) throws ParserException {
        Value value = null;
        String name = function.getText();

        Function f = grammar.getFunction(name, caseSensitive);
        if (f != null) {
            try {
                value = f.execute(function, stack);
            } catch (ParserException ex) {
                setStatusAndFail(function, ex.getMessage());
            }
        } else {
            setStatusAndFail(function, "error.no_handler", name);
        }
        
        return new Symbol().setValue(value);
    }
    
    protected Value RPNtoValue(List<Symbol> tokens) throws ParserException {
        int tcount = 0;
        
        Stack<Symbol> stack = new Stack<Symbol>();

        for (Symbol token : tokens) {
            if (token.isFunction()) {
                stack.push(processFunction(token, stack));
            } else if (token.isConstant()) {
                BigDecimal bd = grammar.getConstant(token.getText(), caseSensitive);
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
                String op = token.getText();
                if (op.equals(UNARY_MINUS) || op.equals(NOT)) {
                    Value value = stack.pop().getValue();
                    switch (value.getType()) {
                        case NUMBER:
                            value.setValue(value.asNumber().negate());
                            break;
                        case BOOLEAN:
                            value.setValue(value.asBoolean().booleanValue() ? Boolean.FALSE : Boolean.TRUE);
                            break;
                        default:
                            setStatusAndFail(token, "error.type_mismatch", value.getType().name());
                    }
                    stack.push(new Symbol(SymbolType.NUMBER, value.asString(), token.getRow(), token.getColumn()));
                    continue;
                } else if (op.equals(TIF)) {
                    tcount--;
                } else if (op.equals(TELSE)) {
                    tcount++;
                    stack.push(token);
                    continue;
                } else if (op.equals(UNARY_PLUS)) {
                    continue;
                }
                
                // Test for TIF without corresponding TELSE
                if (tcount % 2 != 0) {
                    setStatusAndFail(token, "error.missing_telse", TIF, TELSE);
                }
                
                stack.push(processOperators(token, stack));
            } else {
                stack.push(token);
            }
        }
        
        // Test for uncaught TELSE without corresponding TIF
        if (tcount != 0) {
            setStatusAndFail("error.missing_tif", TELSE, TIF);
        }
      
        return stack.pop().getValue();
    }

}    
