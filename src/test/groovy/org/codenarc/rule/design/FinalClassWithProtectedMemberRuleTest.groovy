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
 * Tests for FinalClassWithProtectedMemberRule
 *
 * @author Hamlet D'Arcy
 */
class FinalClassWithProtectedMemberRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'FinalClassWithProtectedMember'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	final class MyClass1 {
                public method1() {}
                def method2() {}
                @PackageScope def method3() {}
                private def method3() {}
            }
        	class MyClass2 {
                protected def method() {}
                protected def closure = {}
                protected String property
            }

        	final class MyClass {
                @Override
                protected def methodName() {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMethodFailure() {
        final SOURCE = '''
        	final class MyClass {
                protected def methodName() {}
            }
        '''
        assertSingleViolation(SOURCE,
                3,
                'protected def methodName() {}',
                'The method methodName has protected visibility but the enclosing class MyClass is marked final')
    }

    @Test
    void testFieldFailure() {
        final SOURCE = '''
        	final class MyClass {
                protected def closure = {}
            }
        '''
        assertSingleViolation(SOURCE,
                3,
                'protected def closure = {}',
                'The field closure has protected visibility but the enclosing class MyClass is marked final')
    }

    @Test
    void testPropertyFailure() {
        final SOURCE = '''
        	final class MyClass {
                protected String myProperty
            }
        '''
        assertSingleViolation(SOURCE,
                3,
                'protected String myProperty',
                'The field myProperty has protected visibility but the enclosing class MyClass is marked final')
    }

    protected Rule createRule() {
        new FinalClassWithProtectedMemberRule()
    }
}
