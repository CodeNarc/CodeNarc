/*
 * Copyright 2011 the original author or authors.
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
 * Tests for UnnecessarySubstringRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessarySubstringRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessarySubstring'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            myVar[5..-1]
            myVar[1..5]
            myVar.substring()   // not enough parms
            myVar.substring(1, 2, 3) // too many parms
            Integer.substring(1)     // clearly a static call
            Integer.substring(1, 2)     // clearly a static call
            myVar.substring(begin: 5)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testOneParmSubstring() {
        final SOURCE = '''
            myVar.substring(5)
        '''
        assertSingleViolation(SOURCE, 2, 'myVar.substring(5)', 'The String.substring(int) method can be replaced with the subscript operator')
    }

    @Test
    void testTwoParmSubstring() {
        final SOURCE = '''
            myVar.substring(1, 5)
        '''
        assertSingleViolation(SOURCE, 2, 'myVar.substring(1, 5)', 'The String.substring(int, int) method can be replaced with the subscript operator')
    }

    protected Rule createRule() {
        new UnnecessarySubstringRule()
    }
}
