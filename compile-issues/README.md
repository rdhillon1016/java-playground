# Imports

You don't necessarily need an import statement to use external classes. The `import` statement is syntactic sugar that basically tells the compiler "everytime you see class `X` in this source file, it actually expands to this longer name defined in the `import`.

These 2 files demonstrate that. Since `Something` is already at the root of the classpath, the compiler finds it and no import or nested class location was necessary.

# Compilation

Note that if you:
1. Compile `Something.java` into `Something.class`
2. Delete `Something.java`
3. Compile `Main.java`

It will still work, since the compiler has no problem referencing and verifying classes/methods from already compiled `.java` files (`.class` files). This is why the compiler works when using external library JARs on the classpath, rather than the external libraries' source files themselves.

This is described in [https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html#searching-for-module-package-and-type-declarations](https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html#searching-for-module-package-and-type-declarations). "`javac` needs type information for every class or interface used, extended, or implemented in the source file." A successful search for some used class may produce a class file, a source file, or both. If both are found, then you can use the `-Xprefer` option to instruct the compiler which to use. If a search finds and uses a source file, then by default `javac` compiles that source file. `javac` gathers just enough information to compile the requisite source.

For example, consider a `Something.java` file that looks like this (assume `Wow` exists):
```java
public class Something {
    public static Wow wow;
    public static int something() {
        wow = new Wow();
        return 0;
    }
}
```
Say that you had already compiled `Something.java`. Then, you deleted the `Wow.java`, `Wow.class`, and `Something.java` files so that only `Something.class` remains. The compiler will not look for `Wow` when you run `javac Main.java`. It only cares that `something` returns an `int` as required in `Main.java`.

