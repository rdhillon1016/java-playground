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

As a rule, you should use the prototype scope for all stateful beans and the singleton scope for stateless beans.

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

However, this is bad since the `CommentService` bean has to be aware of the Spring context. Another way to grab a prototype-scoped bean is to use [lookup method injection](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-method-injection.html):
```java
public abstract class CommandManager {

	public Object process(Object commandState) {
		Command command = createCommand();
		command.setState(commandState);
		return command.execute();
	}

	@Lookup
	protected abstract Command createCommand();
}
```
while making sure that the `Command` bean is prototype-scoped.

# Spring AOP

Aspects are a way the framework intercepts method calls and possibly alters the execution of methods. For example, sometimes it’s not relevant to have parts of the code (like logging and tracing) in the same place with the business logic because it makes the app more difficult to understand.

More generally, aspects intercept "join points" (a point in the execution of a program). Aspects define "pointcuts", which specify the join points at which the "advice" (the code to execute at the point of interception) is executed.

Aspects are used by Spring is implementing transactionality (for data consistency) and security features, just to name a couple.

An aspect is simply a piece of logic the framework executes when you call specific methods of your choice.

You must define a few things in an aspect. First, there is the concept of a **join point** in aspect-oriented programming, which defines the event that triggers the execution of an aspect. But with Spring, this is always a method call. A **pointcut** expression defines which methods the framework needs to intercept and execute the aspect for them, and you'll need to define this in your aspect. You'll also need to define the code that is executed at a particular join point (i.e the advice). Different types of advice include "around", "before", and "after" advice (i.e the code that executes around, before, or after a join point).

Objects that use aspects need to be added to the Spring context. The bean that declares the method intercepted by an aspect is named the **target object**.

When an object is an aspect target and you request it from the context, Spring doesn't give you the actual object. It gives you a *proxy* object. When a method is invoked on the proxy object, it calls the aspect logic and then delegates to the actual object's method. This is called **weaving**. Proxies are created by `BeanPostProcessor`s.

You create an aspect by:
1. Annotating the configuration class with `@EnableAspectJAutoProxy` (this allows you to use AspectJ classes and notation to define your Spring aspects). This is autoconfigured in Spring Boot.
2. Create a new class, annotated with `@Aspect`. Add a bean for it to the Spring context.
3. Define and implement a method that will implement the aspect logic and tell Spring when and which methods to intercept using an advice annotation:
    ```java
    @Around("execution(* services.*.*(..))")
    public void log(ProceedingJoinPoint joinPoint) throws Throwable {
        // aspect logic here
        joinPoint.proceed(); 
    }
    ```
    The string inside the `@Around` is wrriten in AspectJ pointcut language. The `ProceedingJoinPoint joinPoint` represents the intercepted method and the main thing you do with it is tell it when to delegate further to the actual method.

Aspects can also intercept the parameters used to call the method or the value the intercepted method returns.

Make sure you don't go overboard though. The whole idea of decoupling a part of the logic is to avoid duplication and hide what's irrelevant.

