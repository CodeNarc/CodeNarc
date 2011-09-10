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
 * Tests for DuplicateStringLiteralRule
 *
 * @author Hamlet D'Arcy
  */
class DuplicateStringLiteralRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DuplicateStringLiteral'
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	println 'w'
        	println 'x'

            def y = 'y'
            def z = 'z'
            class MyClass {
                def static x = 'xyz'
                def static y = 'xyz'
                def field = System.getProperty('file.seperator')
                def x = 'foo'
                def y = 'bar'
                String a = 'a'
                String b = 'b'
                def method() {
                    method('c', 'd')
                    ('e' == 'f') ? 'g' : 'h'
                    'i' ?: 'j'
                    return 'return'
                }
            }

        	println 123
        	println 123
        '''
        assertNoViolations(SOURCE)
    }

    void testAcrossManyMethodCalls() {
        final SOURCE = '''
        	println 'w'
        	println 'w'
        	println 'w'
        '''
        assertTwoViolations(SOURCE, 3, "println 'w'", 4, "println 'w'")
    }

    void testEnums() {
        final SOURCE = '''
            enum MyEnum {
              FOO(
                'FOO'
                )

              String field

              MyEnum(String field) {
                 this.field = field
              }
            }
            '''
        assertNoViolations SOURCE
    }
    void testMethodCall() {
        final SOURCE = '''
        	println 'w', 'w', 'w'
        '''
        assertTwoViolations(SOURCE, 2, "println 'w', 'w', 'w'", 2, "println 'w', 'w', 'w'")
    }

    void testInAList() {
        final SOURCE = '''
        	def x = ['foo', 'bar', 'foo']
        '''
        assertSingleViolation(SOURCE, 2, "def x = ['foo', 'bar', 'foo']")        
    }

    void testInAMap() {
        final SOURCE = '''
        	def y = [x: 'bar', y: 'bar']
        '''
        assertSingleViolation(SOURCE, 2, "def y = [x: 'bar', y: 'bar']")        
    }

    void testInDeclarations() {
        final SOURCE = '''
        	def x = 'foo'
        	def y = 'foo'
            x = 'bar'
            y = 'bar'
        '''
        assertTwoViolations(SOURCE, 3, "def y = 'foo'", 5, "y = 'bar'")
    }

    void testInFields() {
        final SOURCE = '''
            class MyClass {
            	def x = 'foo'
            	def y = 'foo'
            }
        '''
        assertSingleViolation(SOURCE, 4, "def y = 'foo'")

    }

    void testInTernary() {
        final SOURCE = '''
            ('e' == 'e') ? 'g' : 'h'
            ('e' == 'f') ? 'g' : 'g'
        '''
        assertTwoViolations(SOURCE, 2, "('e' == 'e') ? 'g' : 'h'", 3, "('e' == 'f') ? 'g' : 'g'")        
    }

    void testInElvis() {
        final SOURCE = '''
            'foo' ?: 'foo'
        '''
        assertSingleViolation(SOURCE, 2, "'foo' ?: 'foo'")
    }

    void testInIf() {
        final SOURCE = '''
        	if (x == 'foo') return x
            else if (y == 'foo') return y
            else if (z == 'foo') return z
        '''
        assertTwoViolations(SOURCE, 3, "else if (y == 'foo') return y", 4, "else if (z == 'foo') return z")
    }

    void testInReturn() {
        final SOURCE = '''
        	if (true) return 'foo'
            else return 'foo'
        '''
        assertSingleViolation(SOURCE, 3, "else return 'foo'")
    }

    void testInInvocation() {
        final SOURCE = '''
        	'foo'.equals('bar')
            'foo'.equals('baz')
        '''
        assertSingleViolation(SOURCE, 3, "'foo'.equals('baz')")
    }

    void testInNamedArgumentList() {
        final SOURCE = '''
        	x(b: 'bar')
            y(a: 'bar')
        '''
        assertSingleViolation(SOURCE, 3, "y(a: 'bar')")
    }

    void testIgnoreStrings_IgnoresSingleValue() {
        final SOURCE = '''
        	def x = ['xyz', 'abc', 'xyz']
            def y = ['foo', 'bar', 'foo']
        '''
        rule.ignoreStrings = 'xyz'
        assertSingleViolation(SOURCE, 3, "def y = ['foo', 'bar', 'foo']")
    }

    void testIgnoreStrings_IgnoresMultipleValues() {
        final SOURCE = '''
        	def x = ['xyz', 'abc', 'xyz']
            def y = ['foo', 'bar', 'foo']
        '''
        rule.ignoreStrings = 'xyz,foo'
        assertNoViolations(SOURCE)
    }

    void testIgnoreValues_IgnoresValuesSurroundedByWhitespace() {
        final SOURCE = '''
        	def x = [' xyz ', 'abc', ' xyz ']
            def y = ['foo', 'bar', 'foo']
        '''
        rule.ignoreStrings = ' xyz ,foo'
        assertNoViolations(SOURCE)
    }

    void testIgnoreNumbers_ByDefaultIgnoresEmptyString() {
        final SOURCE = '''
        	def x = ['', 'abc', '', 'def', '']
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new DuplicateStringLiteralRule()
    }
}