# Resources

1. [https://docs.oracle.com/javase/tutorial/jdbc/index.html](https://docs.oracle.com/javase/tutorial/jdbc/index.html)
2. [How ResultSet fetches rows from the database](https://stackoverflow.com/q/42378494)

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

"You access the data in a `ResultSet` object through a cursor. Note that this cursor is not a database cursor. This cursor is a pointer that points to one row of data in the `ResultSet` object. Initially, the cursor is positioned before the first row. You call various methods defined in the `ResultSet` object to move the cursor. There is a set of allowable mappings from SQL data types to Java data types, as described in the `ResultSet` documentation

The execution of the query is done once. This will take an initial amount of time to process the query, (which you can't do anything about other than optimizing your query,) and then it will begin producing rows on the server, which need to be transferred to the client. While the rows are being transferred, the server will probably be continuing to generate more rows to be transferred, and buffering them on the server. This server-side buffering is totally unrelated to the kind of buffering that we are talking about in this Q&A, and you have very little control over it. (Perhaps by means of server configuration, if at all.) At some point all rows will have been collected on the server, and then the only remaining thing to do will be to transfer the remaining rows from the server to the client.

So, as far as the client can tell, once it has sent the query to the server, there is a certain delay while the server is thinking about it, after which rows are becoming available at a rate which is usually as fast as the wire can carry them. So, the client starts reading these rows with resultSet.next().

Without any buffering, each call to resultSet.next() would send a request from the client to the server, telling it to send the next row, and the server would respond with just that row. That would yield the first row very quickly, but it would be very inefficient in the long run, because it would be causing too many round-trips between the client and the server.

With buffering, the first call to resultSet.next() will request a bunch of rows from the server. This will impose a penalty on the time to receive the first row, because you are going to have to wait for 100 rows to be sent over the wire, but in the long run it will significantly reduce total network overhead, because there will be only one round-trip between the client and the server per bunch-of-rows.

The ideal strategy for resultSet.setFetchSize() is to leave it as it is and not worry too much about it." (2)

The default fetch size is JDBC driver dependent.

When you are finished using a `Connection`, `Statement`, or `ResultSet` object, call its close method to immediately release the resources it's using. Alternatively, use a try-with-resources statement to automatically close `Connection`, `Statement`, and `ResultSet` objects, regardless of whether an SQLException has been thrown.

Typically, a JDBC application connects to a target data source using one of two classes:
- `DriverManager`: This fully implemented class connects an application to a data source, which is specified by a database URL. When this class first attempts to establish a connection, it automatically loads any JDBC 4.0 drivers found within the class path. Note that your application must manually load any JDBC drivers (via `Class.forName`) prior to version 4.0.
- `DataSource`: This interface is preferred over `DriverManager` because it allows details about the underlying data source to be transparent to your application. A `DataSource` object's properties are set so that it represents a particular data source. In addition, `DataSource` objects can provide connection pooling (creating new connections is costly) and distributed transactions. The `DataSource` interface is implemented by a driver vendor. It can be implemented in three different ways:
  - A basic implementation produces standard Connection objects that are not pooled or used in a distributed transaction.
  - An implementation that supports connection pooling produces Connection objects that participate in connection pooling, that is, connections that can be recycled.
  - An implementation that supports distributed transactions produces Connection objects that can be used in a distributed transaction, that is, a transaction that accesses two or more DBMS servers.

The exact connection URL syntax differs by DBMS.

Here's an example of some code that creates a `DataSource` and binds it to a naming service that uses the JNDI API (`InitialContext()` here is analagous to a directory root, and files are analagous to objects):
```java
com.dbaccess.BasicDataSource ds = new com.dbaccess.BasicDataSource();
ds.setServerName("grinder");
ds.setDatabaseName("CUSTOMER_ACCOUNTS");
ds.setDescription("Customer accounts database for billing");

Context ctx = new InitialContext();
ctx.bind("jdbc/billingDB", ds);
```

Traditionally, this would be code managed by a system administrator and executed by a deployment tool.

Because of its properties, a `DataSource` object is a better alternative than the `DriverManager` class for getting a connection. Programmers no longer have to hard code the driver name or JDBC URL in their applications, which makes them more portable. Also, `DataSource` properties make maintaining code much simpler. If there is a change, the system administrator can update data source properties and not be concerned about changing every application that makes a connection to the data source. For example, if the data source were moved to a different server, all the system administrator would have to do is set the serverName property to the new server name.

To get a connection-pooling datasource, the vendor needs to have provided a class that implements the `ConnectionPoolDataSource` interface. Additionally, you need a `DataSource`-implementing class that can work with pooled data sources. The system administrator would then write some code that instantiates the `ConnectionPoolDataSource`-implementing class, giving it the properties needed to form connections, similar to the example above. Once this object is deployed, the system administrator instantiates and deploys the `DataSource`-implementing object, and points it towards the `ConnectionPoolDataSource` object.

Note that the implementations to such interfaces can come from different JDBC vendors (and the vendors need not be the same organization as the DBMS owner).

Note that a connection pool library like HikariCP doesn't use the `ConnectionPoolDataSource` interface directly becauase it manages its own connection pooling mechanism efficiently. `ConnectionPoolDataSource` simply provides physical database connections to be used by a connection pooling module (it doesn't manage connection pooling itself; it just provides connections). Instead, `HikariDataSource` decorates an existing JDBC `DataSource` by adding connection pooling functionality. Connections can "leak", if a connection isn't returned to the thread pool by calling `close()`. HikariCP offers a leak detection threshold.

Typically, you access your connection in the same way (by calling `getConnection()` on the `DataSource` object). You then "close" the connection when you are done by calling `close()` on it (or using a try-with-resources block). However, the underlying implementation of the data source doesn't actually close the connection -- it is returned back to the pool. Often, this is achieved by giving you a proxy `Connection` object (that implements `Connection`) when you asked for a connection. Then, when you closed this connection, the proxy doesn't actually close the real connection and returns it to the pool, resetting underlying connection state.

Data sources that support distributed transactions (optionally with connection pooling as well) are administered in much the same way.

When JDBC encounters an error during an interaction with a data source, it throws an instance of `SQLException` as opposed to Exception. A `SQLException` contains a bunch of information, include a SQLState code (standardized), an implementation-specific error code, a cause, and a reference to any chained exceptions.

There is also a subclass of `SQLException` called `SQLWarning`. These warnings are silently chained to the object whose method caused it to be reported, like a `Connection` object. Such objects will have methods to retrieve warnings.

Calling the method `Connection.commit` can close the `ResultSet` objects that have been created during the current transaction. In some cases, however, this may not be the desired behavior. The `ResultSet` property holdability gives the application control over whether `ResultSet` objects (cursors) are closed when commit is called.

You can retrieve columns on a result set entry using methods like `rs.getString()`. Note that although the method `getString` is recommended for retrieving the SQL types CHAR and VARCHAR, it is possible to retrieve any of the basic SQL types with it. Getting all values with `getString` can be very useful, but it also has its limitations. For instance, if it is used to retrieve a numeric type, `getString` converts the numeric value to a Java `String` object, and the value has to be converted back to a numeric type before it can be operated on as a number. In cases where the value is treated as a string anyway, there is no drawback. Furthermore, if you want an application to retrieve values of any standard SQL type other than SQL3 types, use the `getString` method.

You cannot update a default ResultSet object, and you can only move its cursor forward. However, you can create ResultSet objects that can be scrolled (the cursor can move backwards or move to an absolute position) and updated.

`Statement`, `PreparedStatement` and `CallableStatement` objects have a list of commands that is associated with them. This list may contain statements for updating, inserting, or deleting a row; and it may also contain DDL statements such as CREATE TABLE and DROP TABLE. It cannot, however, contain a statement that would produce a ResultSet object, such as a SELECT statement. In other words, the list can contain only statements that produce an update count.

Here's an example of a batch update:
```java
public void batchUpdate() throws SQLException {
    con.setAutoCommit(false);
    try (Statement stmt = con.createStatement()) {

      stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Amaretto', 49, 9.99, 0, 0)");
      stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Hazelnut', 49, 9.99, 0, 0)");
      stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Amaretto_decaf', 49, 10.99, 0, 0)");
      stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Hazelnut_decaf', 49, 10.99, 0, 0)");

      int[] updateCounts = stmt.executeBatch();
      con.commit();
    } catch (BatchUpdateException b) {
      JDBCTutorialUtilities.printBatchUpdateException(b);
    } catch (SQLException ex) {
      JDBCTutorialUtilities.printSQLException(ex);
    } finally {
      con.setAutoCommit(true);
    }
}
```

You can have parameterized batch updates.

You will get a `BatchUpdateException` when you call the method executeBatch if (1) one of the SQL statements you added to the batch produces a result set (usually a query) or (2) one of the SQL statements in the batch does not execute successfully for some other reason.

The main feature of a `PreparedStatement` object is that, unlike a `Statement` object, it is given a SQL statement when it is created. The advantage to this is that in most cases, this SQL statement is sent to the DBMS right away, where it is compiled. As a result, the `PreparedStatement` object contains not just a SQL statement, but a SQL statement that has been precompiled. This means that when the `PreparedStatement` is executed, the DBMS can just run the `PreparedStatement` SQL statement without having to compile it first.

For transactions, disable autocommit mode on the connection, then manually commit once all statements are executed.

To avoid conflicts during a transaction, a DBMS uses locks, mechanisms for blocking access by others to the data that is being accessed by the transaction. (Note that in auto-commit mode, where each statement is a transaction, locks are held for only one statement.) After a lock is set, it remains in force until the transaction is committed or rolled back. For example, a DBMS could lock a row of a table until updates to it have been committed. The effect of this lock would be to prevent a user from getting a dirty read, that is, reading a value before it is made permanent. (Accessing an updated value that has not been committed is considered a dirty read because it is possible for that value to be rolled back to its previous value. If you read a value that is later rolled back, you will have read an invalid value.)

How locks are set is determined by what is called a transaction isolation level, which can range from not supporting transactions at all to supporting transactions that enforce very strict access rules.

One example of a transaction isolation level is `TRANSACTION_READ_COMMITTED`, which will not allow a value to be accessed until after it has been committed. In other words, if the transaction isolation level is set to `TRANSACTION_READ_COMMITTED`, the DBMS does not allow dirty reads to occur. The interface `Connection` includes five values that represent the transaction isolation levels you can use in JDBC.

A non-repeatable read occurs when transaction A retrieves a row, transaction B subsequently updates the row, and transaction A later retrieves the same row again. Transaction A retrieves the same row twice but sees different data.

A phantom read occurs when transaction A retrieves a set of rows satisfying a given condition, transaction B subsequently inserts or updates a row such that the row now meets the condition in transaction A, and transaction A later repeats the conditional retrieval. Transaction A now sees an additional row. This row is referred to as a phantom.

JDBC allows you to find out what transaction isolation level your DBMS is set to (using the Connection method getTransactionIsolation) and also allows you to set it to another level (using the Connection method setTransactionIsolation).

The tradeoffs between transaction isolation levels in is usually performance vs consistency. For example, in locking-based systems, stricter locking means poorer performance -- row locks vs table or range locks. On the other hand, in MVCC (multi-version concurrency control systems), higher isolation levels mean wider snapshots, which require more cleanup.

In JDBC, a `RowSet` is an enhanced version of a ResultSet that provides additional functionality, such as being scrollable, updatable, and disconnected from the database. Unlike `ResultSet`, which requires a constant connection to the database, `RowSet` can operate in a disconnected mode, making it more flexible for certain applications.

JDBC also has support for advanced data types, like user-defined SQL types.