package studio.karllang.karl.lib.std;

import java.util.ArrayList;
import java.util.HashMap;
import studio.karllang.karl.errors.RuntimeError.RuntimeError;
import studio.karllang.karl.lib.Function;
import studio.karllang.karl.lib.Library;
import studio.karllang.karl.lib.std.io.Io;
import studio.karllang.karl.lib.std.math.Math;
import studio.karllang.karl.lib.std.string.Str;
import studio.karllang.karl.modules.File;
import studio.karllang.karl.parser.ast.expressions.Expression;

/**
 * Represents a standard library in a programming environment. The 'Std' library is a built-in
 * library that contains predefined functions and sub-libraries. It extends the 'Library' class.
 */
public class Std extends Library {
  private final HashMap<String, Function> functions =
      new HashMap<>(); // Predefined functions within the standard library.
  private final HashMap<String, Library> subLibraries =
      new HashMap<>(); // Sub-libraries contained within the standard library.
  private final HashMap<String, Library> loadedSubLibraries =
      new HashMap<>(); // Sub-libraries that have been loaded.

  /**
   * Constructs a new 'Std' object, representing the standard library. Initializes the standard
   * library with predefined sub-libraries.
   */
  public Std() {
    super("std"); // Calls the constructor of the 'Library' class with the name "std".

    // Initialize the standard library with predefined sub-libraries.
    subLibraries.put("io", new Io()); // Example: Adding the 'Io' sub-library.
    subLibraries.put("str", new Str()); // Example: Adding the 'Str' sub-library.
    subLibraries.put("math", new Math()); // Example: Adding the 'Math' sub-library.
  }

  /**
   * Loads a sub-library into the 'Std' library.
   *
   * @param name The name of the sub-library to load.
   * @param file The file representing the sub-library.
   * @param line The line number in the source code where the loading occurs.
   * @param pos The position within the line where the loading occurs.
   */
  public void loadSubLibrary(String name, File file, int line, int pos) {
    if (!subLibraries.containsKey(name)) {
      new RuntimeError("Unknown sub library: " + name, file.getStringPath(), line, pos);
      return;
    }
    loadedSubLibraries.put(name, subLibraries.get(name));
  }

  /**
   * Retrieves a specific function from the 'Std' library by name.
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
   * Retrieves the sub-libraries contained within the 'Std' library.
   *
   * @return An ArrayList of Library objects representing sub-libraries in the 'Std' library.
   */
  public ArrayList<Library> getSubLibraries() {
    return new ArrayList<>(subLibraries.values());
  }
}
