/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for IfStatementCouldBeTernaryRule
 *
 * @author Chris Mair
 */
class IfStatementCouldBeTernaryRuleTest extends AbstractRuleTestCase<IfStatementCouldBeTernaryRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'IfStatementCouldBeTernary'
        assert rule.checkLastStatementImplicitElse
    }

    @Test
    void testIfNoElse_NoViolations() {
        final SOURCE = '''
             if (condition) { return 123 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfMoreThanReturn_NoViolations() {
        final SOURCE = '''
            if (condition) {
                doStuff()
                return 44
            } else { return 55 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfNoReturn_NoViolations() {
        final SOURCE = '''
            if (condition) {
                [a:1]
            } else { return 55 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testElseNoReturn_NoViolations() {
        final SOURCE = '''
            if (condition) {
                return 44
            } else { 99 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testElseMoreThanReturn_NoViolations() {
        final SOURCE = '''
            if (condition) {
                return 44
            } else {
                doStuff()
                return 55
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testReturnNotConstantOrLiteral_NoViolations() {
        final SOURCE = '''
            if (condition) {
                return doStuff()
            } else {
                return condition
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testElseIf_NoViolations() {
        final SOURCE = '''
            if (condition) {
                return 44
            } else if (ready) {
                return 55
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMatchingIfElse_Violation() {
        final SOURCE = '''
             if (condition) { return 44 } else { return 'yes' }
             if (check()) { return [a:1] } else { return "count=$count" }
             if (x + y - z) { return [1, 2, 3] } else { return 99.50 }
             if (other.name()) { return null } else { return false }
             if (x) { return } else { return Boolean.FALSE }
        '''
        assertViolations(SOURCE,
            [line:2, source:"if (condition) { return 44 } else { return 'yes' }",
                message:"The if statement in class None can be rewritten using the ternary operator: return condition ? 44 : 'yes'"],
            [line:3, source:'if (check()) { return [a:1] } else { return "count=$count" }',
                message:'The if statement in class None can be rewritten using the ternary operator: return this.check() ? [a:1] : "count=$count"'],
            [line:4, source:'if (x + y - z) { return [1, 2, 3] } else { return 99.50 }',
                message:'The if statement in class None can be rewritten using the ternary operator: return ((x + y) - z) ? [1, 2, 3] : 99.50'],
            [line:5, source:'if (other.name()) { return null } else { return false }',
                message:'The if statement in class None can be rewritten using the ternary operator: return other.name() ? null : false'],
            [line:6, source:'if (x) { return } else { return Boolean.FALSE }',
                message:'The if statement in class None can be rewritten using the ternary operator: return x ? null : Boolean.FALSE'])
    }

    @Test
    void testMatchingIfElse_NoBraces_Violation() {
        final SOURCE = '''
             if (condition)
                return 44
             else return 55
        '''
        assertViolations(SOURCE,
            [line:2, source:'if (condition)', message:'The if statement in class None can be rewritten using the ternary'])
    }

    @Test
    void testMatchingIfElseWithinClosure_Violation() {
        final SOURCE = '''
            class MyDomain {
                static constraints = {
                    registrationState(nullable: true, validator: { val, obj ->
                        if (val) {
                            return false
                        } else {
                            return true
                        }
                    })
                }
            }
        '''
        assertViolations(SOURCE,
            [line:5, source:'if (val) {', message:'The if statement in class MyDomain can be rewritten using the ternary'])
    }

    private static final SOURCE_FALLS_THROUGH_TO_RETURN = '''
            def method1() {
                if (condition) {
                    return 44
                }
                return 'yes'
            }
            def closure1 = {
                if (check())
                    return Boolean.FALSE
                return [1, 2]
            }
        '''

    @Test
    void testMatchingIfReturn_NoElse_FallsThroughToReturn_Violation() {
        assertViolations(SOURCE_FALLS_THROUGH_TO_RETURN,
            [line:3, source:'if (condition)',
                message:"The if statement in class None can be rewritten using the ternary operator: return condition ? 44 : 'yes'"],
            [line:9, source:'if (check())',
                message:'The if statement in class None can be rewritten using the ternary operator: return this.check() ? Boolean.FALSE : [1, 2]'])
    }

    @Test
    void testMatchingIfReturn_NoElse_FallsThroughToReturn_checkLastStatementImplicitElse_False_NoViolation() {
        rule.checkLastStatementImplicitElse = false
        assertNoViolations(SOURCE_FALLS_THROUGH_TO_RETURN)
    }

    @Override
    protected IfStatementCouldBeTernaryRule createRule() {
        new IfStatementCouldBeTernaryRule()
    }
}
