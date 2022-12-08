package fr.aiko.Karl.parser.ast.statements;

import fr.aiko.Karl.errors.RuntimeError.RuntimeError;
import fr.aiko.Karl.parser.Token;
import fr.aiko.Karl.parser.TokenType;
import fr.aiko.Karl.parser.ast.expressions.Expression;
import fr.aiko.Karl.parser.ast.expressions.ValueExpression;
import fr.aiko.Karl.parser.ast.expressions.VariableExpression;
import fr.aiko.Karl.parser.ast.values.Value;
import fr.aiko.Karl.std.ForbiddenNames;
import fr.aiko.Karl.std.Types;
import fr.aiko.Karl.std.VariableManager;

public class VariableDeclarationStatement extends Statement {
    private final String name;
    private final Token type;
    private final String fileName;
    private final int line;
    private final int pos;
    private Expression expression;

    public VariableDeclarationStatement(Expression expression, String name, Token type, String fileName, int line, int pos) {
        this.expression = expression;
        this.name = name;
        this.type = type;
        this.fileName = fileName;
        this.line = line;
        this.pos = pos;
    }

    @Override
    public void eval() {
        if (ForbiddenNames.isForbiddenName(name)) {
            new RuntimeError("Variable name " + name + " is forbidden", fileName, line, pos);
        }

        if (VariableManager.getVariable(name) != null) {
            new RuntimeError("Variable " + name + " is already declared", fileName, line, pos);
        }

        Value value = expression.eval();
        if (type.getType() == TokenType.FLOAT && value.getType() == TokenType.INT_VALUE) {
            expression = new ValueExpression(value.toFloat(), TokenType.FLOAT_VALUE);
            value = expression.eval();
        }

        if (value.toString().equals("null_void")) {
            new RuntimeError("Cannot assign void function to a variable", fileName, line, pos);
        }

        if (!Types.checkValueType(type.getType(), value.getType()) && value.getType() != TokenType.NULL) {
            new RuntimeError("Expected type " + Types.getTypeName(type.getType()) + " but got " + Types.getTypeName(value.getType()), fileName, line, pos - 1);
        }

        if (value.getType() == TokenType.NULL && type.getType() != TokenType.STRING && type.getType() != TokenType.CHAR) {
            new RuntimeError(Types.getTypeName(type.getType()) + " variable cannot be null", fileName, line, pos - 1);
        }

        VariableExpression expr = new VariableExpression(name, value);
        expr.setValue(value);
        expr.eval();
    }
}
