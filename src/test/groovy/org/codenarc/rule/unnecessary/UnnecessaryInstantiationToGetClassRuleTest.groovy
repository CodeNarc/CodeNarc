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
 * Tests for InstantiationToGetClassRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryInstantiationToGetClassRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryInstantiationToGetClass'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	Class c = String.class
            Class b = foo.getClass()
        	Class c = new String().getClass(someArg) // arg means it must be valid
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testBasicViolation() {
        final SOURCE = '''
        	Class c = new String().getClass()
        '''
        assertSingleViolation(SOURCE, 2,
                'Class c = new String().getClass()',
                'String instantiation with getClass() should be simplified to String.class')
    }

    @Test
    void testComplexViolation() {
        final SOURCE = '''
        	new String('parm').getClass()
        '''
        assertSingleViolation(SOURCE, 2,
                "new String('parm').getClass()",
                'String instantiation with getClass() should be simplified to String.class')
    }

    protected Rule createRule() {
        new UnnecessaryInstantiationToGetClassRule()
    }
}
