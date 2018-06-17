/*
 * Copyright 2017 the original author or authors.
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
 * Tests for UnnecessarySetterRule
 */
class UnnecessarySetterRuleTest extends AbstractRuleTestCase<UnnecessarySetterRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessarySetter'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            x.set(1)
            x.setup(2)
            x.setURL('')
            x.setSomething('arg1', 'arg2')
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            x.setProperty(1 + 2)
        '''
        assertSingleViolation(SOURCE, 2, 'x.setProperty(1 + 2)', 'setProperty(..) can probably be rewritten as property = ..')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            x.setProperty(1)
            x.setProperty(this.getA())
            x.setProperty([])
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'x.setProperty(1)', messageText:'setProperty(..) can probably be rewritten as property = ..'],
            [lineNumber:3, sourceLineText:'x.setProperty(this.getA())', messageText:'setProperty(..) can probably be rewritten as property = ..'],
            [lineNumber:4, sourceLineText:'x.setProperty([])', messageText:'setProperty(..) can probably be rewritten as property = ..'])
    }

    @Test
    void testCallingSetterWithinExpression_NoViolations() {
        final SOURCE = '''
            if (!file.setExecutable(true)) {
                throw new Exception("Cannot set ${file} as executable")
            }
            def count = x.setCount(92)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCallingSettersOnSuperDoesNotCauseViolations() {
        final SOURCE = '''
            super.setProperty(1)
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnnecessarySetterRule createRule() {
        new UnnecessarySetterRule()
    }
}
