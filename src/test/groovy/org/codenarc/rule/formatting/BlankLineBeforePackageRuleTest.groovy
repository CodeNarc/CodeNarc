/*
 * Copyright 2014 the original author or authors.
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
 * Tests for BlankLineBeforePackageRule
 *
 * @author Joe Sondow
 */
@SuppressWarnings('ConsecutiveBlankLines')
class BlankLineBeforePackageRuleTest extends AbstractRuleTestCase<BlankLineBeforePackageRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'BlankLineBeforePackage'
    }

    @Test
    void testSuccessScenarioWithPackage() {
        final SOURCE = '''\
            package org.codenarc

            class MyClass {

                    def go() { /* ... */ }
                    def goSomewhere() { /* ... */ }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioWithoutPackage() {
        final SOURCE = '''\

            class MyClass {

                    def go() { /* ... */ }
                    def goSomewhere() { /* ... */ }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testFileStartsWithOneBlankLine() {
        final SOURCE = '''\

            package org.codenarc

            class MyClass {


                    void go() { /* ... */ }
            }
        '''
        assertSingleViolation(SOURCE, 0, '', 'Blank line precedes package declaration in file null')
    }

    @Test
    void testFileStartsWithDoubleBlankLines() {
        final SOURCE = '''\


            package org.codenarc

            class MyClass {


                    void go() { /* ... */ }
            }
        '''
        assertTwoViolations(SOURCE, 0, '', 1, '')
    }

    @Test
    void testBlankLineBetweenCommentAndPackage() {
        final SOURCE = '''\
            /* Copyleft EFF */

            package org.codenarc

            class MyClass {
                    void go() { /* ... */ }
            }
        '''
        assertSingleViolation(SOURCE, 1, '', 'Blank line precedes package declaration in file null')
    }

    @Test
    void testSuccessScenarioWithBlankLineInsideCommentBlock() {
        final SOURCE = '''\
            /*
             Comment block

             has a blank line
             */
            package org.codenarc

            class MyClass {

                    def go() { /* ... */ }
                    def goSomewhere() { /* ... */ }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected BlankLineBeforePackageRule createRule() {
        new BlankLineBeforePackageRule()
    }
}
