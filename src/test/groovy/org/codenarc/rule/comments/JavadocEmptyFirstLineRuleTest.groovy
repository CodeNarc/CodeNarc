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
 * Tests for JavadocEmptyFirstLineRule
 *
 * @author Chris Mair
 */
@SuppressWarnings('JavadocEmptyFirstLine')
class JavadocEmptyFirstLineRuleTest extends AbstractRuleTestCase<JavadocEmptyFirstLineRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JavadocEmptyFirstLine'
    }

    @Test
    void test_JavadocWithoutEmptyFirstLines_NoViolations() {
        final SOURCE = '''
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
                 * @throws RuntimeException
                 *
                 */
                int countThings(int startIndex) {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_JavadocWithEmptyFirstLines_Violations() {
        final SOURCE = '''
            /**
             *
             * Sample class
             *
             * @author Some Developer
             */
            class MyClass {

                /**
                 *
                 * Return the calculated count of some stuff,
                 * starting with the specified startIndex.
                 *
                 * @param startIndex - the starting index
                 * @return the full count
                 * @throws RuntimeException
                 */
                int countThings(int startIndex) {
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'/**', messageText:'The first line of the javadoc is empty'],
            [lineNumber:10, sourceLineText:'/**', messageText:'The first line of the javadoc is empty'])
    }

    @Override
    protected JavadocEmptyFirstLineRule createRule() {
        new JavadocEmptyFirstLineRule()
    }
}
