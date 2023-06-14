package studio.karllang.karl.parser.ast.statements;

import studio.karllang.karl.errors.RuntimeError.RuntimeError;
import studio.karllang.karl.parser.Token;
import studio.karllang.karl.parser.TokenType;
import studio.karllang.karl.parser.ast.expressions.Expression;
import studio.karllang.karl.parser.ast.expressions.ValueExpression;
import studio.karllang.karl.parser.ast.expressions.VariableExpression;
import studio.karllang.karl.parser.ast.values.Value;
import studio.karllang.karl.std.File;
import studio.karllang.karl.std.ForbiddenNames;
import studio.karllang.karl.std.Types;

public class VariableDeclarationStatement extends Statement {
    private final String name;
    private final Token type;
    private final File file;
    private final String fileName;
    private final int line;
    private final int pos;
    private Expression expression;
    private final boolean isFinal;

    public VariableDeclarationStatement(Expression expression, String name, Token type, File file, int line, int pos, boolean isFinal) {
        this.expression = expression;
        this.name = name;
        this.type = type;
        this.file = file;
        this.fileName = file.getName();
        this.line = line;
        this.pos = pos;
        this.isFinal = isFinal;
    }

    @Override
    public void eval() {
        if (ForbiddenNames.isForbiddenName(name)) {
            new RuntimeError("Variable name " + name + " is forbidden", file.getStringPath(), line, pos);
        }

        if (this.file.getVariableManager().getVariable(name) != null) {
            new RuntimeError("Variable " + name + " is already declared", file.getStringPath(), line, pos);
        }

        Value value = expression.eval();
        if (type.getType() == TokenType.FLOAT && value.getType() == TokenType.INT_VALUE) {
            expression = new ValueExpression(value.toFloat(), TokenType.FLOAT_VALUE);
            value = expression.eval();
        }

        if (value.toString().equals("null_void")) {
            new RuntimeError("Cannot assign void function to a variable", file.getStringPath(), line, pos);
        }

        if (!Types.checkValueType(type.getType(), value.getType()) && value.getType() != TokenType.NULL) {
            new RuntimeError("Expected type " + Types.getTypeName(type.getType()) + " but got " + Types.getTypeName(value.getType()), file.getStringPath(), line, pos - 1);
        }

        if (value.getType() == TokenType.NULL && type.getType() != TokenType.STRING && type.getType() != TokenType.CHAR) {
            new RuntimeError(Types.getTypeName(type.getType()) + " variable cannot be null", file.getStringPath(), line, pos - 1);
        }

        VariableExpression expr = new VariableExpression(name, value, isFinal, file);
        expr.setValue(value);
        expr.eval();
    }
}
