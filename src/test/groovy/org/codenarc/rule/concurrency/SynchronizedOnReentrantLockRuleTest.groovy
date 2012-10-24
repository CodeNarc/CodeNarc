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
 * Tests for SynchronizedOnReentrantLockRule
 *
 * @author 'Hamlet D'Arcy'
 */
class SynchronizedOnReentrantLockRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SynchronizedOnReentrantLock'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass1 {
                final lock = new ReentrantLock()
                def method()  {
                   lock.lock()
                   try { } finally { lock.unlock() }
                }

                class MyInnerClass {
                    def method() {
                        lock.lock()
                        try { } finally { lock.unlock() }
                    }
                }
            }

            class MyClass2 {
                final lock = new ReentrantLock()
                def method() {
                   lock.lock()
                   try { } finally { lock.unlock() }
                }
            }

            class MyClass3 {
                final lock = new ReentrantLock()
                def method() {
                    return new Runnable() {
                        public void run() {
                           lock.lock()
                           try { } finally { lock.unlock() }
                        }
                    }
                }
            }

            class MyClass4 {
                final lock = new ReentrantLock()

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
    void testTypeDeclarationInField() {
        final SOURCE = '''
            class MyClass5 {
                final ReentrantLock lock = value()
                def method() {
                    synchronized(lock) { }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'synchronized(lock)', 'Synchronizing on a ReentrantLock field lock. This is almost never the intended usage; use the lock() and unlock() methods instead')
    }
    @Test
    void testFQNViolation() {
        final SOURCE = '''
            class MyClass {

                final lock = new java.util.concurrent.locks.ReentrantLock()

                def method() {
                    synchronized(lock) { }
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'synchronized(lock)', 'Synchronizing on a ReentrantLock field lock. This is almost never the intended usage; use the lock() and unlock() methods instead')
    }

    @Test
    void testInnerClass() {
        final SOURCE = '''
            class MyClass {

                final ReentrantLock lock = (ReentrantLock)"stringLock"

                class MyInnerClass {
                    def method() {
                        // violation
                        synchronized(lock) { }
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 9, 'synchronized(lock)', 'Synchronizing on a ReentrantLock field lock. This is almost never the intended usage; use the lock() and unlock() methods instead')
    }

    @Test
    void testImplicitTyping() {
        final SOURCE = '''
            class MyClass {
                // implicit typing
                final def lock = "stringLock" as ReentrantLock

                def method() {
                    // violation
                    synchronized(lock) { }
                }
            }
        '''
        assertSingleViolation(SOURCE, 8, 'synchronized(lock)', 'Synchronizing on a ReentrantLock field lock. This is almost never the intended usage; use the lock() and unlock() methods instead')
    }

    @Test
    void testAnonymousClass() {
        final SOURCE = '''
            class MyClass {
                // implicit typing
                final def lock = new Object[0] // correct idiom

                def method() {
                    return new Runnable() {
                        final def lock = new ReentrantLock() // shadows parent from inner class
                        public void run() {
                            // violation
                            synchronized(lock) { }
                        }
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 11, 'synchronized(lock)', 'Synchronizing on a ReentrantLock field lock. This is almost never the intended usage; use the lock() and unlock() methods instead')
    }

    @Test
    void testShadowing() {
        final SOURCE = '''
            class MyClass {
                // implicit typing
                final def lock = new Object[0] // correct idiom

                class MyInnerClass {

                    final lock = value as ReentrantLock // shadows parent from inner class
                    def method() {
                        // violation
                        synchronized(lock) { }
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 11, 'synchronized(lock)', 'Synchronizing on a ReentrantLock field lock. This is almost never the intended usage; use the lock() and unlock() methods instead')
    }

    protected Rule createRule() {
        new SynchronizedOnReentrantLockRule()
    }
}
