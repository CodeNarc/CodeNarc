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
 * Tests for ClassNotSerializableRule
 *
 * @author Daniel Zänker
 */
class ClassNotSerializableRuleTest extends AbstractRuleTestCase<ClassNotSerializableRule> {

    boolean skipTestThatUnrelatedCodeHasNoViolations = true

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassNotSerializable'
    }

    @Test
    void testSerializableClasses_NoViolation() {
        final SOURCE = '''
            class SomeClass implements Serializable {}
            
            class SuperClassInheritance extends SomeClass {}
            
            class InterfaceInheritance implements Externalizable {}
            
            abstract class SomeAbstractClass implements Serializable {}
            
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInterface_NoViolation() {
        final SOURCE = '''
            interface SomeInterface {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testScript_NoViolation() {
        final SOURCE = '''
            class SomeClass implements Serializable {}
            def x = 42
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonSerializableClasses_Violation() {
        final SOURCE = '''
            class SomeClass {}
            
            class SuperClassInheritance extends SomeClass {}
            
            class InterfaceInheritance implements Cloneable {}
            
            abstract class SomeAbstractClass {}
        '''
        assertViolations(SOURCE,
            [line: 2, source: 'class SomeClass {}', message: 'Class SomeClass is not Serializable'],
            [line: 4, source: 'class SuperClassInheritance extends SomeClass {}', message: 'Class SuperClassInheritance is not Serializable'],
            [line: 6, source: 'class InterfaceInheritance implements Cloneable {}', message: 'Class InterfaceInheritance is not Serializable'],
            [line: 8, source: 'abstract class SomeAbstractClass {}', message: 'Class SomeAbstractClass is not Serializable'])
    }

    @Override
    protected ClassNotSerializableRule createRule() {
        new ClassNotSerializableRule()
    }
}
