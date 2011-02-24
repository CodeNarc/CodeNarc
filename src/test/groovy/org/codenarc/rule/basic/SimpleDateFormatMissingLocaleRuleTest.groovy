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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for SimpleDateFormatMissingLocaleRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class SimpleDateFormatMissingLocaleRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SimpleDateFormatMissingLocale'
    }

    void testSuccessScenario() {
        final SOURCE = '''
            // OK, includes locale
            new SimpleDateFormat('pattern', Locale.US)

            // OK, includes a variable that perhaps is a locale
            new SimpleDateFormat('pattern', locale)
        '''
        assertNoViolations(SOURCE)
    }

    void testMissingLocale() {
        final SOURCE = '''
            new SimpleDateFormat('pattern')
        '''
        assertSingleViolation(SOURCE, 2, "new SimpleDateFormat('pattern')", 'Created an instance of SimpleDateFormat without specifying a Locale')
    }

    void testMissingLocaleFullyQualified() {
        final SOURCE = '''
            new java.text.SimpleDateFormat('pattern')
        '''
        assertSingleViolation(SOURCE, 2, "new java.text.SimpleDateFormat('pattern')", 'Created an instance of SimpleDateFormat without specifying a Locale')
    }

    protected Rule createRule() {
        new SimpleDateFormatMissingLocaleRule()
    }
}