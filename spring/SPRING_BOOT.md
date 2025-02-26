# Spring Boot

A servlet container is the entrypoint and exit point for HTTP requests in a Spring Boot app. Spring uses a servlet instance under the hood for requests.

Spring Boot offers:
- Simplified project creation: You can use a project initialization service to get an empty but configured skeleton app.
- Dependency starters. You don't have to worry about version compatibility or which dependencies you need for one particular purpose.
- Autoconfiguration based on dependencies. You only need to change the configurations provided by Spring Boot that don't match what you need. It does this autoconfiguration by scanning the classpath for dependencies, or scanning for beans, etc... If you look under the hood, you'll see a bunch of `@ConditionalOnX` beans.

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

If the client specifies that they accept XML in their request, then the dispatcher servlet will automatically use the XML message converter on the object returned from your controller and add the XML content-type header in the response. This is called content negotiation.

If you want finer-grained control over the response, instead of returning an object, you can return a `ResponseEntity<T>`.

You can use `@JdbcTest` to auto configure a test database and to only enable auto-configuration that is relevant to JDBC tests.

Spring Boot grabs properties from a bunch of different sources (with a specific order of overrides) and initializes `@PropertySource`s for you.

You can use `@ConfigurationProperties` to map properties onto a class. The good thing about this is it uses relaxed binding (meaning it will still map properties onto an instance of the class even if the provided property names differ slightly -- e.g. all UPPERCASE in an environment variable, or lowerCamelCase, etc...). The class annotated with this must be a bean.

You can package your Spring Boot application as a fat-JAR. This happens automatically if you use Spring initializr since it adds the Spring Boot Maven plugin. This plugin binds a Maven goal named repackage to the end of the package phase, which takes the JAR containing the bytecode compiled from your source, and packages it into a fat JAR containing all the dependencies it needs and an embedded servlet container (by default, this is Tomcat). The goal will actually retain your original JAR containing just your bytecode, under the name `something.jar.original`.

You can also package your Spring Boot application as a WAR file. This WAR file is fed to a servlet container that dynamically registers servlets. You can also build a hybrid WAR, that can be fed to a servlet container or run as a standalone application.

## Controllers

For a GET endpoint, a 200 status code with the resource in the body is sufficient. If the resource doesn't exist, return a 404 not found. If the resource exists but the client isn't authorized to view it, still return a 404 not found in order to avoid leaking information that the resource exists. 

For a POST endpoint, you're going to want to return a 201 Created status code with the Location header in the response body set to the URI of the newly created resource. You can build the URI to return with `ServletUriComponentsBuilder`.

For a PUT or DELETE endpoint, you're going to want to return a 204 No Content status code.

## REST Clients

You can use the auto-configured `RestTemplateBuilder` to build a `RestTemplate` for making HTTP requests. The good thing about this builder is that REST related properties that are defined such as Jackson preferences will be honoured.

Here is an example:
```java
String uri = "http://example.com/store/orders/{id}/items";

// GET all order items for an existing order with ID 1:
OrderItem[] items = template.getForObject(uri, OrderItem[].class, "1");
```

You can use `getForEntity` in order to get a `ResponseEntity` back.

You can also customize the request by using `template.exchange` and passing in a `RequestEntity`.

Note that the newer `RestClient` is recommended over `RestTemplate` for synchronous, blocking HTTP requests.

## Testing

Spring Boot will autoconfigure for you a `TestRestTemplate` for integration testing. This takes a relative path instead of an absolute path. It is configured to ignore cookies and redirects and is fault tolerant.