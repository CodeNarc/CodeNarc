/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for InterfaceNameSameAsSuperInterfaceRule
 *
 * @author Chris Mair
 */
class InterfaceNameSameAsSuperInterfaceRuleTest extends AbstractRuleTestCase<InterfaceNameSameAsSuperInterfaceRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'InterfaceNameSameAsSuperInterface'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            interface MyInterface { }
            interface Runnable { }
            interface MyOtherInterface extends SomeOtherInterface { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testClasses_NoViolations() {
        final SOURCE = '''
            interface MyInterface extends other.MyClass { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInterfaceName_SameAsSuperInterface() {
        final SOURCE = '''
            interface MyInterface extends other.MyInterface { }
            '''
        assertViolations(SOURCE,
            [line:2, source:'interface MyInterface extends other.MyInterface', message:'Interface MyInterface has the same simple name as its super-interface other.MyInterface'])
    }

    @Test
    void testInterfaceName_SameAsSuperInterface_Package() {
        final SOURCE = '''
            package com.example
            interface MyInterface extends other.MyInterface { }
            '''
        assertViolations(SOURCE,
            [line:3, source:'interface MyInterface extends other.MyInterface', message:'Interface com.example.MyInterface has the same simple name as its super-interface other.MyInterface'])
    }

    @Override
    protected InterfaceNameSameAsSuperInterfaceRule createRule() {
        new InterfaceNameSameAsSuperInterfaceRule()
    }
}
