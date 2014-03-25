/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.codenarc.util.GroovyVersion
import org.junit.Test

/**
 * Tests for UnnecessaryPackageReferenceRule
 *
 * @author Chris Mair
 */
class UnnecessaryPackageReferenceRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryPackageReference'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyClass {
                Map myMap = [:]
                def dateFormat = java.text.SimpleDateFormat('MM')
                Integer calculate(javax.sql.DataSource dataSource) { }
                java.lang.annotation.RetentionPolicy getRetentionPolicy() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testFieldsTypes_Violations() {
        final SOURCE = '''
            class MyClass {
                java.math.BigDecimal amount = 42.10
                java.lang.Integer count = 0
                java.util.Map mapping
                def noViolation
                boolean noViolationBooleanAutoBox = false
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'java.math.BigDecimal amount = 42.10', messageText:'java.math'],
            [lineNumber:4, sourceLineText:'java.lang.Integer count = 0', messageText:'java.lang'],
            [lineNumber:5, sourceLineText:'java.util.Map mapping', messageText:'java.util'] )
    }

    @Test
    void testWithinExpressions_Violations() {
        final SOURCE = '''
            if (value.class == java.math.BigDecimal) { }
            println "isClosure=${value instanceof groovy.lang.Closure}"
            def processors = java.lang.Runtime.availableProcessors()
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (value.class == java.math.BigDecimal) { }', messageText:'java.math'],
            [lineNumber:3, sourceLineText:'println "isClosure=${value instanceof groovy.lang.Closure}"', messageText:'groovy.lang'],
            [lineNumber:4, sourceLineText:'def processors = java.lang.Runtime.availableProcessors()', messageText:'java.lang'] )
    }

    @Test
    void testConstructorCalls_Violations() {
        final SOURCE = '''
            class MyClass {
                def amount = new java.math.BigDecimal('42.10')
                def url = new java.net.URL('http://abc@example.com')
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:"def amount = new java.math.BigDecimal('42.10')", messageText:'java.math'],
            [lineNumber:4, sourceLineText:"def url = new java.net.URL('http://abc@example.com')", messageText:'java.net'] )
    }

    @Test
    void testConstructorCall_CallToSuper_NoViolation() {
        final SOURCE = '''
            class MyClass extends Object {
                MyClass() {
                    super('and')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstructorCall_CallToThis_NoViolation() {
        final SOURCE = '''
            class MyClass {
                private String name
                MyClass() {
                    this('Default')
                }
                private MyClass(String name) {
                    this.name = name
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableTypes_Violations() {
        final SOURCE = '''
            void doSomething() {
                java.math.BigInteger maxValue = 0
                java.net.URI uri
                groovy.lang.Closure closure = { println 'ok' }
                def noViolation = 123
                boolean noViolationBooleanAutoBox = false
                int noViolationInegertAutoBox = 99
                long noViolationLongAutoBox = 999999L
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'java.math.BigInteger maxValue = 0', messageText:'java.math'],
            [lineNumber:4, sourceLineText:'java.net.URI uri', messageText:'java.net'],
            [lineNumber:5, sourceLineText:"groovy.lang.Closure closure = { println 'ok' }", messageText:'groovy.lang'] )
    }

    @Test
    void testMethodReturnTypes_Violations() {
        final SOURCE = '''
            java.net.Socket getSocket() { }
            java.io.Reader getReader() { }
            groovy.util.AntBuilder getAntBuilder() { }
            def noViolation() { }
            int noViolationIntegerAutoBox() { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'java.net.Socket getSocket() { }', messageText:'java.net'],
            [lineNumber:3, sourceLineText:'java.io.Reader getReader() { }', messageText:'java.io'],
            [lineNumber:4, sourceLineText:'groovy.util.AntBuilder getAntBuilder() { }', messageText:'groovy.util'] )
    }

    @Test
    void testMethodParameterTypes_Violations() {
        final SOURCE = '''
            void writeCount(java.io.Writer writer, int count) { }
            void initializeBinding(String name, groovy.lang.Binding binding) { }
            void noViolation(def name, int intAutoBox) { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'void writeCount(java.io.Writer writer, int count)', messageText:'java.io'],
            [lineNumber:3, sourceLineText:'void initializeBinding(String name, groovy.lang.Binding binding) { }', messageText:'groovy.lang'] )
    }

    @Test
    void testClosureParameterTypes_Violations() {
        final SOURCE = '''
            def writeCount = { java.io.Writer writer, int count -> }
            def initializeBinding = { String name, groovy.lang.Binding binding -> }
            def noViolation = { name, binding, int count -> }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'def writeCount = { java.io.Writer writer, int count -> }', messageText:'java.io'],
            [lineNumber:3, sourceLineText:'def initializeBinding = { String name, groovy.lang.Binding binding -> }', messageText:'groovy.lang'] )
    }

    @Test
    void testExtendsSuperclassOrSuperInterfaceTypes_Violations() {
        final SOURCE = '''
            class MyHashMap extends java.util.HashMap { }
            interface MyList extends java.util.List { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyHashMap extends java.util.HashMap { }', messageText:'java.util'],
            [lineNumber:3, sourceLineText:'interface MyList extends java.util.List { }', messageText:'java.util'] )
    }

    @Test
    void testExplicitlyExtendJavaLangObject_KnownLimitation_NoViolations() {
        final SOURCE = '''
            class MyClass extends java.lang.Object { }
        '''
        // Known limitation
        assertNoViolations(SOURCE)
    }

    @Test
    void testImplementsInterfaceTypes_Violations() {
        final SOURCE = '''
            class MyList implements java.util.List { }
            class MyRange implements groovy.lang.Range { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyList implements java.util.List { }', messageText:'java.util'],
            [lineNumber:3, sourceLineText:'class MyRange implements groovy.lang.Range { }', messageText:'groovy.lang'] )
    }

    @Test
    void testAsType_Violations() {
        final SOURCE = '''
            class MyClass {
                def runnable = [:] as java.lang.Runnable
                def string = (java.lang.String)123
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'def runnable = [:] as java.lang.Runnable', messageText:'java.lang'],
            [lineNumber:4, sourceLineText:'def string = (java.lang.String)123', messageText:'java.lang'])
    }

    @Test
    void testAnonymousInnerClassDeclaration_KnownLimitation_NoViolation() {
        final SOURCE = '''
            def runnable = new java.lang.Runnable() {
                void run() { }
            }
        '''
        if (GroovyVersion.groovy1_8_OrGreater ) {
            assertSingleViolation(SOURCE, 2, 'java.lang.Runnable')
        }
        else {
            // Known limitation: Does not check anonymous inner class violations
            assertNoViolations(SOURCE)
        }
    }

    @Test
    void testPackageReferencesForExplicitlyImportedClasses_Violations() {
        final SOURCE = '''
            import javax.servlet.http.Cookie
            import javax.sql.DataSource
            import com.example.OtherClass

            class MyClass {
                void doStuff(javax.servlet.http.Cookie cookie, Cookie[] cookies) {
                    def dataSource = [:] as javax.sql.DataSource
                    DataSource dataSource2 = wrap(dataSource)
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:7, sourceLineText:'void doStuff(javax.servlet.http.Cookie cookie, Cookie[] cookies)', messageText:'javax.servlet.http.Cookie'],
            [lineNumber:8, sourceLineText:'def dataSource = [:] as javax.sql.DataSource', messageText:'javax.sql.DataSource'] )
    }

    @Test
    void testPackageReferencesForStarImports_Violations() {
        final SOURCE = '''
            import javax.servlet.http.*
            import javax.sql.*
            import com.example.OtherClass

            class MyClass {
                void doStuff(javax.servlet.http.Cookie cookie, Cookie[] cookies) {
                    def dataSource = new javax.sql.DataSource()
                    DataSource dataSource2 = wrap(dataSource)
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:7, sourceLineText:'void doStuff(javax.servlet.http.Cookie cookie, Cookie[] cookies)', messageText:'javax.servlet.http.Cookie'],
            [lineNumber:8, sourceLineText:'def dataSource = new javax.sql.DataSource()', messageText:'javax.sql.DataSource'] )
    }

    @Test
    void testEnums() {
        final SOURCE = '''
            package com.company.payment
            enum PaymentStatus {  APPROVED,  REJECTED,  CANCELLED,  UNDETERMINED}
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testScripts() {
        final SOURCE = '''
            package com.example

            println 'hello'
            '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryPackageReferenceRule()
    }
}
