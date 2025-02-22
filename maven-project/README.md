# Maven

## General

In Maven, an **archetype** is a template of a project which is combined with some user input to produce a working Maven project that has been tailored to the user's requirements.

The **POM** contains every important piece of information about your project and is essentially one-stop-shopping for finding anything related to your project.

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

Every artifact in Maven must be identifiable through a unique combination of three identifiers: `groupId`, `artifactId`, and `version`. Each `groupId` should follow Java's package name rules. This means it starts with a reversed domain name you control. There are many legacy projects that do not follow this convention and instead use single word group IDs. However, it will be difficult to get a new single word group ID approved for inclusion in the Maven Central repository. You can create as many subgroups as you want.

Dependencies have scope (most dependencies are needed at compile-time to compile your source -- e.g. to make sure your code is type-safe). Some dependencies may only be needed at runtime (e.g. a JDBC driver).

Where does Maven reference depencies from? Your local repository. When it builds a project, it makes sure to install the dependencies in your local maven repository.

