package studio.karllang.karl.lib;

import java.util.ArrayList;

import studio.karllang.karl.modules.File;
import studio.karllang.karl.parser.ast.expressions.Expression;
import studio.karllang.karl.parser.ast.values.Value;

/**
 * Represents an abstract function within a library in a programming environment.
 * Functions are executable units of code that can take input expressions and produce output.
 */
public abstract class Function {
  private final String name;   // The name of the function.
  private final Library library; // The library to which this function belongs.
  private Value returnValue = null; // The return value of the function.

  /**
   * Constructs a new Function object with the specified name and associated library.
   *
   * @param name The name of the function.
   * @param library The library to which the function belongs.
   */
  public Function(String name, Library library) {
    this.name = name;
    this.library = library;
  }

  /**
   * Executes the function with the given input expressions.
   *
   * @param expressions An ArrayList of expressions representing the input arguments to the function.
   */
  public abstract void eval(ArrayList<Expression> expressions, File file, int line, int pos);

  /**
   * Retrieves the name of the function.
   *
   * @return The name of the function.
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the library to which this function belongs.
   *
   * @return The Library object representing the library to which the function belongs.
   */
  public Library getLibrary() {
    return library;
  }

  /**
   * Retrieves the return value of the function.
   *
   * @return The return value of the function.
   */
  public Value getReturnValue() {
    return returnValue;
  }

  /**
   * Sets the return value of the function.
   *
   * @param returnValue The return value of the function.
   */
  public void setReturnValue(Value returnValue) {
    this.returnValue = returnValue;
  }
}
