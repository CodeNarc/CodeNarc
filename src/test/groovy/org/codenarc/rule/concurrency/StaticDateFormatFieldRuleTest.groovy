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
import org.junit.Test

/**
 * Tests for StaticDateFormatFieldRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class StaticDateFormatFieldRuleTest extends AbstractRuleTestCase<StaticDateFormatFieldRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StaticDateFormatField'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
          // these usages are OK
          class MyCorrectClass {
            private final DateFormat dateFormat1
            static ThreadLocal<DateFormat> dateFormat2
            static object = new Object()
            public static final VALUE = 1234
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticField() {
        final SOURCE = '''
              class MyClass {
                static DateFormat dateFormat
              }
        '''
        assertSingleViolation(SOURCE, 3, 'static DateFormat dateFormat', 'DateFormat instances are not thread safe. Wrap the DateFormat field dateFormat in a ThreadLocal or make it an instance field')
    }

    @Test
    void testStaticFieldFullyQualifiedName() {
        final SOURCE = '''
              class MyClass {
                static java.text.DateFormat dateFormat
              }
        '''
        assertSingleViolation(SOURCE, 3, 'static java.text.DateFormat dateFormat', 'DateFormat instances are not thread safe. Wrap the DateFormat field dateFormat in a ThreadLocal or make it an instance field')
    }

    @Test
    void testStaticUntypedField_InitializesValueToDateFormat() {
        final SOURCE = '''
              class MyClass {
                static final DATE1 = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE)
                static final def DATE2 = DateFormat.getDateInstance(DateFormat.LONG)
                static Object date3 = DateFormat.getDateInstance()
                static date4 = java.text.DateFormat.getDateInstance()

                static final DATETIME1 = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.FRANCE)
                static final def DATETIME2 = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)
                static Object dateTime3 = DateFormat.getDateTimeInstance()
                static dateTime4 = java.text.DateFormat.getDateTimeInstance()

                static final TIME1 = DateFormat.getTimeInstance(DateFormat.LONG, Locale.FRANCE)
                static final def TIME2 = DateFormat.getTimeInstance(DateFormat.LONG)
                static final Object TIME3 = DateFormat.getTimeInstance()
                static time4 = java.text.DateFormat.getTimeInstance()
              }
        '''
        assertViolations(SOURCE,
            [line:3, source:'static final DATE1 = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE)', message:['DateFormat', 'DATE1']],
            [line:4, source:'static final def DATE2 = DateFormat.getDateInstance(DateFormat.LONG)', message:['DateFormat', 'DATE2']],
            [line:5, source:'static Object date3 = DateFormat.getDateInstance()', message:['DateFormat', 'date3']],
            [line:6, source:'static date4 = java.text.DateFormat.getDateInstance()', message:['DateFormat', 'date4']],

            [line:8, source:'static final DATETIME1 = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.FRANCE)', message:['DateFormat', 'DATETIME1']],
            [line:9, source:'static final def DATETIME2 = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)', message:['DateFormat', 'DATETIME2']],
            [line:10, source:'static Object dateTime3 = DateFormat.getDateTimeInstance()', message:['DateFormat', 'dateTime3']],
            [line:11, source:'static dateTime4 = java.text.DateFormat.getDateTimeInstance()', message:['DateFormat', 'dateTime4']],

            [line:13, source:'static final TIME1 = DateFormat.getTimeInstance(DateFormat.LONG, Locale.FRANCE)', message:['DateFormat', 'TIME1']],
            [line:14, source:'static final def TIME2 = DateFormat.getTimeInstance(DateFormat.LONG)', message:['DateFormat', 'TIME2']],
            [line:15, source:'static final Object TIME3 = DateFormat.getTimeInstance()', message:['DateFormat', 'TIME3']],
            [line:16, source:'static time4 = java.text.DateFormat.getTimeInstance()', message:['DateFormat', 'time4']],
        )
    }

    @Test
    void testNonStaticFieldWithDateFormatInitializer() {
        final SOURCE = '''
          class MyClass {
            final DateFormat filenameDateFormat = DateFormat.getTimeInstance(DateFormat.LONG)
            def anotherFormat = DateFormat.getDateInstance()
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected StaticDateFormatFieldRule createRule() {
        new StaticDateFormatFieldRule()
    }
}
