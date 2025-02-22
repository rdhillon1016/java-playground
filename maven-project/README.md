# Maven

## General

In Maven, an **archetype** is a template of a project which is combined with some user input to produce a working Maven project that has been tailored to the user's requirements.

The **POM** contains every important piece of information about your project and is essentially one-stop-shopping for finding anything related to your project.

Every artifact in Maven must be identifiable through a unique combination of three identifiers: `groupId`, `artifactId`, and `version`. Each `groupId` should follow Java's package name rules. This means it starts with a reversed domain name you control. There are many legacy projects that do not follow this convention and instead use single word group IDs. However, it will be difficult to get a new single word group ID approved for inclusion in the Maven Central repository. You can create as many subgroups as you want.

## Lifecycle

Maven is based around the central concept of a build lifecycle.

There are three built-in build lifecycles: default, clean and site. The `default` lifecycle handles your project deployment, the `clean` lifecycle handles project cleaning, while the `site` lifecycle handles the creation of your project's web site.

Each of these build lifecycles is defined by a different list of build phases, wherein a build phase represents a stage in the lifecycle.

For example, the default lifecycle comprises of the following phases (for a complete list of the lifecycle phases, refer to the Lifecycle Reference):

* `validate` - validate the project is correct and all necessary information is available
* `compile` - compile the source code of the project
* `test` - test the compiled source code using a suitable unit testing framework. These tests should not require the code be packaged or deployed
* `package` - take the compiled code and package it in its distributable format, such as a JAR.
* `verify` - run any checks on results of integration tests to ensure quality criteria are met
* `install` - install the package into the local repository, for use as a dependency in other projects locally
* `deploy` - done in the build environment, copies the final package to the remote repository for sharing with other developers and projects.

You can call any of these phases directly, and it will execute all the phases before it as well.

However, even though a build phase is responsible for a specific step in the build lifecycle, the manner in which it carries out those responsibilities may vary. And this is done by declaring the plugin goals bound to those build phases. A **plugin goal** represents a specific task (finer than a build phase) which contributes to the building and managing of a project. It may be bound to zero or more build phases.  goal not bound to any build phase could be executed outside of the build lifecycle by direct invocation.

Consider the command below. The `clean` and `package` arguments are build phases, while the `dependency:copy-dependencies` is a goal (of a plugin).

```
mvn clean dependency:copy-dependencies package
```

If this were to be executed, the `clean` phase will be executed first (meaning it will run all preceding phases of the `clean` lifecycle, plus the `clean` phase itself), and then the `dependency:copy-dependencies` goal, before finally executing the `package` phase (and all its preceding build phases of the default lifecycle).

The phases named with hyphenated-words (`pre-*`, `post-*`, or `process-*`) are not usually directly called from the command line. These phases sequence the build, producing intermediate results that are not useful outside the build.

The packaging (`jar`, `pom`, etc) type of your POM will determine the actual goals that are bound to a particular phase by default.

Whenever you want to customise the build for a Maven project, this is done by adding or reconfiguring plugins. Plugins are artifacts that provide goals to Maven. Furthermore, a plugin may have one or more goals wherein each goal represents a capability of that plugin. Plugins can contain information that indicates which lifecycle phase to bind a goal to. Note that adding the plugin on its own is not enough information - you must also specify the goals you want to run as part of your build. The plugin may have goals that, when specified, are bound by default to some phase. If more than one goal is bound to a particular phase, the order used is that those from the packaging are executed first, followed by those configured in the POM. The `<executions>` element of the POM gives you finer grained control over the order of particular goals, including running the same goal multiple times.

You can fill in variables in the resource files of your project with variables that may only be available at build-time. You have to enable resource file filtering, as Maven calls it.

## POM

A Project Object Model or POM is the fundamental unit of work in Maven. The Super POM is Maven's default POM. All POMs extend the Super POM unless explicitly set, meaning the configuration specified in the Super POM is inherited by the POMs you created for your projects.

