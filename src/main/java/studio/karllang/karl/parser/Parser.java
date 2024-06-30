package studio.karllang.karl.parser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import studio.karllang.karl.errors.RuntimeError.NumberError;
import studio.karllang.karl.errors.RuntimeError.RuntimeError;
import studio.karllang.karl.errors.SyntaxError.SemiColonError;
import studio.karllang.karl.errors.SyntaxError.SyntaxError;
import studio.karllang.karl.modules.*;
import studio.karllang.karl.parser.ast.expressions.*;
import studio.karllang.karl.parser.ast.statements.*;

public final class Parser {
  private final File file;
  private final Path basePath;
  private final String fileName;
  private final int size;
  private final ArrayList<Token> tokens;
  private final ArrayList<Statement> statements = new ArrayList<>();
  private int pos;

  public Parser(ArrayList<Token> tokens, File file) {
    this.tokens = tokens;
    this.file = file;
    this.pos = 0;
    this.size = tokens.size();
    this.basePath = this.file.getPath().getParent();
    this.fileName = this.file.getName();
  }

  /**
   * Parse tokens
   *
   * @return ArrayList of statements
   */
  public ArrayList<Statement> parse() {
    while (pos < size - 1 && !checkType(0, TokenType.EOF)) {
      Statement statement = getStatement();
      if (statement != null) {
        statements.add(statement);
      }
    }
    return statements;
  }

  /**
   * Get statement
   *
   * @return Statement
   */
  private Statement getStatement() {
    if (checkType(0, TokenType.IDENTIFIER) && checkType(1, TokenType.EQUAL)) {
      return variableAssignment();
    } else if (match(TokenType.IF)) {
      return ifElse();
    } else if (checkType(0, TokenType.IDENTIFIER)
        && (checkType(1, TokenType.PLUSPLUS) || checkType(1, TokenType.MINUSMINUS))) {
      return incrementDecrement();
    } else if (match(TokenType.IDENTIFIER)
        && (match(TokenType.LEFT_PARENTHESIS) || match(TokenType.COLON))) {
      return funcCall();
    } else if (match(TokenType.USE)) {
      return use();
    } else return declareOrExport();
  }

  /**
   * Parse declare or export statement
   *
   * @return The statement
   */
  private Statement declareOrExport() {
    Token token = get(0);
    if (!match(TokenType.DECLARE) && !match(TokenType.EXPORT)) {
      new SyntaxError(
          "Unexpected token: " + token.getValue(),
          file.getStringPath(),
          token.getLine(),
          token.getPosition());
    }

    boolean isDeclaration = token.getType() == TokenType.DECLARE;

    if (match(TokenType.FUNC)) {
      return funcDeclaration(isDeclaration);
    } else if (match(TokenType.FINAL) && Types.contains(getType()) && getType() != TokenType.NULL) {
      return variableDeclaration(true, isDeclaration);
    } else if (Types.contains(getType()) && getType() != TokenType.NULL) {
      return variableDeclaration(false, isDeclaration);
    } else {
      new SyntaxError(
          "Unexpected token: " + get(0).getValue(),
          file.getStringPath(),
          get(0).getLine(),
          get(0).getPosition());
      return null;
    }
  }

  /**
   * Parse use statement
   *
   * @return The statement
   */
  private Statement use() {
    if (match(TokenType.STR_VALUE)) {
      Token token = get(-1);
      Expression fileName = new ValueExpression(token.getValue(), token.getType());
      skip(TokenType.AS);
      skip(TokenType.IDENTIFIER);
      Token asIdentifier = get(-1);
      skip(TokenType.SEMICOLON);
      return new UseStatement(
          fileName, this.basePath, asIdentifier, false, get(0).getLine(), get(0).getPosition());

    } else if (match(TokenType.IDENTIFIER)) {
      return new UseStatement(
          getLibraryCall(), this.basePath, null, true, get(0).getLine(), get(0).getPosition());
    } else {
      new SyntaxError(
          "Unexpected token: " + get(0).getValue(),
          file.getStringPath(),
          get(0).getLine(),
          get(0).getPosition());
      return null;
    }
  }

