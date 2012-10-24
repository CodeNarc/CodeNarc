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
 * Tests for ConsecutiveLiteralAppendsRule
 *
 * @author Hamlet D'Arcy
 */
class ConsecutiveLiteralAppendsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ConsecutiveLiteralAppends'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            // usage not chained invocation
            writer.append('Hello')
            writer.append('World')

            writer.append(null).append(5)           // nulls cannot be joined

            writer.append().append('Hello')             // no arg append is unknown
            writer.append('a', 'b').append('Hello')     // two arg append is unknown
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStrings() {
        final SOURCE = '''
            writer.append('foo').append('bar')      // strings can be joined
        '''
        assertSingleViolation(SOURCE, 2, 'writer.append', "Consecutive calls to append method with literal parameters can be joined into append('foobar')")
    }

    @Test
    void testStringAndNumber() {
        final SOURCE = '''
            writer.append('foo').append(5)          // string and number can be joined
        '''
        assertSingleViolation(SOURCE, 2, 'writer.append', "Consecutive calls to append method with literal parameters can be joined into append('foo5')")
    }

    @Test
    void testGString() {
        final SOURCE = '''
            writer.append('Hello').append("$World") // GString can be joined
        '''
        assertSingleViolation(SOURCE, 2, 'writer.append', 'Consecutive calls to append method with literal parameters can be joined into one append() call')
    }

    protected Rule createRule() {
        new ConsecutiveLiteralAppendsRule()
    }
}
