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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ClassForNameRule
 *
 * @author Hamlet D'Arcy
 */
class ClassForNameRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassForName'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            Class.forName() // zero args is not API
            Class.forName(aClassName, true) // two args is not API
            Class.forName(aClassName, true, aClassLoader, unknown)  // four args is not API
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolations() {
        final SOURCE = '''
            Class.forName('SomeClassName')
            Class.forName(aClassName, true, aClassLoader)
        '''
        assertTwoViolations(SOURCE,
                2, "Class.forName('SomeClassName')", 'Methods calls to Class.forName(...) can create resource leaks and should almost always be replaced with calls to ClassLoader.loadClass(...)',
                3, 'Class.forName(aClassName, true, aClassLoader)', 'Methods calls to Class.forName(...) can create resource leaks and should almost always be replaced with calls to ClassLoader.loadClass(...)')
    }

    protected Rule createRule() {
        new ClassForNameRule()
    }
}
