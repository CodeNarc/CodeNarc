package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Unit test for VolatileLongOrDoubleFieldRule.
 * 
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class VolatileLongOrDoubleFieldRuleTest extends AbstractRuleTestCase {
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'VolatileLongOrDoubleField'
    }

    void testApplyTo_Violation_Doubles() {
        final SOURCE = '''
            class VolatileLongOrDoubleFieldClass1 {
                private volatile double d
                private volatile Double e
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'private volatile double d',
                4, 'private volatile Double e')
    }

    void testApplyTo_Violation_Floats() {
        final SOURCE = '''
            class VolatileLongOrDoubleFieldClass2 {
                private volatile long f
                private volatile Long g
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'private volatile long f',
                4, 'private volatile Long g')
    }

    void testApplyTo_Violation_FloatsWithoutModifier() {
        final SOURCE = '''
            class VolatileLongOrDoubleFieldClass3 {
                def volatile long f
                def volatile Long g
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def volatile long f',
                4, 'def volatile Long g')
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''class VolatileLongOrDoubleFieldClass4 {
                double d
                Double e
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new VolatileLongOrDoubleFieldRule()
    }
}
