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
 * Tests for AddEmptyStringRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class AddEmptyStringRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "AddEmptyString"
    }

    void testSuccessScenario() {
        final SOURCE = '''
            def c = 456.toString()
            def d = property?.toString() ?: ""
        '''
        assertNoViolations(SOURCE)
    }

    void testSimpleCase() {
        final SOURCE = '''
            def a = '' + 123
        '''
        assertSingleViolation(SOURCE, 2, "'' + 123", 'Concatenating an empty string is an inefficient way to convert an object to a String. Consider using toString() or String.valueOf(Object)')
    }

    void testMethodParameters() {
        final SOURCE = '''
            def b = method('' + property)
        '''
        assertSingleViolation(SOURCE, 2, "method('' + property)", 'Concatenating an empty string is an inefficient way to convert an object to a String. Consider using toString() or String.valueOf(Object)')
    }

    protected Rule createRule() {
        new AddEmptyStringRule()
    }
}