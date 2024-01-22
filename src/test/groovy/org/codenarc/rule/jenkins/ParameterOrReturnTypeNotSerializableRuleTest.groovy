/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for ParameterOrReturnTypeNotSerializableRule
 *
 * @author Daniel Zänker
 */
class ParameterOrReturnTypeNotSerializableRuleTest extends AbstractRuleTestCase<ParameterOrReturnTypeNotSerializableRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ParameterOrReturnTypeNotSerializable'
    }

    @Test
    void testSerializableTypesInClassMethods_NoViolations() {
        final SOURCE = '''
            class SomeClass implements Serializable {
                void method() {}
                SomeClass otherMethod(SomeClass s, ArrayList l) {}
                boolean primitives(int i, double d, boolean b) {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSerializableTypesInInterfaceMethods_NoViolations() {
        final SOURCE = '''
            class SomeClass implements Serializable {} 
            interface SomeInterface {
                void method()
                SomeClass otherMethod(SomeClass s, ArrayList l)
                boolean primitives(int i, double d, boolean b)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonSerializableTypesInClassMethods_Violations() {
        final SOURCE = '''
            class SomeClass {
                void method(int i, SomeClass s) {}
                SomeClass returnMethod() {}
            }
        '''
        assertViolations(SOURCE,
            [line: 3, source: 'void method(int i, SomeClass s) {}', message: 'Parameter s of method SomeClass.method is not Serializable'],
            [line: 4, source: 'SomeClass returnMethod() {}', message: 'Return type SomeClass of method SomeClass.returnMethod is not Serializable'])
    }

    @Test
    void testNonSerializableTypesInInterfaceMethods_Violations() {
        final SOURCE = '''
            class SomeClass {} 
            interface SomeInterface {
                void method(int i, SomeClass s)
                SomeClass returnMethod()
            }
        '''
        assertViolations(SOURCE,
            [line: 4, source: 'void method(int i, SomeClass s)', message: 'Parameter s of method SomeInterface.method is not Serializable'],
            [line: 5, source: 'SomeClass returnMethod()', message: 'Return type SomeClass of method SomeInterface.returnMethod is not Serializable'])
    }

    @Override
    protected ParameterOrReturnTypeNotSerializableRule createRule() {
        new ParameterOrReturnTypeNotSerializableRule()
    }
}
