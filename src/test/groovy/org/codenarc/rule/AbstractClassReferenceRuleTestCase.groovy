/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule

import org.junit.Test

/**
 * Abstract superclass for tests of classes that check for class references.
 *
 * @author Chris Mair
 */
abstract class AbstractClassReferenceRuleTestCase<T extends Rule> extends AbstractRuleTestCase<T> {

    /**
     * @return the name of the class to check for
     */
    protected abstract String getClassName()

    // Subclasses can override (or define property) to customize violation message
    protected String getViolationMessage() {
        "Found reference to ${getClassName()}"
    }

    //------------------------------------------------------------------------------------
    // Common Tests
    //------------------------------------------------------------------------------------

    @Test
    void testImport_Violation() {
        final SOURCE = """
            import ${getClassName()}
            class MyClass { }
        """
        assertSingleViolation(SOURCE, 2, "import ${getClassName()}", violationMessage)
    }

    @Test
    void testImport_SuppressWarnings() {
        final SOURCE = """
            @SuppressWarnings('${rule.name}')
            import ${getClassName()}

            class MyClass { }
        """
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticImport_Violation() {
        final SOURCE = """
            import static ${getClassName()}.*
            class MyClass { }
        """
        assertSingleViolation(SOURCE, 2, "import static ${getClassName()}.*", violationMessage)
    }

    @Test
    void testStaticImport_SuppressWarnings() {
        final SOURCE = """
            @SuppressWarnings('${rule.name}')
            import static ${getClassName()}.*

            class MyClass { }
        """
        assertNoViolations(SOURCE)
    }

    @Test
    void testFieldType_Violation() {
        final SOURCE = """
            class MyClass {
                ${getClassName()} connection
            }
        """
        assertSingleViolation(SOURCE, 3, "${getClassName()} connection", violationMessage)
    }

    @Test
    void testWithinExpressions_Violations() {
        final SOURCE = """
            if (value.class == ${getClassName()}) { }
            def isCorrectType = value instanceof ${getClassName()}
            def txLevel = ${getClassName()}.TRANSACTION_NONE

            class MyClass {
                def field = new ${getClassName()}()
            }
        """
        assertViolations(SOURCE,
            [line:2, source:"if (value.class == ${getClassName()}) { }", message:violationMessage],
            [line:3, source:"def isCorrectType = value instanceof ${getClassName()}", message:violationMessage],
            [line:4, source:"def txLevel = ${getClassName()}.TRANSACTION_NONE", message:violationMessage],
            [line:7, source:"def field = new ${getClassName()}()", message:violationMessage]
        )
    }

    @Test
    void testConstructorCall_Violation() {
        final SOURCE = """
            def c = new ${getClassName()}()
        """
        assertSingleViolation(SOURCE, 2, "def c = new ${getClassName()}()", violationMessage)
    }

    @Test
    void testConstructorCall_CallToSuper_NoViolation() {
        final SOURCE = """
            class MyClass extends Object {
                MyClass() {
                    super('and')
                }
            }
        """
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableType_Violation() {
        final SOURCE = """
            ${getClassName()} c = getConnection()
        """
        assertSingleViolation(SOURCE, 2, "${getClassName()} c = getConnection()", violationMessage)
    }

    @Test
    void testMethodReturnType_Violation() {
        final SOURCE = """
            class MyClass {
                ${getClassName()} getConnection() { }
            }
        """
        assertSingleViolation(SOURCE, 3, "${getClassName()} getConnection() { }", violationMessage)
    }

    @Test
    void testMethodParameterType_Violations() {
        final SOURCE = """
            void writeCount(${getClassName()} connection, int count) { }
            void initializeBinding(String name, ${getClassName()} connection) { }
        """
        assertViolations(SOURCE,
            [line:2, source:"void writeCount(${getClassName()} connection, int count) { }", message:violationMessage],
            [line:3, source:"void initializeBinding(String name, ${getClassName()} connection) { }", message:violationMessage])
    }

    @Test
    void testConstructorCall_Parameter_Violation() {
        final SOURCE = """
            def handler = new Handler(${getClassName()})
        """
        assertSingleViolation(SOURCE, 2, "def handler = new Handler(${getClassName()})", violationMessage)
    }

    @Test
    void testConstructorParameterType_Violation() {
        final SOURCE = """
            class MyClass {
                MyClass(${getClassName()} connection) { }
            }
        """
        assertSingleViolation(SOURCE, 3, "MyClass(${getClassName()} connection) { }", violationMessage)
    }

    @Test
    void testClosureParameterType_Violations() {
        final SOURCE = """
            def writeCount = { ${getClassName()} connection, int count -> }
            def initializeBinding = { String name, ${getClassName()} connection -> }
        """
        assertViolations(SOURCE,
            [line:2, source:"def writeCount = { ${getClassName()} connection, int count -> }", message:violationMessage],
            [line:3, source:"def initializeBinding = { String name, ${getClassName()} connection -> }", message:violationMessage])
    }

    @Test
    void testAsType_Violation() {
        final SOURCE = """
            def x = value as ${getClassName()}
        """
        assertSingleViolation(SOURCE, 2, "def x = value as ${getClassName()}", violationMessage)
    }

    @Test
    void testExtendsSuperclassOrSuperInterfaceTypes_Violations() {
        final SOURCE = """
            class MyConnection extends ${getClassName()} { }
            interface MyInterface extends ${getClassName()} { }
        """
        assertViolations(SOURCE,
            [line:2, source:"class MyConnection extends ${getClassName()} { }", message:violationMessage],
            [line:3, source:"interface MyInterface extends ${getClassName()} { }", message:violationMessage])
    }

    @Test
    void testAnonymousInnerClass_Violation() {
        final SOURCE = """
            def x = new ${getClassName()}() { }
        """
        assertSingleViolation(SOURCE, 2, "def x = new ${getClassName()}() { }", violationMessage)
    }

    @Test
    void testOtherConnectionClasses_NoViolations() {
        final SOURCE = '''
            import org.example.dating.Connection
            class MyClass {
                def c = new org.codenarc.Connection()
                Connection createConnection() { }
            }
        '''
        assertNoViolations(SOURCE)
    }
}
