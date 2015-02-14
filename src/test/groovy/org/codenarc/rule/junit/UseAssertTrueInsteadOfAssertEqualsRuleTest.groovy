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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UseAssertTrueInsteadOfAssertEqualsRule
 *
 * @author Hamlet D'Arcy
  */
class UseAssertTrueInsteadOfAssertEqualsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UseAssertTrueInsteadOfAssertEquals'
        assert rule.checkAssertStatements == false
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                @Test
                void testMethod() {
                    assertEquals(1, foo())
                    assertEquals('message', foo())
                    assertTrue(foo())
                    assertTrue('message', foo())
                    assertSame(foo(), foo())
                    assertTrue(foo() > bar())

                    assert 1 == foo()
                    assert 'message' == foo()
                    assert foo()
                    assert foo() : 'message'
                    assert foo().is(foo())
                    assert foo() > bar()
                }
              }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAssertTrueViolation() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(true, foo())
                    assertEquals("message", true, foo())
                }
              }
        '''
        assertTwoViolations(SOURCE, 4, 'assertEquals(true, foo())', 5, 'assertEquals("message", true, foo())')
    }

    @Test
    void testAssertStatement_True_checkAssertStatements_True_Violations() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assert true == foo()
                    assert foo() == true : "message"
                }
              }
        '''
        rule.checkAssertStatements = true
        assertTwoViolations(SOURCE,
                4, 'assert true == foo()', "The expression '(true == this.foo())' can be simplified to 'this.foo()'",
                5, 'assert foo() == true : "message"', "The expression '(this.foo() == true)' can be simplified to 'this.foo()'")
    }

    @Test
    void testAssertStatement_True_checkAssertStatements_False_Violations() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assert true == foo()
                    assert foo() == true : "message"
                }
              }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAssertTrueViolation_Backwards() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(foo(), true)
                    assertEquals("message", foo(), true)
                }
              }
        '''
        assertTwoViolations(SOURCE, 4, 'assertEquals(foo(), true)', 5, 'assertEquals("message", foo(), true)')
    }

    @Test
    void testAssertFalseViolation() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(false, foo())
                    assertEquals("message", false, foo())
                }
              }
        '''
        assertTwoViolations(SOURCE, 4, 'assertEquals(false, foo())', 5, 'assertEquals("message", false, foo())')
    }

    @Test
    void testAssertStatement_False_checkAssertStatements_True_Violations() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assert false == foo()
                    assert foo() == false : "message"
                }
              }
        '''
        rule.checkAssertStatements = true
        assertTwoViolations(SOURCE,
                4, 'assert false == foo()', "The expression '(false == this.foo())' can be simplified to '!this.foo()'",
                5, 'assert foo() == false : "message"', "The expression '(this.foo() == false)' can be simplified to '!this.foo()'")
    }

    @Test
    void testAssertStatement_False_checkAssertStatements_False_NoViolations() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assert false == foo()
                    assert foo() == false : "message"
                }
              }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAssertFalseViolation_Backwards() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(foo(), false)
                    assertEquals("message", foo(), false)
                }
              }
        '''
        assertTwoViolations(SOURCE, 4, 'assertEquals(foo(), false)', 5, 'assertEquals("message", foo(), false)')
    }

    protected Rule createRule() {
        new UseAssertTrueInsteadOfAssertEqualsRule()
    }
}
