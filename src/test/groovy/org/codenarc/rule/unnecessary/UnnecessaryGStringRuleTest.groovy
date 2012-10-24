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
 * Tests for UnnecessaryGStringRule
 *
 * @author 'Hamlet D'Arcy'
  */
class UnnecessaryGStringRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryGString'
    }

    @Test
    void testSimpleCase() {
        final SOURCE = '''
            def docFile = "src/site/apt/codenarc-rules-${ruleSetName}.apt"
        '''
        assertNoViolations(SOURCE)
    }
    
    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        // OK
        def g = """
        I am a \\$ string
        """
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario0() {
        final SOURCE = '''
        def h = """
        I am a $string
    """
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario1() {
        final SOURCE = '''
        def i = 'i am a string'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario2() {
        final SOURCE = """
        def j = '''i am a
        string
        '''
        """
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario3() {
        final SOURCE = '''
        def c = "I am a ' string"       // OK

        def d = """I am a ' string"""   // OK

        def e = """I am a ' string"""   // OK

        def f = "I am a \\$ string"  // OK

        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoubleQuotes() {
        final SOURCE = '''
            def a = "I am a string"     // violation
        '''
        assertSingleViolation(SOURCE, 2, '"I am a string"', 'The String \'I am a string\' can be wrapped in single quotes instead of double quotes')
    }

    @Test
    void testMultiline() {
        final SOURCE = '''
            def a = """
I am a string
"""     // violation
        '''
        assertSingleViolation(SOURCE, 2, 'def a = """', """The String '
I am a string
' can be wrapped in single quotes instead of double quotes""")
    }

    @Test
    void testDoubleViolations() {
        final SOURCE = '''
            class Person {
                def name = "Hamlet"
            }        '''
        assertSingleViolation(SOURCE, 3, 'def name = "Hamlet"')
    }

    protected Rule createRule() {
        new UnnecessaryGStringRule()
    }
}