  /**
   * Parse library call
   *
   * @return The lib call expression
   */
  private Expression getLibraryCall() {
    Token classImport = get(-1);
    LibCallExpression expression =
        new LibCallExpression(
            classImport.getValue(), file, classImport.getLine(), classImport.getPosition());
    ArrayList<String> children = new ArrayList<>();

    while (match(TokenType.COLON)) {
      skip(TokenType.COLON);
      skip(TokenType.IDENTIFIER);
      children.add(get(-1).getValue());
    }

    LibCallExpression current = expression;
    for (String child : children) {
      LibCallExpression childExpression =
          new LibCallExpression(child, file, classImport.getLine(), classImport.getPosition());
      current.addChild(childExpression);
      current = childExpression;
    }

    skip(TokenType.SEMICOLON);

    return expression;
  }

  /**
   * Parse function call
   *
   * @return The function call statement
   */
  private Statement funcCall() {
    if (get(-1).getType() == TokenType.LEFT_PARENTHESIS) {
      String name = get(-2).getValue();
      ArrayList<Expression> args = getFuncArgs(true);
      return new FuncCallStatement(
          new FuncCallExpression(
              name, args, false, null, file, get(-2).getLine(), get(-2).getPosition()),
          get(-2).getLine(),
          get(-2).getPosition());
    } else {
      // io:Show("Hello World");
      String libName = get(-2).getValue();
      skip(TokenType.IDENTIFIER);
      String name = get(-1).getValue();

      if (match(TokenType.LEFT_PARENTHESIS)) {
        ArrayList<Expression> args = getFuncArgs(true);
        return new FuncCallStatement(
                new FuncCallExpression(
                        name, args, true, libName, file, get(-2).getLine(), get(-2).getPosition()),
                get(-2).getLine(),
                get(-2).getPosition());
      } else {
        do {
          libName = get(-1).getValue();
          skip(TokenType.COLON);
          skip(TokenType.IDENTIFIER);
          if (match(TokenType.LEFT_PARENTHESIS)) {
            break;
          }

        } while (match(TokenType.COLON));

        name = get(-2).getValue();
        ArrayList<Expression> args = getFuncArgs(true);
        return new FuncCallStatement(
                new FuncCallExpression(
                        name, args, true, libName, file, get(-2).getLine(), get(-2).getPosition()),
                get(-2).getLine(),
                get(-2).getPosition());
      }
    }
  }

  /**
   * Parse function arguments
   *
   * @return The function arguments
   */
  private ArrayList<Expression> getFuncArgs(boolean skipSemiColon) {
    ArrayList<Expression> args = new ArrayList<>();
    while (!match(TokenType.RIGHT_PARENTHESIS) && pos < size - 1 && !checkType(0, TokenType.EOF)) {
      if (match(TokenType.COMMA)) continue;
      if (getType() == TokenType.SEMICOLON) break;
      Expression expression = getExpression();
      args.add(expression);
    }
    if (skipSemiColon) skip(TokenType.SEMICOLON);
    return args;
  }

