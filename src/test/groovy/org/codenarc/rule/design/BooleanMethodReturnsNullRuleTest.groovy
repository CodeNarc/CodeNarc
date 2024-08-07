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
import org.junit.jupiter.api.Test

/**
 * Tests for BooleanMethodReturnsNullRule
 *
 * @author Hamlet D'Arcy
 */
class BooleanMethodReturnsNullRuleTest extends AbstractRuleTestCase<BooleanMethodReturnsNullRule> {

    @Test
    void RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BooleanMethodReturnsNull'
    }

    @Test
    void ProperReturnOfTrueAndFalse_NoViolations() {
        final SOURCE = '''
            def c = {
                    if (foo()) {
                        return true
                    } else {
                        return false
                    }
            }
            def scriptMethod() {
                if (foo()) {
                    return true
                } else {
                    return false
                }
            }
            class MyClass {
                def x = new Object() {
                    def method1() {
                        if (foo()) {
                            return true
                        } else {
                            return false
                        }
                    }
                }
                def method2() {
                    def y = {
                        if (foo()) {
                            return true
                        } else {
                            return false
                        }
                    }
                    if (foo()) {
                        return true
                    } else {
                        return false
                    }
                }
                boolean method3() {
                    if (foo()) {
                        return true
                    } else {
                        return false
                    }
                }
                boolean method4() {
                    if (foo()) {
                        return ret() as Boolean
                    } else {
                        return ret() as Boolean
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void DefMethodReturnsNull() {
        final SOURCE = '''
            def scriptMethod() {
                if (foo()) {
                    return true
                } else {
                    return null
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'return null')
    }

    @Test
    void DefTernaryReturnsNull() {
        final SOURCE = '''
            def scriptMethod() {
                return x ? null : true
            }
            def scriptMethod2() {
                return x ? false : null
            }
        '''
        assertTwoViolations(SOURCE, 3, 'x ? null : true', 6, 'x ? false : null')
    }

    @Test
    void DefClassMethodReturnsNull() {
        final SOURCE = '''
            class MyClass {
                def scriptMethod() {
                    if (foo()) {
                        return true
                    } else {
                        return null
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'return null')
    }

    @Test
    void AnonymousClassMethodReturnsNull() {
        final SOURCE = '''
            def x = new Object() {
                def scriptMethod() {
                    if (foo()) {
                        return true
                    } else {
                        return null
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'return null')
    }

    @Test
    void DefMethodReturnsNullAndTRUE() {
        final SOURCE = '''
            def scriptMethod() {
                if (foo()) {
                    return Boolean.TRUE
                } else {
                    return null
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'return null')
    }

    @Test
    void DefMethodReturnsBooleanCast() {
        final SOURCE = '''
            def scriptMethod() {
                if (foo()) {
                    return ret() as Boolean
                } else {
                    return null
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'return null')
    }

    @Test
    void BooleanMethodReturnsNull() {
        final SOURCE = '''
            boolean scriptMethod() {
                if (foo()) {
                    return ret()
                } else {
                    return null
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'return null')
    }

    @Test
    void MethodReturnsNonBooleanTypesAndNull_NoViolations() {
        final SOURCE = '''
            def scriptMethod(clazz) {
                if (clazz == String) {
                    return 'something'
                }
                if (clazz == Number) {
                    return 1
                }
                if (clazz == Boolean) {
                    return true
                }
                return null
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void Closure_ReturnsNullAndBoolean() {
        final SOURCE = '''
            def closure = {
                if (foo()) {
                    return true
                } else {
                    return null
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'return null')
    }

    @Test
    void ClosureReturnsNonBooleanTypesAndNull_NoViolations() {
        final SOURCE = '''
            def closure = {
                if (clazz == String) {
                    return 'something'
                }
                if (clazz == Number) {
                    return 1
                }
                if (clazz == Boolean) {
                    return true
                }
                return null
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void ClosureWithinMethod_ReturnsFalse_MethodReturnsNull() {
        final SOURCE = '''
            class MyClass {
                void method() {
                    if (foo()) {
                        return
                    }
                    def closure = {
                        return false
                    }
                    closure.call()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected BooleanMethodReturnsNullRule createRule() {
        new BooleanMethodReturnsNullRule()
    }
}
