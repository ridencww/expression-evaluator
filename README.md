# expression-evaluator

An extensible general purpose expression evaluator in Java.

## Features

 - Lightweight and fast expression parser
 - Designed around an extensible base Parser object
 - New functions can easily be added
 - Compiled expressions are cached for performance
 - Result types of STRING, NUMBER, DATE, and BOOLEAN
 - Parameter type validation 
 - Variables
 - Single call to eval() can contain multiple expressions
 - System properties, environment variables, and data sets
 - Supplied with a set of functions (FunctionToolbox) to extend basic expression parsing 

## Requirements

This project uses Java 1.7+ and requires no additional libraries other than the ones provided by the JDK/JRE.

## Installation

### From source

Clone the repository from Github:

    git://github.com/ridencww/expression-evaluator.git

Build the project and create the JAR:

##### Using Maven
    mvn clean install

### Maven

If you're using Maven for your project, add the following to your project's pom.xml:

    <dependency>
      <groupId>com.creativewidgetworks</groupId>
      <artifactId>expression-evaluator</artifactId>
      <version>2.1.0</version>
    </dependency>

## Data types

    STRING     "" and '' styles
    NUMBER     Number of decimal places configurable
    BOOLEAN
    DATE

## Misc    
    
Although seldom required, comments are supported in expressions. They may be useful to document the purpose of a particular function.
    
    // This is a comment 
    /* So is this */
    
A string to be parsed can contain multiple expressions. In the example that follows, two variables, A and B, are initialized and used to return the final result. Variables A and B will survive past the current parser instance evaluation unless cleared explicitly. 
   
    "A=123;B=456;A+B"   =  579
    "A*2"               =  246
     
     parser.clearVariable("A")
     "A"                = 0
     "B"                = 456
     
     parser.clearVariables()
     "A"                = 0
     "B"                = 0
    
## Basic operations supplied by Parser 

    Assignment  =
    Operators   + - * / DIV MOD % ^ 
    Logical     < <= == != >= > AND OR NOT
    Ternary     ? :  
    Shift       << >>
    Property    ${<id>}
    DataSource  @<id>
    Constants   NULL PI
    Functions   CLEARGLOBAL, CLEARGLOBALS, DIM, GETGLOBAL, SETGLOBAL
                NOW PRECISION
   
## Optional functions supplied by FunctionToolbox that can be bound to Parser  
 
    Functions   ABS ARCCOS ARCSIN ARCTAN ARRAYLEN AVERAGE CEILING CONTAINS CONTAINSALL CONTAINSANY
                COS DATEADD DATEBETWEEN DATEBOD, DATEEOD DATEFORMAT DATEWITHIN ENDSWITH EXP FACTORIAL 
                FIND FLOOR FORMAT GUID HEX ISANYOF ISBLANK ISBOOLEAN ISDATE ISNONEOF ISNULL ISNUMBER 
                LEFT LEFTOF LEN LOG LOG10 LOWER MAKEBOOLEAN MAKEDATE MATCH MATCHBYLEN MAX MID MIN NAMECASE 
                RANDOM  REPLACE REPLACEALL REPLACEFIRST RIGHT RIGHTOF SIN SPLIT SQR SQRT STARTSWITH STR 
                STRING TAN TRIM TRIMLEFT TRIMRIGHT UPPER VAL
   
## Usage

The jar contains a small console program that exercises the parser and FunctionToolbox as well as displaying the tokens and RPN stream.

Examples:

    java -cp expression-parser.jar Demo "(1+4)/3"
        EVALUATING (1+4)/3
        RESULT: 1.66667 [number]
      
    java -cp expression-parser.jar Demo "(1+4)/3" -verbose
        EVALUATING (1+4)/3
    
        INPUT (INFIX)
        OPERATOR....... (
        NUMBER......... 1
        OPERATOR....... +
        NUMBER......... 4
        OPERATOR....... )
        OPERATOR....... /
        NUMBER......... 3
           
        RPN (POSTFIX)
        NUMBER......... 1
        NUMBER......... 4
        OPERATOR....... +
        NUMBER......... 3
        OPERATOR....... /
        
        RESULT: 1.66667 [number]
        
    java -cp expression-parser.jar Demo "upper('AbC' + 'def')"
        EVALUATING upper('AbC' + 'def')
        RESULT: ABCDEF [string]
    
## Version History

2.2.0
* Updated README.md
* Support for two dimensional arrays up to 10000 x 256
* Moved date functions (DATEADD, DATEFORMAT. etc.) into FunctionToolbox

2.1.0
* Updated README.md
* Moved GUID, LEFTOF, and RIGHTOF into FunctionToolbox
* Access to system properties and environment variables disabled by default
* Fixed processing of NOT
* Fixed precedence of exponentiation operator
* Improved reporting of dangling operators 
 
2.0.0  
* Structural refactor
* Provided a set of TokenType operators that most can use as-is.
* Supplied a toolbox of functions that can be optionally used by the basic Parser. Users can continue to define their own custom functions.
* Fixed shallow copy issue with token values for expressions that were cached.

1.0.0 Initial release to GitHub
    
## License

expression-parser is licensed under the [Modified BSD][1] license. Permission is granted to anyone to use this software for any purpose, including commercial applications.

[1]: http://www.opensource.org/licenses/BSD-3-Clause

