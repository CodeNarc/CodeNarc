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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SpaceAroundOperatorRule
 *
 * @author Chris Mair
  */
class SpaceAroundOperatorRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAroundOperator'
    }

    // Tests for operators

    @Test
    void testApplyTo_Operators_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def answer = 3 + 5 - x\t* 23    / 100
                    def name = fullname ? fullname + 'ME' : 'unknown'
                    def v = fullname ?
                         fullname + 'ME' :
                         'unknown'
                    def greeting = fullname ?: 'you'
                    def closure = { x -> println this.hashCode() }
                    if (isFirstVisit(statement) && isStatementWithinFinally(statement)) { }
                    list.find { statement.lineNumber in it }
                       expression instanceof PropertyExpression &&
                           expression.objectExpression instanceof VariableExpression
                    list << 'abc'
                    other >> writer
                    def moreInfo = violation.message ? violation.message : ''
                    return expression instanceof BinaryExpression ? leftMostColumn(expression.leftExpression) : expression.columnNumber
                    AstUtil.respondsTo(rule, 'getDescription') ? rule.description : null
                    getResourceBundleString('htmlReport.titlePrefix')  + (title ? ": $title": '')
                    def x = 3 +
                      5
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_UnicodeCharacterLiteral_CausesIncorrectColumnIndexesInAST_NoViolations_KnownIssue() {
        final SOURCE = '''
            class MyClass {
                def mapping = myService.findAllMappings(0)?.collect { domain ->
                  [description: countryCode?.padRight(6, '\\u00A0')+ domain.countryName]
                }

                String myString = ready ? '\\u00A0': 'error'+'99'
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreUnaryOperators_NoViolations() {
        final SOURCE = '''
            doStuff(x++, y--, -x, +y, !ready, x?.name)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreArrayOperator_NoViolations() {
        final SOURCE = '''
            def statement = block.statements[it]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssignmentOperationWithinDeclaration_WithoutSpace_KnownLimitation_NoViolations() {
        final SOURCE = '''
            def x=5
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TernaryOperationWithMethodCall_WithoutSpace_KnownLimitation_NoViolations() {
        final SOURCE = '''
            AstUtil.respondsTo(rule, 'getDescription')?rule.description:null
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_OperatorsWithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    3+ 5-x*23/ 100
                    list <<123
                    other>> writer
                    x=99
                    x&& y
                    x ||y
                    x &y
                    x| y
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:'3+ 5-x*23/ 100', messageText:'The operator "-" within class MyClass is not preceded'],
            [lineNumber:4, sourceLineText:'3+ 5-x*23/ 100', messageText:'The operator "-" within class MyClass is not followed'],
            [lineNumber:4, sourceLineText:'3+ 5-x*23/ 100', messageText:'The operator "+" within class MyClass is not preceded'],
            [lineNumber:4, sourceLineText:'3+ 5-x*23/ 100', messageText:'The operator "/" within class MyClass is not preceded'],
            [lineNumber:4, sourceLineText:'3+ 5-x*23/ 100', messageText:'The operator "*" within class MyClass is not preceded'],
            [lineNumber:4, sourceLineText:'3+ 5-x*23/ 100', messageText:'The operator "*" within class MyClass is not followed'],
            [lineNumber:5, sourceLineText:'list <<123', messageText:'The operator "<<" within class MyClass is not followed'],
            [lineNumber:6, sourceLineText:'other>> writer', messageText:'The operator ">>" within class MyClass is not preceded'],
            [lineNumber:7, sourceLineText:'x=99', messageText:'The operator "=" within class MyClass is not preceded'],
            [lineNumber:7, sourceLineText:'x=99', messageText:'The operator "=" within class MyClass is not followed'],
            [lineNumber:8, sourceLineText:'x&& y', messageText:'The operator "&&" within class MyClass is not preceded'],
            [lineNumber:9, sourceLineText:'x ||y', messageText:'The operator "||" within class MyClass is not followed'],
            [lineNumber:10, sourceLineText:'x &y', messageText:'The operator "&" within class MyClass is not followed'],
            [lineNumber:11, sourceLineText:'x| y', messageText:'The operator "|" within class MyClass is not preceded'])
    }

    @Test
    void testApplyTo_TernaryOperatorsWithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def name = fullname?fullname + 'ME':'unknown'
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:"def name = fullname?fullname + 'ME':'unknown'", messageText:'The operator "?" within class MyClass is not preceded'],
            [lineNumber:4, sourceLineText:"def name = fullname?fullname + 'ME':'unknown'", messageText:'The operator "?" within class MyClass is not followed'],
            [lineNumber:4, sourceLineText:"def name = fullname?fullname + 'ME':'unknown'", messageText:'The operator ":" within class MyClass is not surrounded'])
    }

    @Test
    void testApplyTo_ElvisOperatorsWithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def greeting = fullname?:'you'
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:"def greeting = fullname?:'you'", messageText:'The operator "?:" within class MyClass is not preceded'],
            [lineNumber:4, sourceLineText:"def greeting = fullname?:'you'", messageText:'The operator "?:" within class MyClass is not followed'])
    }

    protected Rule createRule() {
        new SpaceAroundOperatorRule()
    }
}