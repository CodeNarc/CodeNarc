/*
 * Copyright 2018 the original author or authors.
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
 * Tests for VariableTypeRequiredRule
 *
 * @author Chris Mair
 */
class VariableTypeRequiredRuleTest extends AbstractRuleTestCase<VariableTypeRequiredRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'VariableTypeRequired'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyClass {
                void doStuff() {
                    final String NAME = "joe"
                    int count = 0

                    String name = NAME
                    Date date = new Date()

                    String defaultName
                    long maxSoFar
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_FieldTypesNotSpecified_Violations() {
        final SOURCE = '''
            class MyClass {
                void doStuff() {
                    final NAME = "joe"
                    def count = 0, max = 99
                    def defaultName
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:4, sourceLineText:'final NAME = "joe"', messageText:'The type is not specified for variable "NAME"'],
                [lineNumber:5, sourceLineText:'def count = 0, max = 99', messageText:'The type is not specified for variable "count"'],
                [lineNumber:5, sourceLineText:'def count = 0, max = 99', messageText:'The type is not specified for variable "max"'],
                [lineNumber:6, sourceLineText:'def defaultName', messageText:'The type is not specified for variable "defaultName"'],
        )
    }

    @Test
    void test_ignoreVariableNames_MatchesNoName_Violations() {
        final SOURCE = '''
            void doStuff() {
                def max = 99
                def defaultName
            }
        '''
        rule.ignoreVariableNames = 'other'
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'def max = 99', messageText:'The type is not specified for variable "max"'],
                [lineNumber:4, sourceLineText:'def defaultName', messageText:'The type is not specified for variable "defaultName"'],
        )
    }

    @Test
    void test_ignoreVariableNames_MatchesNames() {
        final SOURCE = '''
            void doStuff() {
                def max = 99
                def defaultName
                def alternateNames = ['abc']
            }
        '''
        rule.ignoreVariableNames = 'other, a*Names, max, abc'
        assertViolations(SOURCE,
                [lineNumber:4, sourceLineText:'def defaultName', messageText:'The type is not specified for variable "defaultName"'],
        )
    }

    @Override
    protected VariableTypeRequiredRule createRule() {
        new VariableTypeRequiredRule()
    }
}
