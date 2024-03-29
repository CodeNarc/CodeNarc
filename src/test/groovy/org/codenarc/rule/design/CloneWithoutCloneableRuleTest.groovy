/*
 * Copyright 2013 the original author or authors.
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
 * Tests for CloneWithoutCloneableRule
 *
 * @author ArturGajowy
 */
class CloneWithoutCloneableRuleTest extends AbstractRuleTestCase<CloneWithoutCloneableRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CloneWithoutCloneable'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class Unrelated {
                def irrelevant() {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCloneWithCloneable_NoViolations() {
        final SOURCE = '''
            class ProperValueClass implements Cloneable {
                public Object clone() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCloneWith_IndirectlyImplementedCloneable_NoViolations() {
        final SOURCE = '''
            class ArrayListsAreCloneable extends ArrayList<String> {
                def clone() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class ValueClass {
                ValueClass clone() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'ValueClass clone()', violationMessage('ValueClass'))
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class ValueClass {
                def clone() {
                }
            }

            class ValueClass2 {
                @Override
                public Object clone() {
                }
            }

            class ValueClass3 {
                @Override
                protected Object clone() throws CloneNotSupportedException {
                }
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'def clone()', message: violationMessage('ValueClass')],
            [line:9, source:'Object clone()', message:violationMessage('ValueClass2')],
            [line:15, source:'protected Object clone() throws CloneNotSupportedException', message:violationMessage('ValueClass3')]
        )
    }

    private GString violationMessage(String className) {
        "Class $className declares a clone() method, but does not implement java.lang.Cloneable interface"
    }

    @Override
    protected CloneWithoutCloneableRule createRule() {
        new CloneWithoutCloneableRule()
    }

}
