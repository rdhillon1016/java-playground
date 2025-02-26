# Testing

The goal of the MVC Test framework is to provide first-class support for testing Spring MVC code, without actually running a web container.

You can use the `@WebMvcTest` annotation to only test the web layer (slice testing). This disables full auto-configuration and instead applies only configuration relevant to the MVC tests. This also auto-configures the MVC testing framework, with a `MockMvc` bean and optionally Spring Security. Typically, `@WebMvcTest` is used in combination with `@MockitoBean` for mocking its dependencies.

You can use `@JdbcTest` to auto configure a test database and to only enable auto-configuration that is relevant to JDBC tests.