package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.*;

public class Constant {
    private static final Map<String, BigDecimal> constants = new HashMap<>();

    public static void addConstant(String name, BigDecimal value, boolean caseSensitive) {
        if (name != null) {
            constants.put(caseSensitive ? name : name.toUpperCase(), value);
            TokenType.invalidatePattern();
        }
    }

    public static void clearConstants() {
        constants.clear();
        TokenType.invalidatePattern();
    }

    public static BigDecimal get(String name, boolean caseSensitive) {
        return name == null ? null : constants.get(caseSensitive ? name : name.toUpperCase());
    }

    public static Map<String, BigDecimal> getConstants() {
        return constants;
    }

    static String getConstantRegex() {
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
}
