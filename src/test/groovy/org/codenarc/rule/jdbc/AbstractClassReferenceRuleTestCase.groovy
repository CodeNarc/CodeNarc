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
package org.codenarc.rule.jdbc

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Abstract superclass for tests of classes that check for class references.
 *
 * @author Chris Mair
 */
abstract class AbstractClassReferenceRuleTestCase extends AbstractRuleTestCase {

    /**
     * @return the name of the class to check for
     */
    abstract protected String getClassName()
    
    void testImport_Violation() {
        final SOURCE = """
            import ${getClassName()}
            class MyClass { }
        """
        assertSingleViolation(SOURCE, 2, "import ${getClassName()}", getClassName())
    }

    void testStaticImport_Violation() {
        final SOURCE = """
            import static ${getClassName()}.*
            class MyClass { }
        """
        assertSingleViolation(SOURCE, 2, "import static ${getClassName()}.*", getClassName())
    }

    void testFieldType_Violation() {
        final SOURCE = """
            class MyClass {
                ${getClassName()} connection
            }
        """
        assertSingleViolation(SOURCE, 3, "${getClassName()} connection", getClassName())
    }

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
            [lineNumber:2, sourceLineText:"if (value.class == ${getClassName()}) { }", messageText:getClassName()],
            [lineNumber:3, sourceLineText:"def isCorrectType = value instanceof ${getClassName()}", messageText:getClassName()],
            [lineNumber:4, sourceLineText:"def txLevel = ${getClassName()}.TRANSACTION_NONE", messageText:getClassName()],
            [lineNumber:7, sourceLineText:"def field = new ${getClassName()}()", messageText:getClassName()]
        )
    }

    void testConstructorCall_Violation() {
        final SOURCE = """
            def c = new ${getClassName()}()
        """
        assertSingleViolation(SOURCE, 2, "def c = new ${getClassName()}()", getClassName())
    }

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

    void testVariableType_Violation() {
        final SOURCE = """
            ${getClassName()} c = getConnection()
        """
        assertSingleViolation(SOURCE, 2, "${getClassName()} c = getConnection()", getClassName())
    }

    void testMethodReturnType_Violation() {
        final SOURCE = """
            class MyClass {
                ${getClassName()} getConnection() { }
            }
        """
        assertSingleViolation(SOURCE, 3, "${getClassName()} getConnection() { }", getClassName())
    }

    void testMethodParameterType_Violations() {
        final SOURCE = """
            void writeCount(${getClassName()} connection, int count) { }
            void initializeBinding(String name, ${getClassName()} connection) { }
        """
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:"void writeCount(${getClassName()} connection, int count) { }", messageText:getClassName()],
            [lineNumber:3, sourceLineText:"void initializeBinding(String name, ${getClassName()} connection) { }", messageText:getClassName()])
    }

    void testConstructorCall_Parameter_Violation() {
        final SOURCE = """
            def handler = new Handler(${getClassName()})
        """
        assertSingleViolation(SOURCE, 2, "def handler = new Handler(${getClassName()})", getClassName())
    }

    void testConstructorParameterType_Violation() {
        final SOURCE = """
            class MyClass {
                MyClass(${getClassName()} connection) { }
            }
        """
        assertSingleViolation(SOURCE, 3, "MyClass(${getClassName()} connection) { }", getClassName())
    }

    void testClosureParameterType_Violations() {
        final SOURCE = """
            def writeCount = { ${getClassName()} connection, int count -> }
            def initializeBinding = { String name, ${getClassName()} connection -> }
        """
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:"def writeCount = { ${getClassName()} connection, int count -> }", messageText:getClassName()],
            [lineNumber:3, sourceLineText:"def initializeBinding = { String name, ${getClassName()} connection -> }", messageText:getClassName()])
    }

    void testAsType_Violation() {
        final SOURCE = """
            def x = value as ${getClassName()}
        """
        assertSingleViolation(SOURCE, 2, "def x = value as ${getClassName()}", getClassName())
    }

    void testExtendsSuperclassOrSuperInterfaceTypes_Violations() {
        final SOURCE = """
            class MyConnection extends ${getClassName()} { }
            interface MyInterface extends ${getClassName()} { }
        """
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:"class MyConnection extends ${getClassName()} { }", messageText:getClassName()],
            [lineNumber:3, sourceLineText:"interface MyInterface extends ${getClassName()} { }", messageText:getClassName()])
    }

    void testAnonymousInnerClass_KnownIssue_NoViolation() {
        final SOURCE = """
            def x = new ${getClassName()}() { }
        """
        // TODO This should produce a violation
        assertNoViolations(SOURCE)
    }

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

    protected Rule createRule() {
        new JdbcConnectionReferenceRule()
    }
}