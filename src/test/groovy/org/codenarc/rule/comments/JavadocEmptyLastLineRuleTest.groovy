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
import org.junit.jupiter.api.Test

/**
 * Tests for JavadocEmptyLastLineRule
 *
 * @author Chris Mair
 */
@SuppressWarnings('JavadocEmptyLastLine')
class JavadocEmptyLastLineRuleTest extends AbstractRuleTestCase<JavadocEmptyLastLineRule> {

    private static final String SOURCE_WITH_VIOLATIONS = '''
        /**
         * Sample class
         *
         * @author Some Developer
         *
         */
        class MyClass {

            /**
             * Return the calculated count of some stuff,
             * starting with the specified startIndex.
             *
             * @param startIndex - the starting index
             * @return the full count
             * @throws RuntimeException if it runs out of coffee
             *
             */
            int countThings(int startIndex) {
            }
        }
    '''

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JavadocEmptyLastLine'
    }

    @Test
    void test_JavadocWithoutEmptyLastLines_NoViolations() {
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
                 * @throws RuntimeException if it becomes angry
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
    void test_JavadocWithEmptyLastLines_Violations() {
        assertViolations(SOURCE_WITH_VIOLATIONS,
                [line:6, source:' * ', message:'The last line of the javadoc is empty'],
                [line:17, source:' * ', message:'The last line of the javadoc is empty'])
    }

    @Test
    void test_JavadocWithEmptyLastLines_WindowsLineEndings_Violations() {
        final SOURCE = SOURCE_WITH_VIOLATIONS.replace('\n', '\r\n')
        assertViolations(SOURCE,
                [line:6, source:' * ', message:'The last line of the javadoc is empty'],
                [line:17, source:' * ', message:'The last line of the javadoc is empty'])
    }

    @Override
    protected JavadocEmptyLastLineRule createRule() {
        new JavadocEmptyLastLineRule()
    }
}
