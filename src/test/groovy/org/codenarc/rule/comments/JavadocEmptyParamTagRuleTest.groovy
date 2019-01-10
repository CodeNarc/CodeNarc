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
 * Tests for JavadocEmptyParamTagRule
 *
 * @author Chris Mair
 */
@SuppressWarnings('JavadocEmptyParamTag')
class JavadocEmptyParamTagRuleTest extends AbstractRuleTestCase<JavadocEmptyParamTagRule> {

    private static final String VIOLATION_MESSAGE = 'The javadoc @param tag is empty'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JavadocEmptyParamTag'
    }

    @Test
    void test_JavadocWithoutEmptySeeTag_NoViolations() {
        final SOURCE = '''
            class MyClass {

                /**
                 * Return the calculated count of some stuff.
                 * @param startIndex - the starting index
                 * @param endIndex the ending index
                 * @return the full count
                 */
                int countThings(int startIndex, int endIndex) {
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
             * @param startIndex
             */
            void doThings(int startIndex) { }

            /*
                * @param startIndex
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
            class MyClass {

                /**
                 * Return the calculated count of some stuff.
                 *
                 * @param
                 * @return the full count
                 * @throws RuntimeException
                 *
                 * NOTE: Only the first occurrence of an empty @param tag is found.
                 *       So the following line is not flagged as a violation!!!
                 * @param
                 */
                int countThings(int startIndex) { }

                /**
                 *@param
                 */
                String otherMethod() { }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:7, sourceLineText:'* @param', messageText:VIOLATION_MESSAGE],
                [lineNumber:18, sourceLineText:'*@param', messageText:VIOLATION_MESSAGE])
    }

    @Override
    protected JavadocEmptyParamTagRule createRule() {
        new JavadocEmptyParamTagRule()
    }
}
