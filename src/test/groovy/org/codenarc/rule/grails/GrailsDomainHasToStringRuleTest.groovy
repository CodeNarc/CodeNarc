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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for GrailsDomainHasToStringRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class GrailsDomainHasToStringRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsDomainHasToString'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	class Person {
                @Override
                String toString() { 'xxx' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
        	class Person {
                @Override
                String toString(Object o) { 'xxx' }
            }
        '''

        sourceCodePath = 'project/MyProject/grails-app/domain/com/xxx/Person.groovy'
        assertSingleViolation(SOURCE, 2, 'class Person', 'The domain class Person should define a toString() method')
    }

    @Test
    void testIgnoresClassWithToStringAnnotation() {
        final SOURCE = '''
            @ToString
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

    protected Rule createRule() {
        new GrailsDomainHasToStringRule()
    }
}
