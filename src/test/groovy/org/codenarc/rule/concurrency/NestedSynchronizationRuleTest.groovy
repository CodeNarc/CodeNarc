package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Unit test for NestedSynchronizationRule.
 * 
 * @author Hamlet D'Arcy
 */
class NestedSynchronizationRuleTest extends AbstractRuleTestCase {
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NestedSynchronization'
    }

    void testApplyTo_NoViolations_InClosure() {
        final SOURCE = '''
            class testApplyTo_NoViolations_InClosureClass {
                def myMethod() {
                    synchronized(this) {
                        def closure1 = {
                            synchronized(this) { }
                        }
                        def closure2 = {
                            synchronized(this) { }
                        }
                    }
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoViolations_AnonymousInnerClass() {
        final SOURCE = '''
            class testApplyTo_NoViolations_InAnonymousInnerClass {
                def myMethod() {
                    synchronized(this) {
                        def runnable = new Runnable() {
                            public void run() {
                                synchronized(this) {}
                            }
                        }
                    }
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_FileWithTwoClasses() {
        final SOURCE = '''
            class NestedSynchronizationClass1 {
                def myMethod() {
                    synchronized(this) {}
                }
            }

            // synchronized within a second class
            class NestedSynchronizationClass2 {
                def myMethod() {
                    synchronized(this) {}
                }
                def method2() {
                    synchronized(this) {}
                }
                def method3() {
                    synchronized(this) {
                        // synchronized block within a closure
                        def closure1 = {
                            synchronized(this) { }
                        }
                        def closure2 = {
                            synchronized(this) { }
                        }
                    }
                }
                // synchronized within a static inner class
                static class NestedSynchronizationClass3 {
                    def myMethod() {
                        synchronized(this) {}
                    }
                    def method2() {
                        synchronized(this) {}
                    }
                    def method3() {
                        synchronized(this) {
                            // synchronized block within a closure
                            def closure1 = {
                                synchronized(this) { }
                            }
                            def closure2 = {
                                synchronized(this) { }
                            }
                        }
                    }
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_Method() {
        final SOURCE = '''
            class NestedSynchronizationClass4 {

                // within a method
                def myMethod() {
                    synchronized(this) {
                        synchronized(this) {}
                    }
                }
            } '''

        assertSingleViolation(SOURCE, 7, "synchronized(this) {}")
    }

    void testApplyTo_Closure() {
        final SOURCE = '''
            class NestedSynchronizationClass5 {

                def closure1 = {
                    synchronized(this) {
                        synchronized(this) { }
                    }
                }
            } '''

        assertSingleViolation(SOURCE, 6, "synchronized(this) { }")
    }

    //todo: This test passes fine on Groovy 1.7. Rename it when you upgrade
//    void testApplyTo_AnonymousInnerClass() {
//        final SOURCE = '''
//            class NestedSynchronizationClass6 {
//
//                def runnable = new Runnable() {
//                    public void run() {
//                        synchronized(this) {
//                            synchronized(this) {}
//                        }
//                    }
//                }
//            } '''
//
//        assertSingleViolation(SOURCE, 7, "synchronized(this) {}")
//    }

    //todo: This test passes fine on Groovy 1.7. Rename it when you upgrade
//    void testApplyTo_StaticInnerClass() {
//        final SOURCE = '''
//            class NestedSynchronizationClass7 {
//
//                static class NestedSynchronizationClass8 {
//                    def closure1 = {
//                        synchronized(this) {
//                            synchronized(this) {}
//                        }
//                    }
//                    def myMethod() {
//                        synchronized(this) {
//                            synchronized(this) {}
//                        }
//                    }
//                }
//            } '''
//
//        assertTwoViolations(SOURCE,
//                7, "synchronized(this) {}",
//                12, "synchronized(this) {}")
//    }

    void testApplyTo_SecondClass() {
        final SOURCE = '''
            class NestedSynchronizationClass9 {
                def myMethod() {
                    synchronized(this) {}
                }
            }

            // synchronized within a second class
            class NestedSynchronizationClass2 {
                def myMethod() {
                    synchronized(this) {
                        synchronized(this) {}
                    }
                }
            }
            '''

        assertSingleViolation(SOURCE, 12, "synchronized(this) {}")
    }

    void testApplyTo_DifferentLockObjects() {
        final SOURCE = '''
            class NestedSynchronizationClass10 {
                private lock1 = new Object()
                private lock2 = new Object()

                def myMethod() {
                    synchronized(lock1) {
                        synchronized(lock2) {}
                    }
                }
            } '''

        assertSingleViolation(SOURCE, 8, "synchronized(lock2) {}")
    }

    protected Rule createRule() {
        return new NestedSynchronizationRule()
    }

}
