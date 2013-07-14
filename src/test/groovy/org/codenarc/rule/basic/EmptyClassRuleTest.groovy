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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for EmptyClassRule
 *
 * @author Artur Gajowy
 */
class EmptyClassRuleTest extends AbstractRuleTestCase {

    def skipTestThatUnrelatedCodeHasNoViolations

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptyClass'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class OneField {
                int i
            }

            class OneMethod {
                void method() {}
            }

            class OneConstructor {
                OneConstructor() {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class Empty {
            }
        '''
        assertSingleViolation(SOURCE, 2, 'class Empty', violationMessage('Empty'))
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            abstract class AbstractEmpty {
            }

            class EmptyAsWell {
                //comments do not make a difference
            }
        '''
        assertViolations(SOURCE,
            [lineNumber: 2, sourceLineText: 'class AbstractEmpty', messageText: violationMessage('AbstractEmpty')],
            [lineNumber: 5, sourceLineText: 'class EmptyAsWell', messageText: violationMessage('EmptyAsWell')])
    }

    @Test
    void testAllowsMarkerInterfaces() {
        final SOURCE = '''
            interface Marker {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEmptyEnumDoesNotViolate() {
        final SOURCE = '''
            enum Empty {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEmptyAnonymousClassDoesNotViolate() {
        final SOURCE = '''
            class Outer {
                def foo = new ArrayList<String>() {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEmptyInnerClassViolates() {
        final SOURCE = '''
            class Outer {
                int i

                class Inner {
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'class Inner', violationMessage('Outer$Inner'))
    }

    @Test
    void testClassContainingOnlyTypeNodesViolates() {
        final SOURCE = '''
            class TypesOnly {
                class Inner {
                    int notEmpty
                }

                enum Empty {}
            }
        '''
        assertSingleViolation(SOURCE, 2, 'class TypesOnly', violationMessage('TypesOnly'))
    }

    @Test
    void testEmptySubclasses_NoViolations() {
        final SOURCE = '''
            class MyException extends RuntimeException {
            }

            class ArrayListOfStrings extends ArrayList<String> {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testJUnitSuiteClasses_Annotation_NoViolations() {
        final SOURCE = '''
            @RunWith(Suite.class)
            @Suite.SuiteClasses([TestClass1.class, TestClass2.class])
            class TestSuite { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testOtherAnnotation_NoViolations() {
        final SOURCE = '''
            @AnyAnnotation
            class Empty { }
        '''
        assertNoViolations(SOURCE)
    }

    private String violationMessage(String violatingClass) {
        "Class '$violatingClass' is empty (has no methods, fields or properties). Why would you need a class like this?"
    }

    protected Rule createRule() {
        new EmptyClassRule()
    }
}
