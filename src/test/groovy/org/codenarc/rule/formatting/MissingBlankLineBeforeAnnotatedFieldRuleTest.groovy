/*
 * Copyright 2020 the original author or authors.
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
 * Tests for MissingBlankLineBeforeAnnotatedFieldRule
 */
class MissingBlankLineBeforeAnnotatedFieldRuleTest extends AbstractRuleTestCase<MissingBlankLineBeforeAnnotatedFieldRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MissingBlankLineBeforeAnnotatedField'
    }

    @Test
    void test_BlankLinesBeforeAnnotation_NoViolations() {
        assertNoViolations '''
            class Valid {

                @Delegate
                Foo firstAnnotatedField

                @Delegate
                Bar secondAnnotatedField
                FooBar notAnnotatedField
            }
        '''
    }

    @Test
    void test_FieldWithMultipleAnnotations_BlankLineBeforeFirstAnnotation_NoViolation() {
        assertNoViolations '''
            class Valid {
                Foo firstAnnotatedField

                @Delegate
                @PackageScope
                Bar secondAnnotatedField
            }
        '''
    }

    @Test
    void test_MissingBlankLinesBefore_MultipleViolations() {
        final SOURCE = '''
            class Invalid {
                private static final NAME = 'abc'
                @Delegate
                Foo firstAnnotatedField
                @Delegate
                Bar secondAnnotatedField
            }
        '''
        assertViolations(SOURCE,
            [line: 5, source: 'Foo firstAnnotatedField', message: 'There is no blank line before the declaration for field "firstAnnotatedField" that has annotations'],
            [line: 7, source: 'Bar secondAnnotatedField', message: 'There is no blank line before the declaration for field "secondAnnotatedField" that has annotations']
        )
    }

    @Test
    void test_FieldWithMultipleAnnotations_Violation() {
        final SOURCE = '''
            class Valid {
                Foo firstAnnotatedField
                @Delegate
                @PackageScope Bar secondAnnotatedField
            }
        '''
        assertSingleViolation(SOURCE, 5, 'Bar secondAnnotatedField', 'There is no blank line before the declaration for field "secondAnnotatedField" that has annotations')
    }

    @Test
    void test_OnSameLineAsClassDeclaration_NoViolation() {
        final SOURCE = '''class A { @Delegate Foo annotatedField }'''

        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationIsOnTheSameLineAsTheField_NoViolation() {
        final SOURCE = '''
            class MyClass {
                private @Autowired DataSource dataSource
                private @Value('${name}') String name
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_PreviousLineContainsAComment_NoViolation() {
        final SOURCE = '''
            class Valid {
                // Foo
                @Delegate
                Foo foo
                /* Bar */
                @Delegate
                Bar bar
                /** Fizz **/
                @Delegate
                Fizz fizz
                /**
                 * Bizz
                 */
                @Delegate
                Bizz
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationOnTheFirstLineOfTheClass_NoViolation() {
        final SOURCE = '''
            abstract class JerseySpec extends Specification {
                @Delegate
                private JerseyTest jerseyTest
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationOnTheFirstLineOfTheClassWithAnnotations_NoViolation() {
        final SOURCE = '''
            @SomeClassAnnotation
            abstract class JerseySpec extends Specification {
                @Delegate
                private JerseyTest jerseyTest
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationOnTheFirstLineOfTheClassWithMultipleAnnotations_NoViolation() {
        final SOURCE = '''
            @SomeClassAnnotation1
            @SomeClassAnnotation2
            @SomeClassAnnotation3
            abstract class JerseySpec extends Specification {
                @Delegate
                private JerseyTest jerseyTest
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected MissingBlankLineBeforeAnnotatedFieldRule createRule() {
        new MissingBlankLineBeforeAnnotatedFieldRule()
    }
}
