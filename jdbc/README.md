# Resources

1. [https://docs.oracle.com/javase/tutorial/jdbc/index.html](https://docs.oracle.com/javase/tutorial/jdbc/index.html)

# General

Refer to [https://docs.oracle.com/javase/tutorial/jdbc/overview/index.html](https://docs.oracle.com/javase/tutorial/jdbc/overview/index.html) for a brief overview.

Refer to [https://docs.oracle.com/javase/tutorial/jdbc/overview/database.html](https://docs.oracle.com/javase/tutorial/jdbc/overview/database.html) for a relational database overview.

In general, to process any SQL statement with JDBC, you follow these steps:

1. Establishing a connection.
2. Create a statement.
3. Execute the query.
4. Process the `ResultSet` object.
5. Close the connection.

One type of statement is the `PreparedStatement`, which is used for precompiling SQL statements that might contain input parameters.

Types of Prepared Statement handling:
- Server-Side Prepared Statements (DBMS-Supported)
  - Some DBMSes, like PostgreSQL, MySQL, and SQL Server, natively support prepared statements.
  - The query is compiled and optimized once, and then can be executed multiple times with different parameters.
  - Provides performance benefits by avoiding repeated query parsing and compilation.
  - Helps prevent SQL injection by treating parameters as data instead of SQL code.
- Client-Side (Driver-Emulated) Prepared Statements
  - If a DBMS does not support prepared statements natively, the database driver or ORM (Object-Relational Mapping) tool may emulate them.
  - The driver replaces parameter placeholders (?) with actual values before sending the query to the database.
  - This approach still helps prevent SQL injection but does not provide the same performance optimizations as server-side preparation.

There is also a regular `Statement` which doesn't have support for parameters, and a `CallableStatement`, used for calling stored procedures.

You access the data in a `ResultSet` object through a cursor. Note that this cursor is not a database cursor. This cursor is a pointer that points to one row of data in the `ResultSet` object. Initially, the cursor is positioned before the first row. You call various methods defined in the `ResultSet` object to move the cursor.

When you are finished using a `Connection`, `Statement`, or `ResultSet` object, call its close method to immediately release the resources it's using. Alternatively, use a try-with-resources statement to automatically close `Connection`, `Statement`, and `ResultSet` objects, regardless of whether an SQLException has been thrown.

Typically, a JDBC application connects to a target data source using one of two classes:
- `DriverManager`: This fully implemented class connects an application to a data source, which is specified by a database URL. When this class first attempts to establish a connection, it automatically loads any JDBC 4.0 drivers found within the class path. Note that your application must manually load any JDBC drivers prior to version 4.0.
- `DataSource`: This interface is preferred over `DriverManager` because it allows details about the underlying data source to be transparent to your application. A `DataSource` object's properties are set so that it represents a particular data source