  /**
   * Parse function declaration
   *
   * @param isDeclaration Is the function a declaration or an export
   * @return The function declaration statement
   */
  private Statement funcDeclaration(boolean isDeclaration) {
    String name = get(0).getValue();
    int line = get(0).getLine();
    int pos = get(0).getPosition();
    skip(TokenType.IDENTIFIER);
    skip(TokenType.COLON);
    skip(TokenType.COLON);
    skip(TokenType.LEFT_PARENTHESIS);
    LinkedHashMap<String, TokenType> args = new LinkedHashMap<>();
    while (!match(TokenType.RIGHT_PARENTHESIS) && !checkType(0, TokenType.EOF)) {
      if (match(TokenType.STRING)
          || match(TokenType.INT)
          || match(TokenType.BOOL)
          || match(TokenType.FLOAT)
          || match(TokenType.CHAR)) {
        TokenType type = get(-1).getType();
        skip(TokenType.COLON);
        skip(TokenType.IDENTIFIER);
        String paramName = get(-1).getValue();
        if (args.containsKey(paramName)) {
          new RuntimeError(
              "Parameter " + paramName + " already exists",
              file.getStringPath(),
              get(-1).getLine(),
              get(-1).getPosition());
        }
        args.put(paramName, type);
      } else {
        new SyntaxError(
            "Unexpected token " + get(0).getValue(),
            file.getStringPath(),
            get(0).getLine(),
            get(0).getPosition());
      }
      match(TokenType.COMMA);
    }
    skip(TokenType.COLON);
    if (!Types.isType(getType()) && !checkType(0, TokenType.VOID)) {
      new SyntaxError(
          "Unexpected return type " + get(0).getValue(),
          file.getStringPath(),
          get(0).getLine(),
          get(0).getPosition());
    }
    TokenType returnType = getType();
    match(returnType);
    BlockStatement block = getBlock();

    if (ForbiddenNames.isForbiddenName(name)) {
      new RuntimeError("Function name " + name + " is forbidden", file.getStringPath(), line, pos);
    }

    if (this.file.getFunctionManager().isFunction(name)
        || this.file.getVariableManager().containsVariable(name)) {
      new RuntimeError(
          "Function/Variable " + name + " already exists", file.getStringPath(), line, pos);
    }

    if (block.getStatements().stream().anyMatch(a -> a instanceof FunctionDeclarationStatement)) {
      new RuntimeError(
          "Function declaration inside function is not allowed", file.getStringPath(), line, pos);
    }

    this.file
        .getFunctionManager()
        .addFunction(new Function(name, args, returnType, block), isDeclaration);

    return new FunctionDeclarationStatement(name, args, returnType, block, file, line, pos);
  }

  /**
   * Parse increment or decrement statement
   *
   * @return The increment or decrement statement
   */
  private Statement incrementDecrement() {
    Token nameToken = get(0);
    match(TokenType.IDENTIFIER);
    String name = nameToken.getValue();

    Token operator = get(0);
    match(getType());
    if (operator.getType() == TokenType.PLUSPLUS) {
      skip(TokenType.SEMICOLON);
      return new IncrementDecrementStatement(
          name, TokenType.PLUS, file, nameToken.getLine(), nameToken.getPosition());
    } else {
      skip(TokenType.SEMICOLON);
      return new IncrementDecrementStatement(
          name, TokenType.MINUS, file, nameToken.getLine(), nameToken.getPosition());
    }
  }

  /**
   * Parse block statement
   *
   * @return The block statement
   */
  private BlockStatement getBlock() {
    skip(TokenType.MINUS);
    skip(TokenType.GREATER);
    ArrayList<Statement> statements = new ArrayList<>();
    skip(TokenType.LEFT_BRACE);
    while (!checkType(0, TokenType.RIGHT_BRACE) && !checkType(0, TokenType.EOF) && pos < size - 1) {
      if (match(TokenType.RETURN)) {
        int line = get(-1).getLine();
        int pos = get(-1).getPosition();
        Expression expr = getExpression();
        skip(TokenType.SEMICOLON);
        statements.add(new ReturnStatement(expr, line, pos));
      } else {
        Statement statement = getStatement();
        if (statement != null) {
          statements.add(statement);
        }
      }
    }
    if (!match(TokenType.RIGHT_BRACE))
      new RuntimeError("Missing }", file.getStringPath(), get(-1).getLine(), get(-1).getPosition());
    return new BlockStatement(statements, file, get(-1).getLine(), get(-1).getPosition());
  }

  /**
   * Parse expression
   *
   * @return The expression
   */
  private Expression getExpression() {
    Token token = get(0);
    Expression expression = null;
    if ((getType() == TokenType.IDENTIFIER && checkType(1, TokenType.LEFT_PARENTHESIS))
        || (getType() == TokenType.IDENTIFIER)
        || (Types.isValueType(getType()))
        || (getType() == TokenType.EXCLAMATION)) {
      expression = getValue();
    } else if (match(TokenType.LEFT_PARENTHESIS)) {
      expression = getExpression();
      skip(TokenType.RIGHT_PARENTHESIS);
    } else
      new RuntimeError(
          "Unknown expression : " + token.getValue(),
          file.getStringPath(),
          token.getLine(),
          token.getPosition());

    // Binary operations
    if (Operators.isOperator(getType())) {
      while (Operators.isOperator(getType())) {
        TokenType operator = getType();
        skip(operator);
        int position = pos;
        Expression right = getExpression();
        if (!(right instanceof ValueExpression) && !(right instanceof BinaryExpression)) {
          pos = position;
          right = getValue();
        }
        expression =
            new BinaryExpression(
                expression, right, operator, file, token.getLine(), token.getPosition());
      }
    }

    // Logical operations
    if (LogicalOperators.isOperator(getType())) {
      while (LogicalOperators.isOperator(getType())) {
        TokenType operator = getType();
        skip(operator);
        Expression right;
        if (operator == TokenType.AND || operator == TokenType.OR) {
          right = getExpression();
        } else {
          right = getValue();
        }
        expression =
            new LogicalExpression(
                operator, expression, right, file, token.getLine(), token.getPosition());
      }
    }

    return expression;
  }

