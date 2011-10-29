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

/**
 * Tests for BracesForMethodRule
 *
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author Hamlet D'Arcy
  */
class BracesForMethodRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BracesForMethod'
    }

    void testInterfaces() {
        final SOURCE = '''
            interface MyInterface {
                def method()
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testMultilineInterfaces() {
        final SOURCE = '''
            interface MyInterface
                    extends OtherInterface {
                def method()
            }
        '''
        assertNoViolations(SOURCE)
    }

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

    void testAbstractMethods() {
        final SOURCE = '''
            abstract class MyClass {
                abstract method() 
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testMultipleAnnotations() {
        final SOURCE = '''
            @Override
            @SuppressWarnings('parameter')  // for some reason the parameter is important and causes a failure
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testMultipleAnnotations2() {
        final SOURCE = '''
            @SuppressWarnings('parameter')  // for some reason the parameter is important and causes a failure
            @Override
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testMultipleAnnotations3() {
        final SOURCE = '''
            @Override
            @SuppressWarnings def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testMultipleAnnotations4() {
        final SOURCE = '''
            @Override
            @SuppressWarnings
            def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testMultipleAnnotations5() {
        final SOURCE = '''
            @Override
            @SuppressWarnings('parameter')  def method() {
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testSuccessScenarioSameLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    void testSuccessScenarioNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    void testViolationSameLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 9, sourceLineText: 'First()', messageText: 'Opening brace for the method <init> should start on the same line'],
                [lineNumber: 12, sourceLineText: 'void method1()', messageText: 'Opening brace for the method method1 should start on the same line'],
                [lineNumber: 19, sourceLineText: 'public Second()', messageText: 'Opening brace for the method <init> should start on the same line'],
                [lineNumber: 38, sourceLineText: 'private int method2()', messageText: 'Opening brace for the method method2 should start on the same line'])
    }

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

    protected Rule createRule() {
        new BracesForMethodRule()
    }
}