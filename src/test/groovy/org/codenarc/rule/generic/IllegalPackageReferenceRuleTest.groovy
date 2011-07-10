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
import org.codenarc.rule.Rule

/**
 * Tests for IllegalPackageReferenceRule
 *
 * @author Chris Mair
 */
class IllegalPackageReferenceRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'IllegalPackageReference'
        assert rule.packageNames == null
    }

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

    void testFieldsTypes_Violations() {
        final SOURCE = '''
            class MyClass {
                java.math.BigDecimal amount = 42.10
                com.example.MyClass myClass
            }
        '''
        rule.packageNames = 'java.math'
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'java.math.BigDecimal amount = 42.10', messageText:'java.math'])
    }

    void testWithinExpressions_Violations() {
        final SOURCE = '''
            if (value.class == org.bad.BadClass) { }
            println "isClosure=${value instanceof org.bad.OtherClass}"
            def count = org.bad.Helper.getCount()
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (value.class == org.bad.BadClass) { }', messageText:'org.bad'],
            [lineNumber:3, sourceLineText:'println "isClosure=${value instanceof org.bad.OtherClass}"', messageText:'org.bad'],
            [lineNumber:4, sourceLineText:'def count = org.bad.Helper.getCount()', messageText:'org.bad'] )
    }

    void testConstructorCalls_Violations() {
        final SOURCE = '''
            class MyClass {
                def amount = new org.bad.Value()
                def url = new org.bad.Name('ABC')
            }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'def amount = new org.bad.Value()', messageText:'org.bad'],
            [lineNumber:4, sourceLineText:"def url = new org.bad.Name('ABC')", messageText:'org.bad'] )
    }

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
            [lineNumber:3, sourceLineText:'org.bad.Value maxValue = 0', messageText:'org.bad'],
            [lineNumber:4, sourceLineText:'org.bad.URI uri', messageText:'org.bad'],
            [lineNumber:5, sourceLineText:'org.bad.Code code', messageText:'org.bad'] )
    }

    void testMethodReturnTypes_Violations() {
        final SOURCE = '''
            org.bad.Socket getSocket() { }
            org.bad.Reader getReader() { }
            org.bad.AntBuilder getAntBuilder() { }
            def noViolation() { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'org.bad.Socket getSocket() { }', messageText:'org.bad'],
            [lineNumber:3, sourceLineText:'org.bad.Reader getReader() { }', messageText:'org.bad'],
            [lineNumber:4, sourceLineText:'org.bad.AntBuilder getAntBuilder() { }', messageText:'org.bad'] )
    }

    void testMethodParameterTypes_Violations() {
        final SOURCE = '''
            void writeCount(org.bad.Writer writer, int count) { }
            void initializeBinding(String name, org.bad.Binding binding) { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'void writeCount(org.bad.Writer writer, int count)', messageText:'org.bad'],
            [lineNumber:3, sourceLineText:'void initializeBinding(String name, org.bad.Binding binding) { }', messageText:'org.bad'] )
    }

    void testConstructorCall_Parameter_Violation() {
        final SOURCE = '''
            def handler = new Handler(org.bad.Request)
        '''
        rule.packageNames = 'org.bad'
        assertSingleViolation(SOURCE, 2, 'def handler = new Handler(org.bad.Request)', 'org.bad')
    }

    void testConstructorParameterType_Violation() {
        final SOURCE = '''
            class MyClass {
                MyClass(org.bad.Stuff stuff) { }
            }
        '''
        rule.packageNames = 'org.bad'
        assertSingleViolation(SOURCE, 3, 'MyClass(org.bad.Stuff stuff) { }', 'org.bad')
    }

    void testClosureParameterTypes_Violations() {
        final SOURCE = '''
            def writeCount = { org.bad.Writer writer, int count -> }
            def initializeBinding = { String name, org.bad.Binding binding -> }
            def noViolation = { name, binding, int count -> }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'def writeCount = { org.bad.Writer writer, int count -> }', messageText:'org.bad'],
            [lineNumber:3, sourceLineText:'def initializeBinding = { String name, org.bad.Binding binding -> }', messageText:'org.bad'] )
    }

    void testAsType_Violation() {
        final SOURCE = '''
            def x = value as org.bad.Widget
        '''
        rule.packageNames = 'org.bad'
        assertSingleViolation(SOURCE, 2, 'def x = value as org.bad.Widget', 'org.bad')
    }

    void testExtendsSuperclassOrSuperInterfaceTypes_Violations() {
        final SOURCE = '''
            class MyHashMap extends org.bad.HashMap { }
            class MyScript extends org.bad.Script { }
            interface MyList extends org.bad.List { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyHashMap extends org.bad.HashMap { }', messageText:'org.bad'],
            [lineNumber:3, sourceLineText:'class MyScript extends org.bad.Script { }', messageText:'org.bad'],
            [lineNumber:4, sourceLineText:'interface MyList extends org.bad.List { }', messageText:'org.bad'] )
    }

    void testImplementsInterfaceTypes_Violations() {
        final SOURCE = '''
            class MyList implements org.bad.List { }
            class MyRange implements org.bad.Range { }
        '''
        rule.packageNames = 'org.bad'
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyList implements org.bad.List { }', messageText:'org.bad'],
            [lineNumber:3, sourceLineText:'class MyRange implements org.bad.Range { }', messageText:'org.bad'] )
    }

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
            [lineNumber:2, sourceLineText:'import org.bad.BadStuff', messageText:'org.bad'],
            [lineNumber:4, sourceLineText:'import org.evil.*', messageText:'org.evil'] )
    }

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
            [lineNumber:3, sourceLineText:'import static org.bad.BadUtil.*', messageText:'org.bad'] )
    }

    void testMultiplePackageNames_SingleViolation() {
        final SOURCE = '''
            class MyClass {
                java.text.DateFormat dateFormat = 'MM'
                com.example.service.MyService myService
            }
        '''
        rule.packageNames = 'com.other,com.example.service'
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:'com.example.service.MyService myService', messageText:'com.example.service'])
    }

    void testMultiplePackageNames_MultipleViolations() {
        final SOURCE = '''
            class MyHashMap extends org.bad.HashMap {
                def myMethod() { println 'ok' }
                int getCount(com.example.Widget widget) { return widget.count }
            }
        '''
        rule.packageNames = 'com.example,com.example,org.bad,com.other.ignore'
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyHashMap extends org.bad.HashMap {', messageText:'org.bad'],
            [lineNumber:4, sourceLineText:'int getCount(com.example.Widget widget) { return widget.count }', messageText:'com.example'] )
    }

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
            [lineNumber:2, sourceLineText:'import com.example.ExampleHelper', messageText:'com.example'],
            [lineNumber:3, sourceLineText:'class MyClass implements com.example.print.Printable {', messageText:'com.example.print'],
            [lineNumber:5, sourceLineText:'int getCount(org.bad.widget.Widget widget) { return widget.count }', messageText:'org.bad'] )
    }

    void testAnonymousInnerClass_KnownIssue_NoViolation() {
        final SOURCE = '''
            def x = new org.bad.Handler() { }
        '''
        // TODO This should produce a violation
        rule.packageNames = 'org.bad'
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new IllegalPackageReferenceRule()
    }
}