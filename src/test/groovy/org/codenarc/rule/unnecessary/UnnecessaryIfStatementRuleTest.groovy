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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryIfStatementRule
 *
 * @author Chris Mair
  */
class UnnecessaryIfStatementRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryIfStatement'
        assert rule.checkLastStatementImplicitElse
    }

    // Tests for explicit return of true/false

    @Test
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

    @Test
    void testApplyTo_ReturnFalseAndTrue_IsAViolation() {
        final SOURCE = '''
            if (expression1) return false else return true
            if (expression2) return Boolean.FALSE else return Boolean.TRUE
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (expression1)'],
            [lineNumber:3, sourceLineText:'if (expression2)'] )
    }

    @Test
    void testApplyTo_ReturnTrueFalse_WithBraces_IsAViolation() {
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

    @Test
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

    @Test
    void testApplyTo_ReturnOtherValues_NotAViolation() {
        final SOURCE = '''
            if (someExpression) return 67 else return false
            if (someExpression) return true else return 88
            if (someExpression) return Boolean.TRUE else return "false"
            if (someExpression) return "true" else return Boolean.FALSE
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoElseBlock_NotAViolation() {
        final SOURCE = '''
            if (someExpression) return true
            if (someExpression) return true else { }

        '''
        assertNoViolations(SOURCE)
    }

    private static final SOURCE_FALLS_THROUGH_TO_RETURN = '''
            def method1() {
                if (expression1) {
                    return true
                }
                return false
            }
            def closure1 = {
                if (expression2)
                    return Boolean.FALSE
                return Boolean.TRUE
            }
        '''

    @Test
    void testApplyTo_IfReturn_FallsThroughToReturn_IsAViolation() {
        assertViolations(SOURCE_FALLS_THROUGH_TO_RETURN,
            [lineNumber:3, sourceLineText:'if (expression1)'],
            [lineNumber:9, sourceLineText:'if (expression2)'])
    }

    @Test
    void testApplyTo_IfReturn_FallsThroughToReturn_checkLastStatementImplicitElse_False_NoViolation() {
        rule.checkLastStatementImplicitElse = false
        assertNoViolations(SOURCE_FALLS_THROUGH_TO_RETURN)
    }

    // Tests for implicit return of true/false (last statement in a block)

    @Test
    void testApplyTo_ImplicitReturnAtEndOfMethod_TrueAndFalse_IsAViolation() {
        final SOURCE = '''
            def isSpellingCorrect(word) {
                File file = new File("...")
                def found = false
                file.eachLine {
                    if (it == word) found = true
                }
                if (found) { true } else false
            }
        '''
        assertSingleViolation(SOURCE, 8, 'if (found) { true } else false')
    }

    // Tests for if/else blocks that are merely constant or literal expressions (not at the end of a block)

    @Test
    void testApplyTo_NotLastStatement_IfBlockIsOnlyAConstantExpression_IsAViolation() {
        final SOURCE = '''
            def myClosure = {
                doStuff()
                if (ready) {
                    'abc'
                }
                doOtherStuff()
                if (ready) 123
                doSomeOtherStuff()
            }
        '''
        assertTwoViolations(SOURCE,
            4, 'if (ready) {', 'if block',
            8, 'if (ready) 123', 'if block')
    }

    @Test
    void testApplyTo_NotLastStatement_ElseBlockIsOnlyAConstantExpression_IsAViolation() {
        final SOURCE = '''
            String myMethod() {
                doStuff()
                if (ready) {
                    doStuff()
                } else [a:123, b:456]
                doOtherStuff()
            }
        '''
        assertSingleViolation(SOURCE, 6, '} else [a:123, b:456]', 'else block')
    }

    @Test
    void testApplyTo_NotLastStatement_IfAndElseBlocksAreOnlyAConstantExpressions_IsAViolation() {
        final SOURCE = '''
            Object myMethod() {
                doStuff()
                if (ready) {
                    [1, 2, 3]
                } else {
                    Boolean.FALSE
                }
                doOtherStuff()
            }
        '''
        assertTwoViolations(SOURCE,
            4, 'if (ready) {', 'if block',
            6, '} else {', 'else block')
    }

    @Test
    void testApplyTo_IfElseStatement_LastStatement_IfBlockAndElseBlocksAreOnlyConstantExpressions_NotAViolation() {
        final SOURCE = '''
            String myMethod() {
                doStuff()
                if (ready) 'abc'; else 'xyz'
            }
        '''
        assertNoViolations(SOURCE)
    }

    // Tests for if statements that do not apply for this rule

    @Test
    void testApplyTo_MethodCalls_NotAViolation() {
        final SOURCE = '''
            if (someExpression) doStep1(); else doStep2()
            if (someExpression) { doStep1() } else { doStep2() }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_EmptyBlocks_NotAViolation() {
        final SOURCE = '''
            if (someExpression) {}
            if (someExpression) { doSomething() } else { }
            try { } finally { }

        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryIfStatementRule()
    }
}
