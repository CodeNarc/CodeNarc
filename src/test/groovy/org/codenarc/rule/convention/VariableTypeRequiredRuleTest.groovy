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
                [line:4, source:'final NAME = "joe"', message:'The type is not specified for variable "NAME"'],
                [line:5, source:'def count = 0, max = 99', message:'The type is not specified for variable "count"'],
                [line:5, source:'def count = 0, max = 99', message:'The type is not specified for variable "max"'],
                [line:6, source:'def defaultName', message:'The type is not specified for variable "defaultName"'],
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
                [line:3, source:'def max = 99', message:'The type is not specified for variable "max"'],
                [line:4, source:'def defaultName', message:'The type is not specified for variable "defaultName"'],
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
                [line:4, source:'def defaultName', message:'The type is not specified for variable "defaultName"'],
        )
    }

    @Override
    protected VariableTypeRequiredRule createRule() {
        new VariableTypeRequiredRule()
    }
}
