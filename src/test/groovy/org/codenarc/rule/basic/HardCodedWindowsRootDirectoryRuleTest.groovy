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
import org.junit.jupiter.api.Test

/**
 * Tests for HardCodedWindowsRootDirectoryRule
 *
 * @author Hamlet D'Arcy
 */
class HardCodedWindowsRootDirectoryRuleTest extends AbstractRuleTestCase<HardCodedWindowsRootDirectoryRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'HardCodedWindowsRootDirectory'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
           new File('cc:\\\\')
           new File('a\\\\b\\\\c')
           new File('/a/b')
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCRoot() {
        final SOURCE = '''
           new File('c:\\\\')
        '''
        assertSingleViolation(SOURCE, 2, "new File('c:\\\\')", 'The file location c:\\ is not portable')
    }

    @Test
    void testCRootTwoParm1() {
        final SOURCE = '''
           new File('c:\\\\', null)
        '''
        assertSingleViolation(SOURCE, 2, "new File('c:\\\\', null)", 'The file location c:\\ is not portable')
    }

    @Test
    void testCRootTwoParm2() {
        final SOURCE = '''
           new File(null, 'c:\\\\')
        '''
        assertSingleViolation(SOURCE, 2, "new File(null, 'c:\\\\')", 'The file location c:\\ is not portable')
    }

    @Test
    void testCRootAndDir() {
        final SOURCE = '''
           new File('c:\\\\dir')
        '''
        assertSingleViolation(SOURCE, 2, "new File('c:\\\\dir')", 'The file location c:\\ is not portable')
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
           new File('E:\\\\dir')
        '''
        assertSingleViolation(SOURCE, 2, "new File('E:\\\\dir')", 'The file location E:\\ is not portable')
    }

    @Test
    void testGStringParameter() {
        final SOURCE = '''
           new File("E:\\\\dir\\\\$foo")
        '''
        assertSingleViolation(SOURCE, 2, 'new File("E:\\\\dir\\\\$foo")', 'The file location E:\\ is not portable')
    }

    @Override
    protected HardCodedWindowsRootDirectoryRule createRule() {
        new HardCodedWindowsRootDirectoryRule()
    }
}
