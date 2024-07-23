package studio.karllang.karl.lib.std.string;

import studio.karllang.karl.lib.Function;
import studio.karllang.karl.lib.Library;
import studio.karllang.karl.modules.File;
import studio.karllang.karl.parser.ast.expressions.Expression;

import java.util.ArrayList;
import java.util.HashMap;

public class Str extends Library {

  private final HashMap<java.lang.String, Function> functions =
          new HashMap<>();

  public Str() {
  super("str"); // Calls the constructor of the 'Library' class with the name "io".

    functions.put("Length", new string_Length(this));
}

/**
 * Retrieves the functions provided by the 'Str' library.
 *
 * @return A HashMap containing function names as keys and corresponding Function objects as
 *     values.
 */
@Override
public HashMap<java.lang.String, Function> getFunctions() {
  return functions;
}

/**
 * Retrieves a specific function from the 'Str' library by name.
 *
 * @param name The name of the function to retrieve.
 * @return The Function object corresponding to the given function name, or null if the function
 *     is not found.
 */
@Override
public Function getFunction(java.lang.String name) {
  return functions.get(name);
}

/**
 * Load sub-libraries is not implemented in the 'Str' library, so this method is empty.
 *
 * @param name The name of the sub-library to load.
 * @param file The file representing the sub-library.
 * @param line The line number in the source code where the loading occurs.
 * @param pos The position within the line where the loading occurs.
 */
  @Override
  public void loadSubLibrary(java.lang.String name, File file, int line, int pos) {
    // Not implemented for the 'Str' library.
  }
}
