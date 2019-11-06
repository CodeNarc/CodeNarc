---
layout: default
title: CodeNarc - Serialization Rules
---  

# Serialization Rules  ("*rulesets/serialization.xml*")


## EnumCustomSerializationIgnored Rule

*Since CodeNarc 0.19*

Checks for enums that define `writeObject()` or `writeReplace()` methods, or declare
`serialPersistentFields` or `serialVersionUID` fields, all of which are ignored for enums.

From the javadoc for `ObjectOutputStream`:

*The process by which enum constants are serialized cannot be customized; any class-specific writeObject
and writeReplace methods defined by enum types are ignored during serialization. Similarly, any
serialPersistentFields or serialVersionUID field declarations are also ignored--all enum types have a
fixed serialVersionUID of 0L.*

Example of violations:

```
    enum MyEnum {
        ONE, TWO, THREE
        private static final long serialVersionUID = 1234567L               // violation
        private static final ObjectStreamField[] serialPersistentFields =   // violation
            { new ObjectStreamField("name", String.class) }
        String name;

        Object writeReplace() throws ObjectStreamException { .. }      // violation
        private void writeObject(ObjectOutputStream stream) { .. }     // violation
    }
```


## SerialPersistentFields Rule

*Since CodeNarc 0.14*

To use a **Serializable** object's `serialPersistentFields` correctly, it must be declared `private`, `static`,
and `final`.

The Java Object Serialization Specification allows developers to manually define `Serializable` fields for a
class by specifying them in the `serialPersistentFields` array. This feature will only work if
`serialPersistentFields` is declared as `private`, `static`, and `final`. Also, specific to Groovy,
the field must be of type `ObjectStreamField[]`, and cannot be `Object`.

References:

    * Standards Mapping - Common Weakness Enumeration - (CWE) CWE ID 485

    * Sun Microsystems, Inc. Java Sun Tutorial

    []

Example of violations:

```
    class MyClass implements Serializable {
        public ObjectStreamField[] serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
    }

    // the JVM sees the field type as Object, which won't work
    class MyOtherClass implements Serializable {
        private static final serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
    }
```


## SerialVersionUID Rule

*Since CodeNarc 0.11*

A **serialVersionUID** is normally intended to be used with Serialization. It needs to be of type
`long`, `static`, and `final`. Also, it should be declared `private`. Providing
no modifier creates a *Property* and Groovy generates a *getter*, which is probably not intended.

From API javadoc for `java.io.Serializable`: *It is also strongly advised that explicit serialVersionUID declarations
use the private modifier where possible, since such declarations apply only to the immediately declaring
class--serialVersionUID fields are not useful as inherited members.*


## SerializableClassMustDefineSerialVersionUID Rule

*Since CodeNarc 0.13*

Classes that implement `Serializable` should define a `serialVersionUID`. Deserialization uses this number
to ensure that a loaded class corresponds exactly to a serialized object. If you don't define serialVersionUID, the
system will make one by hashing most of your class's features. Then if you change anything, the UID will change and
Java won't let you reload old data.

An example of a missing serialVersionUID:

```
    class MyClass implements Serializable {
        // missing serialVersionUID
    }
```

