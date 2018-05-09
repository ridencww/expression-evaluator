package com.creativewidgetworks.expressionparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum TokenType {
    COMMENT("/\\*[^*]*\\*+(?:[^*/][^*]*\\*+)*/", false),  //  // comment  /* comment */
    NUMBER("(?:\\b[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+\\b)(?:[eE][-+]?[0-9]+\\b)?", false),
    STRING("\"((?:[^\"\\\\]|\\\\.)*)\"|'((?:[^'\\\\]|\\\\.)*)'", true),  //  "string" 'string'
    OPERATOR("~~dynamically-generated~~", false),
    CONSTANT("~~dynamically-generated~~", false),
    FUNCTION("~~dynamically-generated~~", false),
    IDENTIFIER("[_A-Za-z][_A-Za-z0-9]*", false),
    FIELD("@([@]{0,2}[\\.\\->_/:A-Za-z0-9]+)", true), // @name  @name_first @name:first @name/first
    PROPERTY("\\$\\{(.*)}", true), // ${id}
    NEWLINE("\n", false),
    EOS(";", false),
    WHITESPACE("[ \\t]+", false),
    NOMATCH("", false),
    NOTHROW("", false), // Flag to suppress immediately throwing ParserExceptions
    VALUE("", false); // intermediate value during parse

    /*
     * Field identifiers begin with @ and allow for flexibility with naming:
     *   @name
     *   @name.first
     *   @name-first
     *   @name>first
     *   @name->first
     *   @name_first
     *   @name/first
     *   @name:first
     *   @name::first
     *   @99test
     *   And any permutations of the above. It is strongly recommended that expressions
     *   using fields should include whitespace after the field name to avoid the risk
     *   of an unintended parse because of symbol conflicts.
     *
     *   Do this:
     *   @/metadata/number1/<sp>/<sp>@/metadata/number2/
     */

    private static Pattern combinedPattern;

    private final String regex;
    private final Pattern pattern;

    TokenType(String regex, boolean resolutionNeeded) {
        this.regex = regex;
        this.pattern = resolutionNeeded ? Pattern.compile(regex) : null;
    }

    String getRegex(Parser parser) {
        if (this.equals(OPERATOR)) {
            return Operator.getOperatorRegex();
        } else if (this.equals(CONSTANT)) {
            return parser.getConstantRegex();
        } else if (this.equals(FUNCTION)) {
            return parser.getFunctionRegex();
        } else {
            return regex;
        }
    }

    public static String unescapeString(String str) {
        if (str == null || str.trim().length() == 0) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == str.length() - 1) ? '\\' : str.charAt(i + 1);

                // For completeness, support Octal escape
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < str.length() - 1) && str.charAt(i + 1) >= '0' && str.charAt(i + 1) <= '7') {
                        code += str.charAt(i + 1);
                        i++;
                        if ((i < str.length() - 1) && str.charAt(i + 1) >= '0' && str.charAt(i + 1) <= '7') {
                            code += str.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append(Character.toChars(Integer.parseInt(code, 8)));
                    continue;
                }

                switch (nextChar) {
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    case '\\':
                        ch = '\\';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case 'u':
                        if (i < str.length() - 5) {
                            try {
                                String code = "" + str.charAt(i + 2) + str.charAt(i + 3) + str.charAt(i + 4) + str.charAt(i + 5);
                                sb.append(Character.toChars(Integer.parseInt(code, 16)));
                            } catch (NumberFormatException ex) {
                                // Ignore poorly formed Unicode escape sequence
                            }
                            i += 5;
                            continue;
                        } else {
                            ch = 'u';
                            break;
                        }
                    default:
                        i--; // Cause the backslash and next character to be appended to output
                        break;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /*---------------------------------------------------------------------------------*/

    /**
     * Retrieve the text from within a group.
     * The text for some tokens must be extracted from within the matched text. For example, a PROPERTY
     * could take the form of "${java.version}" and the actual text should be "java.version". If a TokenType
     * has a local pattern, this routine will extract the token text from the first group.
     *
     * @param text whoee actual contents may be inside a group (pattern != null)
     * @return the actual text, which may or may not be in group(1).
     */
    String resolve(String text) {
        if (pattern != null) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find() && matcher.groupCount() > 0) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    if (matcher.group(i) != null) {
                        return unescapeString(matcher.group(i));
                    }
                }
            }
        }

        return text;
    }

    /*---------------------------------------------------------------------------------*/

    /**
     * Returns a regex that will be used to parse OPERATOR tokens
     * @param parser instance using the TokenType
     * @return String, regex expression
     */
    public static Pattern getPattern(Parser parser) {
        if (combinedPattern == null) {
            StringBuilder sb = new StringBuilder();
            for (TokenType tokenType : TokenType.values()) {
                sb.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.getRegex(parser)));
            }

            int options = parser.getCaseSensitive() ? Pattern.UNICODE_CASE : Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
            options |= Pattern.UNICODE_CHARACTER_CLASS;
            combinedPattern = Pattern.compile(sb.substring(1), options);
        }
        return combinedPattern;
    }

   /*---------------------------------------------------------------------------------*/

   public static void invalidatePattern() {
       combinedPattern = null;
   }
}
