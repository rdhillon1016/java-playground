# How to Run Quickly

You can use the `java` launcher to launch single-file source-code without explicitly compiling the source code first. The launcher will automatically invoke the compiler and store the bytecode in-memory. E.g:
```bash
java Something.java
```

If you want to use external/non-JDK classes, you'll have to add the location of the class to your classpath:
```bash
java -cp /path/to/commons-lang3-3.12.0.jar ReferenceNonJDKClass.java
```