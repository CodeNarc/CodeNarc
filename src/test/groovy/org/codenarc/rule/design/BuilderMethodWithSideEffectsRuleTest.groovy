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
 * Tests for BuilderMethodWithSideEffectsRule
 *
 * @author Hamlet D'Arcy
 */
class BuilderMethodWithSideEffectsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BuilderMethodWithSideEffects'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {

                    def make() { /* ... */ }
                    def makeSomething() { /* ... */ }

                    def create() { /* ... */ }
                    def createSomething() { /* ... */ }

                    def build() { /* ... */ }
                    def buildSomething() { /* ... */ }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMakeMethod() {
        final SOURCE = '''
            class MyClass {
                    void make() { /* ... */ }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void make()', "Violation in class MyClass. The method 'make' is named like a builder method but has a void return type")
    }

    @Test
    void testCreateMethod() {
        final SOURCE = '''
            class MyClass {
                    void create() { /* ... */ }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void create()', "Violation in class MyClass. The method 'create' is named like a builder method but has a void return type")
    }

    @Test
    void testBuildMethod() {
        final SOURCE = '''
            class MyClass {
                    void build() { /* ... */ }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void build()', "Violation in class MyClass. The method 'build' is named like a builder method but has a void return type")
    }

    @Test
    void testMakeSomethingMethod() {
        final SOURCE = '''
            class MyClass {
                    void makeSomething() { /* ... */ }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void makeSomething()', "Violation in class MyClass. The method 'makeSomething' is named like a builder method but has a void return type")
    }

    @Test
    void testCreateSomethingMethod() {
        final SOURCE = '''
            class MyClass {
                    void createSomething() { /* ... */ }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void createSomething()', "Violation in class MyClass. The method 'createSomething' is named like a builder method but has a void return type")
    }

    @Test
    void testBuildSomethingMethod() {
        final SOURCE = '''
            class MyClass {
                    void buildSomething() { /* ... */ }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void buildSomething()', "Violation in class MyClass. The method 'buildSomething' is named like a builder method but has a void return type")
    }

    protected Rule createRule() {
        new BuilderMethodWithSideEffectsRule()
    }
}
