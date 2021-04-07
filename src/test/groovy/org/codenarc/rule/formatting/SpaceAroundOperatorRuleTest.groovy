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
import org.codenarc.util.GroovyVersion
import org.junit.Test

/**
 * Tests for SpaceAroundOperatorRule
 *
 * @author Chris Mair
  */
class SpaceAroundOperatorRuleTest extends AbstractRuleTestCase<SpaceAroundOperatorRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAroundOperator'
        assert rule.ignoreParameterDefaultValueAssignments == true
    }

    // Tests for operators

    @Test
    void testApplyTo_Operators_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {

                String name = "Joe"
                private static String LONG_NAME =
                    "aaaaaaabbbbbbbbbbbbbbbcccccccccccccccccccddddddddddd"

                def myMethod() {
                    def answer = 3 + 5 - x\t* 23    / 100
                    def name = fullname ? fullname + 'ME' : 'unknown'
                    String longName =
                        'aaaaabbbbbcccccddddd'
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
                    getResourceBundleString('htmlReport.titlePrefix')  + (title ? " : $title" : '')
                    def x = 3 +
                      5
                    23 as String
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_OperatorFollowingClosure() {
        final SOURCE = '''
            class MyClass {
                def order1 = existingFunds.collect {it.fundSortOrder} ?: []
                def order2 = existingFunds.collect {it.fundSortOrder} ? [1] : [2]
                def order3 = { 23 } << { 37}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_UnicodeCharacterLiteral_Violation() {
        final SOURCE = '''
            class MyClass {
                def mapping = myService.findAllMappings(0)?.collect { domain ->
                  [description: countryCode?.padRight(6, '\\u00A0')+ domain.countryName]
                }

                String myString = ready ? '\\u00A0': 'error'+'99'
            }
        '''
        assertViolations(SOURCE,
                [line:4, source:'[description: countryCode?.padRight(', message:'The operator "+" within class MyClass is not preceded'],
                [line:7, source:'String myString = ready ?', message:'The operator ":" within class MyClass is not surrounded'],
                [line:7, source:'String myString = ready ?', message:'The operator "+" within class MyClass is not preceded'],
                [line:7, source:'String myString = ready ?', message:'The operator "+" within class MyClass is not followed'])
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
    void testApplyTo_TernaryOperator_WithMethodCall_WithoutSpace() {
        final SOURCE = '''
            AstUtil.respondsTo(rule, 'getDescription')?rule.description:
                null
        '''
        assertViolations(SOURCE,
                [line:2, source:"AstUtil.respondsTo(rule, 'getDescription')?rule.description:", message:'The operator "?" within class None is not surrounded'],
                [line:2, source:"AstUtil.respondsTo(rule, 'getDescription')?rule.description:", message:'The operator ":" within class None is not surrounded'])
    }

    @Test
    void testApplyTo_TernaryOperator_QuestionMarkAndColonEachOnSeparateLines() {
        final SOURCE = '''
           def x = alert.isInternal()
                    ? planInfo.isMatchingPlan() ? LOGO_URL_1        // nested ternary
                    : LOGO_URL_2
                    : LOGO_URL_1

           def y = alert.isInternal()
                    ? LOGO_URL_1
                    : LOGO_URL_2

           def z = alert.isInternal() ?
                    LOGO_URL_1 :
                    LOGO_URL_2
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TernaryOperator_SeparateLines_TrueExpressionContainsQuestionMark() {
        final SOURCE = '''
           def x = (condition
              ? "a?b"
              : 'c.d'
            )
           def y = (condition
              ? "a?b" : 'c.d'
            )
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TernaryOperator_SeparateLines_FalseExpressionContainsColon() {
        final SOURCE = '''
           def x = (condition
              ? "a:b"
              : 'c:d'
            )
           def y = (condition
              ? "a:b" : 'c:d'
            )
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TernaryOperator_WithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def name = fullname?fullname + 'ME':'unknown'
                    println name?
                            'yes'  :'no'
                    isEcpr? processRecords(records[0]): ''
                    isField ?processFields(records[0]) :''
                }
            }
        '''
        assertViolations(SOURCE,
                [line:4, source:"def name = fullname?fullname + 'ME':'unknown'", message:'The operator "?" within class MyClass is not surrounded'],
                [line:4, source:"def name = fullname?fullname + 'ME':'unknown'", message:'The operator ":" within class MyClass is not surrounded'],
                [line:5, source:'println name?', message:'The operator "?" within class MyClass is not surrounded'],
                [line:6, source:"'yes'  :'no'", message:'The operator ":" within class MyClass is not surrounded'],
                [line:7, source:"isEcpr? processRecords(records[0]): ''", message:'The operator "?" within class MyClass is not surrounded'],
                [line:7, source:"isEcpr? processRecords(records[0]): ''", message:'The operator ":" within class MyClass is not surrounded'],
                [line:8, source:"isField ?processFields(records[0]) :''", message:'The operator "?" within class MyClass is not surrounded'],
                [line:8, source:"isField ?processFields(records[0]) :''", message:'The operator ":" within class MyClass is not surrounded'])
    }

    @Test
    void testApplyTo_Operator_WithoutSurroundingSpace_Violations() {
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
            [line:4, source:'3+ 5-x*23/ 100', message:'The operator "-" within class MyClass is not preceded'],
            [line:4, source:'3+ 5-x*23/ 100', message:'The operator "-" within class MyClass is not followed'],
            [line:4, source:'3+ 5-x*23/ 100', message:'The operator "+" within class MyClass is not preceded'],
            [line:4, source:'3+ 5-x*23/ 100', message:'The operator "/" within class MyClass is not preceded'],
            [line:4, source:'3+ 5-x*23/ 100', message:'The operator "*" within class MyClass is not preceded'],
            [line:4, source:'3+ 5-x*23/ 100', message:'The operator "*" within class MyClass is not followed'],
            [line:5, source:'list <<123', message:'The operator "<<" within class MyClass is not followed'],
            [line:6, source:'other>> writer', message:'The operator ">>" within class MyClass is not preceded'],
            [line:7, source:'x=99', message:'The operator "=" within class MyClass is not preceded'],
            [line:7, source:'x=99', message:'The operator "=" within class MyClass is not followed'],
            [line:8, source:'x&& y', message:'The operator "&&" within class MyClass is not preceded'],
            [line:9, source:'x ||y', message:'The operator "||" within class MyClass is not followed'],
            [line:10, source:'x &y', message:'The operator "&" within class MyClass is not followed'],
            [line:11, source:'x| y', message:'The operator "|" within class MyClass is not preceded'])
    }

    @Test
    void testApplyTo_AsOperatorWithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    [1,2]as String
                    { -> println 123 } as Runnable      // ok
                    { -> println 456 }as
                        Runnable
                    { -> println 789
                         }as Runnable
                    (int)34.56                          // ignored
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'[1,2]as String', message:'The operator "as" within class MyClass is not surrounded'],
            [line:6, source:'{ -> println 456 }as', message:'The operator "as" within class MyClass is not surrounded'],
            [line:8, source:'{ -> println 789', message:'The operator "as" within class MyClass is not surrounded'])
    }

    @Test
    void testApplyTo_ElvisOperatorsWithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def greeting = fullname?:'you'
                    def f = funds.collect {it.fundSortOrder}?:[]
                    assert model.UserID == expectedModel.UserID?:null
                    def tripleElvis = fullname ?:lastname ?: middleName?:'me'
                }
            }
        '''

        assertViolations(SOURCE,
            [line:4, source:"def greeting = fullname?:'you'", message:'The operator "?:" within class MyClass is not preceded'],
            [line:4, source:"def greeting = fullname?:'you'", message:'The operator "?:" within class MyClass is not followed'],
            [line:5, source:'def f = funds.collect {it.fundSortOrder}?:[]', message:'The operator "?:" within class MyClass is not preceded'],
            [line:5, source:'def f = funds.collect {it.fundSortOrder}?:[]', message:'The operator "?:" within class MyClass is not followed'],
            [line:6, source:'assert model.UserID == expectedModel.UserID?:null', message:'The operator "?:" within class MyClass is not preceded'],
            [line:6, source:'assert model.UserID == expectedModel.UserID?:null', message:'The operator "?:" within class MyClass is not followed'],
            [line:7, source: "def tripleElvis = fullname ?:lastname ?: middleName?:'me'", message:'The operator "?:" within class MyClass is not followed'],
            [line:7, source: "def tripleElvis = fullname ?:lastname ?: middleName?:'me'", message:'The operator "?:" within class MyClass is not preceded'],
            [line:7, source: "def tripleElvis = fullname ?:lastname ?: middleName?:'me'", message:'The operator "?:" within class MyClass is not followed'])
    }

    @Test
    void testApplyTo_ElvisOperatorWithNewLineAsSpace_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def greeting = fullname ?:
                    'you'
                    def doubleElvis = fullname ?: lastname ?:
                    'me'
                    def newLineElvis = fullname \
                    ?: 'you'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_EqualsOperator_InVariableDeclaration_WithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            String bar='bar'
            def bar2\t=[1, 2,
                3, 4]
            int bar3=\t9876

            String other = bar &&
                bar2 == null ||
                bar3
            String other2 = bar instanceof String
            def obj = something.part.subpart
        '''
        assertViolations(SOURCE,
                [line:2, source:"String bar='bar'", message:'The operator "=" within class None is not preceded'],
                [line:2, source:"String bar='bar'", message:'The operator "=" within class None is not followed'],
                [line:3, source:'def bar2\t=[1, 2,', message:'The operator "=" within class None is not followed'],
                [line:5, source:'int bar3=\t9876', message:'The operator "=" within class None is not preceded'])
    }

    @Test
    void testApplyTo_EqualsOperator_InFieldDeclaration_WithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                private static final String BAR='bar'
                def bar2\t=[1, 2,
                    3, 4]
                int bar3=\t9876
                boolean bar4 =BAR &&
                    x == null ||
                    open()

                private String OTHER = BAR &&
                    x == null ||
                    open()
                String other2 = bar instanceof String
                def obj = something.part.subpart
            }
        '''
        assertViolations(SOURCE,
                [line:3, source:"private static final String BAR='bar'", message:'The operator "=" within class MyClass is not preceded'],
                [line:3, source:"private static final String BAR='bar'", message:'The operator "=" within class MyClass is not followed'],
                [line:4, source:'def bar2\t=[1, 2', message:'The operator "=" within class MyClass is not followed'],
                [line:6, source:'int bar3=\t9876', message:'The operator "=" within class MyClass is not preceded'],
                [line:7, source:'boolean bar4 =BAR &&', message:'The operator "=" within class MyClass is not followed'])
    }

    @Test
    void testApplyTo_EqualsOperator_InMethodParameterDefaultValue_WithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                void method1(String name, int count=99,
                    long id =1) { }

                void method_Okay(String name = 'abc', int count = 99) { }
            }
        '''

        rule.ignoreParameterDefaultValueAssignments = false
        assertViolations(SOURCE,
                [line:3, source:'void method1(String name, int count=99', message:'The operator "=" within class MyClass is not preceded'],
                [line:3, source:'void method1(String name, int count=99', message:'The operator "=" within class MyClass is not followed'],
                [line:4, source:'long id =1', message:'The operator "=" within class MyClass is not followed'])

        rule.ignoreParameterDefaultValueAssignments = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_EqualsOperator_InConstructorParameterDefaultValue_WithoutSurroundingSpace_Violations() {
        final SOURCE = '''
            class MyClass {
                MyClass(int id= 88,
                    int maxValue   =99 +
                        23, def other) { }

                MyClass(String name) { }
            }
        '''

        rule.ignoreParameterDefaultValueAssignments = false
        assertViolations(SOURCE,
                [line:3, source:'MyClass(int id= 88', message:'The operator "=" within class MyClass is not preceded'],
                [line:4, source:'int maxValue   =99 +', message:'The operator "=" within class MyClass is not followed'])

        rule.ignoreParameterDefaultValueAssignments = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Operator_LineFollowingAnnotation_NoViolations() {
        final SOURCE = '''
            @SuppressWarnings('UnnecessarySubstring')
            def relativePath = filePath.substring(path.length())

            @SuppressWarnings('ClassForName')
            def driver = Class.forName(driverName)

            @SuppressWarnings('Other')
            String name =  myName + 'abc'

            @SuppressWarnings('Other')
            def otherName = "abc" + "***"

            @SuppressWarnings('Other')
            void method1(String name, int count = 99, long id = 1) { }
            '''
        rule.ignoreParameterDefaultValueAssignments = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Operator_LineFollowingAnnotation_ShouldBeViolations_KnownLimitation() {
        final SOURCE = '''
            @SuppressWarnings('UnnecessarySubstring')
            def relativePath ="111"

            @SuppressWarnings('Other')
            String name =myName+"abc"

            @SuppressWarnings('Other')
            void method1(String name, int count= 99) { }
            '''
        rule.ignoreParameterDefaultValueAssignments = false

        if (GroovyVersion.isGroovyVersion2()) {
            assertViolations(SOURCE,
                // Known Limitation
                //[line:3, source:'def relativePath ="111"', message:'The operator "=" within class None is not followed'],
                //[line:6, source:'String name =myName+"abc"', message:'The operator "=" within class None is not followed'],
                [line:6, source:'String name =myName+"abc"', message:'The operator "+" within class None is not preceded'],
                [line:6, source:'String name =myName+"abc"', message:'The operator "+" within class None is not followed'],
                [line:9, source:'void method1(String name, int count= 99) { }', message:'The operator "=" within class None is not preceded'])
        } else {
            assertViolations(SOURCE,
                [line:3, source:'def relativePath ="111"', message:'The operator "=" within class None is not followed'],
                [line:6, source:'String name =myName+"abc"', message:'The operator "=" within class None is not followed'],
                [line:6, source:'String name =myName+"abc"', message:'The operator "+" within class None is not preceded'],
                [line:6, source:'String name =myName+"abc"', message:'The operator "+" within class None is not followed'],
                [line:9, source:'void method1(String name, int count= 99) { }', message:'The operator "=" within class None is not preceded'])
        }
    }

    @Test
    void testApplyTo_Enum_NoViolations() {
        final SOURCE = '''
            enum Day { YESTERDAY, TODAY, TOMORROW }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected SpaceAroundOperatorRule createRule() {
        new SpaceAroundOperatorRule()
    }
}
