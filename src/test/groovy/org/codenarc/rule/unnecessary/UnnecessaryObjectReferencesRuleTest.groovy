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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryObjectReferencesRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryObjectReferencesRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryObjectReferences'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''

                // 'this' reference ignored
                firstName = 'Hamlet'
                lastName = "D'Arcy"
                employer = 'Canoo'
                street = 'Kirschgaraten 5'
                city = 'Basel'
                zipCode = '4051'
                this.firstName = 'Hamlet'
                this.lastName = "D'Arcy"
                this.employer = 'Canoo'
                this.street = 'Kirschgaraten 5'
                this.city = 'Basel'
                this.zipCode = '4051'
                method1()
                method2()
                method4()
                method6()
                method5()
                this.method1()
                this.method2()
                this.method4()
                this.method6()
                this.method5()

                method1()
                // only 5 references
                def p = new Person()
                p.firstName = 'Hamlet'
                p.lastName = "D'Arcy"
                p.employer = 'Canoo'
                p.street = 'Kirschgaraten 5'
                p.city = 'Basel'

                def p1 = new Person().with {
                    firstName = 'Hamlet'
                    lastName = "D'Arcy"
                    employer = 'Canoo'
                    street = 'Kirschgaraten 5'
                    city = 'Basel'
                    zipCode = '4051'
                }

                def p2 = new Person().identity {
                    firstName = 'Hamlet'
                    lastName = "D'Arcy"
                    employer = 'Canoo'
                    street = 'Kirschgaraten 5'
                    city = 'Basel'
                    zipCode = '4051'
                } '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testExcessivePropertyAccess() {
        final SOURCE = '''
                class Person {
                    String firstName
                    String lastName
                    String employer
                    String street
                    String city
                    String zipCode
                }

                def p1 = new Person()
                p1.firstName = 'Hamlet'
                p1.lastName = "D'Arcy"
                p1.employer = 'Canoo'
                p1.street = 'Kirschgaraten 5'
                p1.city = 'Basel'
                p1.zipCode = '4051'

                def p2 = new Person()
                p2.firstName = 'Hamlet'
                p2.lastName = "D'Arcy"
                p2.employer = 'Canoo'
                p2.street = 'Kirschgaraten 5'
                p2.city = 'Basel'
                p2.zipCode = '4051'  '''
        assertTwoViolations(SOURCE, 17, "p1.zipCode = '4051'", 25, "p2.zipCode = '4051'")
    }

    @Test
    void testOverridingProperty() {
        rule.maxReferencesAllowed = 2
        final SOURCE = '''
                class Person {
                    String firstName
                    String lastName
                    String employer
                    String street
                    String city
                    String zipCode
                }

                def p1 = new Person()
                p1.firstName = 'Hamlet'
                p1.lastName = "D'Arcy"
                p1.employer = 'Canoo'  '''
        assertSingleViolation(SOURCE, 14, "p1.employer = 'Canoo'", 'The code could be more concise by using a with() or identity() block')
    }

    @Test
    void testExcessiveSetters() {
        final SOURCE = '''
                class Person {
                    String firstName
                    String lastName
                    String employer
                    String street
                    String city
                    String zipCode
                }

                def p2 = new Person()
                p2.setFirstName('Hamlet')
                p2.setLastName("D'Arcy")
                p2.setEmployer('Canoo')
                p2.setStreet('Kirschgaraten 5')
                p2.setCity('Basel')
                p2.setZipCode('4051') '''

        assertSingleViolation(SOURCE, 17, "p2.setZipCode('4051')",	'The code could be more concise by using a with() or identity() block')
    }

    @Test
    void testReferencesAcrossMethods() {
        final SOURCE = '''
            class MyClass {
                def method1() {
                    obj.method('param')
                }
                def method2() {
                    obj.method('param')
                }
                def method3() {
                    obj.method('param')
                }
            }
'''
        rule.maxReferencesAllowed = 2
        assertNoViolations(SOURCE)
    }

    @Test
    void testReferencesAcrossFields() {
        final SOURCE = '''
            class MyClass {
                static mappings = [
                0: {IdefixDto idefixDto, def value -> idefixDto.ian = value},
                1: {IdefixDto idefixDto, def value -> idefixDto.iaPosition = value},
                2: {IdefixDto idefixDto, def value -> idefixDto.filiale = value},
                3: {IdefixDto idefixDto, def value -> idefixDto.fiAuftragsId = value},
                ]
            }
'''
        rule.maxReferencesAllowed = 2
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryObjectReferencesRule()
    }
}
