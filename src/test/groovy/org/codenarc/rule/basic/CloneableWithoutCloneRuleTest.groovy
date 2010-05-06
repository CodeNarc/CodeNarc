package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for CloneableWithoutCloneRule
 * 
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class CloneableWithoutCloneRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CloneableWithoutClone'
    }

    void testNonClonableClass() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass0 {
                def myMethod() {
                }
            } '''

        assertNoViolations SOURCE
    }

    void testClonableWithClone() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass1 implements Clonable {
                public Object clone() {
                    return super.clone();
                }
            } '''

        assertNoViolations SOURCE
    }

    void testClonableWithMisnamedClone() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass2 implements Cloneable, Serializable {
                public Object CLONE() {
                }
            } '''

        assertSingleViolation SOURCE, 2, "class CloneableWithoutCloneRuleClass2 implements Cloneable"
    }

    void testClonableWithMisnamedCloneWithPath() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass3 implements java.lang.Cloneable, Serializable {
                public Object CLONE() {
                }
            } '''

        assertSingleViolation SOURCE, 2, "class CloneableWithoutCloneRuleClass3 implements java.lang.Cloneable"
    }

    void testClonableWithParameters() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass3 implements java.lang.Cloneable, Serializable {
                public Object clone(int x) {
                }
            } '''

        assertSingleViolation SOURCE, 2, "class CloneableWithoutCloneRuleClass3 implements java.lang.Cloneable"
    }

    void testClonableWithMissingClone() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass4 implements Cloneable {
            } '''

        assertSingleViolation SOURCE, 2, "class CloneableWithoutCloneRuleClass4 implements Cloneable "
    }


    protected Rule createRule() {
        return new CloneableWithoutCloneRule()
    }

}
