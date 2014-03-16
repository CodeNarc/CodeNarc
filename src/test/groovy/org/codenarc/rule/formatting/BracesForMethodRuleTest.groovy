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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for BracesForMethodRule
 *
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author Hamlet D'Arcy
  */
class BracesForMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BracesForMethod'
    }

    @Test
    void testInterfaces() {
        final SOURCE = '''
            interface MyInterface {
                def method()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineInterfaces() {
        final SOURCE = '''
            interface MyInterface
                    extends OtherInterface {
                def method()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultiLineMethods() {
        final SOURCE = '''
            def myMethod1(String x,
                String y) {

                }

            def myMethod2(String x,
                String y)
                throws Exception {

                }

            def myMethod3()
                throws Exception {

                }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineInterfacesOverride() {
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
    void testAbstractMethods() {
        final SOURCE = '''
            abstract class MyClass {
                abstract method() 
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultipleAnnotations() {
        final SOURCE = '''
            @Override
            @SuppressWarnings('parameter')  // for some reason the parameter is important and causes a failure
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultipleAnnotations2() {
        final SOURCE = '''
            @SuppressWarnings('parameter')  // for some reason the parameter is important and causes a failure
            @Override
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultipleAnnotations3() {
        final SOURCE = '''
            @Override
            @SuppressWarnings def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultipleAnnotations4() {
        final SOURCE = '''
            @Override
            @SuppressWarnings
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultipleAnnotations5() {
        final SOURCE = '''
            @Override
            @SuppressWarnings('parameter')  def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioClosureParameterSameLine() {
        final SOURCE = '''
            def method1(closure = {}) {
            }

            def method2(closure = {}) {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioMultiLineClosureParameterSameLine() {
        final SOURCE = '''
            def method(closure = {

            }) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioMultipleClosureParameterSameLine() {
        final SOURCE = '''
            def method(closure1 = {}, closure2 = {}) {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationClosureParameterSameLine() {
        final SOURCE = '''
            def method(closure = {})
            {
            }
        '''
        assertSingleViolation(SOURCE, 2, 'def method(closure = {})', 'Opening brace for the method method should start on the same line')
    }

    @Test
    void testSuccessScenarioClosureParameterNewLine() {
        final SOURCE = '''
            def method(closure = {})
            {
            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioMultiLineClosureParameterNewLine() {
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
    void testViolationClosureParameterNewLine() {
        final SOURCE = '''
            def method(closure = {}) {
            }
        '''
        rule.sameLine = false
        assertSingleViolation(SOURCE, 2, 'def method(closure = {}) {', 'Opening brace for the method method should start on a new line')
    }

    @Test
    void testSuccessScenarioSameLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationSameLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 9, sourceLineText: 'First()', messageText: 'Opening brace for the method <init> should start on the same line'],
                [lineNumber: 12, sourceLineText: 'void method1()', messageText: 'Opening brace for the method method1 should start on the same line'],
                [lineNumber: 19, sourceLineText: 'public Second()', messageText: 'Opening brace for the method <init> should start on the same line'],
                [lineNumber: 38, sourceLineText: 'private int method2()', messageText: 'Opening brace for the method method2 should start on the same line'])
    }

    @Test
    void testViolationNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 9, sourceLineText: 'First(){}', messageText: 'Opening brace for the method <init> should start on a new line'],
                [lineNumber: 11, sourceLineText: 'void method1(){}', messageText: 'Opening brace for the method method1 should start on a new line'],
                [lineNumber: 15, sourceLineText: 'public Second(){', messageText: 'Opening brace for the method <init> should start on a new line'],
                [lineNumber: 27, sourceLineText: 'private int method2(){', messageText: 'Opening brace for the method method2 should start on a new line'])
    }

    @Test
    void testSingleLineMethod_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int size() { groups.size() }
                AutoScalingGroupData get(int i) { groups[i] }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new BracesForMethodRule()
    }
}
