# Intro

## Inversion of Control

The standard way of programming is you grabbing some third-party libraries, and calling methods in these third-party libraries yourself. You are responsible for the control. [Inversion of control](https://martinfowler.com/bliki/InversionOfControl.html) is when you define the code that you want to get executed, and then a third-party container is responsible for actually calling that code. In addition, the third-party container can provide you your dependencies, instead of you getting them yourself (dependency injection). The Spring framework provides an IoC container.

## Framework vs Library

A framework often requires inversion of control, is opinionated, and comprehensive (often includes tools, libraries and utilities to handle different aspects of development).

On the other hand, with a library, you control the flow.

## Spring Boot

Spring Boot is a project part of the Spring ecosystem that introduces the concept of “convention over configuration.” The main idea of this concept is that instead of setting up all the configurations of a framework yourself, Spring Boot offers you a default configuration that you can customize as needed.

# Spring Context

Object instances managed by the "Spring context" are called **beans**.

Spring is designed to be modular, so if you only want to use the Spring context in your project, you can only import the `spring-context` module in your `pom.xml`.

You usually define beans in a configuration class (class annotated with `@Configuration`) which has methods (annotated with `@Bean`) that return object instances. Then, you pass that configuration class to the constructor of a context (usually `AnnotationConfigApplicationContext`).

The context with instantiate the beans, and you can grab the beans from the context.

Another way to add beans to the context is to use stereotype annotations, like `@Component`. We mark a class as a component, and when the application creares the Spring context, Spring creates an instance of that class. We'll still have a configuration class when we use this approach to tell Spring where to look for the classes annotated with stereotype annotations (using `@ComponentScan`). You can use the `@PostConstruct` Java EE annotation on a component's method to specify a set of instructions to execute after the construction of that component.

You can also register beans dynamically to the context.

You can express relationships between beans in two ways:
- Wiring: Call one instance method from another in the configuration class.
  ```java
  @Configuration
    public class ProjectConfig {
        
        @Bean
        public Parrot parrot() {
            Parrot p = new Parrot();
            p.setName("Koko");
            return p;
        }
 
        @Bean
        public Person person() {
            Person p = new Person();
            p.setName("Ella");
            p.setParrot(parrot());
            return p;
        }
    }
  ```
  Spring is smart enough to only create one instance of parrot.

  Additionally, you can set a parameter of the bean method if you don't want to call the other bean's method directly:
  ```java
  @Bean
    public Person person(Parrot parrot) {
        Person p = new Person();
        p.setName("Ella");
        p.setParrot(parrot);
        return p;
    }
  ```
  - Auto-wiring: Using the `@Autowired` annotation, we mark an object’s property where we want Spring to inject a value from the context.

  You can autowire a field (which can be bad in case you want the field to be `final` or you want to manage the value yourself at initialization) or the constructor of the class (the parameters will then be autowired).

  The possibility to set the values when calling the constructor also helps you when writing specific unit tests where you don’t want to rely on Spring making the field injection for you.

  You can also auto-wire a setter (barely ever used).

Circular dependencies between beans should be avoided as they are an anti-pattern.

If Spring needs to inject a value that has multiple beans of the same type to choose from, it tries the following:
1. If the identifier of the parameter/field that needs to be injected matches the name of a bean, it chooses that one.
2. It checks for a primary bean.
3. It checks if you explicitly selected a bean using the `@Qualifier` annotation.

Otherwise, it will fail.

Be sure to assess whether your object needs to be in the Spring context or not.

It doesn't make sense to add stereotype annotations on interfaces or abstract classes because these cannot be instantiated.

If a class is annotated with `@Component` and only has one constructor, the `@Autowired` annotation on the constructor is optional.

If Spring encounters an injected interface, it will look in its context for beans created with classes that implement these interfaces.

If you have two instances of classes that implement the same required interface, Spring decides based on much of the same prior mentioned criteria:
1. Checks if one is marked `@Primary`
2. Checks if you explicitly selected via `@Qualifier`

There are 2 more stereotype annotations that are actually semantically meaningful, contrary to `@Component`:
1. `@Service`: objects that implement use cases
2. `@Repository`: objects that manage data persistence

**Singleton** is the default scope of a bean. In Spring, singleton means unique instance per name (rather than unique per app like the traditional singleton concept).

Singleton beans must be immutable to avoid race conditions caused by multiple threads accessing and mutating the bean. If you want mutable singleton beans, you need to make these beans thread-safe (although this isn't typical).

Spring's default behaviour is to instantiate singleton beans when it initializes the context. This is called eager instantiation.

Lazy instantiation means Spring creates the instance the first time someone refers to the bean. Use the `@Lazy` annotation for this functionality.

**Prototype-scoped** beans means Spring creates a new object instance for each request for that bean. Use the `@Scope` annotation to change the bean's scope. This is useful to prevent race conditions on singleton beans, although you could just not use the Spring context and just instantiate an instance of that class yourself (as long as the instance doesn't itself need any beans). In general, in any case where we want Spring to augment the object with a specific capability, like dependency injection, it needs to be a bean.

Here's an example where using a prototype-scoped bean is useful (assume that the CommentProcessor has mutable state):
```java
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CommentProcessor {
    @Autowired
    private CommentRepository commentRepository;
    // Omitted code
}
```
```java
@Service
public class CommentService {
    @Autowired
    private ApplicationContext context;

    public void sendComment(Comment c) {
        CommentProcessor p = context.getBean(CommentProcessor.class);
        p.setComment(c);
        p.processComment(c);
        p.validateComment(c);
        c = p.getComment();
        // do something further
}
```

Note how, in that example, we didn't injent the `CommentProcessor` directly in the `CommentService` bean. Since `CommentService` bean is a singleton, it will only be instantiated once by the context, and thus you'll end up with only one injected instance of `CommentProcessor`. Each call of `sendComment()` will use this unique instance.

# Misc

Spring comes with Tomcat as the default embedded web server.