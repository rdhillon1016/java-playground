# General

The entrypoint of a Java application is a main method:
```java
public class MyFirstClass {
    public static void main(String... args) {
        System.out.println("Hello, World!");
    }
}
```

You can have multiple top-level classes in a Java file, but only one `public` class.

# Data Types

- `float`: composed of significand (digits) and an exponent (base 2, tells you where the decimal is).
- `int`

When an integer overflows, it rolls over to the minimum value and begins counting up from there.

# Keywords

`final` variables are used to create constants. `final` methods prevent method overriding. `final` classes prevent inheritance.

`void` indicates a method with no return value..

`static` indicates a static member. Static methods/attributes can be accessed without creating an object of a class.

# Exceptions

Unchecked exceptions (ones that extend `RuntimeException`) are exceptions that are not checked for by the compiler. By “checked for”, we mean the compiler doesn’t check to see if it is caught or explicitly thrown by any calling methods. `RuntimeException` is intended to be used for programmer errors. As such it should never be caught, except in rare circumstances.

# Compilation

Java classes get compiled into bytecode (`.class` files) which get run in the JRE. You can decompile bytecode.

JDK = JRE + Dev Tools (like a compiler, etc) = (JVM + Class libraries) + Dev Tools

It is important to note that the term "JRE" isn't used much anymore.

JAR stands for Java ARchive. It's a file format based on the popular ZIP file format and is used for aggregating many compiled Java classes, resources, and metadata into one. Directories and `.jar`-files are added to the classpath and available to the `ClassLoader` at runtime to find particular classes inside of it. JAR = Java ARchive. A `.java`-file contains Java code. A Java file is compiled to produce a class file that can be loaded by the JVM ([source](https://stackoverflow.com/questions/60916297/difference-between-jar-file-and-java-file)). The jar also has an optiona `META-INF/MANIFEST.MF` which tells us how to use the jar file and/or specifies other jar files for loading with the jar. You can unzip Jar files using the `jar` utility on the command line.

Executable jars will usually contain a `Main-Class` header in `MANIFEST.MF` that describes which class the entrypoint (`main` method) of the application is.

JAR **sealing** is a security and consistency mechanism that ensures all classes in a specific package within a JAR file are loaded from the same JAR file. This prevents situations where classes from the same package are spread across multiple JAR files, which could lead to unpredictable behavior or security risks (prevents malicious or unintended classes from modifying behavior by injecting different implementations into the same package.).

You can either include all the external dependencies you need in your own JAR (i.e creating a fat JAR), or you could leave them out and point to them in the runtime classpath.

The classpath is simply a list of directories, JAR files, and ZIP archives to search for class files. Typically, both the Java compiler `javac` and the runtime need to know about the classpath (for example, the compiler needs to be able to "see" the classes and methods of the external library JARs you used in order to verify that your source code is type-safe; the runtime obviously needs the external library JARs to run them).

The Java compiler usually looks for classes in folders corresponding to the package part of the Fully Qualified Class Name.

A build tool, like **Maven** or Gradle, helps you to build your project, and manage any dependencies (like additional libraries) that you want to use in your Java code. Using a build tool will also make it easier to share your application and build it on a different machine.

The JVM is typically backwards-compatible. Code built by Java 1.4, for example, can be run on a 1.5 JVM. However, the inverse isn't usually true. It is important to know that Java makes no compatibility guarantee.

A question that often popped up for me was:
"If I compiled a java file in the newest JDK, would an older JVM be able to run the `.class` files?"

