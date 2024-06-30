package studio.karllang.karl.lib.std.io;

import studio.karllang.karl.lib.Function;
import studio.karllang.karl.lib.Library;
import studio.karllang.karl.modules.File;
import studio.karllang.karl.parser.ast.expressions.Expression;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents an input/output (I/O) library in a programming environment. The 'Io' library provides
 * functions for handling input and output operations. It extends the 'Library' class.
 */
public class Io extends Library {
  private final HashMap<String, Function> functions =
      new HashMap<>(); // Functions provided by the 'Io' library.

  /**
   * Constructs a new 'Io' object, representing the I/O library. Initializes the 'Io' library with
   * predefined I/O functions.
   */
  public Io() {
    super("io"); // Calls the constructor of the 'Library' class with the name "io".

    // Initialize the 'Io' library with predefined I/O functions.
    functions.put("Write", new io_Write(this)); // Example: Adding the 'Write' function.
    functions.put("WriteLn", new io_WriteLn(this)); // Example: Adding the 'WriteLn' function.
    functions.put("Read", new io_Read(this)); // Example: Adding the 'Read' function.
  }

  /**
   * Retrieves the functions provided by the 'Io' library.
   *
   * @return A HashMap containing function names as keys and corresponding Function objects as
   *     values.
   */
  @Override
  public HashMap<String, Function> getFunctions() {
    return functions;
  }

  /**
   * Executes a function within the 'Io' library.
   *
   * @param function The name of the function to execute.
   * @param expressions A list of expressions as function arguments.
   */
  public void run(String function, ArrayList<Expression> expressions) {
    if (!functions.containsKey(function)) {
      System.out.println("Unknown function: " + function);
      return;
    }
    functions.get(function).eval(expressions);
  }

  /**
   * Retrieves a specific function from the 'Io' library by name.
   *
   * @param name The name of the function to retrieve.
   * @return The Function object corresponding to the given function name, or null if the function
   *     is not found.
   */
  @Override
  public Function getFunction(String name) {
    return functions.get(name);
  }

  /**
   * Load sub-libraries is not implemented in the 'Io' library, so this method is empty.
   *
   * @param name The name of the sub-library to load.
   * @param file The file representing the sub-library.
   * @param line The line number in the source code where the loading occurs.
   * @param pos The position within the line where the loading occurs.
   */
  @Override
  public void loadSubLibrary(String name, File file, int line, int pos) {
    // Not implemented for the 'Io' library.
  }
}
