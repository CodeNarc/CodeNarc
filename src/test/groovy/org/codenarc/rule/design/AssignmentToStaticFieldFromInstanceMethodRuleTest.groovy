/*
 * Copyright 2015 the original author or authors.
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
import org.junit.Test

/**
 * Tests for AssignmentToStaticFieldFromInstanceMethodRule
 *
 * @author Chris Mair
 */
class AssignmentToStaticFieldFromInstanceMethodRuleTest extends AbstractRuleTestCase<AssignmentToStaticFieldFromInstanceMethodRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AssignmentToStaticFieldFromInstanceMethod'
    }

    @Test
    void test_AssignmentToInstanceFields_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private field1
                protected String field2 = 'abc'
                public int field3 = 123
                public final field4 = 456
                String property1 = 'abc'

                private void doStuff() {
                    field1 = new Object()
                    field2 = 'xxx'
                    field3 = 999
                    property1 = 'xxx'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AssignmentToStaticFields_FromStaticMethods_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static field1
                protected static String field2 = 'abc'
                public static int field3 = 123
                public static final field4 = 456
                static String property1 = 'abc'

                private static void doStuff() {
                    field1 = new Object()
                    field2 = 'xxx'
                    field3 = 999
                    property1 = 'xxx'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AssignmentToStaticField_FromInstanceMethod_Violations() {
        final SOURCE = '''
            class MyClass {
                private static field1
                protected static String field2 = 'abc'
                public static int field3 = 123
                static String property1 = 'abc'

                private void doStuff() {
                    field1 = new Object()
                    field2 = 'xxx'
                    field3 = 999
                    property1 = 'xxx'
                }
            }
        '''
        assertViolations(SOURCE,
            [line:9, source:'field1 = new Object()', message:'The instance method doStuff in class MyClass contains an assignment to static field field1'],
            [line:10, source:"field2 = 'xxx'", message:'The instance method doStuff in class MyClass contains an assignment to static field field2'],
            [line:11, source:'field3 = 999', message:'The instance method doStuff in class MyClass contains an assignment to static field field3'],
            [line:12, source:"property1 = 'xxx'", message:'The instance method doStuff in class MyClass contains an assignment to static field property1'])
    }

    @Test
    void test_ReferencesToStaticField_FromInstanceMethods_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static field1
                protected static String field2 = 'abc'
                public static int field3 = 123
                static String property1 = 'abc'

                private void doStuff() {
                    println field1 + 'suffix'
                    def isReady = field2 > 'xxx'
                    println field3 == 999
                    property1 < 'xxx'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AssignmentToLocalVariableWithSameNameAsStaticField_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final TEXT
                public static String NAME

                private void doStuff() {
                    final TEXT = "new"
                    def NAME
                    NAME = 'joe'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected AssignmentToStaticFieldFromInstanceMethodRule createRule() {
        new AssignmentToStaticFieldFromInstanceMethodRule()
    }
}
