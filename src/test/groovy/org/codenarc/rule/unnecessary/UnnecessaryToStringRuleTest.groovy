/*
 * Copyright 2014 the original author or authors.
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
import org.junit.Test

/**
 * Tests for UnnecessaryToStringRule
 *
 * @author Chris Mair
 */
class UnnecessaryToStringRuleTest extends AbstractRuleTestCase<UnnecessaryToStringRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnnecessaryToString'
        assert rule.checkAssignments
    }

    @Test
    void test_NoViolations() {
        final SOURCE = '''
            def name = nameNode.toString()
            def id = idNode.lastChild.toString()
            def code = "$id-1234".toString()     // GString

            '(' + parameters?.collect { it?.type?.toString() }?.join(', ') + ')\'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_StringExpression_ToString_Violation() {
        final SOURCE = '''
            class MyClass {
                def name = "Joe".toString()

                void run() {
                    def id = '123'.toString()
                    def groupId = ((String)currentRow.get('GroupID')).toString()
                }
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'def name = "Joe".toString()', message:'Calling toString() on the String expression in class MyClass is unnecessary'],
            [line:6, source:"def id = '123'.toString()", message:'Calling toString() on the String expression in class MyClass is unnecessary'],
            [line:7, source:"def groupId = ((String)currentRow.get('GroupID')).toString()", message:'Calling toString() on the String expression in class MyClass is unnecessary'])
    }

    @Test
    void test_PlusToStringExpression_Violation() {
        final SOURCE = '''
            class MyClass {
                def name = "Joe" + new Date().toString()

                void run() {
                    Object object = 1
                    def string = 'some string' + object.toString()
                    def withinGString = "processing: ${'prefix' + object.toString()}"

                    def bigString = 'some string' + new Date() + object.toString()      // not a violation; known limitation
                    def other = 123 + object.toString().toInteger()     // not a violation
                }
            }
        '''
        assertViolations(SOURCE,
                [line:3, source:'def name = "Joe" + new Date().toString()', message:'Calling toString() on [new Date()] in class MyClass is unnecessary'],
                [line:7, source:"def string = 'some string' + object.toString()", message:'Calling toString() on [object] in class MyClass is unnecessary'],
                [line:8, source:'def withinGString = "processing: ${\'prefix\' + object.toString()}"', message:'Calling toString() on [object] in class MyClass is unnecessary'])
    }

    @Test
    void test_ToStringWithinGString_Violation() {
        final SOURCE = '''
            def string = "some string${123L.toString()} or ${123} or ${'ABC'} or ${new Date().toString()}"
            def string2 = """
                 processing: ${123L.toString()}
                 processing: ${new Date().toString()}
                """
        '''
        assertViolations(SOURCE,
                [line:2, source:'def string = "some string${123L.toString()} or ${123} or ${\'ABC\'} or ${new Date().toString()}"', message:'Calling toString() on [123] in class None is unnecessary'],
                [line:2, source:'def string = "some string${123L.toString()} or ${123} or ${\'ABC\'} or ${new Date().toString()}"', message:'Calling toString() on [new Date()] in class None is unnecessary'],
                [line:4, source:'processing: ${123L.toString()}', message:'Calling toString() on [123] in class None is unnecessary'],
                [line:5, source:'processing: ${new Date().toString()}', message:'Calling toString() on [new Date()] in class None is unnecessary']
        )
    }

    @Test
    void test_AssignmentToStringField_ToString_Violation() {
        final SOURCE = '''
            class MyClass {
                String name = nameNode.toString()
                String id = account.id.toString()
                String code = account.getCode().toString()
            }
        '''
        def message = 'Calling toString() when assigning to String field "%s" in class MyClass is unnecessary'
        assertViolations(SOURCE,
            [line:3, source:'String name = nameNode.toString()', message:String.format(message, 'name')],
            [line:4, source:'String id = account.id.toString()', message:String.format(message, 'id')],
            [line:5, source:'String code = account.getCode().toString()', message:String.format(message, 'code')])
    }

    @Test
    void test_AssignmentToStringVariable_ToString_Violation() {
        final SOURCE = '''
            String name = nameNode.toString()
            String id = account.id.toString()
            String code = account.getCode().toString()
        '''
        def message = 'Calling toString() when assigning to String variable "%s" in class None is unnecessary'
        assertViolations(SOURCE,
            [line:2, source:'String name = nameNode.toString()', message:String.format(message, 'name')],
            [line:3, source:'String id = account.id.toString()', message:String.format(message, 'id')],
            [line:4, source:'String code = account.getCode().toString()', message:String.format(message, 'code')])
    }

    @Test
    void test_AssignmentToStringVariableOrField_ToString_CheckAssignmentsIsFalse_NoViolations() {
        final SOURCE = '''
            class MyClass {
                String name = nameNode.toString()

                void run() {
                    String id = account.id.toString()
                    String code = account.getCode().toString()
                }
            }
        '''
        rule.checkAssignments = false
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnnecessaryToStringRule createRule() {
        new UnnecessaryToStringRule()
    }
}
