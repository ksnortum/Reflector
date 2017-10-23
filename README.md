# Reflector

**Reflector** is a class that hides some of the complexity and error checking of Java's Reflection.

## Usage

See `ReflectorPlay.java` for usage examples.  The basic syntax is:

    Reflector r = new Reflector();
    r.loadClass("net.snortum.utils.ReflectorPlay");
    r.loadMethod("add", int.class, int.class);
    System.out.println("Sum: " + r.invoke(2, 3));

Consider if you needed to load the Joda Time jar file dynamically.  The process would be something like this:

    String pathToJarFile = "file:///C:/path/to/jar/file/joda-time-2.x.x.jar";
    String className = "org.joda.time.DateTime";
    Reflector r = new Reflector();
    r.loadClass(className, pathToJarFile);
    r.loadConstructor();
    r.loadNewInstance();
    r.loadMethod("getMonthOfYear");
    int month = r.invoke();

## Javadoc

A `pom.xml` file has been provided so that the Javadocs can be build using [Maven](https://maven.apache.org/).  Issue the following at the command line:

    mvn clean javadoc:javadoc

## TODO

* Find a better way to log error messages