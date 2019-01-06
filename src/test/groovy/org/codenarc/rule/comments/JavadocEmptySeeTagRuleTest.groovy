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
 * Tests for JavadocEmptySeeTagRule
 *
 * @author Chris Mair
 */
@SuppressWarnings('JavadocEmptySeeTag')
class JavadocEmptySeeTagRuleTest extends AbstractRuleTestCase<JavadocEmptySeeTagRule> {

    private static final String VIOLATION_MESSAGE = 'The javadoc @see tag is empty'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JavadocEmptySeeTag'
    }

    @Test
    void test_JavadocWithoutEmptySeeTag_NoViolations() {
        final SOURCE = '''
            /**
             * Sample class
             * @see java.lang.Object
             */
            class MyClass {

                /**
                 * Return the calculated count of some stuff,
                 * starting with the specified startIndex.
                 *
                 * @param startIndex - the starting index
                 * @return the full count
                 * @see java.lang.Object
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
             * @see
             */
            void doThings(int startIndex) { }

            /*
                @see
                void otherMethod() {
                    doThings(5);
                }
            */
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_JavadocWithEmptySeeTag_Violations() {
        final SOURCE = '''
            /**
             * Sample class
             *
             * @see
             */
            class MyClass {

                /**
                 * Return the calculated count of some stuff,
                 * starting with the specified startIndex.
                 *
                 * @param startIndex - the starting index
                 * @return the full count
                 * @throws RuntimeException
                 *     @see
                 *
                 * NOTE: Only the first occurrence of an empty @see tag is found.
                 *       So the following line is not flagged as violations!!!
                 * @see
                 */
                int countThings(int startIndex) { }

                /**
                 *@see
                 */
                String name = 'joe'
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:5, sourceLineText:'* @see', messageText:VIOLATION_MESSAGE],
                [lineNumber:16, sourceLineText:'*     @see', messageText:VIOLATION_MESSAGE],
                [lineNumber:25, sourceLineText:'*@see', messageText:VIOLATION_MESSAGE])
    }

    @Override
    protected JavadocEmptySeeTagRule createRule() {
        new JavadocEmptySeeTagRule()
    }
}
