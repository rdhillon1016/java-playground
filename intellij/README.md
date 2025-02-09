# Foundation

IntelliJ has its own concept of "modules", separate from something like Maven. Modules in IntelliJ are essentially subprojects. You probably won't be using multiple projects in your day-to-day development.

# Project Configuration

The `.idea` directory contains configuration files for your project. They contain information core to the project itself, such as names and locations of its component modules, compiler settings, etc.

# Running

You can pass arguments to your application by creating a **Run Configuration**.

# Project Bootstrapping

You can create a bootstrapped project with the IntelliJ project model, Maven, or Gradle.

A Maven bootstrapped project creates a `pom.xml` file and `src/main/java`, `src/main/resources` and `src/test/java` directories, as required by Maven.

# Handy Things to Know

IntelliJ provides handy features for creating tests (generate test, navigate to tests for a class, ...), refactoring (extract methods, rename, ...), documenting code (rendered view for comments).

You can also press **Shift** twice to use the "Search Everywhere" feature. This searches through your project files and directories, as well as your project settings and IntellIJ IDEA settings.

You can press **Ctrl+Shift+F** to use the "Find in Files" feature.