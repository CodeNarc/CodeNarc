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
 * Tests for AddEmptyStringRule
 *
 * @author Hamlet D'Arcy
 */
class AddEmptyStringRuleTest extends AbstractRuleTestCase {

    private static final VIOLATION_MESSAGE = 'Concatenating an empty string is an inefficient way to convert an object to a String. Consider using toString() or String.valueOf(Object)'

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AddEmptyString'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            def c = 456.toString()
            def d = property?.toString() ?: ""
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSimpleCase() {
        final SOURCE = '''
            def a = '' + 123
        '''
        assertSingleViolation(SOURCE, 2, "'' + 123", VIOLATION_MESSAGE)
    }

    @Test
    void testMethodParameters() {
        final SOURCE = '''
            def b = method('' + property)
        '''
        assertSingleViolation(SOURCE, 2, "method('' + property)", VIOLATION_MESSAGE)
    }

    @Test
    void testAddingEmptyStringWithinAClosure() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    out << "" + count
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'out << "" + count', VIOLATION_MESSAGE)
    }

    protected Rule createRule() {
        new AddEmptyStringRule()
    }
}
