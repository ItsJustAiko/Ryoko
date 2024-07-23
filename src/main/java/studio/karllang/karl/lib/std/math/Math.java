package studio.karllang.karl.lib.std.math;

import studio.karllang.karl.lib.Function;
import studio.karllang.karl.lib.Library;
import studio.karllang.karl.modules.File;
import studio.karllang.karl.parser.ast.expressions.Expression;

import java.util.ArrayList;
import java.util.HashMap;

public class Math extends Library {

  private final HashMap<String, Function> functions =
          new HashMap<>();

  public Math() {
    super("math");

    functions.put("Pow", new math_Pow(this));
  }

  public void run(java.lang.String function, ArrayList<Expression> expressions) {
    if (!functions.containsKey(function)) {
      System.out.println("Unknown function: " + function);
      return;
    }
    functions.get(function).eval(expressions, null, 0, 0);
  }

  @Override
  public void loadSubLibrary(String name, File file, int line, int pos) {
    // Not implemented
  }

  @Override
  public Function getFunction(String name) {
    return functions.get(name);
  }
}
