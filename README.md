# Resources

- [https://dev.java/learn](https://dev.java/learn)

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

A resource is an object that must be closed after the program is finished with it. The try-with-resources statement ensures that each resource is closed at the end of the statement. Any object that implements java.lang.AutoCloseable, which includes all objects which implement java.io.Closeable, can be used as a resource.

# Data Types

- `float`: composed of significand (digits) and an exponent (base 2, tells you where the decimal is). 32-bit single-precision. Always use `BigDecimal` if you need precise values, such as currency
- `int`: 32-bit signed 2s complement
- `byte`: 8-bit signed 2s complement
- `short`: 16-bit signed 2s complement
- `long`: 64-bit signed 2s complement
- `double`: double-precision 64-bit float
- `char`: 16-bit Unicode character
- `boolean`: data represents one bit of information, but its "size" isn't precisely defined

When an integer overflows, it rolls over to the minimum value and begins counting up from there.

You can represent an unsigned `int` or `long` using the `Integer` or `Long` classes.

`String` objects are immutable. Once created, their values cannot be changed.

# Keywords

`final` variables are used to create constants. `final` methods prevent method overriding. `final` classes prevent inheritance.

`void` indicates a method with no return value..

`static` indicates a static member. Static methods/attributes can be accessed without creating an object of a class.

# Interfaces/Abstract Classes

An interface is a reference type, similar to a class, that can contain only constants, method signatures, default methods, static methods (private or public, not protected), instance non-abstract methods (private, not public, not protected), and nested types. Method bodies exist only for default methods, private methods and static methods.

Interfaces can extend other interfaces.

Interfaces form a contract between the class and the outside world, and this contract is enforced at build time by the compiler. If your class claims to implement an interface, all methods defined by that interface must appear in its source code before the class will successfully compile.

Classes can implement multiple interfaces. However, if 2 or more interfaces have a method with the same type signature and name, the implementing class must override that method.

Abstract classes are partially implemented classes with more of a focus on code reusability. These partial classes are then extended by subclasses in a concept called inheritance. Abstract classes are meant to model an "is-a?" relationship between superclasses and subclasses. Multiple inheritance (one subclass inheriting from multiple other classes) is not allowed in Java, therefore you should really consider when the relationship you're describing between classes is really an "is-a" relationship before using inheritance. If you need to use code from multiple classes, use composition instead.

Composition models a "has-a?" relationship. Class A, instead of extending Class B, instead holds an instance of Class B in its state and uses it, thereby demonstrating the "has-a" relationship.

If a constructor does not explicitly invoke a superclass constructor, the Java compiler automatically inserts a call to the no-argument constructor of the superclass. If the super class does not have a no-argument constructor, you will get a compile-time error. `Object` does have such a constructor, so if `Object` is the only superclass, there is no problem. Constructors in inheritance hierarchies are chained.

A subclass inherits all of the `public` and `protected` members of its parent, no matter what package the subclass is in. If the subclass is in the same package as its parent, it also inherits the package-private (no modifier) members of the parent. A subclass does not inherit the `private` members of its parent class.

A nested class has access to all the private members of its enclosing class—both fields and methods.

Explicit casting like `MountainBike myBike = (MountainBike)obj` will trigger a check at runtime to see if the types match up. You can use the `instanceof` operator to check the types before the cast to make sure a runtime exception won't be thrown.

Multiple inheritance of state isn't allowed, because the object can inherit fields from different superclasses. What if methods or constructors from different superclasses instantiate the same field? Which method or constructor will take precedence? Because interfaces do not contain fields, you do not have to worry about problems that result from multiple inheritance of state.

Multiple inheritance of implementation is the ability to inherit method definitions from multiple classes. Problems arise with this type of multiple inheritance, such as name conflicts and ambiguity. When compilers of programming languages that support this type of multiple inheritance encounter superclasses that contain methods with the same name, they sometimes cannot determine which member or method to access or invoke. In addition, a programmer can unwittingly introduce a name conflict by adding a new method to a superclass. Default methods introduce one form of multiple inheritance of implementation. A class can implement more than one interface, which can contain default methods that have the same name. The Java compiler provides some rules to determine which default method a particular class uses.

The Java programming language supports multiple inheritance of type, which is the ability of a class to implement more than one interface. An object can have multiple types: the type of its own class and the types of all the interfaces that the class implements. This means that if a variable is declared to be the type of an interface, then its value can reference any object that is instantiated from any class that implements the interface. This is discussed in the section Using an Interface as a Type.

As with multiple inheritance of implementation, a class can inherit different implementations of a method defined (as default or static) in the interfaces that it extends. In this case, the compiler or the user must decide which one to use.

`public` and `abstract` keywords are implicit in interfaces.

# Lambdas

The type of a lambda expression must be a functional interface (an interface that has only one `abstract` method, although the complete definition is a little more complex). The `@FunctionalInterface` is to help you make sure your interface is indeed functional, or else the compiler will raise an error.

A functional interface can have any number of default or static methods -- they don't count.

A lambda expression is an implementation of the only abstract method in some functional interface.

You can't capture local variables with lambdas:
```java
int calculateTotalPrice(List<Product> products) {

    int totalPrice = 0;
    Consumer<Product> consumer =
        product -> totalPrice += product.getPrice();
    for (Product product: products) {
        consumer.accept(product);
    }
}
```

The above code, when trying to compile, will raise an error "Variable used in lambda expression should be final or effectively final". Lambdas can only capture values, thus the variable must be immutable.

"Effectively final" refers to the fact that the compiler, when it sees that a variable is read from a lambda and that the variable isn't modified, will add the `final` declaration for you in the bytecode.

# Generics

Generics are also called **parametric polymorphism**. 

You can have truly generic type parameters to methods/classes/interfaces, and you can also have bounded type parameters (restricting the type to be an instance of X interface or abstract class). You can also have multiple bounds.

The point of type variables on generic meethods is to link things together - say, a parameter and return type, or two parameters (but only when at least one is generic). You can also use them to express generic bounds on parameters like `U extends Comparable<? super U>>`.

Generic types can't be primitive types, since at compile-time, all type parameters in generic types are replaced with their bounds or `Object` if the type parameters are unbounded. This is called *type erasure* Primitives don't extend `Object`. 

You can use a wildcard (a question mark) as the type parameter when using a generic, when you don’t know what exactly that type is going to be.

**Variance** is how subtyping between more complex types relate to subtyping between their component types. For example, how should a `List<Cat>` relate to a `List<Animal>`? Or how should a function that returns `Cat` relate to a function that returns `Animal`?

Generic types are not **covariant** (the ordering of types is preserved -- assume "<=" means "is a subtype of" -- If A <= B, then I\<A\> <= I\<B\>) by default. For example, you cannot pass a `List<Cat>` where a `List<Animal>` is expected. This is because if this was allowed and you tried to modify that `List<Animal>` that was passed as a `List<Cat>` by adding a `Dog`, that would violate type safety. Arrays *are* covariant, however the same issue as above exists (except in this case it's a runtime exception instead of a compile-time error).

You can modify the aforementioned example to demonstrate a compile-safe, covariant relationship by using a wildcard to impose an upper-bound on the supertype. Instead of writing `List<Animal>`, write `List<? extends Animal>`. This basically says whatever the type of the array elements is, it must be a subclass of animal. You can now safely pass a `List<Cat>` where a `List<? extends Animal>` is expected, demonstrating covariance.

Generic types are also not contravariant by default (the ordering of types is reversed -- If A <= B, then I\<\A> >= I\<B\>). For example, you cannot pass a `List<Animal>` where a `List<Cat>` is expected (this is a bit more intuitive). However, it may be useful to express a contravariant relationship. You can do this by using `List<? super Cat>` instead of `List<Cat>`, thereby enforcing a lower bound. Now, you can pass `List<Animal>` where a `List<? super Cat>` is expected.

Bivariance is both contravariance and covariance, while invariance is neither.

"PECS" is a mnemonic. Suppose you want to operate on a type `Thing`. If you are only pulling items from a generic collection, it is a producer and you should use `extends`, since all you care about is that it is a subtype of `Thing` – (covariance). Another way to think about this is that you can’t add anything to the collection, since you don’t know how far below `Thing` in the hierarchy the actual type is. You can however receive things from the collection, since you know it is at least a `Thing` and thus has to be every superclass of `Thing`; if you are only stuffing items in, it is a consumer and you should use `super`, since all you care about is that the list can accept a `Thing` (contravariance). Another way of thinking about this is that you can for sure add `Thing`s and subtypes of `Thing` to the collection, but you can’t receive anything from the Collection, since you have no idea how far above `Thing` in the hierarchy the actual type is (well, you can still receive it as an `Object` since that’s guaranteed to be at the very top of the hierarchy).

# Packages

A package is a grouping of related types providing access protection and name space management. Note that types refers to classes, interfaces, enumerations, and annotation types. Enumerations and annotation types are special kinds of classes and interfaces, respectively, so types are often referred to in this section simply as classes and interfaces.

By convention, companies use their reversed Internet domain name to begin their package names—for example, com.example.mypackage for a package named mypackage created by a programmer at example.com.

Name collisions that occur within a single company need to be handled by convention within that company, perhaps by including the region or the project name after the company name (for example, com.example.region.mypackage).

Packages in the Java language itself begin with java. or javax.

To use a public package member from outside its package, you must do one of the following:
- Refer to the member by its fully qualified name
- Import the package member
- Import the member's entire package

At first, packages appear to be hierarchical, but they are not. For example, the Java API includes a java.awt package, a java.awt.color package, a java.awt.font package, and many others that begin with java.awt. However, the java.awt.color package, the java.awt.font package, and other java.awt.xxxx packages are not included in the java.awt package. The prefix java.awt (the Java Abstract Window Toolkit) is used for a number of related packages to make the relationship evident, but not to show inclusion.

Importing java.awt.* imports all of the types in the java.awt package, but it does not import java.awt.color, java.awt.font, or any other java.awt.xxxx packages.

There are situations where you need frequent access to static final fields (constants) and static methods from one or two classes. Prefixing the name of these classes over and over can result in cluttered code. The static import statement gives you a way to import the constants and static methods that you want to use so that you do not need to prefix the name of their class.

The path names for a package's source and class files mirror the name of the package.

For convenience, the Java compiler automatically imports two entire packages for each source file:
1. the java.lang package and
2. the current package (the package for the current file).

# Annotations

Annotations have a number of uses, among them:

- Information for the compiler — Annotations can be used by the compiler to detect errors or suppress warnings.
- Compile-time and deployment-time processing -- Software tools can process annotation information to generate code, XML files, and so forth.
- Runtime processing — Some annotations are available to be examined at runtime.

Annotations can contain named or unnamed elements. Annotations can be applied to declarations: declarations of classes, fields, methods, and other program elements. When used on a declaration, each annotation often appears, by convention, on its own line. Annotations can also be applied to the use of types.

Annotations that apply to other annotations are called meta-annotations.
- `@Retention` annotation specifies how the marked annotation is stored: at the source level (ignored by the compiler), at the compiler level (ignored by the JVM), or at the JVM level
- `@Target` marks another annotation to restrict what kind of Java elements the annotaiton can be applied to.

# Exceptions

An exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions.

When an error occurs within a method, the method creates an object and hands it off to the runtime system. The object, called an exception object, contains information about the error, including its type and the state of the program when the error occurred. Creating an exception object and handing it to the runtime system is called throwing an exception.

After a method throws an exception, the runtime system attempts to find something in the call stack to handle it.

Three kinds of exceptions:
- Checked exception: exceptional conditions that a well-written application should anticipate and recover from (e.g. `FileNotFoundException`). Subject to the catch (try-catch) or specify (throws) requirement. All except `Error` and `RuntimeException` are checked exceptions.
- Error: excpetional conditions that are external to the application, and that the application usually cannot anticipate or recover from (e.g stack overflow). An application can choose to catch these kinds of exceptions.
- Runtime exception: exceptional conditions that are internal to the application, and that the application usually cannot anticipate or recover from (e.g improper use of an API).

You can catch more than one type of exception with one exception handler, with the multi-catch pattern:
```java
catch (IOException|SQLException ex) {
    logger.log(ex);
    throw ex;
}
```

Note that the close methods of resources in a try-with-resources block are called in the opposite order of their creation.

A try-with-resources differs from a try-catch-finally (with resource closure in the `finally` block) in the exceptions that are suppressed in certain edge cases. For example, if the `try` and `finally` blocks in a try-catch-finally both throw an exception, only the `finally` exception propagates (the `try` exception is suppressed). If a try-with-resources statement throws an exception and the try block throws an exception, only the `try` exception propagates. You can retrieve the suppressed exceptions by calling the `Throwable.getSuppressed()` method from the exception thrown.

Exception handlers are checked in order.

You can add unchecked exceptions into the `throws` clause of a method declaration (it accepts any `Throwable`), but it is not mandatory.

You can `throw` any `Throwable`.

An application often responds to an exception by throwing another exception. In effect, the first exception causes the second exception. It can be very helpful to know when one exception causes another. Chained Exceptions help the programmer do this.

The following are the methods and constructors in `Throwable` that support chained exceptions:
```java
Throwable getCause()
Throwable initCause(Throwable)
Throwable(String, Throwable)
Throwable(Throwable)
```

Here's an example:
```java
try {

} catch (IOException e) {
    throw new SampleException("Other IOException", e);
}
```

You can access stack trace (execution history of the current thread and lists the names of the classes and methods that were called at the point when the exception occurred) information from an exception object.

You should write your own exception classes if you answer yes to any of the following questions; otherwise, you can probably use someone else's.

* Do you need an exception type that isn't represented by those in the Java platform?
* Would it help users if they could differentiate your exceptions from those thrown by classes written by other vendors?
* Does your code throw more than one related exception?
* If you use someone else's exceptions, will users have access to those exceptions? A similar question is, should your package be independent and self-contained?

"If it's so good to document a method's API, including the exceptions it can throw, why not specify runtime exceptions too?" Runtime exceptions represent problems that are the result of a programming problem, and as such, the API client code cannot reasonably be expected to recover from them or to handle them in any way. Such problems include arithmetic exceptions, such as dividing by zero; pointer exceptions, such as trying to access an object through a null reference; and indexing exceptions, such as attempting to access an array element through an index that is too large or too small.

Advantages of exceptions:
- Exceptions provide the means to separate the details of what to do when something out of the ordinary happens from the main logic of a program.
- Easier propagation of errors up the call stack (as opposed to something like Go's error system, where you have to first check if an error is returned, throw it if it is returned, and proceed with the main logic if it isn't).
- Grouping or categorization of exceptions is a natural outcome of the class hierarchy.

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

# Enumerations

In Java, an enum (enumeration) is a special data type used to define a fixed set of constants. Enums are more powerful than simple constants (final static variables) because they can have methods, fields, and constructors.

Enums can implement interfaces.

# Synchronization

Thread interference happens when two operations, running in different threads, but acting on the same data, interleave.

Simple statements can translate to multiple steps by the virtual machine. We won't examine the specific steps the virtual machine takes — it is enough to know that the single expression `c++` can be decomposed into three steps:

1. Retrieve the current value of c.
2. Increment the retrieved value by 1.
3. Store the incremented value back in c.

Suppose Thread A invokes `increment` at about the same time Thread B invokes `decrement`. If the initial value of `c` is 0, their interleaved actions might follow this sequence:

1. Thread A: Retrieve c.
2. Thread B: Retrieve c.
3. Thread A: Increment retrieved value; result is 1.
4. Thread B: Decrement retrieved value; result is -1.
5. Thread A: Store result in c; c is now 1.
6. Thread B: Store result in c; c is now -1.

Thread A's result is lost, overwritten by Thread B. This particular interleaving is only one possibility. Under different circumstances it might be Thread B's result that gets lost, or there could be no error at all. Because they are unpredictable, thread interference bugs can be difficult to detect and fix.

It is not possible for two invocations of `synchronized` methods on the same object to interleave. When one thread is executing a `synchronized` method for an object, all other threads that invoke `synchronized` methods for the same object block (suspend execution) until the first thread is done with the object. This strategy is effective, but can present problems with liveness.

Synchronization is built around an internal entity known as the *intrinsic lock*. Every object has one.

You can use synchronized statements to avoid synchronizing invocations of other objects' methods (which can create liveness problems):
```java
public void addName(String name) {
    synchronized(this) {
        lastName = name;
        nameCount++;
    }
    nameList.add(name);
}
```

You don't need to worry about thread interference on operations that are atomic.

- Reads and writes are atomic for reference variables and for most primitive variables (all types except `long` and `double`).
- Reads and writes are atomic for all variables declared `volatile` (including `long` and `double` variables).

However, this does not eliminate all need to synchronize atomic actions, because [memory consistency errors](https://docs.oracle.com/javase/tutorial/essential/concurrency/memconsist.html) (threads having inconsistent views of the state) are still possible. 

Using `volatile` variables reduces the risk of memory consistency errors, because any write to a `volatile` variable establishes a happens-before relationship with subsequent reads of that same variable.

Some liveness (the ability for a program to make meaningful progress) problems include deadlock (2 threads waiting on locks held by each other), starvation (greedy threads), and livelocks (2 threads are too busy reacting to each other to make progress).

Immutable objects are particularly useful in concurrent applications. Since they cannot change state, they cannot be corrupted by thread interference or observed in an inconsistent state.

There are some higher-level concurrency objects you can use too:
- `Lock` objects work very much like the implicit locks used by synchronized code. As with implicit locks, only one thread can own a Lock object at a time. `Lock` objects also support a wait/notify mechanism, through their associated `Condition` objects. The biggest advantage of `Lock` objects over implicit locks is their ability to back out of an attempt to acquire a lock. The `tryLock` method backs out if the lock is not available immediately or before a timeout expires (if specified). The `lockInterruptibly` method backs out if another thread sends an interrupt before the lock is acquired.
- In large-scale applications, it makes sense to separate thread management and creation from the rest of the application. Objects that encapsulate these functions are known as *executors*. Most of the executor implementations in `java.util.concurrent` use thread pools, which consist of worker threads. Using worker threads minimizes the overhead due to thread creation. Thread objects use a significant amount of memory, and in a large-scale application, allocating and deallocating many thread objects creates a significant memory management overhead.
- As with any ExecutorService implementation, the *fork/join* framework distributes tasks to worker threads in a thread pool. The fork/join framework is distinct because it uses a work-stealing algorithm. Worker threads that run out of things to do can steal tasks from other threads that are still busy.
- The `java.util.concurrent.atomic` package defines classes that support atomic operations on single variables.
- The `java.util.concurrent` package includes a number of additions to the Java Collections Framework that are handy for concurrency, like `BlockingQueue`.

# Reflection

You can use the `.class` literal to get the `Class` object of a `class`.

Once we have the class, we can find out a lot of information about it, such as who the superclasses are, what public members it has, what interfaces it has implemented. If it is a sealed type, we can even find the subtypes. This is like looking in the mirror to see what you contain.

You can try to find and invoke a method at runtime. The only part that waiss a bit dangerous is that we do not have a compiler check that these methods exist and are accessible. So far, this has been shallow reflective access.

Deep reflective access allows you to look more deeply into the mirror. More specifically, it allows you to do things like change access modifiers in classes.

# JUnit 5

Components:
- JUnit Platform (the testing engine)
- JUnit Jupiter (the API that you'll use to write tests). Note how it's the 5th planet of the solar system.
- JUnit Vintage (an engine for running JUnit 3 and 4 tests on the platform)

Some annotations of JUnit 5:
- `@BeforeEach`
- `@BeforeAll`
- `@AfterEach`
- `@AfterAll`
- `@Disabled`

# Advanced

## Meta

The Java SE specification is defined through the JCP (Java Community Process) as individual JSRs (Java Specification Requests).

Some time ago, Sun open-sourced their implementation of the JDK, which has become the reference implementation for the Java SE standard. This is what we now know as OpenJDK JDK, and is now owned by Oracle.

Oracle contributed a number of previously closed-source features to the OpenJDK JDK so that eventually there was pretty much no functional difference between the Oracle JDK binary and one built from OpenJDK JDK source. According to [this post](https://www.reddit.com/r/java/comments/15sk0h8/why_did_oracle_give_rights_for_java_ee_to_eclipse/jwi49h7/), "OpenJDK JDK really is Oracle's implementation of Java SE. It is also the reference implementation of Java SE, and it is also open source, so that other companies build and distribute that Oracle software. Other companies contribute to OpenJDK's development, but Oracle employs ~90% of the contributors and does ~90% of the development."

Companies may pay to use Oracle's support services for OracleJDK (which is essentially the same as OpenJDK JDK, with different build licensing). You can also pay for extended support of older versions, critical patch updates, and some other things.

Other vendors can distribute their own OpenJDK builds under the GPLv2+CPE license. Oracle doesn't need to distribute under this license because they hold the copyright of all OpenJDK source code. This is why they can release OracleJDK under a different license than GPL.

New features to Java almost always start out as a JDK Enhancement Proposal (JEP). JEPs are typically for enhancements that are not ready to be specified yet. JSRs take mature ideas (e.g. resulting from a JEP) and produce a new or modified specification.

Note that Oracle owns a lot of the copyright associated with Java and its APIs.

## Java Evolution

See [https://dev.java/evolution/](https://dev.java/evolution/).

## Security using JDK

The Java Cryptography Architecture (JCA) is the framework for working with cryptography using the Java programming language and is part of the Java Security API. Its goals are to offer cryptography algorithm independence and extensibility, interoperability, and an implementation agnostic from security providers.

The JCA encompasses engine classes that interact with a specific type of cryptographic service via:

* cryptographic operations like encryption, digital signatures, message digests, etc.
* keys and algorithm parameters
* keystores or certificates that encapsulate the cryptographic data and can be used at higher layers of abstraction.

To use the JCA, an application requests a particular type of object (such as a `MessageDigest`) and a particular algorithm or service (such as the SHA-256 algorithm) and gets an implementation from one of the installed providers. Or you can request the objects from a specific provider (such as `ProviderC`).

Some popular provider examples include: `SunPKCS11`, `SunMSCAPI (Windows)`, `BouncyCastle`, `RSA JSAFE`, `SafeNet`. If the provider you would like to use is not among the list printed, you can also register it following the steps below:

1. Place provider classes on CLASSPATH.
2. Register the provider either:
  - Statically by modifying the conf/security/java.security configuration file, e.g. `security.provider.5=SunJCEII`. Be aware that in JDK 8 the `java.security` file is in java.home/lib/security/java.security.
  - Dynamically by invoking `Security.addProvider(java.security.Provider)` and `Security.insertProviderAt(java.security.Provider,int)`.
3. The preference order for a provider is declared via simple number ordering.

When working with data encryption, you can use this security control mechanism to protect three types of data states:

* **Data at rest** is information not actively moving between devices or networks, stored in a database, or kept on a disk.
* **Data in motion** represents information traveling from one network point to another.
* **Data in use** refers to information loaded in memory actively accessed and processed by users.

## Virtual Threads

After a series of experiments with different APIs, the designers of Java virtual threads decided to simply reuse the familiar `Thread` API. A virtual thread is an instance of `Thread`. Many virtual threads can run on a single OS thread, avoiding the CPU and memory overhead of starting a bunch of OS threads that are likely to block. Usually, the number of OS threads is the same as the number of CPU cores. This is much like Go's concurrency system. Cancellation works the same way as for platform threads, by calling `interrupt`.

Note that there is no way to find the platform thread on which a virtual thread executes.

Here's an example:
```java
import java.util.concurrent.*;
import java.net.*;
import java.net.http.*;

public class VirtualThreadDemo {
   public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      Future<String> f1 = service.submit(() -> get("https://horstmann.com/random/adjective"));
      Future<String> f2 = service.submit(() -> get("https://horstmann.com/random/noun"));
      String result = f1.get() + " " + f2.get();
      System.out.println(result);
      service.close();
   }

   private static HttpClient client = HttpClient.newHttpClient();

   public static String get(String url) {
      try {
         var request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
         return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
      } catch (Exception ex) {
         var rex = new RuntimeException();
         rex.initCause(ex);
         throw rex;
      }
   }   
}
```

A thread is called pinned in either of the two following situations:

1. When executing a synchronized method or block
2. When calling a native method or foreign function

Being pinned is not bad in itself. But when a pinned thread blocks, it cannot be unmounted. The carrier thread is blocked, and, in Java 21, no additional carrier thread is started. That leaves fewer carrier threads for running virtual threads.

Pinning is harmless if `synchronized` is used to avoid a race condition in an in-memory operation. However, if there are blocking calls, it would be best to replace `synchronized` with a `ReentrantLock`. This is of course only an option if you have control over the source code. The reason for this, is because up until Java 24, while code inside a `synchronized` block is running, Java cannot detect if that code is blocked or not. That task cannot be switched out for another virtual thread to execute. This was solved in Java 24.

Minimize thread-local variables in virtual threads (as there will typically be a lot more virtual threads than if you were just using OS threads). Here's an example of thread locals in Java:
```java
public class VirtualThreadLocalExample {
    private static final ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "Initial Value");

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    threadLocal.set("Task-" + taskId);
                    System.out.println(Thread.currentThread() + ": " + threadLocal.get());
                });
            }
        }
    }
}
```

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

Servlet is a Java EE specification. An HTTP servlet container implements the Servlet specification and translates HTTP requests and responses to a Java app. A servlet itself is nothing more than a Java object corresponding to a set of routes that gets fed to the servlet container. When the container gets an HTTP request to one of those routes, it calls a servlet object's method and provides the HTTP request object and a modifiable HTTP reponse object as parameters. The servlet object then does some work. After that is done, the container then reads the HTTP response object and sends the response to the client.

## Misc

Java [does not have tail call optimization](https://softwareengineering.stackexchange.com/a/216848).

In software engineering, a WAR file is a file used to distribute a collection of JAR-files, JavaServer Pages, Java Servlets, Java classes, XML files, tag libraries, static web pages (HTML and related files) and other resources that together constitute a web application. This file is provided to an application server like Tomcat.

You can use method references in place of functional interfaces like `Arrays.sort(rosterAsArray, Person::compareByAge);` (`Person::compareByAge` is the method reference).

The [Stream API](https://dev.java/learn/api/streams/) is your best tool to process your in-memory data following a map/filter/reduce approach.

System properties are properties provided to the process at runtime.

## JDBC

Note that on a JDBC connection, you can start a transaction with `connection.setAutoCommit(false);`. Additionally, you can [set the isolation level](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html) of the transaction with `connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);`.

Note that if a DBMS doesn't support the isolation level specified, it will go with a higher, more restrictive isolation level. If it can't do that, then the driver will throw a SQLException. If you don't specify an isolation level, it will go with the DBMS default.

An isolation level of a transaction A determines whether it can, for example, make or allow dirty reads from rows that other transactions are operating on. Other types of reads that are allowed/disallowed in certain isolation levels are nonrepeatable reads and phantom reads. In a nutshell, different isolation levels are implemented with locking algorithms of varying aggression.

## Serialization

`Serializable` is a marker interface -- it has no methods to implement. Java's built-in serialization mechanism uses it to identify if an object can be serialized (i.e by checking if the object is an instance of `Serializable`), which is useful for something like saving objects to a file. It is important to note that this interface isn't necessary in the case that you're using a serialization library (like Jackson) that doesn't rely on Java's built in serialization mechanism.