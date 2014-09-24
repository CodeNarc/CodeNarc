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
package org.codenarc.rule.grails

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Before

/**
 * Tests for GrailsDuplicateConstraintRule
 *
 * @author Chris Mair
 */
class GrailsDuplicateConstraintRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsDuplicateConstraint'
    }

    @Test
    void testNoDuplicates_NoViolations() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    firstName nullable:true
                    lastName nullable:true, maxSize:30
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEmptyConstraints_NoViolations() {
        final SOURCE = '''
            class Person {
                String firstName
                static constraints = {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCallsToOverloadedMethods_NoViolations() {
        final SOURCE = '''
            class Person {
                void run() {
                    doStuff()
                    doStuff(99)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDuplicateConstraints_Violation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    firstName nullable:true
                    lastName nullable:true, maxSize:30
                    firstName nullable:false
                    lastName nullable:false, maxSize:30
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:8, sourceLineText:'firstName nullable:false', messageText:'The constraint for firstName in domain class Person has already been specified'],
            [lineNumber:9, sourceLineText:'lastName nullable:false, maxSize:30', messageText:'The constraint for lastName in domain class Person has already been specified'])
    }

    @Test
    void testImportFrom_NoViolation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    importFrom Entity, include: ["firstName"]
                    importFrom Entity, include: ["lastName"]
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testImportFrom_SameImportViolation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    importFrom Entity, include: ["firstName"]
                    importFrom Entity, include: ["firstName"]
                }
            }
        '''
        assertViolations(SOURCE, [lineNumber:7, sourceLineText:'importFrom Entity, include: ["firstName"]', messageText:'The constraint for firstName in domain class Person has already been specified'])
    }

    @Test
    void testImportFrom_ImportAndRegularConstraintViolation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    importFrom Entity, include: ["firstName"]
                    firstName blank: false
                }
            }
        '''
        assertViolations(SOURCE, [lineNumber:7, sourceLineText:'firstName blank: false', messageText:'The constraint for firstName in domain class Person has already been specified'])
    }

    @Test
    void testMappingsAndConstraints_NoViolation() {
        final SOURCE = '''
            class Person {
                String firstName
                static constraints = {
                    firstName nullable:true
                }
                static mapping = {
                    columns {
                        firstName column: 'First_Name'
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstraints_ContainsMethodCalls_NoViolation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    firstName nullable:true, validator:buildValidator(1)
                    lastName nullable:true, validator:buildValidator(2)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNotDomainClass_DuplicateMappings_NoViolation() {
        final SOURCE = '''
            class Person {
                static constraints = {
                    firstName nullable:true
                    lastName nullable:true, maxSize:30
                    firstName nullable:false
                    lastName nullable:false, maxSize:30
                }
            }
        '''
        sourceCodePath = 'MyProject/other/Person.groovy'
        assertNoViolations(SOURCE)
    }

    @Before
    void setUp() {
        sourceCodePath = 'MyProject/grails-app/domain/com/example/Person.groovy'
    }

    protected Rule createRule() {
        new GrailsDuplicateConstraintRule()
    }
}