Elements in the POM that are merged via project inheritance (specifying a parent in the child's POM) are the following:

* dependencies
* developers and contributors
* plugin lists (including reports)
* plugin executions with matching ids
* plugin configuration
* resources

Project Aggregation is similar to Project Inheritance. But instead of specifying the parent POM from the module, it specifies the modules from the parent POM. By doing so, the parent project now knows its modules, and if a Maven command is invoked against the parent project, that Maven command will then be executed to the parent's modules as well. Project aggregation is good for when you have a group of projects that are built or processed together. But of course, you can have both Project Inheritance and Project Aggregation. Meaning, you can have your modules specify a parent project, and at the same time, have that parent project specify those Maven projects as its modules.

Maven allows you to use both your own (via `<properties>`) and pre-defined variables in the POM. Any field of the model that is a single value element can be referenced as a variable, and there are also some other special pre-defined variables.

## Dependencies

Where does Maven reference depencies from? Your local repository. When it builds a project, it makes sure to install the dependencies, including transitive and inherited dependencies in your local maven repository.

With transitive dependencies, the graph of included libraries can quickly grow quite large. For this reason, there are additional features that limit which dependencies are included:
- Dependency mediation - this determines what version of an artifact will be chosen when multiple versions are encountered as dependencies. Maven picks the "nearest definition". That is, it uses the version of the closest dependency to your project in the tree of dependencies. You can always guarantee a version by declaring it explicitly in your project's POM. Since versions are arbitrary strings and may not follow a strict semantic sequence, Maven can't just choose the newest version.
- Dependency management - this allows project authors to directly specify the versions of artifacts to be used when they are encountered in transitive dependencies or in dependencies where no version has been specified.
- Dependency scope - this allows you to only include dependencies appropriate for the current stage of the build.
- Excluded dependencies - If project X depends on project Y, and project Y depends on project Z, the owner of project X can explicitly exclude project Z as a dependency, using the "exclusion" element.
- Optional dependencies - If project Y depends on project Z, the owner of project Y can mark project Z as an optional dependency, using the "optional" element. When project X depends on project Y, X will depend only on Y and not on Y's optional dependency Z. The owner of project X may then explicitly add a dependency on Z, at her option. (It may be helpful to think of optional dependencies as "excluded by default.")

Dependency management takes precedence over dependency mediation for transitive dependencies. The dependency management won't affect the (transitive) dependencies of any plugins used in the same effective POM but only the (transitive) project dependencies.

Note that using 2 versions of the same dependency in your project will cause all sorts of problems. Remember that JAR versioning is basically meaningless to the compiler and the runtime -- a JAR is just a place for the compiler or the runtime to look for bytecode. So if you have 2 JARs, with different versions, but containing the same classes, the classloader will have two places it can find such classes, potentially causing issues if a component in your program relies on a certain version of the class, but is given the wrong one. This could potentially work if you have two classloaders, each loading a separate version, but this is highly uncommon.

Dependencies have scope (most dependencies are needed at compile-time to compile your source -- e.g. to make sure your code is type-safe). Some dependencies may only be needed at runtime (e.g. a JDBC driver).

Although transitive dependencies can implicitly include desired dependencies, it is a good practice to explicitly specify the dependencies your source code uses directly. This best practice proves its value especially when the dependencies of your project change their dependencies. For example, assume that your project A specifies a dependency on another project B, and project B specifies a dependency on project C. If you are directly using components in project C, and you don't specify project C in your project A, it may cause build failure when project B suddenly updates/removes its dependency on project C. Another reason to directly specify dependencies is that it provides better documentation for your project: one can learn more information by just reading the POM file in your project, or by executing `mvn dependency:tree`. Maven also provides `dependency:analyze` plugin goal for analyzing the dependencies.

The scope of your dependencies [affects the scope of transitive dependencies](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html) in your projects.

The examples in the previous section describe how to specify managed dependencies through inheritance. However, in larger projects it may be impossible to accomplish this since a project can only inherit from a single parent. To accommodate this, projects can import managed dependencies from other projects. This is accomplished by declaring a POM artifact as a dependency with a scope of "import".

Optional dependencies are used when it's not possible (for whatever reason) to split a project into sub-modules. The idea is that some of the dependencies are only used for certain features in the project and will not be needed if that feature isn't used. Ideally, such a feature would be split into a sub-module that depends on the core functionality project. This new subproject would have only non-optional dependencies, since you'd need them all if you decided to use the subproject's functionality.

A bill of materials POM (a BOM) is a POM with a dependency management section that lists dependencies with versions that play nicely with each other. Other projects that wish to use the library of artifacts specified in the BOM POM should import this POM into the dependencyManagement section of their POM.

## Profiles

Under certain conditions, plugins may need to be configured with local filesystem paths. Under other circumstances, a slightly different dependency set will be required, and the project's artifact name may need to be adjusted slightly. And at still other times, you may even need to include a whole plugin in the build lifecycle depending on the detected build environment.

To address these circumstances, Maven supports build profiles.

## Directory Layout

Maven has a [standard directory layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).

