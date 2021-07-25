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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for ExplicitCallToDivMethodRule
 *
 * @author Hamlet D'Arcy
 */
class ExplicitCallToDivMethodRuleTest extends AbstractRuleTestCase<ExplicitCallToDivMethodRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitCallToDivMethod'
    }

    @Test
    void test_NoViolations() {
        rule.ignoreThisReference = true
        final SOURCE = '''
            a / b
            a.div()
            a.div(a, b)
            div(a)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Violations() {
        final SOURCE = '''
            a.div(b)
        '''
        assertSingleViolation(SOURCE, 2, 'a.div(b)')
    }

    @Test
    void test_MarkupBuilderUsage_Div() {
        final SOURCE = '''
            html {
                body {
                    div("Some text")
                    div(a:'aaa', b:'bbb')
                    div { p("some text") }
                    // div(7)           // violation
                    // div(3.1415)      // violation
                    //div(someVariable) // violation
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected ExplicitCallToDivMethodRule createRule() {
        new ExplicitCallToDivMethodRule()
    }
}
