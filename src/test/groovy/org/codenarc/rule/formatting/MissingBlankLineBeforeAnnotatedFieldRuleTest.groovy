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
    void ruleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MissingBlankLineBeforeAnnotatedField'
    }

    @Test
    void noViolations() {
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
    void noViolationsForAFieldWithMultipleAnnotations() {
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
    void testSingleViolation() {
        final SOURCE = '''
            class Invalid {
                @Delegate
                Foo foo
            }
        '''

        assertSingleViolation(SOURCE, 4, 'Foo foo', 'There is no blank line before the declaration for field "foo" that has annotations')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class Invalid {
                @Delegate
                Foo firstAnnotatedField
                @Delegate
                Bar secondAnnotatedField
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 4, sourceLineText: 'Foo firstAnnotatedField', messageText: 'There is no blank line before the declaration for field "firstAnnotatedField" that has annotations'],
            [lineNumber: 6, sourceLineText: 'Bar secondAnnotatedField', messageText: 'There is no blank line before the declaration for field "secondAnnotatedField" that has annotations']
        )
    }

    @Test
    void testViolationForAFieldWithMultipleAnnotations() {
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
    void noViolationsWhenFieldIsOnTheFirstLine() {
        final SOURCE = '''class A { @Delegate Foo annotatedField }'''

        assertNoViolations(SOURCE)
    }

    @Test
    void noViolationsWhenTheAnnotationIsOnTheSameLineAsTheField() {
        final SOURCE = '''
            class MyClass {
                private @Autowired DataSource dataSource
                private @Value('${name}') String name
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void noViolationWhenPreviousLineContainsAComment() {
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

    @Override
    protected MissingBlankLineBeforeAnnotatedFieldRule createRule() {
        new MissingBlankLineBeforeAnnotatedFieldRule()
    }
}
