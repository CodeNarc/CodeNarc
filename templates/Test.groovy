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
package org.codenarc.rule.${ruleCategory}

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for ${ruleName}Rule
 *
 * @author '${authorName}'
 * @version \$Revision: 329 \$ - \$Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) \$
 */
class ${ruleName}RuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "$ruleName"
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	// todo: replace with source for passing edge case
        '''
        assertNoViolations(SOURCE)
    }

    void testSingleViolation() {
        final SOURCE = '''
            // todo: replace with source that triggers a violation
        '''
        assertSingleViolation(SOURCE, 1, '...')
    }

    void testTwoViolations() {
        final SOURCE = '''
            // todo: replace with source that triggers 2 violations
        '''
        assertTwoViolations(SOURCE,
                1, '...',	// todo: replace violation line number and message
                2, '...')   // todo: replace violation line number and message
    }

    protected Rule createRule() {
        new ${ruleName}Rule()
    }
}