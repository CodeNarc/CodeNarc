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
import org.junit.Test

/**
 * Tests for LoggerWithWrongModifiersRule
 *
 * @author Hamlet D'Arcy
  */
class LoggerWithWrongModifiersRuleTest extends AbstractRuleTestCase<LoggerWithWrongModifiersRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'LoggerWithWrongModifiers'
    }

    /*
     * static logger
     */

    @Test
    void testSuccessScenario_staticLogger() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = LoggerFactory.getLogger(MyClass)
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testNotPrivate_staticLogger() {
        final SOURCE = '''
            class MyClass {
                public static final LOG = LoggerFactory.getLogger(MyClass)
            }
        '''

        assertSingleViolation(SOURCE, 3,
                'public static final LOG = LoggerFactory.getLogger(MyClass)',
                'The Logger field LOG should be private, static and final')
    }

    @Test
    void testNotStatic_staticLogger() {
        final SOURCE = '''
            class MyClass {
                private final LOG = LoggerFactory.getLogger(MyClass)
            }
        '''

        assertSingleViolation(SOURCE, 3,
                'private final LOG = LoggerFactory.getLogger(MyClass)',
                'The Logger field LOG should be private, static and final')
    }

    @Test
    void testNotFinal_staticLogger() {
        final SOURCE = '''
            class MyClass {
                private static LOG = LoggerFactory.getLogger(MyClass)
            }
        '''

        assertSingleViolation(SOURCE, 3,
                'private static LOG = LoggerFactory.getLogger(MyClass)',
                'The Logger field LOG should be private, static and final')
    }

    @Test
    void testSuccessScenario_derivedLogger() {
        rule.allowProtectedLogger = true
        rule.allowNonStaticLogger = true

        final SOURCE = '''
            class MyClass1 {
                protected final LOG = LoggerFactory.getLogger(this.class)
            }

            class MyClass2 {
                protected final LOG = LoggerFactory.getLogger(this.getClass())
            }

            class MyClass3 {
                protected final LOG = LoggerFactory.getLogger(getClass())
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testPublic_derivedLogger() {
        rule.allowProtectedLogger = true
        rule.allowNonStaticLogger = true

        final SOURCE = '''
            class MyClass {
                public final LOG = LoggerFactory.getLogger(this.class)
            }
        '''

        assertSingleViolation(SOURCE, 3,
                'public final LOG = LoggerFactory.getLogger(this.class)',
                'The Logger field LOG should be private (or protected) and final')
    }

    @Test
    void testPublic() {
        rule.allowProtectedLogger = true

        final SOURCE = '''
            class MyClass {
                public static final LOG = LoggerFactory.getLogger(this.class)
            }
        '''

        assertSingleViolation(SOURCE, 3,
                'public static final LOG = LoggerFactory.getLogger(this.class)',
                'The Logger field LOG should be private (or protected), static and final')
    }

    @Test
    void testPrivate_derivedLogger() {
        rule.allowProtectedLogger = true
        rule.allowNonStaticLogger = true

        final SOURCE = '''
            class MyClass {
                private final LOG = LoggerFactory.getLogger(this.class)
                protected static final LOG2 = LoggerFactory.getLogger(this.class)
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testStatic_derivedLogger() {
        rule.allowProtectedLogger = true
        rule.allowNonStaticLogger = true

        final SOURCE = '''
            class MyClass {
                protected static final LOG = LoggerFactory.getLogger(this.class)
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testNotFinal_derivedLogger() {
        rule.allowProtectedLogger = true

        final SOURCE = '''
            class MyClass {
                protected LOG = LoggerFactory.getLogger(this.class)
            }
        '''

        assertSingleViolation(SOURCE, 3,
                'protected LOG = LoggerFactory.getLogger(this.class)',
                'The Logger field LOG should be private (or protected), static and final')
    }

    @Override
    protected LoggerWithWrongModifiersRule createRule() {
        new LoggerWithWrongModifiersRule()
    }
}
