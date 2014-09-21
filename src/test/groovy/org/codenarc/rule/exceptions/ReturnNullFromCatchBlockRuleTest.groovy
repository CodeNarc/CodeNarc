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
package org.codenarc.rule.exceptions

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ReturnNullFromCatchBlockRule
 *
 * @author Hamlet D'Arcy
  */
class ReturnNullFromCatchBlockRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ReturnNullFromCatchBlock'
    }

    @Test
    void testReturnsVariable_NoViolation() {
        final SOURCE = '''
      	    def x = null
        	try {
                return x
        	} catch (Exception e) {
                return x
            } finally {
                return x 
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testExplicitReturnNull() {
        final SOURCE = '''
        	try {
      	        def x = null
                return x
        	} catch (IOException e) {
                return null
            } catch (Exception e) {
                LOG.error(e.getMessage())
                return null
            } 
        '''
        assertTwoViolations(SOURCE,
                6, 'return null',
                9, 'return null')
    }

    @Test
    void testImplicitReturnNull_Violation() {
        final SOURCE = '''
        	try {
      	        doStuff()
            } catch (Exception e) {
                return
            }
        '''
        assertSingleViolation(SOURCE, 5, 'return')
    }

    @Test
    void testNonVoidMethod_ImplicitReturnNull_Violation() {
        final SOURCE = '''
            def doStuff() {
                try {
                    doStuff()
                } catch (Exception e) {
                    return
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'return')
    }

    @Test
    void testVoidMethod_ImplicitReturnNull_NoViolation() {
        final SOURCE = '''
            void doStuff() {
                try {
                    doStuff()
                } catch (Exception e) {
                    return
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ReturnNullFromCatchBlockRule()
    }
}
