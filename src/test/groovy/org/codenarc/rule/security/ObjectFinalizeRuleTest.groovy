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
package org.codenarc.rule.security

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ObjectFinalizeRule
 *
 * @author Hamlet D'Arcy
  */
class ObjectFinalizeRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ObjectFinalize'
    }

    @Test
    void testApplyTo_Violation_Initializers() {
        final SOURCE = '''
            class MyClass {
                static {
                    this.finalize()
                }
                {
                    widget.finalize()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'this.finalize()', 'The finalize() method should only be called by the JVM after the object has been garbage collected',
                7, 'widget.finalize()', 'The finalize() method should only be called by the JVM after the object has been garbage collected')
    }

    @Test
    void testApplyTo_Violation_Methods() {
        final SOURCE = '''
            class MyClass {
                static def method1() {
                    foo.finalize()
                }
                def method2() {
                    property.finalize()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'foo.finalize()', 'The finalize() method should only be called by the JVM after the object has been garbage collected',
                7, 'property.finalize()', 'The finalize() method should only be called by the JVM after the object has been garbage collected')
    }

    @Test
    void testApplyTo_Violation_Closures() {
        final SOURCE = '''
            File.finalize()
            def method = {
                file.finalize()
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'File.finalize()',
                4, 'file.finalize()')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    widget.Finalize()
                    widget.finalize('')
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ObjectFinalizeRule()
    }
}
