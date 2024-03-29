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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for GrailsDomainHasEqualsRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class GrailsDomainHasEqualsRuleTest extends AbstractRuleTestCase<GrailsDomainHasEqualsRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsDomainHasEquals'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class Person {
                @Override
                boolean equals(Object o) { true }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class Person {
                @Override
                boolean equals(Object o, Object x) { true }
            }
        '''

        sourceCodePath = 'project/MyProject/grails-app/domain/com/xxx/Person.groovy'
        assertSingleViolation(SOURCE, 2, 'class Person', 'The domain class Person should define an equals(Object) method')
    }

    @Test
    void testIgnoresClassWithToStringAnnotation() {
        final SOURCE = '''
            @EqualsAndHashCode
            class Person {
            }
        '''
        sourceCodePath = 'project/MyProject/grails-app/domain/com/xxx/Person.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresClassWithCanonicalAnnotation() {
        final SOURCE = '''
            @Canonical class Person {
            }
        '''
        sourceCodePath = 'project/MyProject/grails-app/domain/com/xxx/Person.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationOnInnerEnums() {
        final SOURCE = '''
            @EqualsAndHashCode
            class Person {
                Gender gender

                enum Gender {
                    MALE,
                    FEMALE
                }
            }
        '''

        sourceCodePath = 'project/MyProject/grails-app/domain/com/xxx/Person.groovy'
        assertNoViolations(SOURCE)
    }

    @Override
    protected GrailsDomainHasEqualsRule createRule() {
        new GrailsDomainHasEqualsRule()
    }
}
