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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SimpleDateFormatMissingLocaleRule
 *
 * @author Hamlet D'Arcy
 */
class SimpleDateFormatMissingLocaleRuleTest extends AbstractRuleTestCase {

    private static final VIOLATION_MESSAGE = 'Created an instance of SimpleDateFormat without specifying a Locale'

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SimpleDateFormatMissingLocale'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            // OK, includes locale
            new SimpleDateFormat('pattern', Locale.US)

            // OK, includes a variable that perhaps is a locale
            new SimpleDateFormat('pattern', locale)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMissingLocale() {
        final SOURCE = '''
            new SimpleDateFormat('pattern')
        '''
        assertSingleViolation(SOURCE, 2, "new SimpleDateFormat('pattern')", VIOLATION_MESSAGE)
    }

    @Test
    void testMissingLocaleFullyQualified() {
        final SOURCE = '''
            new java.text.SimpleDateFormat('pattern')
        '''
        assertSingleViolation(SOURCE, 2, "new java.text.SimpleDateFormat('pattern')", VIOLATION_MESSAGE)
    }

    @Test
    void testMissingLocale_NoDuplicateViolation() {
        final SOURCE = '''
            class CalendarUtil {
                static FORMAT = new SimpleDateFormat('MM/dd/YYYY')
            }
        '''
        assertSingleViolation(SOURCE, 3, "new SimpleDateFormat('MM/dd/YYYY')", VIOLATION_MESSAGE)
    }

    protected Rule createRule() {
        new SimpleDateFormatMissingLocaleRule()
    }
}
