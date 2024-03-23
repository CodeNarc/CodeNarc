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

import org.junit.jupiter.api.Test

/**
 * Tests for ExpressionInCpsMethodNotSerializableRule
 *
 * @author Daniel Zänker
 */
class ExpressionInCpsMethodNotSerializableRuleTest extends AbstractJenkinsRuleTestCase<ExpressionInCpsMethodNotSerializableRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExpressionInCpsMethodNotSerializable'
    }

    @Test
    void testVariableDeclaration_NoViolation() {
        final SOURCE = '''
            class SomeClass implements Serializable {}
            def main() {
                def something = []
                ArrayList<Integer> arrayList = [1,2,3]
                List list = [4,5,6] 
                Map map = [key1: 1, key2: 2]
                SomeClass c = new SomeClass()
                int number = 42
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testForLoop_NoViolation() {
        final SOURCE = '''
            def main() {
                ArrayList<Integer> arrayList = [1,2,3]
                List list = [4,5,6] 
                for (int i in list) {
                }
                for (int i in arrayList) {
                }
                for (int i in (0..3).toList()) {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTupleDeclaration_NoViolation() {
        final SOURCE = '''
            class SomeClass implements Serializable {}
            def main() {
                def (int i, String j, SomeClass s, dynamic, List l) = [10, 'foo', new SomeClass(), 42, [1,2,3]]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableDeclaration_Violation() {
        final SOURCE = '''
            class SomeClass {}
            def main() {
                ArrayList<SomeClass> list = []
                SomeClass c = new SomeClass()
            }
        '''
        assertViolations(SOURCE,
            [line: 4, source: 'ArrayList<SomeClass> list = []', message: 'Variable list is not Serializable and used in CPS transformed code'],
            [line: 5, source: 'SomeClass c = new SomeClass()', message: 'Variable c is not Serializable and used in CPS transformed code'])
    }

    @Test
    void testTupleDeclaration_Violation() {
        final SOURCE = '''
            class SomeClass {} 
            def main() {
                def (ArrayList<SomeClass> list, SomeClass c) = [[1,2,3], new SomeClass()]
            }
        '''
        assertViolations(SOURCE,
            [line: 4, source: 'def (ArrayList<SomeClass> list, SomeClass c) = [[1,2,3], new SomeClass()]',
                message: 'Variable list is not Serializable and used in CPS transformed code'],
            [line: 4, source: 'def (ArrayList<SomeClass> list, SomeClass c) = [[1,2,3], new SomeClass()]',
                message: 'Variable c is not Serializable and used in CPS transformed code'])
    }

    @Test
    void testForLoop_Violation() {
        final SOURCE = '''
            class SomeClass {}
            def main() {
                for (int i in 0..3) {
                }
                ArrayList<SomeClass> list = []
                for (SomeClass sc in list) {
                }
            }
        '''
        assertViolations(SOURCE,
            [line: 4, source: 'for (int i in 0..3) {', message: 'The type of the collection that is iterated over is not Serializable and used in CPS transformed code'],
            [line: 6, source: 'ArrayList<SomeClass> list = []', message: 'Variable list is not Serializable and used in CPS transformed code'],
            [line: 7, source: 'for (SomeClass sc in list) {', message: 'Variable sc is not Serializable and used in CPS transformed code'],
            [line: 7, source: 'for (SomeClass sc in list) {', message: 'The type of the collection that is iterated over is not Serializable and used in CPS transformed code']
        )
    }

    @Override
    protected ExpressionInCpsMethodNotSerializableRule createRule() {
        new ExpressionInCpsMethodNotSerializableRule()
    }
}
