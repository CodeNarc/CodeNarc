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

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for IfStatementCouldBeTernaryRule
 *
 * @author Chris Mair
 */
class IfStatementCouldBeTernaryRuleTest extends AbstractRuleTestCase {

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
            [lineNumber:2, sourceLineText:"if (condition) { return 44 } else { return 'yes' }",
                messageText:"The if statement in class None can be rewritten using the ternary operator: return condition ? 44 : 'yes'"],
            [lineNumber:3, sourceLineText:'if (check()) { return [a:1] } else { return "count=$count" }',
                messageText:'The if statement in class None can be rewritten using the ternary operator: return this.check() ? [a:1] : "count=$count"'],
            [lineNumber:4, sourceLineText:'if (x + y - z) { return [1, 2, 3] } else { return 99.50 }',
                messageText:'The if statement in class None can be rewritten using the ternary operator: return ((x + y) - z) ? [1, 2, 3] : 99.50'],
            [lineNumber:5, sourceLineText:'if (other.name()) { return null } else { return false }',
                messageText:'The if statement in class None can be rewritten using the ternary operator: return other.name() ? null : false'],
            [lineNumber:6, sourceLineText:'if (x) { return } else { return Boolean.FALSE }',
                messageText:'The if statement in class None can be rewritten using the ternary operator: return x ? null : Boolean.FALSE'])
    }

    @Test
    void testMatchingIfElse_NoBraces_Violation() {
        final SOURCE = '''
             if (condition)
                return 44
             else return 55
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (condition)', messageText:'The if statement in class None can be rewritten using the ternary'])
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
            [lineNumber:5, sourceLineText:'if (val) {', messageText:'The if statement in class MyDomain can be rewritten using the ternary'])
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
            [lineNumber:3, sourceLineText:'if (condition)',
                messageText:"The if statement in class None can be rewritten using the ternary operator: return condition ? 44 : 'yes'"],
            [lineNumber:9, sourceLineText:'if (check())',
                messageText:'The if statement in class None can be rewritten using the ternary operator: return this.check() ? Boolean.FALSE : [1, 2]'] )
    }

    @Test
    void testMatchingIfReturn_NoElse_FallsThroughToReturn_checkLastStatementImplicitElse_False_NoViolation() {
        rule.checkLastStatementImplicitElse = false
        assertNoViolations(SOURCE_FALLS_THROUGH_TO_RETURN)
    }

    protected Rule createRule() {
        new IfStatementCouldBeTernaryRule()
    }
}
