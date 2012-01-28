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

    void testApplyTo_PrivateFieldSetWithinClosure_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count = 11
                def myClosure = { count = 1 }
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

    void testApplyTo_PrivateField_AssignedUsingOtherOperators_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private value1 = 0
                private value2 = 3
                private value3 = 2
                private value4 = 0
                private value5 = 0
                private value6 = 0

                void initialize() {
                    value1 += 2
                    value2 *= 3
                    value3 |= 1
                    value4++
                    value5--
                    ++value6
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

    void testApplyTo_PrivateField_ComparedWithinMethodButNotSet_Violation() {
        final SOURCE = '''
            class MyClass {
                private int count = 0
                boolean hasCount() {
                    count > 0
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count = 0', VIOLATION_MESSAGE)
    }

    void testApplyTo_PrivateField_ReferencedWithinMethodButNotSet_Violation() {
        final SOURCE = '''
            class MyClass {
                private int count = 0
                void printCount() {
                    println count
                }
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
                private other = 'abc'
            }
            class MyOtherClass {
                int defaultCount = count
                int other = 123
                void init() { other = 456 }
            }
        '''
        // Should also get a violation for other from MyClass, but that is "overshadowed" by other in MyOtherClass
        assertSingleViolation(SOURCE, 3, 'private int count = 0', VIOLATION_MESSAGE)
    }

    void testApplyTo_PrivateField_ReferencedWithinInnerClass_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count = 0
                class MyInnerClass {
                    def doStuff() {
                        count = count + 5
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateField_ReferencedWithinAnonymousInnerClass_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count = 0
                def runnable = new Runnable() {
                    void run() {
                        count = count + 5
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_IgnoreFieldNames_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count = 0
                private int other = 0
            }
        '''
        rule.ignoreFieldNames = 'count, xxx, ot*r'
        assertNoViolations(SOURCE)
    }

    void testApplyTo_DoNotApplyToClassNames_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private int count = 0
            }
        '''
        rule.doNotApplyToClassNames = 'MyClass'
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SuppressWarningsOnClass_NoViolations() {
        final SOURCE = '''
            @SuppressWarnings('PrivateFieldCouldBeFinal')
            class MyClass {
                private int count = 0
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new PrivateFieldCouldBeFinalRule()
    }
}
