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
import org.junit.Test

/**
 * Tests for UnnecessaryPackageReferenceRule
 *
 * @author Chris Mair
 */
class UnnecessaryPackageReferenceRuleTest extends AbstractRuleTestCase<UnnecessaryPackageReferenceRule> {

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
            [line:3, source:'java.math.BigDecimal amount = 42.10', message:'java.math'],
            [line:4, source:'java.lang.Integer count = 0', message:'java.lang'],
            [line:5, source:'java.util.Map mapping', message:'java.util'])
    }

    @Test
    void testWithinExpressions_Violations() {
        final SOURCE = '''
            if (value.class == java.math.BigDecimal) { }
            println "isClosure=${value instanceof groovy.lang.Closure}"
            def processors = java.lang.Runtime.availableProcessors()
        '''
        assertViolations(SOURCE,
            [line:2, source:'if (value.class == java.math.BigDecimal) { }', message:'java.math'],
            [line:3, source:'println "isClosure=${value instanceof groovy.lang.Closure}"', message:'groovy.lang'],
            [line:4, source:'def processors = java.lang.Runtime.availableProcessors()', message:'java.lang'])
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
            [line:3, source:"def amount = new java.math.BigDecimal('42.10')", message:'java.math'],
            [line:4, source:"def url = new java.net.URL('http://abc@example.com')", message:'java.net'])
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
            [line:3, source:'java.math.BigInteger maxValue = 0', message:'java.math'],
            [line:4, source:'java.net.URI uri', message:'java.net'],
            [line:5, source:"groovy.lang.Closure closure = { println 'ok' }", message:'groovy.lang'])
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
            [line:2, source:'java.net.Socket getSocket() { }', message:'java.net'],
            [line:3, source:'java.io.Reader getReader() { }', message:'java.io'],
            [line:4, source:'groovy.util.AntBuilder getAntBuilder() { }', message:'groovy.util'])
    }

    @Test
    void testMethodParameterTypes_Violations() {
        final SOURCE = '''
            void writeCount(java.io.Writer writer, int count) { }
            void initializeBinding(String name, groovy.lang.Binding binding) { }
            void noViolation(def name, int intAutoBox) { }
        '''
        assertViolations(SOURCE,
            [line:2, source:'void writeCount(java.io.Writer writer, int count)', message:'java.io'],
            [line:3, source:'void initializeBinding(String name, groovy.lang.Binding binding) { }', message:'groovy.lang'])
    }

    @Test
    void testClosureParameterTypes_Violations() {
        final SOURCE = '''
            def writeCount = { java.io.Writer writer, int count -> }
            def initializeBinding = { String name, groovy.lang.Binding binding -> }
            def noViolation = { name, binding, int count -> }
        '''
        assertViolations(SOURCE,
            [line:2, source:'def writeCount = { java.io.Writer writer, int count -> }', message:'java.io'],
            [line:3, source:'def initializeBinding = { String name, groovy.lang.Binding binding -> }', message:'groovy.lang'])
    }

    @Test
    void testExtendsSuperclassOrSuperInterfaceTypes_Violations() {
        final SOURCE = '''
            class MyHashMap extends java.util.HashMap { }
            interface MyList extends java.util.List { }
        '''
        assertViolations(SOURCE,
            [line:2, source:'class MyHashMap extends java.util.HashMap { }', message:'java.util'],
            [line:3, source:'interface MyList extends java.util.List { }', message:'java.util'])
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
            [line:2, source:'class MyList implements java.util.List { }', message:'java.util'],
            [line:3, source:'class MyRange implements groovy.lang.Range { }', message:'groovy.lang'])
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
            [line:3, source:'def runnable = [:] as java.lang.Runnable', message:'java.lang'],
            [line:4, source:'def string = (java.lang.String)123', message:'java.lang'])
    }

    @Test
    void testAnonymousInnerClassDeclaration_Violation() {
        final SOURCE = '''
            def runnable = new java.lang.Runnable() {
                void run() { }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'java.lang.Runnable')
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
            [line:7, source:'void doStuff(javax.servlet.http.Cookie cookie, Cookie[] cookies)', message:'javax.servlet.http.Cookie'],
            [line:8, source:'def dataSource = [:] as javax.sql.DataSource', message:'javax.sql.DataSource'])
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
            [line:7, source:'void doStuff(javax.servlet.http.Cookie cookie, Cookie[] cookies)', message:'javax.servlet.http.Cookie'],
            [line:8, source:'def dataSource = new javax.sql.DataSource()', message:'javax.sql.DataSource'])
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

    @Override
    protected UnnecessaryPackageReferenceRule createRule() {
        new UnnecessaryPackageReferenceRule()
    }
}
