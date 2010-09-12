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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for UnnecessaryTernaryExpressionRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnnecessaryIfStatementRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryIfStatement'
    }

    void testApplyTo_ReturnTrueAndFalse_IsAViolation() {
        final SOURCE = '''
            if (expression1) return true else return false
            if (expression2) return Boolean.TRUE else return Boolean.FALSE
            if (expression3) return Boolean.TRUE else return false
            if (expression4) return true else return Boolean.FALSE
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (expression1)'],
            [lineNumber:3, sourceLineText:'if (expression2)'],
            [lineNumber:4, sourceLineText:'if (expression3)'],
            [lineNumber:5, sourceLineText:'if (expression4)'])
    }

    void testApplyTo_ReturnFalseAndTrue_IsAViolation() {
        final SOURCE = '''
            if (expression1) return false else return true
            if (expression2) return Boolean.FALSE else return Boolean.TRUE
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (expression1)'],
            [lineNumber:3, sourceLineText:'if (expression2)'] )
    }

    void testApplyTo_WithBraces_IsAViolation() {
        final SOURCE = '''
            if (expression1) { return true } else { return false }
            if (expression2) return Boolean.FALSE else { return Boolean.TRUE }
            if (expression3) { return false } else return true
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (expression1)'],
            [lineNumber:3, sourceLineText:'if (expression2)'],
            [lineNumber:4, sourceLineText:'if (expression3)'])
    }

    void testApplyTo_MultipleStatementBlocks_NotAViolation() {
        final SOURCE = '''
            if (expression1) { println 123; return true } else { return false }
            if (expression2) return Boolean.FALSE else { doSomething(); return Boolean.TRUE }
            if (expression3) {
                x = 98.6 
                return false
            } else return true
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ReturnOtherValues_NotAViolation() {
        final SOURCE = '''
            if (someExpression) return 67 else return false
            if (someExpression) return true else return 88
            if (someExpression) return Boolean.TRUE else return "false"
            if (someExpression) return "true" else return Boolean.FALSE
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoElseBlock_NotAViolation() {
        final SOURCE = '''
            if (someExpression) return true
            if (someExpression) return true else { }

        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new UnnecessaryIfStatementRule()
    }
}