  /**
   * Parse value
   *
   * @return The value
   */
  private Expression getValue() {
    if (getType() == TokenType.IDENTIFIER && get(1).getType() == TokenType.LEFT_PARENTHESIS) {
      Token nameToken = get(0);
      String name = nameToken.getValue();
      match(TokenType.IDENTIFIER);
      match(TokenType.LEFT_PARENTHESIS);
      ArrayList<Expression> args = new ArrayList<>();
      while (!match(TokenType.RIGHT_PARENTHESIS)
          && pos < size - 1
          && !checkType(0, TokenType.EOF)) {
        args.add(getExpression());
        match(TokenType.COMMA);
      }

      return new FuncCallExpression(
          name, args, false, null, file, nameToken.getLine(), nameToken.getPosition());
    } else if (match(TokenType.EXCLAMATION)) {
      if (getType() != TokenType.IDENTIFIER
          && getType() != TokenType.BOOL_VALUE
          && getType() != TokenType.LEFT_PARENTHESIS)
        new RuntimeError(
            "Unexpected token " + get(-1).getValue(),
            file.getStringPath(),
            get(-1).getLine(),
            get(-1).getPosition());

      Expression expr;
      if (match(TokenType.LEFT_PARENTHESIS)) {
        expr = getExpression();
        skip(TokenType.RIGHT_PARENTHESIS);
      } else expr = getValue();

      return new UnaryExpression(
          TokenType.EXCLAMATION, expr, file, get(-1).getLine(), get(-1).getPosition());
    } else if (match(TokenType.STR_VALUE)
        || match(TokenType.INT_VALUE)
        || match(TokenType.BOOL_VALUE)
        || match(TokenType.FLOAT_VALUE)
        || match(TokenType.CHAR_VALUE)
        || match(TokenType.NULL)) {
      Token token = get(-1);
      return switch (token.getType()) {
        case STR_VALUE -> new ValueExpression(token.getValue(), token.getType());
        case INT_VALUE -> {
          try {
            yield new ValueExpression(Integer.parseInt(token.getValue()), token.getType());
          } catch (NumberFormatException e) {
            new NumberError(
                "Invalid number: " + token.getValue(),
                file.getStringPath(),
                token.getLine(),
                token.getPosition());
            yield null;
          }
        }
        case BOOL_VALUE -> new ValueExpression(
            Boolean.parseBoolean(token.getValue()), token.getType());
        case FLOAT_VALUE -> {
          try {
            yield new ValueExpression(Float.parseFloat(token.getValue()), token.getType());
          } catch (NumberFormatException e) {
            new NumberError(
                "Invalid number: " + token.getValue(),
                file.getStringPath(),
                token.getLine(),
                token.getPosition());
            yield null;
          }
        }
        case CHAR_VALUE -> new ValueExpression(token.getValue().charAt(0), token.getType());
        case NULL -> new ValueExpression((String) null, token.getType());
        default -> null;
      };
    } else if (match(TokenType.IDENTIFIER)) {
      if (match(TokenType.COLON)) { // If lib function call
        String libName = get(-2).getValue();
        skip(TokenType.IDENTIFIER);
        String name = get(-1).getValue();
        skip(TokenType.LEFT_PARENTHESIS);
        ArrayList<Expression> args = getFuncArgs(false);
        return new FuncCallExpression(
            name, args, true, libName, file, get(-2).getLine(), get(-2).getPosition());
      } else {
        return new VariableCallExpression(
                get(-1).getValue(), file, get(0).getLine(), get(0).getPosition());
      }
    } else {
      new RuntimeError(
          "Unknown expression : " + get(-1).getValue(),
          file.getStringPath(),
          get(-1).getLine(),
          get(-1).getPosition());
      return null;
    }
  }

