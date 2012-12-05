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
                doStuff()
        	} else { return 55 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testElseNoReturn_NoViolations() {
        final SOURCE = '''
            if (condition) {
                return 44
        	} else { doStuff() }
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
    void testIfReturnWithoutValue_NoViolations() {
        final SOURCE = '''
            if (condition) {
        	    return
        	} else {
        	    return 55
        	}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testElseReturnWithoutValue_NoViolations() {
        final SOURCE = '''
            if (condition) {
        	    return 44
        	} else {
        	    return
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
             if (check()) { return doStuff() } else { return "count=$count" }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:"if (condition) { return 44 } else { return 'yes' }",
                messageText:"The if statement in class None can be rewritten using the ternary operator: return condition ? 44 : 'yes'"],
            [lineNumber:3, sourceLineText:'if (check()) { return doStuff() } else { return "count=$count" }',
                messageText:'The if statement in class None can be rewritten using the ternary operator: return this.check() ? this.doStuff() : "count=$count"'])
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

    protected Rule createRule() {
        new IfStatementCouldBeTernaryRule()
    }
}