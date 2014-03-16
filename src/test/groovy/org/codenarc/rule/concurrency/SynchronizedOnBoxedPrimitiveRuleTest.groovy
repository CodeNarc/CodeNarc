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
 * Tests for SynchronizedOnBoxedPrimitiveRule
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedOnBoxedPrimitiveRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SynchronizedOnBoxedPrimitive'
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
                final def lock = true

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
    void testReferenceTypeDeclaration() {
        final SOURCE = '''
            class MyClass {
                Byte byte1 = 100
                Short short1 = 1
                Double double1 = 1
                Integer integer1 = 1
                Long long1 = 1
                Float float1 = 1
                Character char1 = 1

                def method() {
                    synchronized(byte1) {}
                    synchronized(short1) {}
                    synchronized(double1) {}
                    synchronized(integer1) {}
                    synchronized(long1) {}
                    synchronized(float1) {}
                    synchronized(char1) {}
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 12, sourceLineText: 'synchronized(byte1)', messageText: 'Synchronizing on the Byte field byte1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 13, sourceLineText: 'synchronized(short1)', messageText: 'Synchronizing on the Short field short1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 14, sourceLineText: 'synchronized(double1)', messageText: 'Synchronizing on the Double field double1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 15, sourceLineText: 'synchronized(integer1)', messageText: 'Synchronizing on the Integer field integer1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 16, sourceLineText: 'synchronized(long1)', messageText: 'Synchronizing on the Long field long1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 17, sourceLineText: 'synchronized(float1)', messageText: 'Synchronizing on the Float field float1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 18, sourceLineText: 'synchronized(char1)', messageText: 'Synchronizing on the Character field char1 is unsafe. Do not synchronize on boxed types'])
    }

    @Test
    void testPrimitiveTypeDeclaration() {
        final SOURCE = '''
            class MyClass {
                byte byte2 = getValue()
                short short2 = getValue()
                double double2 = getValue()
                int integer2 = getValue()
                long long2 = getValue()
                float float2 = getValue()
                char char2 = getValue()

                def method() {
                    synchronized(byte2) {}
                    synchronized(short2) {}
                    synchronized(double2) {}
                    synchronized(integer2) {}
                    synchronized(long2) {}
                    synchronized(float2) {}
                    synchronized(char2) {}
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 12, sourceLineText: 'synchronized(byte2)', messageText: 'Synchronizing on the Byte field byte2 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 13, sourceLineText: 'synchronized(short2)', messageText: 'Synchronizing on the Short field short2 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 14, sourceLineText: 'synchronized(double2)', messageText: 'Synchronizing on the Double field double2 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 15, sourceLineText: 'synchronized(integer2)', messageText: 'Synchronizing on the Integer field integer2 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 16, sourceLineText: 'synchronized(long2)', messageText: 'Synchronizing on the Long field long2 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 17, sourceLineText: 'synchronized(float2)', messageText: 'Synchronizing on the Float field float2 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 18, sourceLineText: 'synchronized(char2)', messageText: 'Synchronizing on the Character field char2 is unsafe. Do not synchronize on boxed types'])
    }

    @Test
    void testReferenceInstanceDeclaration() {
        final SOURCE = '''
            class MyClass {
                def byte3 = new Byte((byte)100)
                def short3 = new Short((short)1)
                def double3 = new Double((double)1)
                def integer3 = new Integer(1)
                def long3 = new Long(1)
                def float3 = new Float(1)
                def char3 = new Character((char)'1')

                def method() {
                    synchronized(byte3) {}
                    synchronized(short3) {}
                    synchronized(double3) {}
                    synchronized(integer3) {}
                    synchronized(long3) {}
                    synchronized(float3) {}
                    synchronized(char3) {}
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 12, sourceLineText: 'synchronized(byte3)', messageText: 'Synchronizing on the Byte field byte3 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 13, sourceLineText: 'synchronized(short3)', messageText: 'Synchronizing on the Short field short3 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 14, sourceLineText: 'synchronized(double3)', messageText: 'Synchronizing on the Double field double3 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 15, sourceLineText: 'synchronized(integer3)', messageText: 'Synchronizing on the Integer field integer3 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 16, sourceLineText: 'synchronized(long3)', messageText: 'Synchronizing on the Long field long3 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 17, sourceLineText: 'synchronized(float3)', messageText: 'Synchronizing on the Float field float3 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 18, sourceLineText: 'synchronized(char3)', messageText: 'Synchronizing on the Character field char3 is unsafe. Do not synchronize on boxed types'])
    }

    @Test
    void testPrimitiveCastDeclaration() {
        final SOURCE = '''
            class MyClass {
                def byte4 = 1 as byte
                def short4 = 1 as short
                def double4 = 1 as double
                def integer4 = 1 as int
                def long4 = 1 as long
                def float4 = 1 as float
                def char4 = 1 as char

                def method() {
                    synchronized(byte4) {}
                    synchronized(short4) {}
                    synchronized(double4) {}
                    synchronized(integer4) {}
                    synchronized(long4) {}
                    synchronized(float4) {}
                    synchronized(char4) {}
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 12, sourceLineText: 'synchronized(byte4)', messageText: 'Synchronizing on the Byte field byte4 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 13, sourceLineText: 'synchronized(short4)', messageText: 'Synchronizing on the Short field short4 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 14, sourceLineText: 'synchronized(double4)', messageText: 'Synchronizing on the Double field double4 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 15, sourceLineText: 'synchronized(integer4)', messageText: 'Synchronizing on the Integer field integer4 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 16, sourceLineText: 'synchronized(long4)', messageText: 'Synchronizing on the Long field long4 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 17, sourceLineText: 'synchronized(float4)', messageText: 'Synchronizing on the Float field float4 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 18, sourceLineText: 'synchronized(char4)', messageText: 'Synchronizing on the Character field char4 is unsafe. Do not synchronize on boxed types'])
    }

    @Test
    void testReferenceCastDeclaration() {
        final SOURCE = '''
            class MyClass {
                def byte5 = 1 as Byte
                def short5 = 1 as Short
                def double5 = 1 as Double
                def integer5 = 1 as Integer
                def long5 = 1 as Long
                def float5 = 1 as Float
                def char5 = 1 as Character

                def method() {
                    synchronized(byte5) {}
                    synchronized(short5) {}
                    synchronized(double5) {}
                    synchronized(integer5) {}
                    synchronized(long5) {}
                    synchronized(float5) {}
                    synchronized(char5) {}
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 12, sourceLineText: 'synchronized(byte5)', messageText: 'Synchronizing on the Byte field byte5 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 13, sourceLineText: 'synchronized(short5)', messageText: 'Synchronizing on the Short field short5 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 14, sourceLineText: 'synchronized(double5)', messageText: 'Synchronizing on the Double field double5 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 15, sourceLineText: 'synchronized(integer5)', messageText: 'Synchronizing on the Integer field integer5 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 16, sourceLineText: 'synchronized(long5)', messageText: 'Synchronizing on the Long field long5 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 17, sourceLineText: 'synchronized(float5)', messageText: 'Synchronizing on the Float field float5 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 18, sourceLineText: 'synchronized(char5)', messageText: 'Synchronizing on the Character field char5 is unsafe. Do not synchronize on boxed types'])
    }

    @Test
    void testPrimitiveJava5StyleCastDeclaration() {
        final SOURCE = '''
            class MyClass {
                def byte7 = (byte)1
                def short7 = (short)1
                def double7 = (double)1
                def integer7 = (int)1
                def long7 = (long)1
                def float7 = (float)1
                def char7 = (char)1

                def method() {
                    synchronized(byte7) {}
                    synchronized(short7) {}
                    synchronized(double7) {}
                    synchronized(integer7) {}
                    synchronized(long7) {}
                    synchronized(float7) {}
                    synchronized(char7) {}
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 12, sourceLineText: 'synchronized(byte7)', messageText: 'Synchronizing on the Byte field byte7 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 13, sourceLineText: 'synchronized(short7)', messageText: 'Synchronizing on the Short field short7 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 14, sourceLineText: 'synchronized(double7)', messageText: 'Synchronizing on the Double field double7 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 15, sourceLineText: 'synchronized(integer7)', messageText: 'Synchronizing on the Integer field integer7 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 16, sourceLineText: 'synchronized(long7)', messageText: 'Synchronizing on the Long field long7 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 17, sourceLineText: 'synchronized(float7)', messageText: 'Synchronizing on the Float field float7 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 18, sourceLineText: 'synchronized(char7)', messageText: 'Synchronizing on the Character field char7 is unsafe. Do not synchronize on boxed types'])
    }

    @Test
    void testLiteralDeclaration() {
        final SOURCE = '''
            class MyClass {
                def integer1 = 5I
                def integer2 = 5
                def long1 = 5L
                def double1 = 1d
                def float1 = 5F

                def method() {
                    synchronized(integer1) {}
                    synchronized(integer2) {}
                    synchronized(long1) {}
                    synchronized(double1) {}
                    synchronized(float1) {}
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 10, sourceLineText: 'synchronized(integer1)', messageText: 'Synchronizing on the Integer field integer1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 11, sourceLineText: 'synchronized(integer2)', messageText: 'Synchronizing on the Integer field integer2 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 12, sourceLineText: 'synchronized(long1)', messageText: 'Synchronizing on the Long field long1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 13, sourceLineText: 'synchronized(double1)', messageText: 'Synchronizing on the Double field double1 is unsafe. Do not synchronize on boxed types'],
                [lineNumber: 14, sourceLineText: 'synchronized(float1)', messageText: 'Synchronizing on the Float field float1 is unsafe. Do not synchronize on boxed types'])
    }

    @Test
    void testBooleanSuccessScenario() {
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
                    final def lock = true

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
    void testBoolPrimitiveField() {
        final SOURCE = '''
                class MyClass5 {
                    final boolean lock = someMethod() // not interned, but quite possibly is
                    def method() {
                        synchronized(lock) { }
                    }
                }
            '''
        assertSingleViolation(SOURCE, 5, 'synchronized(lock)', 'Synchronizing on the Boolean field lock is unsafe. Do not synchronize on boxed types')
    }

    @Test
    void testBoolField() {
        final SOURCE = '''
                class MyClass5 {
                    final Boolean lock = someMethod() // not interned, but quite possibly is
                    def method() {
                        synchronized(lock) { }
                    }
                }
            '''
        assertSingleViolation(SOURCE, 5, 'synchronized(lock)', 'Synchronizing on the Boolean field lock is unsafe. Do not synchronize on boxed types')
    }

    @Test
    void testBasicViolation() {
        final SOURCE = '''
                class MyClass {

                    final boolean lock = true

                    def method() {
                        synchronized(lock) { }
                    }
                }
            '''
        assertSingleViolation(SOURCE, 7, 'synchronized(lock)', 'Synchronizing on the Boolean field lock is unsafe. Do not synchronize on boxed types')
    }

    @Test
    void testInnerClass() {
        final SOURCE = '''
                class MyClass {

                    final Boolean boollock = true

                    class MyInnerClass {
                        def method() {
                            // violation
                            synchronized(boollock) { }
                        }
                    }
                }
            '''
        assertSingleViolation(SOURCE, 9, 'synchronized(boollock)', 'Synchronizing on the Boolean field boollock is unsafe. Do not synchronize on boxed types')
    }

    @Test
    void testImplicitTyping() {
        final SOURCE = '''
                class MyClass {
                    // implicit typing
                    final def lock = true

                    def method() {
                        // violation
                        synchronized(lock) { }
                    }
                }
            '''
        assertSingleViolation(SOURCE, 8, 'synchronized(lock)', 'Synchronizing on the Boolean field lock is unsafe. Do not synchronize on boxed types')
    }

    @Test
    void testAnonymousClass() {
        final SOURCE = '''
                class MyClass {
                    // implicit typing
                    final def lock = new Object[0] // correct idiom

                    def method() {
                        return new Runnable() {
                            final def lock = false // shadows parent from inner class
                            public void run() {
                                // violation
                                synchronized(lock) { }
                            }
                        }
                    }
                }
            '''
        assertSingleViolation(SOURCE, 11, 'synchronized(lock)', 'Synchronizing on the Boolean field lock is unsafe. Do not synchronize on boxed types')
    }

    @Test
    void testShadowing() {
        final SOURCE = '''
                class MyClass {
                    // implicit typing
                    final def lock = new Object[0] // correct idiom

                    class MyInnerClass {

                        final def lock = true // shadows parent from inner class
                        def method() {
                            // violation
                            synchronized(lock) { }
                        }
                    }
                }
            '''
        assertSingleViolation(SOURCE, 11, 'synchronized(lock)', 'Synchronizing on the Boolean field lock is unsafe. Do not synchronize on boxed types')
    }

    protected Rule createRule() {
        new SynchronizedOnBoxedPrimitiveRule()
    }
}
