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

/**
 * Tests for StaticDateFormatFieldRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class StaticDateFormatFieldRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StaticDateFormatField'
    }

    void testSuccessScenario() {
        final SOURCE = '''
          // these usages are OK
          class MyCorrectClass {
            private final DateFormat dateFormat1
            static ThreadLocal<DateFormat> dateFormat2
          }
        '''
        assertNoViolations(SOURCE)
    }

    void testStaticField() {
        final SOURCE = '''
              class MyClass {
                static DateFormat dateFormat
              }
        '''
        assertSingleViolation(SOURCE, 3, 'static DateFormat dateFormat', 'DateFormat instances are not thread safe. Wrap the DateFormat field dateFormat in a ThreadLocal or make it an instance field')
    }

    void testStaticFieldFullyQUalifiedName() {
        final SOURCE = '''
              class MyClass {
                static java.text.DateFormat dateFormat
              }
        '''
        assertSingleViolation(SOURCE, 3, 'static java.text.DateFormat dateFormat', 'DateFormat instances are not thread safe. Wrap the DateFormat field dateFormat in a ThreadLocal or make it an instance field')
    }

    protected Rule createRule() {
        new StaticDateFormatFieldRule()
    }
}