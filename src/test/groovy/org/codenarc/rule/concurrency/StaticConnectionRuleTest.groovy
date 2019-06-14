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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for StaticConnectionRule
 *
 * @author 'Hamlet D'Arcy'
 */
class StaticConnectionRuleTest extends AbstractRuleTestCase<StaticConnectionRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StaticConnection'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                Connection conn
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class MyClass {
                static Connection conn
            }
        '''
        assertSingleViolation(SOURCE, 3, 'static Connection conn',
            'Violation in class MyClass. The field conn is marked static, meaning the Connection will be shared between threads and will possibly experience race conditions')
    }

    @Override
    protected StaticConnectionRule createRule() {
        new StaticConnectionRule()
    }
}
