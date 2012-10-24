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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for FactoryMethodNameRule
 *
 * @author Hamlet D'Arcy
 */
class FactoryMethodNameRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'FactoryMethodName'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {

                def doSomething() {}
                def make() {}
                def makeSomething() {}
                @Override build() { } // OK, overriding
            }

            class WidgetBuilder {
                def build() {}
                def buildSomething() {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCreate() {
        final SOURCE = '''
            class MyClass {

                // violation. Factory methods should be named make()
                def create() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'def create()',
            "Violation in class MyClass. The method 'create' matches the regular expression /(build.*|create.*)/ and does not appear in a class matching /*.Builder/")
    }

    @Test
    void testCreateSomething() {
        final SOURCE = '''
            class MyClass {

                // violation. Factory methods should be named make()
                def createSomething() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'def createSomething()',
            "Violation in class MyClass. The method 'createSomething' matches the regular expression /(build.*|create.*)/ and does not appear in a class matching /*.Builder/")
    }

    @Test
    void testBuild() {
        final SOURCE = '''
            package test

            class MyClass {

                // violation. Builder method not in class named *Builder
                def build() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'def build()',
            "Violation in class MyClass. The method 'build' matches the regular expression /$rule.regex/ and does not appear in a class matching /*.Builder/")
    }

    @Test
    void testBuildSomething() {
        final SOURCE = '''
            package test

            class MyClass {

                // violation. Builder method not in class named *Builder
                def buildSomething() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'def buildSomething()',
            "The method 'buildSomething' matches the regular expression /(build.*|create.*)/ and does not appear in a class matching /*.Builder/")
    }

    @Test
    void testCreateInBuilder() {
        final SOURCE = '''
            class WidgetBuilder {
                def create() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def create()',
            "Violation in class WidgetBuilder. The method 'create' matches the regular expression /(build.*|create.*)/")
    }

    protected Rule createRule() {
        new FactoryMethodNameRule()
    }
}
