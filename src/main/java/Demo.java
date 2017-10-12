import com.creativewidgetworks.expressionparser.*;

import java.util.List;

public class Demo {

    /* The only reason that this program is using DemoParser is to expose the tokenize
     * and RPN methods which normally aren't called directly outside of a demo like this.
     */
    class DemoParser extends Parser {
        private List<Token> infix;
        private List<Token> rpn;

        DemoParser() {
            super();
        }

        List<Token> getInfix() {
            return infix;
        }

        List<Token> getPostfix() {
            return rpn;
        }

        @Override
        public List<Token> infixToRPN(List<Token> inputTokens) throws ParserException {
            infix = inputTokens;
            rpn = super.infixToRPN(inputTokens);
            return rpn;
        }
    }
    
    private static void usage() {
        System.out.println("usage: Demo \"expression\" [-verbose]");
        System.exit(0);
    }
    
    public static void main(String[] args) throws ParserException {
        boolean verbose = false;
        String expression = null;
        if (args.length == 0) {
            usage();
        } else {
            expression = args[0];
            verbose = args.length > 1 && args[1].toLowerCase().startsWith("-v");
        }

        DemoParser parser = new Demo().new DemoParser();

        FunctionToolbox.register(parser);

        Value value = parser.eval(expression);

        System.out.println();
        System.out.println("EVALUATING " + expression);

        if (verbose) {
            System.out.println();

            // Tokenize
            List<Token> tokens = parser.getInfix();
            System.out.println("INPUT (INFIX)");
            for (Token token : tokens) {
                System.out.println(token);
            }
            System.out.println();
            
            // Convert infix (left to right) to RPN
            List<Token> rpn = parser.getPostfix();
            System.out.println("RPN (POSTFIX)");
            for (Token token : rpn) {
                System.out.println(token);
            }
            System.out.println();        
        }

        System.out.print("RESULT: ");

        if (value != null) {
            switch (value.getType()) {
                case BOOLEAN:
                    System.out.println(value.asBoolean() + " [boolean]");
                    break;
                    
                case DATE:
                    System.out.println(value.asDate() + " [date]");
                    break;
                    
                case STRING:
                    System.out.println(value.asString() + " [string]");
                    break;
                    
                case NUMBER:
                    System.out.println(value.asNumber() + " [number]");
                    break;
                    
                case OBJECT:
                    System.out.println(value.asObject() + " [object]");
                    break;
                    
                case UNDEFINED:
                    System.out.println("undefined");
                    break;
            } 
        } else {
            System.out.println("null");
        }
    }

}
