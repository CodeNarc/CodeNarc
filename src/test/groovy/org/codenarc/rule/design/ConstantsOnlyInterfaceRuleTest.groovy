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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ConstantsOnlyInterfaceRule
 *
 * @author Hamlet D'Arcy
 */
class ConstantsOnlyInterfaceRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ConstantsOnlyInterface'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            public interface Interface1 {
                public static final int CONSTANT_1 = 0
                public static final String CONSTANT_2 = "1"
                void method()
            }
            public interface Interface2 {
                // don't know why you'd want to do this, but it is legal
            }
            public interface Interface3 {
                void method()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessInScript() {
        final SOURCE = '''
            int CONSTANT_1 = 0
            String CONSTANT_2 = "1"
            println CONSTANT_1
        '''
        assertNoViolations(SOURCE)
    }
    
    @Test
    void testViolation() {
        final SOURCE = '''
            public interface ConstantInterface {
                public static final int CONSTANT_1 = 0
                public static final String CONSTANT_2 = "1"
            }
        '''
        assertSingleViolation(SOURCE,
                2,  'public interface ConstantInterface',
                'The interface ConstantInterface has only fields and no methods defined')
    }

    protected Rule createRule() {
        new ConstantsOnlyInterfaceRule()
    }
}
