/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.generic

import static org.codenarc.test.TestUtil.containsAll

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for RequiredRegexRule
 *
 * @author Chris Mair
  */
class RequiredRegexRuleTest extends AbstractRuleTestCase<RequiredRegexRule> {

    static skipTestThatUnrelatedCodeHasNoViolations
    static skipTestThatInvalidCodeHasNoViolations

    private static final REGEX = /\@author Joe/

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'RequiredRegex'
    }

    @Test
    void testRegexIsNull() {
        final SOURCE = 'class MyClass { } '
        rule.regex = null
        assert !rule.ready
        assertNoViolations(SOURCE)
    }

    @Test
    void testRegexIsPresent() {
        final SOURCE = '''
            /** @author Joe */
            class MyClass {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testRegexIsNotPresent() {
        final SOURCE = '''
            /** @author Mike */
            class MyClass {
            }
        '''
        assertSingleViolation(SOURCE) { v -> containsAll(v.message, ['regular expression', REGEX]) }
    }

    @Override
    protected RequiredRegexRule createRule() {
        new RequiredRegexRule(regex:REGEX)
    }

}
