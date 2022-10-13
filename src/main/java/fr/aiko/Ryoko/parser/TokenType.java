package fr.aiko.Ryoko.parser;// Create enum token type

public enum TokenType {
    FUNC("func"),
    RETURN("return"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    FOR("for"),
    PRINT("System.print"),
    INT("int"),
    FLOAT("float"),
    STRING("string"),
    BOOL("bool"),
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
    EQUALS("="),
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    COMMA(","),
    COLON(":"),
    LT("<"),
    GT(">"),
    AMP("&"),
    BAR("|"),
    POINT("."),
    POW("^"),
    TILDE("~"),
    QUESTION("?"),
    EXCLAMATION("!"),
    SEMICOLON(";"),
    IDENTIFIER("identifier"),
    FINAL("final"),
    EOF("EOF");

    private final String name;

    TokenType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}