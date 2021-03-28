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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for BracesForMethodRule
 *
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class BracesForMethodRuleTest extends AbstractRuleTestCase<BracesForMethodRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BracesForMethod'
    }

    @Test
    void test_Interfaces_NoViolations() {
        final SOURCE = '''
            interface MyInterface {
                def method()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultilineInterfaces_NoViolations() {
        final SOURCE = '''
            interface MyInterface
                    extends OtherInterface {
                def method()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultiLineMethodDeclarations_NoViolations() {
        final SOURCE = '''
            def myMethod1(String x,
                String y) {
            }

            def myMethod2(String x,
                String y)
                throws Exception {
            }

            def myMethod3()
                throws Exception,
                        OtherException {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultiLineMethodDeclarations_Violations() {
        final SOURCE = '''
            void method1(String x,
                String y)
            {
            }

            def method2(String x,
                String y)
                throws Exception
            {
            }

            void method3()
                throws Exception,
                        OtherException
            {
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 2, sourceLineText: 'void method1(String x,', messageText: 'Opening brace for the method method1 should start on the same line'],
                [lineNumber: 7, sourceLineText: 'def method2(String x,', messageText: 'Opening brace for the method method2 should start on the same line'],
                [lineNumber: 13, sourceLineText: 'void method3()', messageText: 'Opening brace for the method method3 should start on the same line'])
    }

    @Test
    void test_MultiLineMethodDeclarations_SameLineFalse_NoViolations() {
        final SOURCE = '''
            void method1(String x,
                String y)
            {
            }

            def method2(String x,
                String y)
                throws Exception
            {
            }

            void method3()
                throws Exception,
                        OtherException
            {
            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultiLineMethodDeclarations_SameLineFalse_Violations() {
        final SOURCE = '''
            void method1(String x,
                String y) {
            }

            def method2(String x,
                String y)
                throws Exception {
            }

            void method3()
                throws Exception,
                        OtherException {
            }
        '''
        rule.sameLine = false
        assertViolations(SOURCE,
                [lineNumber: 2, sourceLineText: 'void method1(String x,', messageText: 'Opening brace for the method method1 should start on a new line'],
                [lineNumber: 6, sourceLineText: 'def method2(String x,', messageText: 'Opening brace for the method method2 should start on a new line'],
                [lineNumber: 11, sourceLineText: 'void method3()', messageText: 'Opening brace for the method method3 should start on a new line'])
    }

    @Test
    void test_MultilineInterfacesOverride_NoViolations() {
        final SOURCE = '''
            interface MyInterface
                    extends OtherInterface
            {
                def method()
            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AbstractMethods_NoViolations() {
        final SOURCE = '''
            abstract class MyClass {
                abstract method()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultipleAnnotations_NoViolations() {
        final SOURCE = '''
            @Override
            @SuppressWarnings('parameter')  // for some reason the parameter is important and causes a failure
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultipleAnnotations2_NoViolations() {
        final SOURCE = '''
            @SuppressWarnings('parameter')  // for some reason the parameter is important and causes a failure
            @Override
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultipleAnnotations3_NoViolations() {
        final SOURCE = '''
            @Override
            @SuppressWarnings def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultipleAnnotations4_NoViolations() {
        final SOURCE = '''
            @Override
            @SuppressWarnings
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultipleAnnotations5_NoViolations() {
        final SOURCE = '''
            @Override
            @SuppressWarnings('parameter')  def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ClosureParameterSameLine_NoViolations() {
        final SOURCE = '''
            def method1(closure = {}) {
            }

            def method2(closure = {}) {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultiLineClosureParameterSameLine_NoViolations() {
        final SOURCE = '''
            def method(closure = {

            }) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultipleClosureParameterSameLine_NoViolations() {
        final SOURCE = '''
            def method(closure1 = {}, closure2 = {}) {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ClosureParameterSameLine_Violation() {
        final SOURCE = '''
            def method(closure = {})
            {
            }
        '''
        assertSingleViolation(SOURCE, 2, 'def method(closure = {})', 'Opening brace for the method method should start on the same line')
    }

    @Test
    void test_ClosureParameterNewLine_NoViolations() {
        final SOURCE = '''
            def method(closure = {})
            {
            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultiLineClosureParameterNewLine_NoViolations() {
        final SOURCE = '''
            def method(closure = {

            })
            {
            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ClosureParameterNewLine_Violation() {
        final SOURCE = '''
            def method(closure = {}) {
            }
        '''
        rule.sameLine = false
        assertSingleViolation(SOURCE, 2, 'def method(closure = {}) {', 'Opening brace for the method method should start on a new line')
    }

    @Test
    void test_SameLine_NoViolations() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void test_NewLine_NoViolations() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void test_SameLine_Violation() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 9, sourceLineText: 'First()', messageText: 'Opening brace for the method <init> should start on the same line'],
                [lineNumber: 12, sourceLineText: 'void method1()', messageText: 'Opening brace for the method method1 should start on the same line'],
                [lineNumber: 19, sourceLineText: 'public Second()', messageText: 'Opening brace for the method <init> should start on the same line'],
                [lineNumber: 38, sourceLineText: 'private int method2()', messageText: 'Opening brace for the method method2 should start on the same line'],
                [lineNumber: 71, sourceLineText: 'def singleLine()', messageText: 'Opening brace for the method singleLine should start on the same line'],
                [lineNumber: 80, sourceLineText: 'def multiLine()', messageText: 'Opening brace for the method multiLine should start on the same line'])
    }

    @Test
    void test_NewLine_Violation() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 9, sourceLineText: 'First(){}', messageText: 'Opening brace for the method <init> should start on a new line'],
                [lineNumber: 11, sourceLineText: 'void method1(){}', messageText: 'Opening brace for the method method1 should start on a new line'],
                [lineNumber: 15, sourceLineText: 'public Second(){', messageText: 'Opening brace for the method <init> should start on a new line'],
                [lineNumber: 27, sourceLineText: 'private int method2(){', messageText: 'Opening brace for the method method2 should start on a new line'],
                [lineNumber: 50, sourceLineText: 'def singleLine() {', messageText: 'Opening brace for the method singleLine should start on a new line'],
                [lineNumber: 56, sourceLineText: 'def multiLine() {', messageText: 'Opening brace for the method multiLine should start on a new line'])
    }

    @Test
    void test_SingleLineMethod_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int size() { groups.size() }
                AutoScalingGroupData get(int i) { groups[i] }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_SameLineFalse_GString_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int size(String name = "${SomeClass.SOME_CONSTANT}")
                {
                    return 99
                }
            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationsFollowedByCommentLine_SameLineTrue_NoViolations() {
        final SOURCE = '''
            class MyClass {
                @AnAnnotation
                // some comment
                void aMethod() {
                }

                @AnAnnotation("abc")
                // some comment
                void aMethod() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationsFollowedByCommentLine_SameLineFalse_NoViolations() {
        final SOURCE = '''
            class MyClass {
                @AnAnnotation
                // some comment
                void aMethod()
                {
                }

                @AnAnnotation("abc")
                // some comment
                void aMethod()
                {
                }
            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void test_BracesWithinComment_Violation() {
        final SOURCE = '''
            class MyClass {
                int size(String name)    // What about {}
                {
                    return 99
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'int size(String name)', 'Opening brace for the method size should start on the same line')
    }

    @Test
    void test_EndingParenthesisOnItsOwnLine() {
        final SOURCE = '''
            class RemoteWebDriverWithExpectations {
                RemoteWebDriverWithExpectations(
                    URL remoteAddress, Capabilities capabilities, List<String> ignoredCommands = DEFAULT_IGNORED_COMMANDS
                ) {
                    super(remoteAddress, capabilities)
                    this.ignoredCommands = ignoredCommands
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected BracesForMethodRule createRule() {
        new BracesForMethodRule()
    }
}
