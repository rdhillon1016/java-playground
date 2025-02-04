# Data Types

- `float`: composed of significand (digits) and an exponent (base 2, tells you where the decimal is).
- `int`

When an integer overflows, it rolls over to the minimum value and begins counting up from there.

# Keywords

`final` variables are used to create constants. `final` methods prevent method overriding. `final` classes prevent inheritance.

# Exceptions

Unchecked exceptions (ones that extend `RuntimeException`) are exceptions that are not checked for by the compiler. By “checked for”, we mean the compiler doesn’t check to see if it is caught or explicitly thrown by any calling methods. `RuntimeException` is intended to be used for programmer errors. As such it should never be caught, except in rare circumstances.

# Compilation

Java classes get compiled into bytecode (`.class` files) which get run in the JRE.

JDK = JRE + Dev Tools (like a compiler, etc) = (JVM + Class libraries) + Dev Tools

JAR stands for Java ARchive. It's a file format based on the popular ZIP file format and is used for aggregating many compiled Java classes, resources, and metadata into one. Directories and `.jar`-files are added to the classpath and available to the `ClassLoader` at runtime to find particular classes inside of it. JAR = Java ARchive. A `.java`-file contains Java code. A Java file is compiled to produce a class file that can be loaded by the JVM ([source](https://stackoverflow.com/questions/60916297/difference-between-jar-file-and-java-file)). The jar also has an optiona `META-INF/MANIFEST.MF` which tells us how to use the jar file and/or specifies other jar files for loading with the jar. You can unzip Jar files using the `jar` utility on the command line.

Executable jars will usually contain a `Main-Class` header in `MANIFEST.MF` that describes which class the entrypoint (`main` method) of the application is.

JAR **sealing** is a security and consistency mechanism that ensures all classes in a specific package within a JAR file are loaded from the same JAR file. This prevents situations where classes from the same package are spread across multiple JAR files, which could lead to unpredictable behavior or security risks (prevents malicious or unintended classes from modifying behavior by injecting different implementations into the same package.).

The **compile classpath** is a list of dependencies that are required for the JDK to be able to compile Java code into `.class` files. The runtime classpath is a list of dependencies is required to actually run the compiled Java code.

# Annotations

There are 3 types of annotations:
1. **Runtime**: JVM loads annotation info at runtime, and thus can be accessed via reflection
2. **Class**: Annotation info is present in the compiled bytecode, but won’t be loaded by the JVM
3. **Source**: Annotation info is only present in the source-code, and is not present in the compiled bytecode


# Advanced

## Memory

Three types of memory:

1. Heap memory: memory within the JVM process that is used to hold Java Objects and is maintained by the JVMs Garbage Collector.
2. Native memory/Off-heap: is memory allocated within the processes address space that is not within the heap and thus is not freed up by the Java Garbage Collector.
3. Direct memory: is similar to native, but also implies that an underlying buffer within the hardware is being shared. For example, a buffer within the network adapter or graphics display. The goal here is to reduce the number of times the same bytes is being copied about in memory.

## Logging

SLF4J is a Java logging facade. You can use different implementations of it like logback and log4j2.

## Java EE

Java EE (now Jakarta EE) is a set of specifications and APIs for building enterprise-level applications in Java. A Java EE implementation is a concrete software product that provides the runtime environment and tools necessary to execute applications built using Java EE (now Jakarta EE) specifications. Since Java EE itself is just a set of specifications, vendors create implementations that adhere to these standards.