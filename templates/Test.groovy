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
package org.codenarc.rule.${ruleCategory}

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for ${ruleName}Rule
 *
 * @author ${authorName}
 */
class ${ruleName}RuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == '$ruleName'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
        	// todo: replace with source for passing edge case(s)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            // todo: replace with source that triggers a violation
        '''
        assertSingleViolation(SOURCE, 1, '...')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            // todo: replace with source that triggers 2 violations
        '''
        assertViolations(SOURCE,
            [lineNumber:1, sourceLineText:'...', messageText:'...'],	// todo: replace line number, source line and message
            [lineNumber:1, sourceLineText:'...', messageText:'...'])	// todo: replace line number, source line and message
    }

    protected Rule createRule() {
        new ${ruleName}Rule()
    }
}
