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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for CloneableWithoutCloneRule
 * 
 * @author Hamlet D'Arcy
 */
class CloneableWithoutCloneRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CloneableWithoutClone'
    }

    @Test
    void testNonCloneableClass() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass0 {
                def myMethod() {
                }
            } '''

        assertNoViolations SOURCE
    }

    @Test
    void testCloneableWithClone() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass1 implements Cloneable {
                public Object clone() {
                    return super.clone()
                }
            } '''

        assertNoViolations SOURCE
    }

    @Test
    void testCloneableWithMisnamedClone() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass2 implements Cloneable, Serializable {
                public Object CLONE() {
                }
            } '''

        assertSingleViolation SOURCE, 2, 'class CloneableWithoutCloneRuleClass2 implements Cloneable'
    }

    @Test
    void testCloneableWithMisnamedCloneWithPath() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass3 implements java.lang.Cloneable, Serializable {
                public Object CLONE() {
                }
            } '''

        assertSingleViolation SOURCE, 2, 'class CloneableWithoutCloneRuleClass3 implements java.lang.Cloneable'
    }

    @Test
    void testCloneableWithParameters() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass3 implements java.lang.Cloneable, Serializable {
                public Object clone(int x) {
                }
            } '''

        assertSingleViolation SOURCE, 2, 'class CloneableWithoutCloneRuleClass3 implements java.lang.Cloneable'
    }

    @Test
    void testCloneableWithMissingClone() {
        final SOURCE = '''
            class CloneableWithoutCloneRuleClass4 implements Cloneable {
            } '''

        assertSingleViolation SOURCE, 2, 'class CloneableWithoutCloneRuleClass4 implements Cloneable '
    }

    protected Rule createRule() {
        new CloneableWithoutCloneRule()
    }

}
