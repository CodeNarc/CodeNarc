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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for ClassNameRule
 *
 * @author Chris Mair
  */
class ClassNameRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassName'
    }

    @Test
    void testRegexIsNull() {
        rule.regex = null
        shouldFailWithMessageContaining('regex') { applyRuleTo('println 1') }
    }

    @Test
    void testApplyTo_DoesNotMatchDefaultRegex() {
        final SOURCE = ' class _MyClass { } '
        assertSingleViolation(SOURCE, 1, '_MyClass')
    }

    @Test
    void testApplyTo_NestedClassDoesNotMatchDefaultRegex() {
        final SOURCE = ' class MyClass { static class _MyNestedClass { }  } '
        assertSingleViolation(SOURCE, 1, '_MyNestedClass')
    }

    @Test
    void testApplyTo_InnerClassDoesNotMatchDefaultRegex() {
        final SOURCE = ''' class MyClass {
            class _MyInnerClass { }
        } '''
        assertSingleViolation(SOURCE, 2, '_MyInnerClass')
    }

    @Test
    void testApplyTo_MatchesDefaultRegex() {
        final SOURCE = ''' class MyClass {
            MyClass() {
                new _MyAnonymousClass() {}
            }
            class MyInnerClass {}
            static class MyNestedClass {
                class MyInnerInnerClass {}
            }
        } '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_WithPackage_MatchesDefaultRegex() {
        final SOURCE = '''
            package org.codenarc.sample
            class MyClass { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_DoesNotMatchCustomRegex() {
        rule.regex = /z.*/
        final SOURCE = ' class MyClass { } '
        assertSingleViolation(SOURCE, 1, 'MyClass')
    }

    @Test
    void testApplyTo_MatchesCustomRegex() {
        rule.regex = /z.*/
        final SOURCE = ' class zClass { } '
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoClassDefinition_DoesNotMatchRegex() {
        final SOURCE = '''
            if (isReady) {
                println 'ready'
            }
        '''
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClosureDefinition() {
        final SOURCE = '''
            class MyClass {
                def c = { println 'ok' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInnerClasses() {
        final SOURCE = '''
        class Outer {
            private class InnerRunnable implements Runnable {
                final Logger LOGGER = LoggerFactory.getLogger(InnerRunnable.class)
            }
        }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ClassNameRule()
    }
}
