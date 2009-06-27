/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.unused

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for UnusedPrivateFieldRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnusedPrivateFieldRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedPrivateField'
    }

    void testApplyTo_SingleUnusedPrivateField() {
        final SOURCE = '''
          class MyClass {
              private int count
          }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count')
    }

    void testApplyTo_MultipleUnusedPrivateFields() {
        final SOURCE = '''
          class MyClass {
              private int count
              def otherField
              private static String globalName = 'xxx'
              MyClass() {
                  otherField = 'abc'
              }
          }
        '''
        assertTwoViolations(SOURCE, 3, 'private int count', 5, "private static String globalName = 'xxx'")
    }

    void testApplyTo_UnusedPrivateFieldWithAssignment() {
        final SOURCE = '''
          class MyClass {
              private int count = 23
          }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count = 23')
    }

    void testApplyTo_AllPrivateFieldsUsed() {
        final SOURCE = '''
            class MyClass {
                private int count
                def otherField
                static String globalName = 'xxx'
                private defaultName = 'abc'
                private startName = defaultName
                def dateFormat = java.text.DateFormat.getDateTimeInstance()

                def doStuff() {
                    this.count = 23
                    def newName = globalName + startName
                }

                def myClosure = { println "using $otherField" }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NonPrivateFields() {
        final SOURCE = '''
            class MyClass {
                public int count
                protected String name
                def myOtherField        // property
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ReferencePropertyOfAnotherObject() {
        final SOURCE = '''
            class MyClass {
                private int count
                def doSomething() {
                    someOtherObject.count = 23
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count')
    }

    void testApplyTo_GStringPropertyReference() {
        final SOURCE = '''
            class MyClass {
                private int count
                def other = this."${count}"
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_StringPropertyReference() {
        final SOURCE = '''
            class MyClass {
                private int count
                def other = this."count"
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_DereferencedGStringPropertyReference() {
        final SOURCE = '''
            class MyClass {
                private int count
                def varName = "count"
                def other = this."${varName}"     // can't see this
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count')
    }

    void testApplyTo_MoreThanOneClassInASourceFile() {
        final SOURCE = '''
            class MyClass {
                private int count
            }
            class OtherClass {
                int defaultCount = count
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count')
    }

    void testApplyTo_Script() {
        final SOURCE = '''
            private BigDecimal depositAmount       // not considered a field
            private int count                      // not considered a field
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoFieldDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new UnusedPrivateFieldRule()
    }

}