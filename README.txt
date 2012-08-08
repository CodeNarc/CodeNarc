CodeNarc  --  http://codenarc.org/
-------------------------------------------------------------------------------

CodeNarc is a static analysis tool for Groovy source code, enabling monitoring and enforcement of
many coding standards and best practices. CodeNarc applies a set of Rules (predefined and/or custom)
that are applied to each Groovy file, and generates an HTML or XML report of the results, including a
list of rules violated for each source file, and a count of the number of violations per package and
for the whole project.

CodeNarc is similar to popular static analysis tools such as PMD or Checkstyle. Unlike those tools
which analyze Java code, CodeNarc analyzes Groovy code.

DEPENDENCIES

CodeNarc requires
 - Groovy version 1.7 or later
 - The Log4J jar, version 1.2.13 or later, accessible on the CLASSPATH
   (http://logging.apache.org/log4j/index.html).

AVAILABLE FROM MAVEN CENTRAL REPOSITORY

For projects built using Maven, CodeNarc is available from the Maven Central Repository.
  - groupId = org.codenarc
  - artifactId = CodeNarc
