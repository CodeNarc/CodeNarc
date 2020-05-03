---
layout: default
title: CodeNarc - Rule Configuration for Grails, Spock, etc.
---

# CodeNarc - Rule Configuration for Grails, Spock, etc.

This page describes some useful rule configuration when running **CodeNarc** against code for the Grails and Spock frameworks. 
It is by no means complete or comprehensive. Suggestions and pull requests for corrections and additions are welcome: <https://github.com/CodeNarc/CodeNarc/issues>.

## Grails

If running **CodeNarc** against Grails code, you can copy the below into your `codenarc.properties` file.
Alternatively, you can set these config fields within your ruleset files (though the syntax will be slightly different).

```
# Grails uses def methods a lot.
NoDef.enabled=false

# Don't require static fields above instance fields for Grails domain classes. 
StaticFieldsBeforeInstanceFields.doNotApplyToFilesMatching=.+/grails\\-app/domain/.+

# Grails controller methods are often empty.
EmptyMethod.doNotApplyToClassNames=*Controller

# Grails service classes have several more common fields that do not violate reentrancy.
GrailsStatelessService.addToIgnoreFieldNames=grailsApplication,applicationContext,sessionFactory

# This will be the default in CodeNarc 2.0.
VariableName.finalRegex=null
```

## Spock

If running **CodeNarc** against Spock code, you can copy the below into your `codenarc.properties` file.
Alternatively, you can set these config fields within your ruleset files (though the syntax will be slightly different).

```
# Do not not complain about non-standard method names in Spec classes. 
# Admittedly this will also skip checking “regular” helper methods in those classes.
MethodName.doNotApplyToClassNames=*Spec,*Specification

# Spec methods may use boolean expressions to specify validation criteria, e.g. 
#     where:
#        code    || result
#        'EN'    || 'en'
UnnecessaryBooleanExpression.doNotApplyToClassNames=*Spec,*Specification

# Spock Specification methods conventionally use "def".
NoDef.doNotApplyToClassNames=*Spec,*Specification
# OR NoDef.enabled=false

# This will be the default in CodeNarc 2.0.
VariableName.finalRegex=null
```



