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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for InterfaceNameRule
 *
 * @author Chris Mair
  */
class InterfaceNameRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'InterfaceName'
        assert rule.regex == null
    }

    @Test
    void testRegexIsNull() {
        final SOURCE = 'interface aaa$bbb{ }'
        assert !rule.ready
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_WithPackage_MatchesRegex() {
        final SOURCE = '''
            package org.codenarc.sample
            interface AbstractClass { }
        '''
        rule.regex = /[A-Z].*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_DoesNotMatchRegex() {
        final SOURCE = ' interface MyInterface { } '
        rule.regex = /z.*/
        assertSingleViolation(SOURCE, 1, 'interface MyInterface')
    }

    @Test
    void testApplyTo_MatchesRegex() {
        final SOURCE = ' interface zClass { } '
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonInterfaceClass() {
        final SOURCE = ' class nonInterfaceClass { } '
        rule.regex = /[A-Z].*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Interface() {
        final SOURCE = 'abstract class abstractClass { }'
        rule.regex = /[A-Z].*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoClassDefinition() {
        rule.regex = /[A-Z].*/
        final SOURCE = '''
            if (isReady) {
                println 'ready'
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new InterfaceNameRule()
    }

}
