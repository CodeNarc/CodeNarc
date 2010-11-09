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
package org.codenarc.rule.dry

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for DuplicateNumberLiteralRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 439 $ - $Date: 2010-11-08 15:16:30 -0500 (Mon, 08 Nov 2010) $
 */
class DuplicateNumberLiteralRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "DuplicateNumberLiteral"
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	println 123
        	println -17

            def y = 0
            def z = 9876543
            class MyClass {
                def static x = 'xyz'
                def static y = 'xyz'
                def field = System.getProperty('file.seperator')
                def x = 'foo'
                def y = 11.783
                String a = 'a'
                String b = 'b'
                def method() {
                    method('c', 'd')
                    ('e' == 'f') ? 'g' : 'h'
                    'i' ?: 'j'
                    return 'return'
                }
            }
            @SuppressWarnings('DuplicateNumberLiteral')
            class MyClass2 {
                def y = -17
                def z = -17
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testAcrossManyMethodCalls() {
        final SOURCE = '''
        	println 123
        	println 123
        	println 123
        '''
        assertTwoViolations(SOURCE, 3, "println 123", 4, "println 123")
    }

    void testMethodCall() {
        final SOURCE = '''
        	println 123, 123, 123
        '''
        assertTwoViolations(SOURCE, 2, "println 123, 123, 123", 2, "println 123, 123, 123")
    }

    void testInAList() {
        final SOURCE = '''
        	def x = [0, 11.783, 0]
        '''
        assertSingleViolation(SOURCE, 2, "def x = [0, 11.783, 0]")
    }

    void testInAMap() {
        final SOURCE = '''
        	def y = [x: -99, y: -99]
        '''
        assertSingleViolation(SOURCE, 2, "def y = [x: -99, y: -99]")        
    }

    void testInDeclarations() {
        final SOURCE = '''
        	def x = 99
        	def y = 99
            x = 11.783
            y = 11.783
        '''
        assertTwoViolations(SOURCE, 3, "def y = 99", 5, "y = 11.783")
    }

    void testInFields() {
        final SOURCE = '''
            class MyClass {
            	def x = 67890
            	def y = 67890
            }
        '''
        assertSingleViolation(SOURCE, 4, "def y = 67890")

    }

    void testInTernary() {
        final SOURCE = '''
            (0.7 == 0.7) ? -5.13 : 'h'
            (0.7 == 12) ? -5.13 : -5.13
        '''
        assertTwoViolations(SOURCE, 2, "(0.7 == 0.7) ? -5.13 : 'h'", 3, "(0.7 == 12) ? -5.13 : -5.13")        
    }

    void testInElvis() {
        final SOURCE = '''
            67890 ?: 67890
        '''
        assertSingleViolation(SOURCE, 2, "67890 ?: 67890")
    }

    void testInIf() {
        final SOURCE = '''
        	if (x == 67890) return x
            else if (y == 67890) return y
            else if (z == 67890) return z
        '''
        assertTwoViolations(SOURCE, 3, "else if (y == 67890) return y", 4, "else if (z == 67890) return z")
    }

    void testInReturn() {
        final SOURCE = '''
        	if (true) return 67890
            else return 67890
        '''
        assertSingleViolation(SOURCE, 3, "else return 67890")
    }

    void testInInvocation() {
        final SOURCE = '''
        	67890.equals(x)
            67890.equals(y)
        '''
        assertSingleViolation(SOURCE, 3, "67890.equals(y)")
    }

    void testInNamedArgumentList() {
        final SOURCE = '''
        	x(b: 11.783)
            y(a: 11.783)
        '''
        assertSingleViolation(SOURCE, 3, "y(a: 11.783)")
    }

    protected Rule createRule() {
        new DuplicateNumberLiteralRule()
    }
}