According to [this post](https://stackoverflow.com/a/4062109), the answer depends on the actual Java versions in play, the compilation flags used (`-target` flag tells the compiler to generate code that will run on the specified JVM, and the `-source` flag tells the compiler to only accept the older JVM's language features), and the library classes that the class file uses (if it uses library classes that don't exist in an older JRE, then it won't run). You can use the `-bootclasspath` option to avoid this problem by compiling against the older Java version's APIs.

# Annotations

There are 3 types of annotations:
1. **Runtime**: JVM loads annotation info at runtime, and thus can be accessed via reflection
2. **Class**: Annotation info is present in the compiled bytecode, but won’t be loaded by the JVM
3. **Source**: Annotation info is only present in the source-code, and is not present in the compiled bytecode

# Runtime

The classpath can be supplied to `java` in a number of ways including environment variables, the param `-cp`, or the `Class-Path` key in a manifest (if the program is run using `java -jar`).

Even though a compiler for the Java programming language must only produce class files that satisfy all the static and structural constraints in the previous sections, the Java Virtual Machine has no guarantee that any file it is asked to load was generated by that compiler or is properly formed. Applications such as web browsers do not download source code, which they then compile; these applications download already-compiled class files. The browser needs to determine whether the class file was produced by a trustworthy compiler or by an adversary attempting to exploit the Java Virtual Machine. Because of these potential problems, the Java Virtual Machine needs to verify for itself that the desired constraints are satisfied by the class files it attempts to incorporate. A Java Virtual Machine implementation verifies that each class file satisfies the necessary constraints at linking time.

# Advanced

## Meta

The Java SE specification is defined through the JCP (Java Community Process) as individual JSRs (Java Specification Requests).

Some time ago, Sun open-sourced their implementation of the JDK, which has become the reference implementation for the Java SE standard. This is what we now know as OpenJDK JDK, and is now owned by Oracle.

Oracle contributed a number of previously closed-source features to the OpenJDK JDK so that eventually there was pretty much no functional difference between the Oracle JDK binary and one built from OpenJDK JDK source. According to [this post](https://www.reddit.com/r/java/comments/15sk0h8/why_did_oracle_give_rights_for_java_ee_to_eclipse/jwi49h7/), "OpenJDK JDK really is Oracle's implementation of Java SE. It is also the reference implementation of Java SE, and it is also open source, so that other companies build and distribute that Oracle software. Other companies contribute to OpenJDK's development, but Oracle employs ~90% of the contributors and does ~90% of the development."

Companies may pay to use Oracle's support services for OracleJDK (which is essentially the same as OpenJDK JDK, with different build licensing). You can also pay for extended support of older versions, critical patch updates, and some other things.

Other vendors can distribute their own OpenJDK builds under the GPLv2+CPE license. Oracle doesn't need to distribute under this license because they hold the copyright of all OpenJDK source code. This is why they can release OracleJDK under a different license than GPL.

New features to Java almost always start out as a JDK Enhancement Proposal (JEP). JEPs are typically for enhancements that are not ready to be specified yet. JSRs take mature ideas (e.g. resulting from a JEP) and produce a new or modified specification.

Note that Oracle owns a lot of the copyright associated with Java and its APIs.

## Memory

Three types of memory:

1. Heap memory: memory within the JVM process that is used to hold Java Objects and is maintained by the JVMs Garbage Collector.
2. Native memory/Off-heap: is memory allocated within the processes address space that is not within the heap and thus is not freed up by the Java Garbage Collector.
3. Direct memory: is similar to native, but also implies that an underlying buffer within the hardware is being shared. For example, a buffer within the network adapter or graphics display. The goal here is to reduce the number of times the same bytes is being copied about in memory.

## Logging

SLF4J is a Java logging facade. You can use different implementations of it like logback and log4j2.

## Java EE

Java EE (now Jakarta EE) is a set of specifications and APIs for building enterprise-level applications in Java. A Java EE implementation is a concrete software product that provides the runtime environment and tools necessary to execute applications built using Java EE (now Jakarta EE) specifications. Since Java EE itself is just a set of specifications, vendors create implementations that adhere to these standards.

Java EE was originally maintained by Oracle, but it was submitted to the Eclipse Foundation. Oracle owens the trademark for the names `Java` and `javax`, so Java EE was renamed to Jakarta EE.

