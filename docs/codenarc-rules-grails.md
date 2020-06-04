---
layout: default
title: CodeNarc - Grails Rules
---  

# Grails Rules  ("*rulesets/grails.xml*")

## GrailsDomainGormMethods Rule

<Since CodeNarc 1.6>

Database operation should be performed by Data Services instead of calling GORM static and instance methods.

Using the GORM static and instance methods may lead to spreading the persistence logic across the whole
application instead of concentrating it into services. It makes difficult to find all the code working
with the database in case of upgrades to the newer versions of Grails which require all persistence code
running inside transactions.

Data Services are available since Grails 3.3 and GORM 6.1.

NOTE: This is a [CodeNarc Enhanced Classpath Rule](./codenarc-enhanced-classpath-rules.html).
It requires **CodeNarc** to have the application classes being analyzed, as well as any referenced classes, on the classpath.

Example of violations:

```
    class Person {
        String firstName
        String lastName
    }

    class PersonService {
        
        Person createPerson(String firstName, String lastName) {
            Person person = new Person(firstName: firstName, lastName: lastName)
            return person.save()
        }
    
    }
```

Example of valid configuration:

```
    class Person {
        String firstName
        String lastName
    }

    @Service(Person)
    class PersonDataService {
        Person save(Person person)
    }

    class PersonService {

        PersonDataService personDataService
        
        Person createPerson(String firstName, String lastName) {
            Person person = new Person(firstName: firstName, lastName: lastName)
            return personDataService.save(person)
        }
    
    }
```

