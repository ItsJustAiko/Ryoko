package fr.aiko.Karl.parser.ast.expressions;

import fr.aiko.Karl.errors.RuntimeError.RuntimeError;
import fr.aiko.Karl.parser.TokenType;
import fr.aiko.Karl.parser.ast.values.FloatValue;
import fr.aiko.Karl.parser.ast.values.IntValue;
import fr.aiko.Karl.parser.ast.values.Value;

import java.util.Arrays;

public class BinaryExpression extends Expression {
    private final Expression left;
    private final Expression right;
    private final TokenType operator;
    private final String fileName;
    private final int line;
    private final int pos;

    public BinaryExpression(Expression left, Expression right, TokenType operator, String fileName, int line, int pos) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.fileName = fileName;
        this.line = line;
        this.pos = pos;
    }

    @Override
    public Value eval() {

        Value leftValue = left.eval();
        Value rightValue = right.eval();

        if (leftValue.getType() == TokenType.INT && rightValue.getType() == TokenType.INT) {
            return switch (operator) {
                case PLUS -> new IntValue(leftValue.toInt() + rightValue.toInt());
                case MINUS -> new IntValue(leftValue.toInt() - rightValue.toInt());
                case MULTIPLY -> new IntValue(leftValue.toInt() * rightValue.toInt());
                case DIVIDE -> new IntValue(leftValue.toInt() / rightValue.toInt());
                case MODULO -> new IntValue(leftValue.toInt() % rightValue.toInt());
                default -> throw new RuntimeException("Bad operator: " + operator);
            };
        } else if ((leftValue.getType() == TokenType.FLOAT || leftValue.getType() == TokenType.INT) && (rightValue.getType() == TokenType.INT || rightValue.getType() == TokenType.FLOAT)) {
            return switch (operator) {
                case PLUS -> new FloatValue(leftValue.toFloat() + rightValue.toFloat());
                case MINUS -> new FloatValue(leftValue.toFloat() - rightValue.toFloat());
                case MULTIPLY -> new FloatValue(leftValue.toFloat() * rightValue.toFloat());
                case DIVIDE -> new FloatValue(leftValue.toFloat() / rightValue.toFloat());
                case MODULO -> new FloatValue(leftValue.toFloat() % rightValue.toFloat());
                default -> throw new RuntimeException("Bad operator: " + operator);
            };
        } else if (leftValue.getType() == TokenType.STRING || rightValue.getType() == TokenType.STRING) {
            return switch (operator) {
                case PLUS -> new fr.aiko.Karl.parser.ast.values.StringValue(leftValue + rightValue.toString());
                default -> throw new RuntimeException("Bad operator: " + operator);
            };
        } else {
            throw new RuntimeException("Unauthorized type for operation " + leftValue.getType().getName());
        }
    }
}
