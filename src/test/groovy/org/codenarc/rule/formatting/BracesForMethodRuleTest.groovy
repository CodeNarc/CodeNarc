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
                [line: 2, source: 'void method1(String x,', message: 'Opening brace for the method method1 should start on the same line'],
                [line: 7, source: 'def method2(String x,', message: 'Opening brace for the method method2 should start on the same line'],
                [line: 13, source: 'void method3()', message: 'Opening brace for the method method3 should start on the same line'])
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
                [line: 2, source: 'void method1(String x,', message: 'Opening brace for the method method1 should start on a new line'],
                [line: 6, source: 'def method2(String x,', message: 'Opening brace for the method method2 should start on a new line'],
                [line: 11, source: 'void method3()', message: 'Opening brace for the method method3 should start on a new line'])
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
                [line: 9, source: 'First()', message: 'Opening brace for the method <init> should start on the same line'],
                [line: 12, source: 'void method1()', message: 'Opening brace for the method method1 should start on the same line'],
                [line: 19, source: 'public Second()', message: 'Opening brace for the method <init> should start on the same line'],
                [line: 38, source: 'private int method2()', message: 'Opening brace for the method method2 should start on the same line'],
                [line: 71, source: 'def singleLine()', message: 'Opening brace for the method singleLine should start on the same line'],
                [line: 80, source: 'def multiLine()', message: 'Opening brace for the method multiLine should start on the same line'])
    }

    @Test
    void test_NewLine_Violation() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [line: 9, source: 'First(){}', message: 'Opening brace for the method <init> should start on a new line'],
                [line: 11, source: 'void method1(){}', message: 'Opening brace for the method method1 should start on a new line'],
                [line: 15, source: 'public Second(){', message: 'Opening brace for the method <init> should start on a new line'],
                [line: 27, source: 'private int method2(){', message: 'Opening brace for the method method2 should start on a new line'],
                [line: 50, source: 'def singleLine() {', message: 'Opening brace for the method singleLine should start on a new line'],
                [line: 56, source: 'def multiLine() {', message: 'Opening brace for the method multiLine should start on a new line'])
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

    @Test
    void test_OpeningBraceIsOnItsOwnLine_allowBraceOnNextLineForMultilineDeclarationsTrue_NoViolations() {
        final SOURCE = '''
            class RemoteWebDriverWithExpectations {
                RemoteWebDriverWithExpectations(
                    URL remoteAddress, Capabilities capabilities, List<String> ignoredCommands = DEFAULT_IGNORED_COMMANDS)
                {
                    super(remoteAddress, capabilities)
                    this.ignoredCommands = ignoredCommands
                }
            }
        '''
        rule.sameLine = true
        rule.allowBraceOnNextLineForMultilineDeclarations = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OpeningBraceIsOnItsOwnLine_withLineComment_allowBraceOnNextLineForMultilineDeclarationsTrue_NoViolations() {
        final SOURCE = '''
            class RemoteWebDriverWithExpectations {
                RemoteWebDriverWithExpectations(
                    URL remoteAddress, Capabilities capabilities, List<String> ignoredCommands = DEFAULT_IGNORED_COMMANDS)
                { // Some comment
                    super(remoteAddress, capabilities)
                    this.ignoredCommands = ignoredCommands
                }
            }
        '''
        rule.sameLine = true
        rule.allowBraceOnNextLineForMultilineDeclarations = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OpeningBraceIsOnItsOwnLine_withExceptions_allowBraceOnNextLineForMultilineDeclarationsTrue_NoViolations() {
        final SOURCE = '''
            class RemoteWebDriverWithExpectations {
                RemoteWebDriverWithExpectations(
                    URL remoteAddress, Capabilities capabilities, List<String> ignoredCommands = DEFAULT_IGNORED_COMMANDS)
                    throws Exception, OtherException
                {
                    super(remoteAddress, capabilities)
                    this.ignoredCommands = ignoredCommands
                }
            }
        '''
        rule.sameLine = true
        rule.allowBraceOnNextLineForMultilineDeclarations = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OpeningBraceIsOnItsOwnLine_withExceptions_withLineComment_allowBraceOnNextLineForMultilineDeclarationsTrue_NoViolations() {
        final SOURCE = '''
            class RemoteWebDriverWithExpectations {
                RemoteWebDriverWithExpectations(
                    URL remoteAddress, Capabilities capabilities, List<String> ignoredCommands = DEFAULT_IGNORED_COMMANDS)
                    throws Exception, OtherException
                { // Some comment
                    super(remoteAddress, capabilities)
                    this.ignoredCommands = ignoredCommands
                }
            }
        '''
        rule.sameLine = true
        rule.allowBraceOnNextLineForMultilineDeclarations = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OpeningBraceIsOnItsOwnLine_allowBraceOnNextLineForMultilineDeclarationsFalse_Violations() {
        final SOURCE = '''
            class RemoteWebDriverWithExpectations {
                RemoteWebDriverWithExpectations(
                    URL remoteAddress, Capabilities capabilities, List<String> ignoredCommands = DEFAULT_IGNORED_COMMANDS)
                {
                    super(remoteAddress, capabilities)
                    this.ignoredCommands = ignoredCommands
                }
            }
        '''
        rule.sameLine = true
        rule.allowBraceOnNextLineForMultilineDeclarations = false
        assertSingleViolation(SOURCE, 3, 'RemoteWebDriverWithExpectations(', 'Opening brace for the method <init> should start on the same line')
    }

    @Test
    void test_OpeningBraceIsOnItsOwnLine_withExceptions_allowBraceOnNextLineForMultilineDeclarationsFalse_Violations() {
        final SOURCE = '''
            class RemoteWebDriverWithExpectations {
                RemoteWebDriverWithExpectations(
                    URL remoteAddress, Capabilities capabilities, List<String> ignoredCommands = DEFAULT_IGNORED_COMMANDS)
                    throws Exception, OtherException
                {
                    super(remoteAddress, capabilities)
                    this.ignoredCommands = ignoredCommands
                }
            }
        '''
        rule.sameLine = true
        rule.allowBraceOnNextLineForMultilineDeclarations = false
        assertSingleViolation(SOURCE, 3, 'RemoteWebDriverWithExpectations(', 'Opening brace for the method <init> should start on the same line')
    }

    @Override
    protected BracesForMethodRule createRule() {
        new BracesForMethodRule()
    }
}
