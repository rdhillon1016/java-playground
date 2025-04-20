# Spring Security

## OAuth 2 flow

The OAuth 2 framework defines two separate entities: the **authorization server** and the **resource server**. The purpose of the authorization server is to authorize the user and provide them with a token that specifies, among other things, a set of privileges that they can use.

To make it simple and only give you an overview, I’ve described the OAuth 2 flow called the password grant type. OAuth 2 defines multiple grant types and, as you’ll see in chapters 12 through 15, the client application does not always have the credentials. If we use the authorization code grant, the application redirects the authentication in the browser directly to a login implemented by the authorization server.

## Spring Boot

By default, by including spring security in your project, Spring Boot configures it to authenticate every endpoint using HTTP Basic authentication. The username + password combination against which your request credentials are verified is generated randomly at application start-up.

To accomplish the above (which is just meant to be a placeholder configuration -- not something that you would actually use in production), Spring Boot auto-configures the following for you. This flow represents the backbone of Spring Security authentication:
1. The request is intercepted by the authentication filter.
2. Authentication responsibility is delegated to the authentication manager.
3. The manager uses the authentication provider, which implements the authentication logic.
4. The provider finds the user with a user details service and validates the password using a password encoder.
5. The result is returned upstream to the filter.
6. Details about the authenticated entity are stored in the security context.

An object of type `UserDetails` has a username, password, and an authority (an action allowed for that user) -- you can use any string for this.

You can override this default configuration. In the past, much of this overriding was done by creating a configuration class that extends `WebSecurityConfigurerAdapter`, and overriding its methods. That class is now depcrated. This [blog](https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter) outlines how to convert a bunch of common use cases away from `WebSecurityConfigurerAdapter`.

The `UserDetailsService` is only responsible for retrieving the user by username. This action is the only one needed by the framework to complete authentication. The `UserDetailsManager` (if needed) adds behavior that refers to adding, modifying, or deleting the user.

The `UserDetails` interface has one or more authorities. Spring Security uses authorities to refer either to fine-grained privileges or to roles, which are groups of privileges.

71