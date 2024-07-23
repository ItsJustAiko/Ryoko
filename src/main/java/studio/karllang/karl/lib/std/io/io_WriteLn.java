package studio.karllang.karl.lib.std.io;

import studio.karllang.karl.lib.Function;
import studio.karllang.karl.modules.File;
import studio.karllang.karl.parser.ast.expressions.Expression;
import studio.karllang.karl.parser.ast.values.Value;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents the 'WriteLn' function within the I/O library (Io). The 'WriteLn' function is
 * responsible for writing the string representations of given expressions to the standard output
 * followed by a newline. It extends the 'Function' class.
 */
public class io_WriteLn extends Function {
  /**
   * Constructs a new 'io_WriteLn' object representing the 'WriteLn' function within the I/O
   * library.
   *
   * @param io The 'Io' library to which this function belongs.
   */
  public io_WriteLn(Io io) {
    super("WriteLn", io);
  }

  /**
   * Executes the 'WriteLn' function with the given input expressions. The function evaluates the
   * expressions, converts them to strings, and prints the result to the standard output followed by
   * a newline.
   *
   * @param expressions An ArrayList of expressions representing the input arguments to the
   *     function.
   */
  @Override
  public void eval(ArrayList<Expression> expressions, File file, int line, int pos) {
    // Evaluate the expressions, convert them to strings, and join them together.
    String result =
        expressions.stream()
            .map(Expression::eval)
            .map(Value::toString)
            .collect(Collectors.joining());

    // Print the result to the standard output followed by a newline.
    System.out.println(result);
  }
}
