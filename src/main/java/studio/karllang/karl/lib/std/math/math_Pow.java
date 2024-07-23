package studio.karllang.karl.lib.std.math;

import studio.karllang.karl.errors.RuntimeError.RuntimeError;
import studio.karllang.karl.lib.Function;
import studio.karllang.karl.modules.File;
import studio.karllang.karl.parser.ast.expressions.Expression;
import studio.karllang.karl.parser.ast.values.FloatValue;

import java.util.ArrayList;

public class math_Pow extends Function {

  public math_Pow(Math math) {
    super("Pow", math);
  }
  @Override
  public void eval(ArrayList<Expression> expressions, File file, int line, int pos) {
    // If the number of arguments is not equal to 2, throw an error.
    if (expressions.size() != 2) {
      new RuntimeError("Error: Pow function expects 2 arguments.", file.getStringPath(), line, pos);
    }
    // Get the base and exponent values from the expressions.
    float base = expressions.get(0).eval().toFloat();
    float exponent = expressions.get(1).eval().toFloat();

    // Calculate the result of raising the base to the exponent.
    float result = (float) java.lang.Math.pow(base, exponent);

    this.setReturnValue(new FloatValue(result));
  }
}
