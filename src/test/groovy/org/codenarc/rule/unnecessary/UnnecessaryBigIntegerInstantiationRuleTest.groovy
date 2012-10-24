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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryBigIntegerInstantiationRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryBigIntegerInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryBigIntegerInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            assert 42G == foo()
            assert 42G == new BigInteger([] as byte[])
            assert 42G == new BigInteger("42", 10)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStringConstructor() {
        final SOURCE = '''
            new BigInteger("42")
        '''
        assertSingleViolation(SOURCE, 2, 'new BigInteger("42")', 'Can be rewritten as 42G')
    }

    protected Rule createRule() {
        new UnnecessaryBigIntegerInstantiationRule()
    }
}
