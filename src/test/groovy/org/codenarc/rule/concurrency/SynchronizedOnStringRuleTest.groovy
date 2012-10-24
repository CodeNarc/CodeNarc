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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SynchronizedOnStringRule
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedOnStringRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SynchronizedOnString'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass1 {
                final Object lock = new Object[0]
                def method() {
                    synchronized(lock) { }
                }

                class MyInnerClass {
                    def method() {
                        synchronized(lock) { }
                    }
                }
            }

            class MyClass2 {
                final def lock = new Object[0]
                def method() {
                    synchronized(lock) { }
                }
            }

            class MyClass3 {
                final def lock = new Object[0] // correct idiom
                def method() {
                    return new Runnable() {
                        public void run() {
                            synchronized(lock) { }
                        }
                    }
                }
            }

            class MyClass4 {
                final def lock = ""

                class MyInnerClass {

                    final def lock = new Object[0] // shadows parent from inner class
                    def method() {
                        synchronized(lock) { }
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStringField() {
        final SOURCE = '''
            class MyClass5 {
                final String lock = someMethod() // not interned, but quite possibly is
                def method() {
                    synchronized(lock) { }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'synchronized(lock)', 'Synchronizing on the constant String field lock is unsafe. Do not synchronize on interned strings')
    }
    @Test
    void testBasicViolation() {
        final SOURCE = '''
            class MyClass {

                final String stringLock = "stringLock"

                def method() {
                    synchronized(stringLock) { }
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'synchronized(stringLock)', 'Synchronizing on the constant String field stringLock is unsafe. Do not synchronize on interned strings')
    }

    @Test
    void testInnerClass() {
        final SOURCE = '''
            class MyClass {

                final String stringLock = "stringLock"

                class MyInnerClass {
                    def method() {
                        // violation
                        synchronized(stringLock) { }
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 9, 'synchronized(stringLock)', 'Synchronizing on the constant String field stringLock is unsafe. Do not synchronize on interned strings')
    }

    @Test
    void testImplicitTyping() {
        final SOURCE = '''
            class MyClass {
                // implicit typing
                final def stringLock = "stringLock"

                def method() {
                    // violation
                    synchronized(stringLock) { }
                }
            }
        '''
        assertSingleViolation(SOURCE, 8, 'synchronized(stringLock)', 'Synchronizing on the constant String field stringLock is unsafe. Do not synchronize on interned strings')
    }

    @Test
    void testAnonymousClass() {
        final SOURCE = '''
            class MyClass {
                // implicit typing
                final def lock = new Object[0] // correct idiom

                def method() {
                    return new Runnable() {
                        final def lock = "" // shadows parent from inner class
                        public void run() {
                            // violation
                            synchronized(lock) { }
                        }
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 11, 'synchronized(lock)', 'Synchronizing on the constant String field lock is unsafe. Do not synchronize on interned strings')
    }

    @Test
    void testShadowing() {
        final SOURCE = '''
            class MyClass {
                // implicit typing
                final def lock = new Object[0] // correct idiom

                class MyInnerClass {

                    final def lock = "" // shadows parent from inner class
                    def method() {
                        // violation
                        synchronized(lock) { }
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 11, 'synchronized(lock)', 'Synchronizing on the constant String field lock is unsafe. Do not synchronize on interned strings')
    }

    protected Rule createRule() {
        new SynchronizedOnStringRule()
    }
}
