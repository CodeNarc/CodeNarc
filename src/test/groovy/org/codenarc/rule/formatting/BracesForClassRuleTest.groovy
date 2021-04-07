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
 * Tests for BracesForClassRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class BracesForClassRuleTest extends AbstractRuleTestCase<BracesForClassRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BracesForClass'
        assert rule.sameLine == true
    }

    @Test
    void testMultilineDefinition() {
        final SOURCE = '''
            class MyClass
                        extends File {

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineDefinitionOfTrait() {
        final SOURCE = '''
            trait MyTrait {

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineDefinitionViolation() {
        final SOURCE = '''
            class MyClass
                        extends File
            {

            }
        '''
        assertSingleViolation(SOURCE, 4, '{')
    }

    @Test
    void testMultilineDefinitionOverride() {
        final SOURCE = '''
            class MyClass
                        extends File
            {

            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineDefinitionOverrideViolation() {
        final SOURCE = '''
            class MyClass
                        extends File {

            }
        '''
        rule.sameLine = false
        assertSingleViolation(SOURCE, 3, 'extends File {', 'Opening brace for the class MyClass should start on a new line')
    }

    @Test
    void testIgnoredForAnnotationTypeDefinition1() {
        final SOURCE = '''
            @interface MyClass {

            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoredForAnnotationTypeDefinition2() {
        final SOURCE = '''
            public @interface MyClass {

            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnumSuccessScenarioSameLine() {
        final SOURCE = '''
            enum MyEnum {

            }
        '''
        rule.sameLine = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnumViolationSameLine() {
        final SOURCE = '''
            enum MyEnum
            {

            }
        '''
        rule.sameLine = true
        assertSingleViolation(SOURCE, 3, '{', 'Opening brace for the class MyEnum should start on the same line')
    }

    @Test
    void testEnumSuccessScenarioNewLine() {
        final SOURCE = '''
            enum MyEnum
            {

            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnumViolationNewLine() {
        final SOURCE = '''
            enum MyEnum {

            }
        '''
        rule.sameLine = false
        assertSingleViolation(SOURCE, 2, '{', 'Opening brace for the class MyEnum should start on a new line')
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
    void testSameLineFalse_BracesWithinComment_KnownIssue_Violation() {
        rule.sameLine = false
        final SOURCE = '''
            class MyClass extends File  // What about {}
            {

            }
        '''
        assertSingleViolation(SOURCE, 2, 'class MyClass extends File  // What about {}', 'Opening brace for the class MyClass')
    }

    @Test
    void testViolationSameLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [line: 6, source: '{', message: 'Opening brace for the class First should start on the same line'],
                [line: 18, source: '{', message: 'Opening brace for the class Second should start on the same line'],
                [line: 64, source: '{', message: 'Opening brace for the interface Third should start on the same line'],
                [line: 70, source: '{', message: 'Opening brace for the class Forth should start on the same line'])
    }

    @Test
    void testViolationNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [line: 6, source: 'class First{', message: 'Opening brace for the class First should start on a new line'],
                [line: 14, source: 'class Second{', message: 'Opening brace for the class Second should start on a new line'],
                [line: 44, source: 'interface Third{', message: 'Opening brace for the interface Third should start on a new line'],
                [line: 49, source: 'private class Forth {', message: 'Opening brace for the class Forth should start on a new line'])
    }

    @Test
    void testNoViolationForScriptClassNodes() {
        final SOURCE = '''
             def x = """
             {
             }
             """
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected BracesForClassRule createRule() {
        new BracesForClassRule()
    }
}
