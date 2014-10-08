/*
 * Copyright 2014 the original author or authors.
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

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for InstanceofRule
 *
 * @author Chris Mair
 */
class InstanceofRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'Instanceof'
        assert rule.ignoreTypeNames == '*Exception'
        assert rule.doNotApplyToFilesMatching == DEFAULT_TEST_FILES
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
        	class MyClass {
        	    def myMethod() {
        	        println 123 + 99
        	    }
        	}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_instanceof_Violation() {
        final SOURCE = '''
            class MyClass {
                boolean isRunnable = this instanceof Runnable
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'boolean isRunnable = this instanceof Runnable', messageText:'The instanceof operator is used in class MyClass'])
    }

    @Test
    void test_instanceof_Exception_NoViolation() {
        final SOURCE = '''
            class MyClass {
                boolean isRuntimeException = this instanceof RuntimeException
                boolean isIOException = this instanceof IOException
                boolean isException = this instanceof Exception
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_instanceof_IgnoreTypeNames_Matches_NoViolation() {
        final SOURCE = '''
            class MyClass {
                boolean isA = this instanceof MyClass
                boolean isB = this instanceof MyOtherClass
            }
        '''
        rule.ignoreTypeNames = 'My*Class'
        assertNoViolations(SOURCE)
    }

    @Test
    void test_instanceof_IgnoreTypeNames_NoMatch_Violation() {
        final SOURCE = '''
            class MyClass {
                boolean isRunnable = this instanceof Runnable
            }
        '''
        rule.ignoreTypeNames = 'SomeClass'
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'boolean isRunnable = this instanceof Runnable', messageText:'The instanceof operator is used in class MyClass'])
    }

    protected Rule createRule() {
        new InstanceofRule()
    }
}
