package studio.karllang.karl.lib;

import java.util.ArrayList;
import java.util.HashMap;
import studio.karllang.karl.modules.File;
import studio.karllang.karl.parser.ast.expressions.Expression;

/**
 * Represents an abstract library in a programming environment.
 * Libraries are containers for functions and sub-libraries.
 */
public abstract class Library {
  private final String name; // The name of the library.
  private final ArrayList<Library> subLibraries = new ArrayList<>(); // Sub-libraries contained within this library.
  private final HashMap<String, Function> functions = new HashMap<>(); // Functions defined within this library.
  private final HashMap<String, Library> loadedSubLibraries = new HashMap<>(); // Sub-libraries loaded into this library.

  /**
   * Constructs a new Library object with the specified name.
   *
   * @param name The name of the library.
   */
  public Library(String name) {
    this.name = name;
  }

  /**
   * Retrieves the functions defined within this library.
   *
   * @return A HashMap containing function names as keys and corresponding Function objects as values.
   */
  public HashMap<String, Function> getFunctions() {
    return functions;
  }

  /**
   * Loads a sub-library into this library.
   *
   * @param name The name of the sub-library to load.
   * @param file The file representing the sub-library.
   * @param line The line number in the source code where the loading occurs.
   * @param pos The position within the line where the loading occurs.
   */
  public abstract void loadSubLibrary(String name, File file, int line, int pos);

  /**
   * Adds a sub-library to this library.
   *
   * @param library The sub-library to add.
   */
  public void addSubLibrary(Library library) {
    subLibraries.add(library);
  }

  /**
   * Retrieves a specific function from this library by name.
   *
   * @param name The name of the function to retrieve.
   * @return The Function object corresponding to the given function name.
   */
  public abstract Function getFunction(String name);

  /**
   * Retrieves the sub-libraries loaded into this library.
   *
   * @return A HashMap containing sub-library names as keys and corresponding Library objects as values.
   */
  public HashMap<String, Library> getLoadedSubLibraries() {
    return loadedSubLibraries;
  }

  /**
   * Retrieves the sub-libraries contained within this library.
   *
   * @return An ArrayList of Library objects representing sub-libraries.
   */
  public ArrayList<Library> getSubLibraries() {
    return subLibraries;
  }

  /**
   * Retrieves the name of this library.
   *
   * @return The name of the library.
   */
  public String getName() {
    return name;
  }
}
