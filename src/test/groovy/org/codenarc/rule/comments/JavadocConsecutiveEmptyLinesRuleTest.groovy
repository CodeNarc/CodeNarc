/*
 * Copyright 2019 the original author or authors.
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
package org.codenarc.rule.comments

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for JavadocConsecutiveEmptyLinesRule
 *
 * @author Chris Mair
 */
@SuppressWarnings(['ConsecutiveBlankLines', 'JavadocConsecutiveEmptyLines', 'JavadocEmptyFirstLine', 'JavadocEmptyLastLine'])
class JavadocConsecutiveEmptyLinesRuleTest extends AbstractRuleTestCase<JavadocConsecutiveEmptyLinesRule> {

    private static final String VIOLATION_MESSAGE = 'The javadoc contains consecutive empty lines'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JavadocConsecutiveEmptyLines'
    }

    @Test
    void test_JavadocWithoutConsecutiveEmptyLines_NoViolations() {
        final SOURCE = '''
            /**
             * Sample class
             *
             * @author Some Developer
             */
            class MyClass {

                /**
                 * Return the calculated count of some stuff,
                 * starting with the specified startIndex.
                 *
                 * @param startIndex - the starting index
                 * @return the full count
                 * @throws RuntimeException upon the Rapture
                 */
                int countThings(int startIndex) {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_RegularComments_NoViolations() {
        final SOURCE = '''
            /**
             * Initialize starting values.
             * Do not call this ... ever!!
             */
            void initialize() { }

            /*
             * Some comment
             *
             *
             */
            void doThings(int startIndex) { }

            /*
                void otherMethod() {
                    doThings(5);
                }


            */
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_JavadocWithConsecutiveEmptyLines_Violations() {
        final SOURCE = '''
            /**
             * Sample class
             *
             * @author Some Developer
             *
             *
             */
            class MyClass {

                /**
                 *
                 *
                 */
                String name

                /**
                 * Return the calculated count of some stuff,
                 * starting with the specified startIndex.
                 *
                 *
                 * @param startIndex - the starting index
                 * @return the full count
                 * @throws RuntimeException upon the Singularity
                 *
                 * NOTE: Only the first occurrence of consecutive empty lines is found.
                 *       So the following lines are not flagged as violations!!!
                 *
                 *
                 */
                int countThings(int startIndex) {
                }
            }
        '''
        assertViolations(SOURCE,
                [line:7, source:' * ', message:VIOLATION_MESSAGE],
                [line:13, source:' * ', message:VIOLATION_MESSAGE],
                [line:21, source:' * ', message:VIOLATION_MESSAGE])
    }

    @Override
    protected JavadocConsecutiveEmptyLinesRule createRule() {
        new JavadocConsecutiveEmptyLinesRule()
    }
}
