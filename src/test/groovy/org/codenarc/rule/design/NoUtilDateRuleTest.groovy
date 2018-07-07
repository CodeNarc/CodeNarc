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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for NoUtilDateRule
 *
 * @author Eric Helgeson
 */
class NoUtilDateRuleTest extends AbstractRuleTestCase<NoUtilDateRule> {

    private static final VIOLATION_MESSAGE = 'Created an instance of java.util.Date(), prefer java.time.* package.'

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NoUtilDateRule'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            // OK, not date.
            new LocalDateTime()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testUsingDate() {
        final SOURCE = '''
            new Date()
        '''
        assertSingleViolation(SOURCE, 2, "new Date()", VIOLATION_MESSAGE)
    }

    @Override
    protected NoUtilDateRule createRule() {
        new NoUtilDateRule()
    }
}