See [GORM Data Services](https://gorm.grails.org/latest/hibernate/manual/index.html#dataServices)

See [Grails GORM Data Services Guide](https://guides.grails.org/grails-gorm-data-services/guide/index.html)


## GrailsDomainHasEquals Rule

*Since CodeNarc 0.15*

Checks that Grails domain classes redefine `equals()`.

Ignores classes annotated with `@EqualsAndHashCode` or `@Canonical`.

This rule sets the default value of `applyToFilesMatching` to only match files
under the 'grails-app/domain' folder. You can override this with a different regular
expression value if appropriate.


## GrailsDomainHasToString Rule

*Since CodeNarc 0.15*

Checks that Grails domain classes redefine `toString()`.

Ignores classes annotated with `@ToString` or `@Canonical`.

This rule sets the default value of `applyToFilesMatching` to only match files
under the 'grails-app/domain' folder. You can override this with a different regular
expression value if appropriate.


## GrailsDomainReservedSqlKeywordName Rule

*Since CodeNarc 0.19*

Forbids usage of SQL reserved keywords as class or field names in Grails domain classes.
Naming a domain class (or its field) with such a keyword causes SQL schema creation errors and/or redundant
table/column name mappings.

Note: due to limited type information available during CodeNarc's operation, this rule will report fields
of type `java.io.Serializable`, but not of its implementations. Please specify any implementations
used as domain properties in `additionalHibernateBasicTypes`.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| additionalHibernateBasicTypes | Comma-separated list of simple class names of additional classes that Hibernate maps as basic types (creates a column for a field of such class). Add your custom basic types here.  | `''` |
| additionalReservedSqlKeywords | Comma-separated list of additional reserved SQL keywords (just in case the 337 keywords of nowadays SQL-* standards weren't enough)  | `''` |


## GrailsDomainStringPropertyMaxSize Rule

*Since CodeNarc 1.2*

String properties in Grails domain classes have to define maximum size otherwise the property is mapped to VARCHAR(255) causing runtime exceptions to occur.
To fix this issue either declare `size`* or `maxSize` constraint for the property inside `constraints` DSL closure of your Grails domain class or
declare the `type` of the property inside `mapping` DSL closure. If you use the second option inside **mapping** DSL closure then please pay attention that the value of  `type` is
not checked so using for example `VARCHAR(50)` would still cause runtime exceptions.

Example of violations:

```
    // both firstName and lastName will probably have database limit of 255 characters
    // which is not validated by Grails validation causing runtime JDBC exception
    class Person {

        String firstName
        String lastName

        static constraints = {
            firstName nullable:true
            lastName nullable:true
        }
    }
```

Example of valid configuration:

```
    class Person {

        String firstName
        String lastName

        static constraints = {
            firstName nullable:true, maxSize: 255
            lastName nullable:true
        }

        static mapping = {
            lastName type: 'text'
        }
    }
```


## GrailsDomainWithServiceReference Rule

*Since CodeNarc 0.19*

Checks that Grails Domain classes do not have Service classes injected.

This rule sets the default value of `applyToFilesMatching` to only match files
under the 'grails-app/domain' folder. You can override this with a different regular
expression value if appropriate.


## GrailsDuplicateConstraint Rule

*Since CodeNarc 0.18*

Check for duplicate name in a Grails domain class constraints. Duplicate names/entries are legal,
but can be confusing and error-prone.

NOTE: This rule does not check that the values of the entries are duplicated, only that there are two entries with the same name.

Example of violations:

```
    class Person {
        String firstName
        String lastName

        static constraints = {
            firstName nullable:true
            lastName nullable:true, maxSize:30
            firstName nullable:false                // violation
        }
    }
```


## GrailsDuplicateMapping Rule

*Since CodeNarc 0.18*

Check for duplicate name in a Grails domain class mapping. Duplicate names/entries are legal, but can be
confusing and error-prone.

NOTE: This rule does not check that the values of the entries are duplicated, only that there are two entries with the same name.

Example of violations:

```
    class Person {
        String firstName
        String lastName

        static mapping = {
            table 'people'
            firstName column: 'First_Name'
            lastName column: 'Last_Name'
            firstName column: 'First_Name'      // violation
            table 'people2'                     // violation
        }
    }
```


## GrailsMassAssignment Rule

*Since CodeNarc 0.21*

Untrusted input should not be allowed to set arbitrary object fields without restriction.

Example of violations:

```
   // Person would be a grails domain object
   def person = new Person(params)
   person.save()

   // or using .properties
   def person = Person.get(1)
   person.properties = params
   person.save()
```


## GrailsPublicControllerMethod Rule (disabled)

**NOTE:** This rule has been disabled by default (i.e., by setting its *enabled* property to
*false*). Given that Grails 2.x allows and encourages controller actions to be defined as methods
instead of closures, this rule makes no sense for Grails 2.x projects.

Rule that checks for public methods on Grails controller classes. Static methods are ignored.

Grails controller actions and interceptors are defined as properties on the controller class.
Public methods on a controller class are unnecessary. They break encapsulation and can
be confusing.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreMethodNames           | Specifies one or more (comma-separated) method names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

This rule sets the default value of `applyToFilesMatching` to only match files
under the 'grails-app/controllers' folder. You can override this with a different regular
expression value if appropriate.

This rule also sets the default value of `applyToClassNames` to only match class names
ending in 'Controller'. You can override this with a different class name pattern
(String with wildcards) if appropriate.


## GrailsServletContextReference Rule

Rule that checks for references to the `servletContext` object from within Grails controller and
taglib classes.

This rule is intended as a "governance" rule to enable monitoring and controlling access to the
`servletContext` from within application source code. Storing objects in the `servletContext`
may inhibit scalability and/or performance and should be carefully considered. Furthermore, access to
the `servletContext` is not synchronized, so reading/writing objects from the `servletContext`
must be manually synchronized, as described in
[The Definitive Guide to Grails (2nd edition)](http://www.amazon.com/Definitive-Grails-Second-Experts-Development/dp/1590599950).

Note that this rule does not check for direct access to the `servletContext` from within GSP
(Groovy Server Pages) files.

Enabling this rule may make most sense in a team environment where team members exhibit a broad
range of skill and experience levels. Appropriate `servletContext` access can be configured as exceptions
to this rule by configuring either the `doNotApplyToFilenames` or
`doNotApplyToFilesMatching` property of the rule. And, as always, it is easy to
just [turn off the rule](./codenarc-configuring-rules.html#Turning_Off_A_Rule) if it does not
make sense it your environment.

This rule sets the default value of `applyToFilesMatching` to only match files
under the 'grails-app/controllers' or 'grails-app/taglib' folders. You can override this
with a different regular expression value if appropriate.


## GrailsStatelessService Rule

Checks for non-`final` fields on a Grails service class. Grails service classes are singletons by
default, and so they should be reentrant. In most cases, this implies (or at least encourages)
that they should be stateless.

This rule ignores (i.e., does not cause violations for) the following:
  * All `final` fields (either instance or static). Note that fields that are `static` and non-`final`, however, do cause a violation.
  * Non-`static` properties (i.e., no visibility modifier specified) declared with `def`.
  * All classes annotated with the `@Immutable` transformation. See [http://groovy.codehaus.org/Immutable+transformation](http://groovy.codehaus.org/Immutable+transformation).
  * All fields annotated with the `@Inject` annotation.
  * All fields with names matching the *ignoreFieldNames* property.
  * All fields with types matching the *ignoreFieldTypes* property.

The `ignoreFieldNames` property of this rule is preconfigured to ignore the standard Grails
service configuration field names ('scope', 'transactional') and the standard injected bean names
('dataSource', 'sessionFactory'), as well as all other field names ending with 'Service'.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreFieldNames            | Specifies one or more (comma-separated) field names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `'dataSource,scope,sessionFactory,transactional,*Service'` |
| addToIgnoreFieldNames       | Specifies one or more (comma-separated) field names to be added to the `ignoreFieldNames` property value. This is a special write-only property, and each call to `setAddIgnoreFieldNames()` adds to (rather than overwrites) the list of field names to be ignored. | `null` |
| ignoreFieldTypes            | Specifies one or more (comma-separated) field types that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

This rule sets the default value of `applyToFilesMatching` to only match files under the
'grails-app/services' folder. You can override this with a different regular expression value if appropriate.

This rule also sets the default value of `applyToClassNames` to only match class names
ending in 'Service'. You can override this with a different class name pattern (String with wildcards)
if appropriate.


###  Notes

  1.  The `ignoreFieldTypes` property matches the field type name as indicated
  in the field declaration, only including a full package specification IF it is included in
  the source code. For example, the field declaration `BigDecimal value` matches
  an `ignoreFieldTypes` value of `BigDecimal`, but not
  `java.lang.BigDecimal`.

  2.  There is one exception for the `ignoreFieldTypes` property: if the field is declared
  with a modifier/type of `def`, then the type resolves to `java.lang.Object`.

