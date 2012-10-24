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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for StaticMatcherFieldRule
 *
 * @author Hamlet D'Arcy
 */
class StaticMatcherFieldRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StaticMatcherField'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
          // these usages are OK
          class MyCorrectClass {
            private Matcher matcher1
            static ThreadLocal<Matcher> matcher2
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticField() {
        final SOURCE = '''
          class MyClass {
            static Matcher matcher1
          }
        '''
        assertSingleViolation(SOURCE, 3, 'static Matcher matcher1', 'Matcher instances are not thread safe. Wrap the Matcher field matcher1 in a ThreadLocal or make it an instance field')
    }

    @Test
    void testStaticFieldFullyQUalifiedName() {
        final SOURCE = '''
          class MyClass {
            static java.util.regex.Matcher matcher2
          }
        '''
        assertSingleViolation(SOURCE, 3, 'static java.util.regex.Matcher matcher2', 'Matcher instances are not thread safe. Wrap the Matcher field matcher2 in a ThreadLocal or make it an instance field')
    }

    protected Rule createRule() {
        new StaticMatcherFieldRule()
    }
}
