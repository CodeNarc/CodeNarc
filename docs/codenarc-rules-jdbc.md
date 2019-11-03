---
layout: default
title: CodeNarc - JDBC Rules
---  

# JDBC Rules  ("*rulesets/jdbc.xml*")


## DirectConnectionManagement Rule

*Since CodeNarc 0.14*

The J2EE standard requires that applications use the container's resource management facilities to obtain connections
to resources. Every major web application container provides pooled database connection management as part of its
resource management framework. Duplicating this functionality in an application is difficult and error prone, which
is part of the reason it is forbidden under the J2EE standard.

For more information see: <https://vulncat.fortify.com/en/detail?id=desc.semantic.java.j2ee_badpractices_getconnection>.

Example of violations:

```
    DriverManager.getConnection()
    java.sql.DriverManager.getConnection()
```


## JdbcConnectionReference Rule

*Since CodeNarc 0.15*

Checks for direct use of `java.sql.Connection`, which is discouraged and almost never necessary
in application code.

For a more *Groovy* alternative, see <http://groovy-lang.org/databases.html> for information on the
**Groovy Sql** abstraction layer for JDBC/SQL.

Note: If a violation is triggered from an **import** statement, then you may get multiple violations per
import if there are multiple classes in the source file. In that case, the imports are processed once per class.


## JdbcResultSetReference Rule

*Since CodeNarc 0.15*

Checks for direct use of `java.sql.ResultSet`, which is not necessary if using the Groovy **Sql** facility or an
ORM framework such as *Hibernate*.

See <http://groovy-lang.org/databases.html> for information on the **Groovy Sql** abstraction
layer for JDBC/SQL.

Note: If a violation is triggered from an **import** statement, then you may get multiple violations per
import if there are multiple classes in the source file. In that case, the imports are processed once per class.


## JdbcStatementReference Rule

*Since CodeNarc 0.15*

Checks for direct use of `java.sql.Statement`, `java.sql.PreparedStatement`, or
`java.sql.CallableStatement`, which is not necessary if using the Groovy **Sql** facility or an
ORM framework such as *Hibernate*.

See <http://groovy-lang.org/databases.html> for information on the **Groovy Sql** abstraction
layer for JDBC/SQL.

Note: If a violation is triggered from an **import** statement, then you may get multiple violations per
import if there are multiple classes in the source file. In that case, the imports are processed once per class.

