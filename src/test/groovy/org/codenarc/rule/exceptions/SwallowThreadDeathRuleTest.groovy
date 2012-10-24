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
package org.codenarc.rule.exceptions

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SwallowThreadDeathRule
 *
 * @author Rob Fletcher
 * @author Klaus Baumecker
  */
class SwallowThreadDeathRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SwallowThreadDeath'
    }

    @Test
    void testRethrowingCaughtErrorIsLegal() {
        final SOURCE = '''
        	try {
                def a = 0
            } catch (ThreadDeath td) {
                throw td
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testReThrowingAnotherInstanceOfThreadDeathIsLegal() {
        final SOURCE = '''
        	try {
                def a = 0
            } catch (ThreadDeath td) {
                throw new ThreadDeath()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNesting() {
        final SOURCE = '''
        	try {
                def a = 0
            } catch (ThreadDeath td) {
                try { println 4 } catch (ThreadDeath ttdd) { throw ttdd }
                throw td
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCatchingWithoutRethrowingAnythingIsAViolation() {
        final SOURCE = '''
        	try {
                def a = 0
            } catch (ThreadDeath td) {
                td.printStackTrace()
            }
        '''
        assertSingleViolation(SOURCE, 4, '} catch (ThreadDeath td) {')
    }

    @Test
    void testCatchingAndThrowingAnotherVariableIsAViolation() {
        final SOURCE = '''
            def other = new ThreadDeath()
        	try {
                def a = 0
            } catch (ThreadDeath td) {
                throw other
            }
        '''
        assertSingleViolation(SOURCE, 5, '} catch (ThreadDeath td) {')
    }

    @Test
    void testCatchingAndThrowingSomethingElseIsAViolation() {
        final SOURCE = '''
        	try {
                def a = 0
            } catch (ThreadDeath td) {
                throw new RuntimeException("o noes")
            }
        '''
        assertSingleViolation(SOURCE, 4, '} catch (ThreadDeath td) {')
    }

    protected Rule createRule() {
        new SwallowThreadDeathRule()
    }
}
