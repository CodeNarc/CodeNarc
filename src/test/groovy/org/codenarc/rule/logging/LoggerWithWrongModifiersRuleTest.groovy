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
package org.codenarc.rule.logging

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for LoggerWithWrongModifiersRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class LoggerWithWrongModifiersRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "LoggerWithWrongModifiers"
    }

    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = Logger.getLogger(MyClass)
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testNotPrivate() {
        final SOURCE = '''
            class MyClass {
                public static final LOG = Logger.getLogger(MyClass)
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'static final LOG = Logger.getLogger(MyClass)',
                'The Logger field LOG should be private, static, and final')
    }

    void testNotStatic() {
        final SOURCE = '''
            class MyClass {
                private final LOG = Logger.getLogger(MyClass)
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'private final LOG = Logger.getLogger(MyClass)',
                'The Logger field LOG should be private, static, and final')
    }

    void testNotFinal() {
        final SOURCE = '''
            class MyClass {
                private static LOG = Logger.getLogger(MyClass)
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'private static LOG = Logger.getLogger(MyClass)',
                'The Logger field LOG should be private, static, and final')
    }

    protected Rule createRule() {
        new LoggerWithWrongModifiersRule()
    }
}