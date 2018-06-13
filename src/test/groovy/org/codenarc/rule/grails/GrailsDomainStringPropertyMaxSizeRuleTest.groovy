/*
 * Copyright 2018 the original author or authors.
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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Before
import org.junit.Test

/**
 * Tests for GrailsDomainStringPropertyMaxSizeRule
 *
 * @author Vladimir Orany
 */
class GrailsDomainStringPropertyMaxSizeRuleTest extends AbstractRuleTestCase<GrailsDomainStringPropertyMaxSizeRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsDomainStringPropertyMaxSize'
    }

    @Test
    void testSizeConstrained_NoViolations() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    firstName nullable:true, size: 0..50
                    lastName nullable:true, maxSize: 30
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSizeNoString_NoViolations() {
        final SOURCE = '''
            class Person {
                Long versionNumber

                static constraints = {
                    versionNumber nullable:true
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticAndTransient_NoViolations() {
        final SOURCE = '''
            class Person {
                transient String firstName
                static String lastName

                static constraints = {
                    firstName nullable:true
                    lastName nullable:true
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoClosures_Violations() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName

                static constraints = [size: 1..100]
                static mapping = [firstName: 'text']
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'String firstName', messageText:'There is no constraint on the size of String property \'firstName\' which will result in applying database defaults'],
                [lineNumber:4, sourceLineText:'String lastName', messageText:'There is no constraint on the size of String property \'lastName\' which will result in applying database defaults'])
    }

    @Test
    void testSizeConstrainedNestedClass_Violations() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    firstName nullable:true, size: 0..50
                    lastName nullable:true
                }

                class Address {  }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:4, sourceLineText:'String lastName', messageText:'There is no constraint on the size of String property \'lastName\' which will result in applying database defaults'])
    }

    @Test
    void testPropertyMapped_NoViolations() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    firstName nullable:true
                    lastName nullable:true, maxSize: 30
                }

                static mapping = {
                    firstName type: 'text'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPropertyNotMappedType_Violation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    firstName nullable:true
                    lastName nullable:true, maxSize: 30
                }

                static mapping = {
                    firstName column: 'prenom'
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'String firstName', messageText:'There is no constraint on the size of String property \'firstName\' which will result in applying database defaults'])
    }

    @Test
    void testSizeNotConstrained_Violation() {
        final SOURCE = '''
            class Person {
                String firstName
                String lastName
                static constraints = {
                    lastName nullable:true
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'String firstName', messageText:'There is no constraint on the size of String property \'firstName\' which will result in applying database defaults'],
                [lineNumber:4, sourceLineText:'String lastName', messageText:'There is no constraint on the size of String property \'lastName\' which will result in applying database defaults'])
    }

    @Test
    void testImportFrom_NoViolation() {
        final SOURCE = '''
            class Entity {
                String firstName

                static constraints = {
                    firstName size: 0..50, nullable: true
                }
            }

            class Person {
                String firstName
                String lastName
                static constraints = {
                    importFrom Entity, include: ["firstName"]
                    lastName maxSize: 30
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testImportFromMultipleAttributes_NoViolation() {
        final SOURCE = '''
            class Entity {
                String firstName

                static constraints = {
                    firstName size: 0..50, nullable: true
                    lastName nullable:true, maxSize: 30
                }
            }

            class Person {
                String firstName
                String lastName
                static constraints = {
                    importFrom Entity, include: ["firstName", "lastName"]
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testImportFromNoInclude_Violation() {
        final SOURCE = '''
            class Entity {
                String firstName

                static constraints = {
                    firstName size: 0..50, nullable: true
                }
            }

            class Person {
                String firstName
                String lastName
                static constraints = {
                    importFrom Entity
                    lastName maxSize: 30
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:11, sourceLineText:'String firstName', messageText:'There is no constraint on the size of String property \'firstName\' which will result in applying database defaults'])
    }

    @Before
    void setUp() {
        sourceCodePath = 'MyProject/grails-app/domain/com/example/Person.groovy'
    }

    @Override
    protected GrailsDomainStringPropertyMaxSizeRule createRule() {
        new GrailsDomainStringPropertyMaxSizeRule()
    }
}
