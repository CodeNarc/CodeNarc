/*
 * Copyright 2010 the original author or authors.
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
 * Tests for AbstractClassWithoutAbstractMethodRule
 *
 * @author 'Hamlet D'Arcy'
 */
class AbstractClassWithoutAbstractMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AbstractClassWithoutAbstractMethod'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                void method1() { }
                void method2() {  }
            }
            abstract class MyClass2 extends AbstractParent{
                void method1() { }
                void method2() {  }
            }
            abstract class MyClass3 extends BaseParent{
                void method1() { }
                void method2() {  }
            }
            abstract class MyBaseClass {
                void method1() { }
                abstract void method2()
            } 

            interface MyMarkerInterface {
            } '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            abstract class MyBaseClass {
                void method1() { }
                void method2() { }
            } '''
        assertSingleViolation(SOURCE, 2,
                'abstract class MyBaseClass',
                'The abstract class MyBaseClass contains no abstract methods')
    }

    protected Rule createRule() {
        new AbstractClassWithoutAbstractMethodRule()
    }
}
