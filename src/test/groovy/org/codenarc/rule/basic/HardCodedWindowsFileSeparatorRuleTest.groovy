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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for HardCodedWindowsFileSeparatorRule
 *
 * @author Hamlet D'Arcy
 */
class HardCodedWindowsFileSeparatorRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'HardCodedWindowsFileSeparator'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
           new File('/a/b/c')
           new File("", "a/b/")
           new File('..')
           new File('a\\$b')
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCRoot() {
        final SOURCE = '''
           new File('.\\\\c')
        '''
        assertSingleViolation(SOURCE, 2, "new File('.\\\\c')", 'The windows file separator is not portable')
    }

    @Test
    void testCRootTwoParm1() {
        final SOURCE = '''
           new File('../b\\\\', null)
        '''
        assertSingleViolation(SOURCE, 2, "new File('../b\\\\', null)", 'The windows file separator is not portable')
    }

    @Test
    void testCRootTwoParm2() {
        final SOURCE = '''
           new File(null, '\\\\')
        '''
        assertSingleViolation(SOURCE, 2, "new File(null, '\\\\')", 'The windows file separator is not portable')
    }

    @Test
    void testCRootAndDir() {
        final SOURCE = '''
           new File('c:\\\\dir')
        '''
        assertSingleViolation(SOURCE, 2, "new File('c:\\\\dir')", 'The windows file separator is not portable')
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
           new File('E:\\\\dir')
        '''
        assertSingleViolation(SOURCE, 2, "new File('E:\\\\dir')", 'The windows file separator is not portable')
    }

    @Test
    void testGStringParameter() {
        final SOURCE = '''
           new File("E:\\\\dir\\\\$foo")
        '''
        assertSingleViolation(SOURCE, 2, 'new File("E:\\\\dir\\\\$foo")', 'The windows file separator is not portable')
    }

    protected Rule createRule() {
        new HardCodedWindowsFileSeparatorRule()
    }
}
