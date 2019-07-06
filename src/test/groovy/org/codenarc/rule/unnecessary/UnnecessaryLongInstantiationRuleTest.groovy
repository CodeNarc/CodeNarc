/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for UnnecessaryLongInstantiationRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryLongInstantiationRuleTest extends AbstractRuleTestCase<UnnecessaryLongInstantiationRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryLongInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            assert 42L == foo()
            assert 42L == new Long([] as char[])
            assert 42L == new Long("42", 10)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStringConstructor() {
        final SOURCE = '''
            new Long("42")
        '''
        assertSingleViolation(SOURCE, 2, 'new Long("42")', 'Can be rewritten as 42L')
    }

    @Test
    void testLongConstructor() {
        final SOURCE = '''
            new Long(42L)
        '''
        assertSingleViolation(SOURCE, 2, 'new Long(42L)', 'Can be rewritten as 42L')
    }

    @Override
    protected UnnecessaryLongInstantiationRule createRule() {
        new UnnecessaryLongInstantiationRule()
    }
}
