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
 * Tests for JdbcConnectionReferenceRule
 *
 * @author Chris Mair
 */
class JdbcConnectionReferenceRuleTest extends AbstractRuleTestCase {

    private static final CONNECTION_CLASS_NAME = 'java.sql.Connection'

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JdbcConnectionReference'
    }

    void testImport_Violation() {
        final SOURCE = '''
            import java.sql.Connection
            class MyClass { }
        '''
        assertSingleViolation(SOURCE, 2, 'import java.sql.Connection', CONNECTION_CLASS_NAME)
    }

    void testStaticImport_Violation() {
        final SOURCE = '''
            import static java.sql.Connection.*
            class MyClass { }
        '''
        assertSingleViolation(SOURCE, 2, 'import static java.sql.Connection.*', CONNECTION_CLASS_NAME)
    }

    void testFieldType_Violation() {
        final SOURCE = '''
            class MyClass {
                java.sql.Connection connection
            }
        '''
        assertSingleViolation(SOURCE, 3, 'java.sql.Connection connection', CONNECTION_CLASS_NAME)
    }

    void testWithinExpressions_Violations() {
        final SOURCE = '''
            if (value.class == java.sql.Connection) { }
            println "isClosure=${value instanceof java.sql.Connection}"
            def txLevel = java.sql.Connection.TRANSACTION_NONE

            class MyClass {
                def field = new java.sql.Connection()
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (value.class == java.sql.Connection) { }', messageText:CONNECTION_CLASS_NAME],
            [lineNumber:3, sourceLineText:'println "isClosure=${value instanceof java.sql.Connection}"', messageText:CONNECTION_CLASS_NAME],
            [lineNumber:4, sourceLineText:'def txLevel = java.sql.Connection.TRANSACTION_NONE', messageText:CONNECTION_CLASS_NAME],
            [lineNumber:7, sourceLineText:'def field = new java.sql.Connection()', messageText:CONNECTION_CLASS_NAME]
        )
    }

    void testConstructorCall_Violation() {
        final SOURCE = '''
            def c = new java.sql.Connection()
        '''
        assertSingleViolation(SOURCE, 2, 'def c = new java.sql.Connection()', CONNECTION_CLASS_NAME)
    }

    void testVariableType_Violation() {
        final SOURCE = '''
            java.sql.Connection c = getConnection()
        '''
        assertSingleViolation(SOURCE, 2, 'java.sql.Connection c = getConnection()', CONNECTION_CLASS_NAME)
    }

    void testMethodReturnType_Violation() {
        final SOURCE = '''
            class MyClass {
                java.sql.Connection getConnection() { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'java.sql.Connection getConnection() { }', CONNECTION_CLASS_NAME)
    }

    void testMethodParameterType_Violations() {
        final SOURCE = '''
            void writeCount(java.sql.Connection connection, int count) { }
            void initializeBinding(String name, java.sql.Connection connection) { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'void writeCount(java.sql.Connection connection, int count) { }', messageText:CONNECTION_CLASS_NAME],
            [lineNumber:3, sourceLineText:'void initializeBinding(String name, java.sql.Connection connection) { }', messageText:CONNECTION_CLASS_NAME])
    }

    void testConstructorCall_ParameterType_Violation() {
        final SOURCE = '''
            def handler = new Handler(java.sql.Connection)
        '''
        assertSingleViolation(SOURCE, 2, 'def handler = new Handler(java.sql.Connection)', CONNECTION_CLASS_NAME)
    }

    void testConstructorParameterType_Violation() {
        final SOURCE = '''
            class MyClass {
                MyClass(java.sql.Connection connection) { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'MyClass(java.sql.Connection connection) { }', CONNECTION_CLASS_NAME)
    }

    void testClosureParameterType_Violations() {
        final SOURCE = '''
            def writeCount = { java.sql.Connection connection, int count -> }
            def initializeBinding = { String name, java.sql.Connection connection -> }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'def writeCount = { java.sql.Connection connection, int count -> }', messageText:CONNECTION_CLASS_NAME],
            [lineNumber:3, sourceLineText:'def initializeBinding = { String name, java.sql.Connection connection -> }', messageText:CONNECTION_CLASS_NAME])
    }

    void testAsType_Violation() {
        final SOURCE = '''
            def x = value as java.sql.Connection
        '''
        assertSingleViolation(SOURCE, 2, 'def x = value as java.sql.Connection', CONNECTION_CLASS_NAME)
    }

    void testExtendsSuperclassOrSuperInterfaceTypes_Violations() {
        final SOURCE = '''
            class MyConnection extends java.sql.Connection { }
            interface MyInterface extends java.sql.Connection { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyConnection extends java.sql.Connection { }', messageText:CONNECTION_CLASS_NAME],
            [lineNumber:3, sourceLineText:'interface MyInterface extends java.sql.Connection { }', messageText:CONNECTION_CLASS_NAME])
    }

    void testAnonymousInnerClass_KnownIssue_NoViolation() {
        final SOURCE = '''
            def x = new java.sql.Connection() { }
        '''
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