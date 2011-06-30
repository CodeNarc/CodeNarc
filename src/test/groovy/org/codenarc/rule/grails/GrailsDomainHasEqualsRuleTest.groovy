/*
 * Copyright 2011 the original author or authors.
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

/**
 * Tests for GrailsDomainHasEqualsRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class GrailsDomainHasEqualsRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsDomainHasEquals'
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	class Person {
                @Override
                boolean equals(Object o) { true }
            }
        '''
        assertNoViolations(SOURCE)
    }

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

    protected Rule createRule() {
        new GrailsDomainHasEqualsRule()
    }
}