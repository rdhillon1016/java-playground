# Resources

1. [https://dev.java/learn/java-io](https://dev.java/learn/java-io) (official Java learning guide)
2. [https://dev.java/learn/modernio/](https://dev.java/learn/modernio/) (official Java learning guide)
3. [https://web.archive.org/web/20140528182031/https://blogs.oracle.com/slc/entry/javanio_vs_javaio](https://web.archive.org/web/20140528182031/https://blogs.oracle.com/slc/entry/javanio_vs_javaio)

# General

There are two main concepts in Java I/O: locating the resource you need to access (it can be a file or a network resource), and opening a stream to this resource.

An _I/O Stream_ represents an input source or an output destination. A stream can represent many different kinds of sources and destinations, including disk files, devices, other programs, and memory arrays.

Streams support many different kinds of data, including simple bytes, primitive data types, localized characters, and objects. Some streams simply pass on data; others manipulate and transform the data in useful ways. No matter how they work internally, all streams present the same simple model to programs that use them: a stream is a sequence of data. A program uses an input stream to read data from a source, one item at a time and an output stream to write data to a destination, one item at time.

I/O streams are a different concept than the streams from the Stream API introduced in Java SE 8.

The Java I/O API defines two kinds of content for a resource:

* character content, think of a text file, a XML or JSON document,
* and byte content, think of an image or a video.

Following this, there are four abstract base classes, each modeling a type of I/O stream and an operation:
- `Reader`: reading streams of characters
- `Writer`: writing streams of characters
- `InputStream`: reading streams of bytes
- `OutputStream`: writing streams of bytes

These streams and readers can optionally be buffered (rather than reading/writing one byte/character at a time). A `BufferedInputStream's` read() method, for example, still returns one byte at a time but it reads ahead internally to fill a buffer. That way, most calls to read() are non-blocking. The byte is already in the buffer and is immediately returned to the caller. Buffered Readers/Writers/InputStreams/OutputStreams read and write to the OS in large chunks for optimization.

Be sure to consult documentation for the methods on these abstract base classes. For example, the `skip(long n)` method on an `InputStream` may, for a variety of reasons, end up skipping over some smaller number of bytes, possibly 0. The actual number of bytes skipped is returned. Thus, in the case that you need to skip a fixed number of bytes, you need to check for the exact number of bytes skipped, and try to skip again:
```java
long skip(BufferedInputStream bis, int offset) throws IOException {
    long skip = 0L;
    while (skip < offset) {
        skip += bis.skip(offset - skip);
    }
    return skip;
}
```

The same goes for reading a fixed amount of bytes.

# Files

The `File` class represents the legacy way to access files. The `Path` interface was introduced as part of the Java NIO2 API to fix several drawbacks of `File`.

A `Path` is not system independent. You cannot compare a `Path` from a Solaris file system and expect it to match a `Path` from a Windows file system, even if the directory structure is identical and both instances locate the same relative file.

A `Path` instance contains the information used to specify the location of a file or directory. Example using the `Paths` helper class:
```java
Path p1 = Paths.get("/tmp/foo");
```

You can think of the `Path` as storing these name elements of the path as an indexable sequence.

The `normalize()` method removes any redundant elements (like trailing `/.`s). It is important to note that normalize does not check at the file system when it cleans up a path. It is a purely syntactic operation. Iff `sally` were a symbolic link, removing `sally/..` from `/home/sally/../joe/foo` might result in a `Path` that no longer locates the intended file.

You can combine paths by using the `resolve()` method.

A common requirement when you are writing file I/O code is the capability to construct a path from one location in the file system to another location. You can meet this using the `relativize()` method.

The `Path` interface extends the `Iterable` interface. The `iterator()` method returns an object that enables you to iterate over the name elements in the path. The first element returned is that closest to the root in the directory tree. The following code snippet iterates over a path, printing each name element:

```java
Path path = ...;
for (Path name: path) {
    System.out.println(name);
}
```

`Path` also extends `Comparable`.

A `Path` instance is always bound to a file system. If no file system is provided when a path is created, then the default file system is used. You can retrieve the specific `Path` separator (e.g / or \\) via `FileSystems.getDefault().getSeparator()`.

A file system has one or more file stores to hold its files and directories. The file store represents the underlying storage device. In UNIX operating systems, each mounted file system is represented by a file store. In Microsoft Windows, each volume is represented by a file store.

To retrieve a list of all the file stores for the file system, you can use the `getFileStores()` method.

You can use the `Files` class to copy, delete, move, check the existence of files, and check file attributes, among other things. The `Files` class is "link aware." Every Files method either detects what to do when a symbolic link is encountered, or it provides an option enabling you to configure the behavior when a symbolic link is encountered.

To walk a file tree, you first need to implement a `FileVisitor`.

Once you have implemented your `FileVisitor`, how do you initiate the file walk? There are two `walkFileTree()` methods in the `Files` class.

The java.nio.file package provides a file change notification API, called the Watch Service API. This API enables you to register a directory (or directories) with the watch service. When registering, you tell the service which types of events you are interested in: file creation, file deletion, or file modification. When the service detects an event of interest, it is forwarded to the registered process. The registered process has a thread (or a pool of threads) dedicated to watching for any events it has registered for. When an event comes in, it is handled as needed.

All the I/O operations throw the same, default exception in the Java I/O API: the `IOException`. Depending on the type of resource you are accessing, some more exceptions can be thrown. For instance, if your reader reads characters from a file, you may have to handle the `FileNotFoundException`. 

The closing of I/O resources (as required by resources implementing or extending java.io.Closeable) can be done using the try-with-resources statement. The try-with-resources statement has the advantage that the compiler automatically generates the code to close the resource(s) when no longer required. All methods that access the file system can throw an IOException. It is best practice to catch these exceptions by embedding these methods into a try-with-resources statement. Here's an example of closing several resources:
```java
File file = new File("file.txt");

try (FileReader fileReader = new FileReader(file);
     BufferedReader bufferedReader = new BufferedReader(fileReader);) {

    // do something with the bufferedReader or the fileReader

} catch (IOException e) {
    // do something with the exception
}
```

There are a bunch of file I/O methods on the `Files` class, including `readAllLines()`, `newBufferedReader()`, `newOutputStream()`, `createFile()` etc.

Here are some examples:

```java
Path file = ...;
byte[] fileArray;
fileArray = Files.readAllBytes(file);
```

```java
Path file = ...;
byte[] buf = ...;
Files.write(file, buf);
```

```java
Path file = ...;
try {
    // Create the empty file with default permissions, etc.
    Files.createFile(file);
} catch (FileAlreadyExistsException x) {
    System.err.format("file named %s" +
        " already exists%n", file);
} catch (IOException x) {
    // Some other sort of failure, such as permissions.
    System.err.format("createFile error: %s%n", x);
}
```

```java
try {
    Path tempFile = Files.createTempFile(null, ".myapp");
    System.out.format("The temporary file" +
        " has been created: %s%n", tempFile)
;
} catch (IOException x) {
    System.err.format("IOException: %s%n", x);
}
```

You can add specific file attributes at the time of creation with the overloaded `createFile()` method. Within such file attributes, you can add POSIX specific file permissions, for example.

Random access files permit nonsequential, or random, access to a file's contents. To access a file randomly, you open the file, seek a particular location, and read from or write to that file. This functionality is possible with the `SeekableByteChannel` interface. The SeekableByteChannel interface extends channel I/O with the notion of a current position. Methods enable you to set or query the position, and you can then read the data from, or write the data to, that location. On the default file system, you can use that channel as is, or you can cast it to a `FileChannel` giving you access to more advanced features, such as mapping a region of the file directly into memory for faster access, locking a region of the file, or reading and writing bytes from an absolute location without affecting the channel's current position.

Channels are a concept of NIO (New I/O APIs), while streams are a product of the `java.io` package. In NIO, you find new abstractions, like Buffers and Channels (portals through which I/O transfers take place). Buffers are the sources (in the case of writes) or targets (in the case of reads) of those data transfers. Some types of Channels, like SocketChannel, can operate in non-blocking mode. Selectors provide the ability to have a channel readiness selection, which enables multiplexed I/O.

Here is an example of buffered stream I/O to read text files:
```java
Path path = Path.of("file.txt");

try (BufferedReader reader = Files.newBufferedReader(path)) {

    // The closing of the reader and the handling of the exceptions
    // have been omitted
    // String line = reader.readLine();
    long count = 0L;
    while (line != null) {
        count++;
        line = reader.readLine();
    }
    System.out.println("Number of lines in this file = " + count);
}
```

While stream I/O reads a character at a time, channel I/O reads a buffer at a time. The `ByteChannel` interface provides basic read and write functionality. A `SeekableByteChannel` is a `ByteChannel` that has the capability to maintain a position in the channel and to change that position.

`BufferedReader` is an example of the usage of the _Decorator_ pattern. A decorator is simply a wrapper that implements the same interface as the wrapped object and requires a wrapped object to be provided (i.e for construction). The wrapper To create an instance of `BufferedReader`, you must provide a `Reader` object that acts as a delegate for the `BufferedReader` object. The `BufferedReader` class then adds several methods to the base `Reader` class. The decoration of the `BufferedReader` class allows for the overriding of the existing concrete methods of the `Reader` class, as well as the addition of new methods.

You can read and write characters from/to binary files using `InputStreamReader` and `OutputStreamWriter`.

The JAVA I/O API also gives classes to access the content of in-memory structures, namely arrays of characters or bytes, and strings of characters.

Often times, you'll stack decorations of readers (order matters), like in the following example:
```java
try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
     GZIPInputStream gzbais = new GZIPInputStream(bais);
     InputStreamReader isr = new InputStreamReader(gzbais);
     BufferedReader reader = new BufferedReader(isr);) {
}
```
This code:
- builds an input binary stream on a byte array 
- decorates it with decompressor functionality
- decorates it with character-reading functionality
- decorates it with buffering

# Modern I/O (deprecations, etc)

Note, however, that `BufferedReader` is now obsolete, and you should just use the methods proved by the `Files` class. To split your input into something else than lines, use a `java.util.Scanner`. 

Be careful when parsing numbers from text files, since their format may be locale-dependent. For example, the input 100.000 is 100.0 in the US locale but 100000.0 in the German locale.

Java has a Zip File System provider built-in, thus you can call `FileSystems.newFileSystem(pathToZipFile)` and treat it like a file system.

# Network

"Socket classes are used to represent the connection between a client program and a server program. The java.net package provides two classes: Socket and ServerSocket. These implement the client side of the connection and the server side of the connection, respectively.

The client knows the host-name of the machine on which the server is running ,and the port number on which the server is listening. Clients try to connect to the server and if everything goes well, the server accepts the connection. Upon acceptance, the server gets a new socket bound to the same local port and also has its remote endpoint set to the address and port of the client. It needs a new socket so that it can continue to listen to the original socket for connection requests while tending to the needs of the connected client.

The server waits for a client connection in blocking mode: serverSocket.accept() is a blocking instruction, the server waits for a connection and no other operation can be executed by the thread which runs the server. Because of this, the server can work in multitasking only by implementing a multi-thread server: having to create a thread for every new socket created by the server." (3).