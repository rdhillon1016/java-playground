# Inversion of Control

The standard way of programming is you grabbing some third-party libraries, and calling methods in these third-party libraries yourself. You are responsible for the control. Inversion of control is when you define the code that you want to get executed, and then a third-party container is responsible for actually calling that code. In addition, the third-party container can provide you your dependencies, instead of you getting them yourself (dependency injection). The Spring framework provides an IoC container.

# Misc

Spring comes with Tomcat as the default embedded web server.