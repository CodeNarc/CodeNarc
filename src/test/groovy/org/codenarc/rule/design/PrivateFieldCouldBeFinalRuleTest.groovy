/*
 * Copyright 2012 the original author or authors.
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

/**
 * Tests for PrivateFieldCouldBeFinalRule
 *
 * @author Chris Mair
 */
class PrivateFieldCouldBeFinalRuleTest extends AbstractRuleTestCase {

    private static final VIOLATION_MESSAGE = 'Private field [count] is only'

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'PrivateFieldCouldBeFinal'
    }

    // TODO set within initializer and closure field
    // TODO set within initializer and inner class method
    // TODO set within initializer and anonymous inner class assigned to field
    // TODO set within initializer and anonymous inner class within method
    // TODO set within initializer and referenced within method but not set
    // TODO set within initializer and compared within method (<) but not set

    void testApplyTo_NonPrivateField_OnlySetWithinInitializer_NoViolations() {
        final SOURCE = '''
            class MyClass {
                protected int count = 23
                int someProperty = 99
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateFieldSetWithinMethod_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count = 11
                void initialize() {
                    count = 1
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateFieldNeverSet_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateFieldSetWithinInitializerAndWithinMethod_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count = 99
                void initialize() {
                    count = 1
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateFieldSetWithinConstructorAndWithinMethod_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count
                void initialize() {
                    count = 1
                }
                MyClass(int c) {
                    count = 99
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateFieldSetUsingThis_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count = 99
                void initialize() {
                    this.count = 1
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateField_OnlySetWithinInitializer_Violation() {
        final SOURCE = '''
            class MyClass {
                private int count = 0
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count = 0', VIOLATION_MESSAGE)
    }

    void testApplyTo_PrivateField_OnlySetWithinConstructor_Violation() {
        final SOURCE = '''
            class MyClass {
                private int count
                MyClass() {
                    count = 1
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count', VIOLATION_MESSAGE)
    }

    void testApplyTo_PrivateField_OnlySetWithinConstructorUsingThis_Violation() {
        final SOURCE = '''
            class MyClass {
                private int count
                MyClass() {
                    this.count = 1
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count', VIOLATION_MESSAGE)
    }

    void testApplyTo_PrivateFields_MultipleViolations() {
        final SOURCE = '''
            class MyClass {
                private int count
                private name = 'abc'
                MyClass() {
                    count = 1
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'private int count', messageText:VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:"private name = 'abc'", messageText:'Private field [name] is only'])
    }

    void testApplyTo_PrivateField_SetWithinInitializerAndConstructor_Violation() {
        final SOURCE = '''
            class MyClass {
                private int count = 2
                MyClass() {
                    count = 0
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count', VIOLATION_MESSAGE)
    }

    void testApplyTo_PrivateFinalField_OnlySetWithinInitializer_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private final int count = 99
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateFinalField_OnlySetWithinConstructor_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private final int count
                MyClass() {
                    count = 1
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateField_MultipleClassesWithinSource() {
        final SOURCE = '''
            class MyClass {
                private int count = 0
            }
            class MyOtherClass {
                int defaultCount = count
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count = 0', VIOLATION_MESSAGE)
    }

    protected Rule createRule() {
        new PrivateFieldCouldBeFinalRule()
    }
}
