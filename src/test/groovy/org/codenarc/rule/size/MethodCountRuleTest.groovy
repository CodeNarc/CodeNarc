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
package org.codenarc.rule.size

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for MethodCountRule
 *
 * @author 'Tomasz Bujok'
  */
class MethodCountRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'MethodCount'
        assert rule.maxMethods == 30
    }

    @Test
    void testSuccessScenario() {
        String classContent = 'class MyClass {\n'
        for (int i = 0; i < rule.maxMethods; i++) {
            classContent += "public void method${i}() {}\n"
        }
        classContent += '\n}'
        assertNoViolations(classContent)
    }

    @Test
    void testSingleViolation() {
        String classContent = '''
            class MyClass {
                void method1() { }
                void method2() { }
                void method3() { }
            }
        '''
        rule.maxMethods = 2
        assertSingleViolation(classContent, 2, 'class MyClass {', ['MyClass', '3'])
    }

    @Test
    void testIgnoreGeneratedMethods() {
        rule.maxMethods = 2

        // A script will result in generated run and main methods
        String classContent = '''
            void method1() {}
            void method2() {}
        '''
        assertNoViolations(classContent)
    }

    protected Rule createRule() {
        new MethodCountRule()
    }
}
