package studio.karllang.karl.lib.std.string;

import studio.karllang.karl.lib.Function;
import studio.karllang.karl.parser.TokenType;
import studio.karllang.karl.parser.ast.expressions.Expression;
import studio.karllang.karl.parser.ast.values.IntValue;
import studio.karllang.karl.parser.ast.values.Value;

import java.util.ArrayList;

public class string_Length extends Function {
  /**
   * Constructs a new Function object with the specified name and associated library.
   *
   * @param string The library
   */
  public string_Length(Str string) {
    super("Length", string);
  }

  @Override
  public void eval(ArrayList<Expression> expressions) {
    if (expressions.size() != 1) {
      System.out.println("Error: Length function takes exactly one argument.");
    }

    Expression expression = expressions.get(0);
    Value val = expression.eval();

    if (val.getType() != TokenType.STR_VALUE) {
      System.out.println("Error: Length function takes a string as an argument.");
    }

    java.lang.String str = val.toString();
    this.setReturnValue(new IntValue(str.length()));
  }
}
