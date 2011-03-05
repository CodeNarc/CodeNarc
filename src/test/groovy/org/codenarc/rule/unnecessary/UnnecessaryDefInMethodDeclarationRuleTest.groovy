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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for UnnecessaryDefInMethodDeclarationRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class UnnecessaryDefInMethodDeclarationRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryDefInMethodDeclaration'
    }

    void testSuccessScenario() {
        final SOURCE = '''
            def method() { return 4 }

            private method1() { return 4 }
            protected method2() { return 4 }
            public method3() { return 4 }
            static method4() { return 4 }
            Object method5() { return 4 }
        '''
        assertNoViolations(SOURCE)
    }

    void testPrivateAndDef() {
        final SOURCE = '''
            // private and def is redundant
            def private method() { return 4 }
        '''
        assertSingleViolation(SOURCE, 3, 'def private method()', 'The def keyword is unneeded when a method is marked private')
    }

    void testProtectedAndDef() {
        final SOURCE = '''
            // def and protected is redundant
            def protected method() { return 4 }
        '''
        assertSingleViolation(SOURCE, 3, 'def protected method()', 'The def keyword is unneeded when a method is marked protected')
    }

    void testPublicAndDef() {
        final SOURCE = '''
            // def and protected is redundant
            def public method() { return 4 }
        '''
        assertSingleViolation(SOURCE, 3, 'def public method()', 'The def keyword is unneeded when a method is marked public')
    }

    void testStaticAndDef() {
        final SOURCE = '''
            // static and def is redundant
            def static method() { return 4 }
        '''
        assertSingleViolation(SOURCE, 3, 'def static method()', 'The def keyword is unneeded when a method is marked static')
    }

    void testObjectAndDef() {
        final SOURCE = '''
            // static and def is redundant
            def Object method() { return 4 }
        '''
        assertSingleViolation(SOURCE, 3, 'def Object method', 'The def keyword is unneeded when a method returns the Object type')
    }

    protected Rule createRule() {
        new UnnecessaryDefInMethodDeclarationRule()
    }
}