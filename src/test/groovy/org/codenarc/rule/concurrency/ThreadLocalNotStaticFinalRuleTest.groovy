package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for ThreadLocalNotStaticFinalRule.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class ThreadLocalNotStaticFinalRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ThreadLocalNotStaticFinal'
    }

    void testApplyTo_Violation_StaticOrFinalButNotBoth() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass1 {
                private static ThreadLocal local1 = new ThreadLocal()
                private final ThreadLocal local2 = new ThreadLocal()

            }
        '''
        assertTwoViolations(SOURCE,
                3, 'private static ThreadLocal local1 = new ThreadLocal()',
                4, 'private final ThreadLocal local2 = new ThreadLocal()')
    }

    void testApplyTo_Violation_NotFinalOrStatic() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass1 {
                ThreadLocal local1 = new ThreadLocal()
                protected ThreadLocal local2 = new ThreadLocal()
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'ThreadLocal local1 = new ThreadLocal()',
                4, 'protected ThreadLocal local2 = new ThreadLocal()')
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass3 {
                private static final ThreadLocal local1 = new ThreadLocal()
                private static final ThreadLocal local2 = new ThreadLocal()
            }'''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoViolationsInnerClass() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass4 {
                static class ThreadLocalNotStaticFinalInnerClass4 {
                    private static final ThreadLocal local1 = new ThreadLocal()
                    private static final ThreadLocal local2 = new ThreadLocal()
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new ThreadLocalNotStaticFinalRule()
    }

}

