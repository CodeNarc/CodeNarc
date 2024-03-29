/*
 * Copyright 2013 the original author or authors.
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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for IllegalStringRule
 *
 * @author Chris Mair
 */
class IllegalStringRuleTest extends AbstractRuleTestCase<IllegalStringRule> {

    private static final TEXT = '@author Joe'

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'IllegalString'
    }

    @Test
    void testStringIsNull() {
        final SOURCE = 'class MyClass { } '
        rule.string = null
        assert !rule.ready
        assertNoViolations(SOURCE)
    }

    @Test
    void testStringIsNotPresent() {
        final SOURCE = '''
            /** @author Mike */
            class MyClass {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStringIsPresent_Comment() {
        final SOURCE = '''
            /** @author Joe */
            class MyClass {
            }
        '''
        assertSingleViolation(SOURCE, 2, null, ['string', TEXT])
    }

    @Test
    void testStringIsPresent_String() {
        final SOURCE = """
            class MyClass {
                int count;
                String name = '$TEXT' 
            }
        """
        assertSingleViolation(SOURCE, 4, null, ['string', TEXT])
    }

    @Override
    protected IllegalStringRule createRule() {
        new IllegalStringRule(string:TEXT)
    }
}
