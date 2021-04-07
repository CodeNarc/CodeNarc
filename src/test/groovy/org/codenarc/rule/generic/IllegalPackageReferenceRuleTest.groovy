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
package org.codenarc.rule.generic

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for IllegalPackageReferenceRule
 *
 * @author Chris Mair
 */
class IllegalPackageReferenceRuleTest extends AbstractRuleTestCase<IllegalPackageReferenceRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'IllegalPackageReference'
        assert rule.packageNames == null
    }

    @Test
    void testDefaultConfiguration_NoViolations() {
        final SOURCE = '''
            class MyClass {
                Map myMap = [:]
                def dateFormat = java.text.SimpleDateFormat('MM')
                Integer calculate(javax.sql.DataSource dataSource) { }
                java.lang.annotation.RetentionPolicy getRetentionPolicy() { }
            }
        '''
        assertNoViolations(SOURCE)
        assert !rule.ready
    }

    @Test
    void testFieldsTypes_Violations() {
        final SOURCE = '''
            class MyClass {
                java.math.BigDecimal amount = 42.10
                com.example.MyClass myClass
            }
        '''
        rule.packageNames = 'java.math'
        assertViolations(SOURCE,
            [line:3, source:'java.math.BigDecimal amount = 42.10', message:'java.math'])
    }

    @Test
    void testWithinExpressions_Violations() {
        final SOURCE = '''
            if (value.class == org.bad.BadClass) { }
            println "isClosure=${value instanceof org.bad.OtherClass}"
            def count = org.bad.Helper.getCount()
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:2, source:'if (value.class == org.bad.BadClass) { }', message:'org.bad'],
            [line:3, source:'println "isClosure=${value instanceof org.bad.OtherClass}"', message:'org.bad'],
            [line:4, source:'def count = org.bad.Helper.getCount()', message:'org.bad'])
    }

    @Test
    void testConstructorCalls_Violations() {
        final SOURCE = '''
            class MyClass {
                def amount = new org.bad.Value()
                def url = new org.bad.Name('ABC')
            }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:3, source:'def amount = new org.bad.Value()', message:'org.bad'],
            [line:4, source:"def url = new org.bad.Name('ABC')", message:'org.bad'])
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
        rule.packageNames = 'org.bad'
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableTypes_Violations() {
        final SOURCE = '''
            void doSomething() {
                org.bad.Value maxValue = 0
                org.bad.URI uri
                org.bad.Code code
                def noViolation = 123
            }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:3, source:'org.bad.Value maxValue = 0', message:'org.bad'],
            [line:4, source:'org.bad.URI uri', message:'org.bad'],
            [line:5, source:'org.bad.Code code', message:'org.bad'])
    }

    @Test
    void testMethodReturnTypes_Violations() {
        final SOURCE = '''
            org.bad.Socket getSocket() { }
            org.bad.Reader getReader() { }
            org.bad.AntBuilder getAntBuilder() { }
            def noViolation() { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:2, source:'org.bad.Socket getSocket() { }', message:'org.bad'],
            [line:3, source:'org.bad.Reader getReader() { }', message:'org.bad'],
            [line:4, source:'org.bad.AntBuilder getAntBuilder() { }', message:'org.bad'])
    }

    @Test
    void testMethodParameterTypes_Violations() {
        final SOURCE = '''
            void writeCount(org.bad.Writer writer, int count) { }
            void initializeBinding(String name, org.bad.Binding binding) { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:2, source:'void writeCount(org.bad.Writer writer, int count)', message:'org.bad'],
            [line:3, source:'void initializeBinding(String name, org.bad.Binding binding) { }', message:'org.bad'])
    }

    @Test
    void testConstructorCall_Parameter_Violation() {
        final SOURCE = '''
            def handler = new Handler(org.bad.Request)
        '''
        rule.packageNames = 'org.bad'
        assertSingleViolation(SOURCE, 2, 'def handler = new Handler(org.bad.Request)', 'org.bad')
    }

    @Test
    void testConstructorParameterType_Violation() {
        final SOURCE = '''
            class MyClass {
                MyClass(org.bad.Stuff stuff) { }
            }
        '''
        rule.packageNames = 'org.bad'
        assertSingleViolation(SOURCE, 3, 'MyClass(org.bad.Stuff stuff) { }', 'org.bad')
    }

    @Test
    void testClosureParameterTypes_Violations() {
        final SOURCE = '''
            def writeCount = { org.bad.Writer writer, int count -> }
            def initializeBinding = { String name, org.bad.Binding binding -> }
            def noViolation = { name, binding, int count -> }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:2, source:'def writeCount = { org.bad.Writer writer, int count -> }', message:'org.bad'],
            [line:3, source:'def initializeBinding = { String name, org.bad.Binding binding -> }', message:'org.bad'])
    }

    @Test
    void testAsType_Violation() {
        final SOURCE = '''
            def x = value as org.bad.Widget
        '''
        rule.packageNames = 'org.bad'
        assertSingleViolation(SOURCE, 2, 'def x = value as org.bad.Widget', 'org.bad')
    }

    @Test
    void testExtendsSuperclassOrSuperInterfaceTypes_Violations() {
        final SOURCE = '''
            class MyHashMap extends org.bad.HashMap { }
            class MyScript extends org.bad.Script { }
            interface MyList extends org.bad.List { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:2, source:'class MyHashMap extends org.bad.HashMap { }', message:'org.bad'],
            [line:3, source:'class MyScript extends org.bad.Script { }', message:'org.bad'],
            [line:4, source:'interface MyList extends org.bad.List { }', message:'org.bad'])
    }

    @Test
    void testImplementsInterfaceTypes_Violations() {
        final SOURCE = '''
            class MyList implements org.bad.List { }
            class MyRange implements org.bad.Range { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:2, source:'class MyList implements org.bad.List { }', message:'org.bad'],
            [line:3, source:'class MyRange implements org.bad.Range { }', message:'org.bad'])
    }

    @Test
    void testImports_Violations() {
        final SOURCE = '''
            import org.bad.BadStuff
            import org.bad.other.Stuff
            import org.evil.*
            class MyList {
                BadStuff badStuff = new Stuff()
            }
        '''
        rule.packageNames = 'org.bad, org.evil'
        assertViolations(SOURCE,
            [line:2, source:'import org.bad.BadStuff', message:'org.bad'],
            [line:4, source:'import org.evil.*', message:'org.evil'])
    }

    @Test
    void testStaticImports_Violations() {
        final SOURCE = '''
            import org.other.Stuff
            import static org.bad.BadUtil.*
            class MyList {
                def name = BAD_NAME
            }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:3, source:'import static org.bad.BadUtil.*', message:'org.bad'])
    }

    @Test
    void testMultiplePackageNames_SingleViolation() {
        final SOURCE = '''
            class MyClass {
                java.text.DateFormat dateFormat = 'MM'
                com.example.service.MyService myService
            }
        '''
        rule.packageNames = 'com.other,com.example.service'
        assertViolations(SOURCE,
            [line:4, source:'com.example.service.MyService myService', message:'com.example.service'])
    }

    @Test
    void testMultiplePackageNames_MultipleViolations() {
        final SOURCE = '''
            class MyHashMap extends org.bad.HashMap {
                def myMethod() { println 'ok' }
                int getCount(com.example.Widget widget) { return widget.count }
            }
        '''
        rule.packageNames = 'com.example,com.example,org.bad,com.other.ignore'
        assertViolations(SOURCE,
            [line:2, source:'class MyHashMap extends org.bad.HashMap {', message:'org.bad'],
            [line:4, source:'int getCount(com.example.Widget widget) { return widget.count }', message:'com.example'])
    }

    @Test
    void testMultiplePackageNames_Wildcards_MultipleViolations() {
        final SOURCE = '''
            import com.example.ExampleHelper
            class MyClass implements com.example.print.Printable {
                def myMethod() { println 'ok' }
                int getCount(org.bad.widget.Widget widget) { return widget.count }
            }
        '''
        rule.packageNames = 'com.ex?mple*,org.*,com.other.*'
        assertViolations(SOURCE,
            [line:2, source:'import com.example.ExampleHelper', message:'com.example'],
            [line:3, source:'class MyClass implements com.example.print.Printable {', message:'com.example.print'],
            [line:5, source:'int getCount(org.bad.widget.Widget widget) { return widget.count }', message:'org.bad'])
    }

    @Test
    void testAnonymousInnerClass_KnownIssue_NoViolation() {
        final SOURCE = '''
            def x = new org.bad.Handler() { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [line:2, source:'def x = new org.bad.Handler() { }', message:'org.bad'])
    }

    @Override
    protected IllegalPackageReferenceRule createRule() {
        new IllegalPackageReferenceRule()
    }
}
