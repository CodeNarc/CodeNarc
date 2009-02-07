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

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for ClassSizeRule
 *
 * @author Chris Mair
 * @version $Revision: 24 $ - $Date: 2009-01-31 07:47:09 -0500 (Sat, 31 Jan 2009) $
 */
class ClassSizeRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ClassSize'
    }

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

    void testApplyTo_SetMaxLines() {
        final SOURCE = """
            /** class description */
            class MyClass {
                def myMethod() {
                    'println 23'
                }
            }
        """
        rule.maxLines = 4
        assertSingleViolation(SOURCE, 3, null, 'MyClass')
    }

    void testApplyTo_NoClassDefinition() {
        final SOURCE = '''
            if (isReady) {
                println 'ready'
            }
        '''
        assertNoViolations(SOURCE)
    }

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
        return new ClassSizeRule()
    }

}