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
import org.apache.log4j.Level

/**
 * Tests for UnnecessaryPublicModifierRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryPublicModifierRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryPublicModifier'
    }

    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                void myMethod() {}
                public String field
            }

            class publicClass {
                void publicMyMethod() {}
                public String field
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testClass0() {
        final SOURCE = '''public class MyClass {
            }
        '''
        assertSingleViolation(SOURCE, 1, 'public class MyClass', 'The public keyword is unnecessary for classes')
    }

    void testClass() {
        final SOURCE = '''
            public class MyClass {
            }
        '''
        assertSingleViolation(SOURCE, 2, 'public class MyClass', 'The public keyword is unnecessary for classes')
    }

    void testClassSplit() {
        final SOURCE = '''
            public
            class MyClass {
            }
        '''
        assertSingleViolation(SOURCE, 2, 'public', 'The public keyword is unnecessary for classes')
    }

    void testClassSplit2() {
        final SOURCE = '''public
            class MyClass
            {
            }
        '''
        assertSingleViolation(SOURCE, 1, 'public', 'The public keyword is unnecessary for classes')
    }

    void testMethodSplit() {
        final SOURCE = '''
            class MyClass {
                public
                void myMethod() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public', 'The public keyword is unnecessary for methods')
    }

    void testMethod() {
        final SOURCE = '''
            class MyClass {
                public void myMethod() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public void myMethod()', 'The public keyword is unnecessary for methods')
    }

    void testConstructor() {
        final SOURCE = '''
            class MyClass {
                public MyClass() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public MyClass() {}', 'The public keyword is unnecessary for constructors')
    }

    void testGantScript() {
        final SOURCE = '''
            includeTargets << grailsScript("_GrailsClean")
            setDefaultTarget("cleanAll")
        '''
        def logMessages = captureLog4JMessages {
            assertNoViolations(SOURCE)
        }
        log('Messages=' + logMessages?.message)
        def warnMessages = logMessages.findAll { it.level == Level.WARN }
        assert warnMessages.empty
    }

    protected Rule createRule() {
        new UnnecessaryPublicModifierRule()
    }
}