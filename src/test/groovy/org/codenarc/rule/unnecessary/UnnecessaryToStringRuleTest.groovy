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
            [lineNumber:3, sourceLineText:'def name = "Joe".toString()', messageText:'Calling toString() on the String expression in class MyClass is unnecessary'],
            [lineNumber:6, sourceLineText:"def id = '123'.toString()", messageText:'Calling toString() on the String expression in class MyClass is unnecessary'],
            [lineNumber:7, sourceLineText:"def groupId = ((String)currentRow.get('GroupID')).toString()", messageText:'Calling toString() on the String expression in class MyClass is unnecessary'] )
    }

    @Test
    void test_PlusToStringExpression_Violation() {
        final SOURCE = '''
            class MyClass {
                def name = "Joe" + new Date().toString()

                void run() {
                    Object object = 1
                    def string = 'some string' + object.toString()                
                    
                    def bigString = 'some string' + new Date() + object.toString()      // not a violation; known limitation                
                    def other = 123 + object.toString().toInteger()     // not a violation                
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'def name = "Joe" + new Date().toString()', messageText:'Calling toString() on [new Date()] in class MyClass is unnecessary'],
                [lineNumber:7, sourceLineText:"def string = 'some string' + object.toString()", messageText:'Calling toString() on [object] in class MyClass is unnecessary'] )
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
            [lineNumber:3, sourceLineText:'String name = nameNode.toString()', messageText:String.format(message, 'name')],
            [lineNumber:4, sourceLineText:'String id = account.id.toString()', messageText:String.format(message, 'id')],
            [lineNumber:5, sourceLineText:'String code = account.getCode().toString()', messageText:String.format(message, 'code')] )
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
            [lineNumber:2, sourceLineText:'String name = nameNode.toString()', messageText:String.format(message, 'name')],
            [lineNumber:3, sourceLineText:'String id = account.id.toString()', messageText:String.format(message, 'id')],
            [lineNumber:4, sourceLineText:'String code = account.getCode().toString()', messageText:String.format(message, 'code')] )
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
