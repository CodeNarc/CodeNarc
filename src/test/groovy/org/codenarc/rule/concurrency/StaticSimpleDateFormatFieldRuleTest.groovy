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
 * Tests for StaticSimpleDateFormatFieldRule
 *
 * @author Chris Mair
 */
class StaticSimpleDateFormatFieldRuleTest extends AbstractRuleTestCase<StaticSimpleDateFormatFieldRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StaticSimpleDateFormatField'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
          // these usages are OK
          class MyCorrectClass {
            private final SimpleDateFormat dateFormat1
            static ThreadLocal<SimpleDateFormat> dateFormat2
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
                static SimpleDateFormat dateFormat
              }
        '''
        assertSingleViolation(SOURCE, 3, 'static SimpleDateFormat dateFormat', 'SimpleDateFormat instances are not thread safe. Wrap the SimpleDateFormat field dateFormat in a ThreadLocal or make it an instance field')
    }

    @Test
    void testStaticFieldFullyQualifiedName() {
        final SOURCE = '''
              class MyClass {
                static java.text.SimpleDateFormat dateFormat
              }
        '''
        assertSingleViolation(SOURCE, 3, 'static java.text.SimpleDateFormat dateFormat', 'SimpleDateFormat instances are not thread safe. Wrap the SimpleDateFormat field dateFormat in a ThreadLocal or make it an instance field')
    }

    @Test
    void testStaticUntypedField_InitializesValueToDateFormat() {
        final SOURCE = '''
              class MyClass {
                static final DATE1 = new SimpleDateFormat()
                static final DATE2 = new SimpleDateFormat('MM/dd')
                static final DATE3 = new SimpleDateFormat('MM/dd', DateFormatSymbols.instance)
                static date4 = new SimpleDateFormat('MM/dd', Locale.FRANCE)
                static date5 = new java.text.SimpleDateFormat('MM/dd')
              }
        '''
        assertViolations(SOURCE,
            [line:3, source:'static final DATE1 = new SimpleDateFormat()', message:['SimpleDateFormat', 'DATE1']],
            [line:4, source:"static final DATE2 = new SimpleDateFormat('MM/dd')", message:['SimpleDateFormat', 'DATE2']],
            [line:5, source:"static final DATE3 = new SimpleDateFormat('MM/dd', DateFormatSymbols.instance)", message:['SimpleDateFormat', 'DATE3']],
            [line:6, source:"static date4 = new SimpleDateFormat('MM/dd', Locale.FRANCE)", message:['SimpleDateFormat', 'date4']],
            [line:7, source:"static date5 = new java.text.SimpleDateFormat('MM/dd')", message:['SimpleDateFormat', 'date5']]
        )
    }

    @Test
    void testNonStaticFieldWithSimpleDateFormatInitializer() {
        final SOURCE = '''
          class MyClass {
            final SimpleDateFormat filenameDateFormat = new SimpleDateFormat("yyyyMMdd")
            def anotherFormat = new SimpleDateFormat("yyyyMMdd")
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected StaticSimpleDateFormatFieldRule createRule() {
        new StaticSimpleDateFormatFieldRule()
    }
}
