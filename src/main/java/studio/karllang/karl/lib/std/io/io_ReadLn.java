package studio.karllang.karl.lib.std.io;

import studio.karllang.karl.lib.Function;
import studio.karllang.karl.parser.ast.expressions.Expression;
import studio.karllang.karl.parser.ast.values.StringValue;

import java.util.ArrayList;
import java.util.Scanner;

public class io_ReadLn extends Function {
  /**
   * Constructs a new Function object with the specified name and associated library.
   *
   * @param name    The name of the function.
   * @param library The library to which the function belongs.
   */
  public io_ReadLn(Io io) {
    super("ReadLn", io);
  }

  @Override
  public void eval(ArrayList<Expression> expressions) {
    Scanner scanner = new java.util.Scanner(System.in);
    String input = scanner.nextLine();
    scanner.close();

    this.setReturnValue(new StringValue(input));
  }
}
