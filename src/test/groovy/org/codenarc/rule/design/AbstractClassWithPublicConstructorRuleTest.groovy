/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for AbstractClassWithPublicConstructorRule
 *
 * @author Chris Mair
 */
class AbstractClassWithPublicConstructorRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AbstractClassWithPublicConstructor'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyClass { }

            abstract class MyClass2 { }

            abstract class MyClass3 {
                protected MyClass3() { }
                def method1() { }
                public int method2() { }
            }

            abstract class MyClass4 extends AbstractParent {
                protected MyClass4() {
                    this(23)
                }
                private MyClass4(int count) { }
            }

            interface MyInterface { }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAbstractClassWithOnlyPublicConstructor() {
        final SOURCE = '''
            abstract class MyClass {
                MyClass() { }
            } '''
        assertSingleViolation(SOURCE, 3, 'MyClass() { }', 'MyClass')
    }

    @Test
    void testAbstractClassWithMultipleConstructors() {
        final SOURCE = '''
            abstract class MyClass {
                protected MyClass() { }
                public MyClass(int count) { }
                private MyClass(String name) { }
            } '''
        assertSingleViolation(SOURCE, 4, 'public MyClass(int count) { }', 'MyClass')
    }

    protected Rule createRule() {
        new AbstractClassWithPublicConstructorRule()
    }
}
