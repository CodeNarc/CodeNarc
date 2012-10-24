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
 * Tests for UnnecessaryGetterRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryGetterRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryGetter'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            x.get()
            x.property
            x.first
            x.firstName
            x.a
            x.getnotagetter()
            x.getClass()
            x.getProperty('key') '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTwoSimpleGetters() {
        final SOURCE = '''
            x.getProperty()
            x.getPi()
        '''
        assertTwoViolations SOURCE,
                2, 'x.getProperty()', 'getProperty() can probably be rewritten as property',
                3, 'x.getPi()', 'getPi() can probably be rewritten as pi'
    }

    @Test
    void testCamelCaseGetters() {
        final SOURCE = '''
            x.getFirstName()
        '''
        assertSingleViolation(SOURCE, 2, 'x.getFirstName()', 'getFirstName() can probably be rewritten as firstName')
    }

    @Test
    void testSingleLetterNamedGetters() {
        final SOURCE = '''
            x.getA()
        '''
        assertSingleViolation(SOURCE, 2, 'x.getA()', 'getA() can probably be rewritten as a')
    }

    @Test
    void testUpperCaseGetter1() {
        final SOURCE = '''
            x.getURLs()
        '''
        assertSingleViolation(SOURCE, 2, 'x.getURLs()', 'getURLs() can probably be rewritten as URLs')
    }

    @Test
    void testUpperCaseGetter2() {
        final SOURCE = '''
            x.getURL()
        '''
        assertSingleViolation(SOURCE, 2, 'x.getURL()', 'getURL() can probably be rewritten as URL')
    }

    @Test
    void testNonGetter() {
        final SOURCE = '''
            def allPaths = resultsMap.keySet()
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryGetterRule()
    }
}
