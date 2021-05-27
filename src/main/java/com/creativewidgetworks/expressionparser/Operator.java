package com.creativewidgetworks.expressionparser;

import java.util.*;

// Precedence and associativity of Java operators courtesy of http://introcs.cs.princeton.edu/java/11precedence/
enum Operator {
    // Groups/Containers
    COMMA        (1, Operator.NONE_ASSOCIATIVE, ",", ","),
    LBRACKET     (1, Operator.LEFT_ASSOCIATIVE, "[", "\\["),
    RBRACKET     (1, Operator.LEFT_ASSOCIATIVE, "]", "\\]"),
    LPAREN       (1, Operator.LEFT_ASSOCIATIVE, "(", "\\("),
    RPAREN       (1, Operator.LEFT_ASSOCIATIVE, ")", "\\)"),

    // Exponentiation
    EXP          (2, Operator.NONE_ASSOCIATIVE, "^", "\\^"),

    // Unary
    PERCENT      (3, Operator.RIGHT_ASSOCIATIVE, "%", "%"),
    UNARY_MINUS  (3, Operator.RIGHT_ASSOCIATIVE, "!", "!"),
    UNARY_PLUS   (3, Operator.RIGHT_ASSOCIATIVE, "!!", "!!"),

    // Logical NOT
    NOT          (3, Operator.RIGHT_ASSOCIATIVE, "NOT", "not"),

    // -- Math operators
    MULT         (4, Operator.LEFT_ASSOCIATIVE, "*", "\\*"),
    DIV          (4, Operator.LEFT_ASSOCIATIVE, "/", "/"),
    IDIV         (4, Operator.LEFT_ASSOCIATIVE, "DIV", "div"),
    MODULUS      (4, Operator.LEFT_ASSOCIATIVE, "MOD", "mod"),
    PLUS         (5, Operator.LEFT_ASSOCIATIVE, "+", "\\+"),
    MINUS        (5, Operator.LEFT_ASSOCIATIVE, "-", "-"),

    // -- Bitwise
    LSHIFT       (6, Operator.LEFT_ASSOCIATIVE, "<<", "<<"),
    RSHIFT       (6, Operator.LEFT_ASSOCIATIVE, ">>", ">>"),

    // Comparison
    LT           (7, Operator.LEFT_ASSOCIATIVE, "<", "<"),
    LTE          (7, Operator.LEFT_ASSOCIATIVE, "<=", "<="),
    GT           (7, Operator.LEFT_ASSOCIATIVE, ">", ">"),
    GTE          (7, Operator.LEFT_ASSOCIATIVE, ">=", ">="),
    EQU          (8, Operator.LEFT_ASSOCIATIVE, "==", "=="),
    NEQ          (8, Operator.LEFT_ASSOCIATIVE, "!=", "!="),

    // Logical
    AND          (12, Operator.LEFT_ASSOCIATIVE, "AND", "and"),
    OR           (13, Operator.LEFT_ASSOCIATIVE, "OR", "or"),

    // -- Ternary
    TIF          (14, Operator.RIGHT_ASSOCIATIVE, "?", "\\?"),
    TELSE        (14, Operator.RIGHT_ASSOCIATIVE, ":", ":"),

    // Assignment
    ASSIGNMENT   (15, Operator.RIGHT_ASSOCIATIVE, "=", "=");

    // Associative rule constants (package level for testing)
    static final int NONE_ASSOCIATIVE  = 0;
    static final int LEFT_ASSOCIATIVE  = 1;
    static final int RIGHT_ASSOCIATIVE = 2;

    private static final Map<String, Operator> caseInsensitiveMap = new HashMap<String, Operator>();
    private static final Map<String, Operator> caseSensitiveMap = new HashMap<String, Operator>();

    static {
        for (Operator operator : values()) {
            caseInsensitiveMap.put(operator.text.toUpperCase(), operator);
            caseSensitiveMap.put(operator.text, operator);
        }
    }

    private final int precedence;
    private final int association;
    private final String text;
    private final String regex;

    Operator(int precedence, int assocation, String text, String regex) {
        this.precedence = precedence;
        this.association = assocation;
        this.text = text;
        this.regex = regex;
    }

    /*
     * If caseSensitive is TRUE, entire operator case must match:
     * TRUE:  and AND (match) And (no match)
     * FALSE: and AND (match) And (match)
     *
     */
    public static Operator find(Token token, boolean caseSensitive) {
        String key = (token == null || token.getText() == null) ? "" : token.getText().toUpperCase();
        return caseInsensitiveMap.get(key);
    }

    public boolean inSet(Operator... operators) {
        for (Operator operator : operators) {
            if (this.equals(operator)) {
                return true;
            }
        }
        return false;
    }

    public int getAssociation() {
        return association;
    }

    public int getPrecedence() {
        return precedence;
    }

    public String getText() {
        return text;
    }

    static String getOperatorRegex() {
        List<String> regexs = new ArrayList<>();
        for (Operator operator : values()) {
            regexs.add(operator.regex);
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

        return sb.toString();
    }

}