You can also use annotations to mark methods you want an aspect to intercept. You need to define a custom runtime annotation, like 

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ToLog {
}
```

and then use it in the aspect pointcut expression like `@Around("@annotation(ToLog)")`.

By default, Spring doesn’t guarantee the order in which two aspects in the same execution chain are called. If you need to define the aspects' execution order, use the `@Order` annotation which receives an ordinal.

There are limitations to Spring Aspects vs AspectJ:
- AspectJ aspects are weaved in at load-time, while Spring aspects are dynamically generated subclasses that delegate to the super class after executing the advice.
- Spring aspects can only advice non-private methods
- Spring aspects only apply to Spring Beans
- Suppose method `a()` calls method `b()` on the same class/interface. Then, the advice will never be executed for method `b()`, since Spring AOP weaves with proxies.

Spring does allow you to use AspectJ classes and notation to define your Spring Aspects, however.

Take this example of annotating an advice method using AspectJ's pointcut expression language:
```java
@Before("execution(* rewards.internal.*.*Repository.find*(..))")
```

The "execution" specifies that the join point will be a method. The first asterisk says that the method can return anything. The class should be anywhere in a package that starts with `rewards.internal`. The class name should end with `Repository`. The method should start with `find`, and it can take zero or more arguments.

To grab something like an exception or a return value and use it in your advice, you can do something like:
```java
@AfterThrowing(value = "execution(* rewards.internal.*.*Repository.find*(..))", throwing = "e")
public void something(SomeSpecificException e)
```

This will make sure that the advice only executes if the exception thrown is of type `SomeSpecificException`. You could do this in the pointcut expression itself by using the fully qualified class name of the exception, but doing it this way allows to capture it and use it. Use `returning` rather than `throwing` if you need a return value of a successfully returned method.

# Spring Boot

A servlet container is the entrypoint and exit point for HTTP requests in a Spring Boot app. Spring uses a servlet instance under the hood for requests.

Spring Boot offers:
- Simplified project creation: You can use a project initialization service to get an empty but configured skeleton app.
- Dependency starters. You don't have to worry about version compatibility or which dependencies you need for one particular purpose.
- Autoconfiguration based on dependencies. You only need to change the configurations provided by Spring Boot that don't match what you need.

A Spring Boot projected generated with Spring Initializr usually contains the following:
- The app's main class annotated with `@SpringBootApplication`.
- The artifact `spring-boot-starter-parent` as a parent in the `pom.xml`. This provides compatible versions for dependencies you'll add to your project.
- The plugin `spring-boot-maven-plugin` which is responsible for adding part of the default configurations you'll observe in your project.
- Dependencies that you specify.
- An `application.properties` file.

Dependency starters are capability-oriented groups of compatible dependencies. `spring-boot-starter-web` includes dependencies like Tomcat, context, and AOP.

A controller is a component of the Spring Boot web app that contains methods executed for a specific HTTP request. It is annotated with `@Controller` (another stereotype annotation).

```java
@Controller
public class MainController {
    @RequestMapping("/home")
    public String home() {
        return "home.html";
    }
}
```

The anatomy of an HTTP request to a Spring Boot app using Spring MVC is something like this:
1. Client makes request.
2. Tomcat gets the request. It calls a servlet component for the request. In the case of Spring MVC, Tomcat called a servlet Spring Boot configured called a *dispatcher servlet*.
3. The dispatcher servlet (also known as a *front controller*) finds what controller action to call for the request by delegating to a component named *handler mapping*.
4. The dispatcher servlet calls that specific controller action.
5. The controller does stuff, and returns the page name it needs to render for the response (i.e the view) to the servlet.
6. The dispatcher servlet delegates to the view resolver to find the view, and returns the rendered view in the response.

In web apps, you can use other bean scopes that are relevant only to web applications:
- Request scope. An instance for every HTTP request.
- Session scope. An instance for every HTTP session.
- Application scope. A unique instance in the app's context.  The application scope is close to how a singleton works. The difference is that you can’t have more instances of the same type in the context.

For REST services, we tell the dispatcher servlet not to look for a view. Use the `@RestController` annotation rather than `@Controller`. By default, responses are serialized to JSON when you return an object.

If you want to customize the HTTP response, you can return a `ResponseEntity<T>` object. You can also catch exceptions in the controller and modify the `ResponseEntity` accordingly.

Alternatively, you can use a REST controller advice to intercept exceptions and apply custom logic to handle the error case. The controller can focus on the happy case. Use the `@RestControllerAdvice` annotation.

Use the `@RequestBody` annotation on a parameter of the controller's action to denote that the request body (Spring assumes that this will be in JSON by default) should be deserialized into an object of that type.

# REST Clients

There are 3 REST clients that you can use from a Spring app (although `RestTemplate` will soon be deprecated):

- Spring Cloud OpenFeign. All you have to do is declare an interface annotated with `@FeignClient` and define the requests that you need implemented in the interface (with URL, request header, request body, etc details). `OpenFeign` will handle the actual implementation for you.
- RestTemplate. This is more imperative.
- WebClient. This is built on a methodology called *reactive programmming*.

# Data sources

The data source is a component that manages connections to the server handling the database. A data source object can efficiently manage the connections to minimize the number of unnecessary operations. One commonly used one is HikariCP (Hikari connection pool). Spring Boot considers HikariCP the default data source implementation.

One choice as a tool for working with a relational database besides using JDBC directly is `JdbcTemplate` which uses a datasource under the hood.

By convention, we name classes that relate to the persistence layer "repositories" and annotate them with `@Repository` to add them to the context. Our controllers will use "service" classes, which use these repositories, which use this `JdbcTemplate`. This is convention.

The `@Transactional` annotation adds an aspect to the method that wraps the method with the logic for a transaction. If one of the operations in the method throws a runtime exception, and the exception makes it to the aspect (you don't catch the exception, which you shouldn't most of the time), the aspect will rollback the transaction.

`JdbcTemplate`s require that you define the mapping from a result set to a domain object, and thus doesn't provide easy object-relational mapping.

# Spring Data

Spring Data is a project that allows you to only write a few lines of code to define the repositories of our Spring app. Spring Data offers a common abstraction layer over different ways of persisting data (e.g. JDBC, ORM frameworks that build on top of JDBC, or even non-relational database drivers like MongoDB's driver).

Spring Data is a high-level layer over the various ways to implement the persistence.

`CrudRepository` is the simplest Spring Data contract (interface) that also provides some persistence capabilities. Don't confuse the `Repository` interface with the aforementioned stereotype annotation `@Repository`.

Here's an example of using Spring Data JDBC:

```java
public interface AccountRepository
    extends CrudRepository<Account, Long> {
    @Query("SELECT * FROM account WHERE name = :name")
    List<Account> findAccountsByName(String name);

    @Modifying
    @Query("UPDATE account SET amount = :amount WHERE id = :id")
    void changeAmount(long id, BigDecimal amount);
}
```

Spring Data creates a dynamic implementation of the interface and adds a bean to your app's context when you first need it.

The `@Transactional` annotation can still be used with Spring Data.

# Testing

You can use Mockito to mock dependencies of the class you want to test. You can use `@DisplayName` to further describe the test.

Here's an example:

```java
public class TransferServiceUnitTests {
    @Test
    @DisplayName("Test the amount is transferred " + "from one account to another if no exception occurs.")
    public void moneyTransferHappyFlow() {
        AccountRepository accountRepository = mock(AccountRepository.class);
        TransferService transferService = new TransferService(accountRepository);
        
        Account sender = new Account();
        sender.setId(1);
        sender.setAmount(new BigDecimal(1000));
        
        Account destination = new Account();
        destination.setId(2);
        destination.setAmount(new BigDecimal(1000));

        given(accountRepository.findById(sender.getId())).willReturn(Optional.of(sender));

        given(accountRepository.findById(destination.getId())).willReturn(Optional.of(destination));

        transferService.transferMoney(sender.getId(), destination.getId(), new BigDecimal(100));

        verify(accountRepository).changeAmount(1, new BigDecimal(900));
        verify(accountRepository).changeAmount(2, new BigDecimal(1100));
    }
}
```

The above is an example of white-box testing.

You can also create mocks with Mockito using annotations.

For integration testing, you can use the `@SpringBootTest` annotation on the class, which allows Spring Boot to create a context and add beans as it would in a running app. You can also use the Spring `@MockBean` annotation for mocks if you need mocks (although you'll likely be running an in-memory database, for example, since you're going to want to test the real integration).

To save runtime, the best approach is to rely on unit tests to validate your apps’ components’ logic and use the integration tests only to validate how they integrate with the framework.

Profiles are one strategy you can use to prevent certain beans (like JDBC repository classes) from being instantiated so that you can replace them with stub beans.

# Misc

Spring comes with Tomcat as the default embedded web server.