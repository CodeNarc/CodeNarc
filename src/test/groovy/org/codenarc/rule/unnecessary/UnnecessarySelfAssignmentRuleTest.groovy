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
 * Tests for UnnecessarySelfAssignmentRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessarySelfAssignmentRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessarySelfAssignment'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            x = y               // acceptable
            a.b = a.zz          // acceptable
            a.b = a().b         // acceptable
            a.b.c = a?.b?.c     // acceptable
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableAssignment() {
        final SOURCE = '''
            x = x               // violation
        '''
        assertSingleViolation(SOURCE, 2, 'x = x', 'Assignment a variable to itself should be unnecessary. Remove this dead code')
    }

    @Test
    void testParameterAssignment() {
        final SOURCE = '''
            def method(y) {
                y = y           // violation
            }
        '''
        assertSingleViolation(SOURCE, 3, 'y = y', 'Assignment a variable to itself should be unnecessary. Remove this dead code')
    }

    @Test
    void testPropertyAssignment() {
        final SOURCE = '''
            a.b = a.b       // violation
        '''
        assertSingleViolation(SOURCE, 2, 'a.b = a.b', 'Assignment a variable to itself should be unnecessary. Remove this dead code')
    }

    @Test
    void testPropertyAssignment2() {
        final SOURCE = '''
            a.b.c = a.b.c       // violation
        '''
        assertSingleViolation(SOURCE, 2, 'a.b.c = a.b.c', 'Assignment a variable to itself should be unnecessary. Remove this dead code')
    }

    protected Rule createRule() {
        new UnnecessarySelfAssignmentRule()
    }
}
