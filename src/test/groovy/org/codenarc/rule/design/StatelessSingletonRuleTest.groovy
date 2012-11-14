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
 * Tests for StatelessSingletonRule
 *
 * @author Victor Savkin
  */
class StatelessSingletonRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StatelessSingleton'
    }

    @Test
    void testShouldAddNoViolationsForStatelessClassThatIsNotSingleton() {
        final SOURCE = '''
            class Service {
                void processItem(item){
                }
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldAddNoViolationsForStatefulClassThatIsNotSingleton() {
        final SOURCE = '''
            class Service {
                def state

                void processItem(item){
                }
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldAddNoViolationsForStatefulClassAnnotatedAsSingleton() {
        final SOURCE = '''
            @groovy.lang.Singleton
            class Service {
                def state
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldAddNoViolationsForStatefulClassImplementingSingletonPattern() {
        final SOURCE = '''
            class Service {
                static Service instance = new Service()
                def state
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldAddViolationForClassAnnotatedAsSingletonThatDoesNotHaveState() {
        final SOURCE = '''
            @groovy.lang.Singleton
            class Service {
                void processItem(item){
                }
            }
        '''
        assertSingleViolation SOURCE, 3, 'class Service', createErrorMsgForClass('Service')
    }

    @Test
    void testShouldAddNoViolationsForClassAnnotatedAsSingletonExtendingAnotherClass() {
        final SOURCE = '''
            class Parent {}

            @groovy.lang.Singleton
            class Service extends Parent {
                void processItem(item){
                }
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldAddViolationForClassHavingOneStaticFieldOfItsOwnType() {
        final SOURCE = '''
            class Service {
                static Service srv
                void processItem(item){
                }
            }
        '''
        assertSingleViolation SOURCE, 2, 'class Service', createErrorMsgForClass('Service')
    }

    @Test
    void testShouldAddViolationForClassHavingOneStaticFieldNamedInstance() {
        final SOURCE = '''
            class Service {
                static instance
                void processItem(item){
                }
            }
        '''
        assertSingleViolation SOURCE, 2, 'class Service', createErrorMsgForClass('Service')
    }

    @Test
    void testShouldAddViolationForClassHavingOneStaticFieldNamed_Instance() {
        final SOURCE = '''
            class Service {
                static _instance
                void processItem(item){
                }
            }
        '''
        assertSingleViolation SOURCE, 2, 'class Service', createErrorMsgForClass('Service')
    }

    @Test
    void testShouldAddNoViolationsForClassHavingStaticFieldNamedInstanceOfDifferentType() {
        final SOURCE = '''
            class Service {
                static String instance
                void processItem(item){
                }
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldAddNoViolationsForClassImplementingSingletonPatternAndExtendingAnotherClass() {
        final SOURCE = '''
            class Parent {}

            class Service extends Parent {
                static Service srv
                void processItem(item){
                }
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldAddNoViolationsForClassHaving2StaticFieldsOfItsOwnType() {
        final SOURCE = '''
            class Service {
                static Service srv1, srv1
                void processItem(item){
                }
            }
        '''
        assertNoViolations SOURCE
    }

    protected Rule createRule() {
        new StatelessSingletonRule()
    }

    private createErrorMsgForClass(name) {
        "There is no point in creating a stateless Singleton. Make a new instance of '$name' with the new keyword instead."
    }
}
