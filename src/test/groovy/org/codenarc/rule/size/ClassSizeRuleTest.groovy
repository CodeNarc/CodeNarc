/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.size

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ClassSizeRule
 *
 * @author Chris Mair
  */
class ClassSizeRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ClassSize'
    }

    @Test
    void testApplyTo_LongerThanDefaultMaxLines() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    ${'println 23\n' * 996}
                }
            }
        """
        assertSingleViolation(SOURCE, 2, null, 'MyClass')
    }

    @Test
    void testApplyTo_SetMaxLines() {
        final SOURCE = """
            package some.pkg
            /** class description */
            class MyClass {
                def myMethod() {
                    'println 23'
                }
            }
        """
        rule.maxLines = 4
        assertSingleViolation(SOURCE, 4, null, '"MyClass"')
    }

    @Test
    void testApplyTo_GeneratedClass() {
        final SOURCE = '''
            if (isReady) {
                println 'ready'
            }
        '''
        rule.maxLines = 1
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_EqualToMaxLines() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    ${'println 23\n' * 995}
                }
            }"""
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ClassSizeRule()
    }

}
