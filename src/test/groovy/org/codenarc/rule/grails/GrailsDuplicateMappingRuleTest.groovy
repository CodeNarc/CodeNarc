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
 * Tests for GrailsDuplicateMappingRule
 *
 * @author Chris Mair
 */
class GrailsDuplicateMappingRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsDuplicateMapping'
    }

    @Test
    void testNoDuplicates_NoViolations() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static mapping = {
                    table 'people'
                    firstName column: 'First_Name'
                    lastName column: 'Last_Name'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEmptyMapping_NoViolations() {
        final SOURCE = '''
            class Person {
                String firstName
                static mapping = {
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
    void testDuplicateMappings_Violation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static mapping = {
                    table 'people'
                    firstName column: 'First_Name'
                    lastName column: 'Last_Name'
                    firstName column: 'First_Name'
                    table 'people2'
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:9, sourceLineText:"firstName column: 'First_Name'", messageText:'The mapping for firstName in domain class Person has already been specified'],
            [lineNumber:10, sourceLineText:"table 'people2'", messageText:'The mapping for table in domain class Person has already been specified'])
    }

    @Test
    void testDuplicateMappings_NestedColumnsClosure_Violation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static mapping = {
                    table 'people'
                    columns {
                        firstName column: 'First_Name'
                        lastName column: 'Last_Name'
                        firstName column: 'First_Name'
                    }
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:10, sourceLineText:"firstName column: 'First_Name'", messageText:'The mapping for firstName in domain class Person has already been specified'])
    }

    @Test
    void testMappingsAndConstraints_NoViolation() {
        final SOURCE = '''
            class Person {
                String firstName
                static mapping = {
                    columns {
                        firstName column: 'First_Name'
                    }
                }
                static constraints = {
                    firstName nullable:true
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMappings_ContainsMethodCalls_NoViolation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static mapping = {
                    columns {
                        firstName column: buildColumnName(1)
                        lastName column: buildColumnName(2)
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNotDomainClass_DuplicateMappings_NoViolation() {
        final SOURCE = '''
            class Person {
                static mapping = {
                    table 'people'
                    table 'people2'
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
        new GrailsDuplicateMappingRule()
    }
}
