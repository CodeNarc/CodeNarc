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
import org.junit.jupiter.api.Test

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
    void testTrait_Multiline_NoViolation() {
        final SOURCE = '''
            trait MyTrait {

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTrait_Multiline_Violation() {
        final SOURCE = '''
            class MyClass
                        extends File
            {

            }
        '''
        assertSingleViolation(SOURCE, 4, '{')
    }

    @Test
    void testClass_Multiline_NoViolation() {
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
    void testClass_Multiline_sameLineFalse_Violation() {
        final SOURCE = '''
            class MyClass
                        extends File {

            }
        '''
        rule.sameLine = false
        assertSingleViolation(SOURCE, 3, 'extends File {', 'Opening brace for the class MyClass should start on a new line')
    }

    @Test
    void testAnnotationTypeDefinition_Ignored() {
        final SOURCE = '''
            @interface MyClass {

            }
            
            public @interface MyClass2 {

            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnum_BraceSameLine_NoViolation() {
        final SOURCE = '''
            enum MyEnum {

            }
        '''
        rule.sameLine = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnum_BraceNextLine_Violation() {
        final SOURCE = '''
            enum MyEnum
            {

            }
        '''
        rule.sameLine = true
        assertSingleViolation(SOURCE, 3, '{', 'Opening brace for the class MyEnum should start on the same line')
    }

    @Test
    void testEnum_Brace_NextLine_sameLineFalse_NoViolation() {
        final SOURCE = '''
            enum MyEnum
            {

            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnum_BraceSameLine_sameLineFalse_Violation() {
        final SOURCE = '''
            enum MyEnum {

            }
        '''
        rule.sameLine = false
        assertSingleViolation(SOURCE, 2, '{', 'Opening brace for the class MyEnum should start on a new line')
    }

    @Test
    void test_SameLine_NoViolations() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void test_NextLine_sameLineFalse_NoViolations() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void testClass_sameLineFalse_BracesWithinComment_KnownIssue_Violation() {
        rule.sameLine = false
        final SOURCE = '''
            class MyClass extends File  // What about {}
            {

            }
        '''
        assertSingleViolation(SOURCE, 2, 'class MyClass extends File  // What about {}', 'Opening brace for the class MyClass')
    }

    @Test
    void test_NextLine_Violations() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [line: 6, source: '{', message: 'Opening brace for the class First should start on the same line'],
                [line: 18, source: '{', message: 'Opening brace for the class Second should start on the same line'],
                [line: 64, source: '{', message: 'Opening brace for the interface Third should start on the same line'],
                [line: 70, source: '{', message: 'Opening brace for the class Forth should start on the same line'])
    }

    @Test
    void test_SameLine_sameLineFalse_Violations() {
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
    void testScript_NoViolation() {
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
