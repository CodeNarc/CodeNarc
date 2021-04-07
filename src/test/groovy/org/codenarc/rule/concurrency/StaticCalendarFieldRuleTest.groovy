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
 * Tests for StaticCalendarFieldRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class StaticCalendarFieldRuleTest extends AbstractRuleTestCase<StaticCalendarFieldRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StaticCalendarField'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
          // these usages are OK
          class MyCorrectClass {
            private final Calendar calendar1
            static ThreadLocal<Calendar> calendar2
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticField() {
        final SOURCE = '''
              class MyClass {
                static Calendar calendar
              }
        '''
        assertSingleViolation(SOURCE, 3, 'static Calendar calendar', 'Calendar instances are not thread safe. Wrap the Calendar field calendar in a ThreadLocal or make it an instance field')
    }

    @Test
    void testStaticFieldFullyQualifiedName() {
        final SOURCE = '''
              class MyClass {
                static java.util.Calendar calendar
              }
        '''
        assertSingleViolation(SOURCE, 3, 'static java.util.Calendar calendar', 'Calendar instances are not thread safe. Wrap the Calendar field calendar in a ThreadLocal or make it an instance field')
    }

    @Test
    void testStaticUntypedField_InitializesValueToCalendar() {
        final SOURCE = '''
            class MyClass {
                static final CAL1 = Calendar.getInstance()
                static final CAL2 = Calendar.getInstance(Locale.FRANCE)
                static def cal3 = Calendar.getInstance(timezone)
                static Object cal4 = Calendar.getInstance(timezone, locale)
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'static final CAL1 = Calendar.getInstance()', message:['Calendar', 'CAL1']],
            [line:4, source:'static final CAL2 = Calendar.getInstance(Locale.FRANCE)', message:['Calendar', 'CAL2']],
            [line:5, source:'static def cal3 = Calendar.getInstance(timezone)', message:['Calendar', 'cal3']],
            [line:6, source:'static Object cal4 = Calendar.getInstance(timezone, locale)', message:['Calendar', 'cal4']],
        )
    }

    @Test
    void testNonStaticFieldWithCalendarInitializer() {
        final SOURCE = '''
          class MyClass {
            final Calendar cal = Calendar.getInstance()
            def anotherCalendar = Calendar.getInstance(timezone)
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected StaticCalendarFieldRule createRule() {
        new StaticCalendarFieldRule()
    }
}