  /**
   * Parse if else statement
   *
   * @return The if else statement
   */
  private Statement ifElse() {
    int line = get(0).getLine();
    int pos = get(0).getPosition();
    skip(TokenType.LEFT_PARENTHESIS);
    Expression condition = getExpression();
    skip(TokenType.RIGHT_PARENTHESIS);
    BlockStatement ifBlock = getBlock();

    if (match(TokenType.ELSE)) {
      if (match(TokenType.IF)) {
        return new IfElseStatement(condition, ifBlock, ifElse(), line, pos);
      } else {
        BlockStatement elseBlock = getBlock();
        return new IfElseStatement(condition, ifBlock, elseBlock, line, pos);
      }
    } else return new IfElseStatement(condition, ifBlock, null, line, pos);
  }

  /**
   * Parse variable assignment
   *
   * @return The variable assignment statement
   */
  private Statement variableAssignment() {
    String name = get(0).getValue();
    match(TokenType.IDENTIFIER);
    skip(TokenType.EQUAL);

    Expression expr = getExpression();

    if (expr == null) {
      new RuntimeError(
          "Unknown expression : " + get(0).getValue(),
          file.getStringPath(),
          get(0).getLine(),
          get(0).getPosition());
    }

    skip(TokenType.SEMICOLON);

    assert expr != null;
    return new VariableAssignmentStatement(
        name, expr, file, get(0).getLine(), get(0).getPosition());
  }

  /**
   * Parse variable declaration
   *
   * @param isFinal Is the variable final
   * @param isDeclaration Is the variable a declaration or an export
   * @return The variable declaration statement
   */
  private Statement variableDeclaration(boolean isFinal, boolean isDeclaration) {
    Token type = get(0);
    match(type.getType());
    skip(TokenType.COLON);
    Token name = get(0);

    skip(TokenType.IDENTIFIER);
    skip(TokenType.EQUAL);
    Expression expression = getExpression();
    skip(TokenType.SEMICOLON);

    if (expression == null) {
      new RuntimeError(
          "Expected expression after " + name.getValue(),
          file.getStringPath(),
          name.getLine(),
          get(0).getPosition());
    }

    return new VariableDeclarationStatement(
        expression,
        name.getValue(),
        type,
        file,
        name.getLine(),
        name.getPosition(),
        isFinal,
        isDeclaration);
  }

  /**
   * Skip token
   *
   * @param type Token type
   */
  private void skip(TokenType type) {
    if (get(0).getType() != type) {
      if (type == TokenType.SEMICOLON) {
        new SemiColonError(file.getStringPath(), get(-1).getLine(), get(-1).getPosition());
      } else
        new SyntaxError(
            "Excepted "
                + Types.getTypeName(type)
                + " but got "
                + Types.getTypeName(get(0).getType()),
            file.getStringPath(),
            get(0).getLine(),
            get(0).getPosition());
    }
    pos++;
  }

  /**
   * Get token at relative position
   *
   * @param relativePosition Relative position
   * @return Token at relative position
   */
  private Token get(int relativePosition) {
    int newPos = pos + relativePosition;
    if (newPos >= size || newPos < 0) {
      return new Token(TokenType.EOF, "", -1, -1);
    }
    return tokens.get(pos + relativePosition);
  }

  /**
   * Get token type
   *
   * @return Token type
   */
  private TokenType getType() {
    return get(0).getType();
  }

  /**
   * Check token type at relative position
   *
   * @param pos Relative position
   * @param type Token type
   * @return True if token type matches
   */
  private boolean checkType(int pos, TokenType type) {
    return get(pos).getType() == type;
  }

  /**
   * Match token type
   *
   * @param type Token type
   * @return True if token type matches
   */
  private boolean match(TokenType type) {
    if (checkType(0, type)) {
      pos++;
      return true;
    }
    return false;
  }
}
