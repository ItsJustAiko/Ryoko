package studio.karllang.karl.std;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class FunctionManager {
    private final File file;
    private final LinkedHashMap<String, Function> functions = new LinkedHashMap<>();

    public FunctionManager(File file) {
        this.file = file;
    }

    public void addFunction(Function function) {
        functions.put(function.getName(), function);
    }

    public Function getFunction(String name) {
        return functions.get(name);
    }

    public boolean isFunction(String name) {
        return functions.containsKey(name);
    }

    public HashMap<String, Function> getFunctions() {
        return functions;
    }

    public void clear() {
        functions.clear();
    }

    public void removeFunction(String name) {
        functions.remove(name);
    }

    public File getFile() {
        return file;
    }
}
