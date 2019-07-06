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
import org.junit.Test

/**
 * Tests for UnnecessaryPublicModifierRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryPublicModifierRuleTest extends AbstractRuleTestCase<UnnecessaryPublicModifierRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryPublicModifier'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                void "my public Method"() {}
                public String field
            }

            class publicClass {
                void publicMyMethod() {}
                public String field
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testClass0() {
        final SOURCE = '''public class MyClass {
            }
        '''
        assertSingleViolation(SOURCE, 1, 'public class MyClass', 'The public keyword is unnecessary for classes')
    }

    @Test
    void testClass() {
        final SOURCE = '''
            public class MyClass {
            }
        '''
        assertSingleViolation(SOURCE, 2, 'public class MyClass', 'The public keyword is unnecessary for classes')
    }

    @Test
    void testClassSplit() {
        final SOURCE = '''
            public
            class MyClass {
            }
        '''
        assertSingleViolation(SOURCE, 2, 'public', 'The public keyword is unnecessary for classes')
    }

    @Test
    void testClassSplit2() {
        final SOURCE = '''public
            class MyClass
            {
            }
        '''
        assertSingleViolation(SOURCE, 1, 'public', 'The public keyword is unnecessary for classes')
    }

    @Test
    void testMethodSplit() {
        final SOURCE = '''
            class MyClass {
                public
                void myMethod() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public', 'The public keyword is unnecessary for methods')
    }

    @Test
    void testMethod() {
        final SOURCE = '''
            class MyClass {
                public void myMethod() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public void myMethod()', 'The public keyword is unnecessary for methods')
    }

    @Test
    void testConstructor() {
        final SOURCE = '''
            class MyClass {
                public MyClass() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public MyClass() {}', 'The public keyword is unnecessary for constructors')
    }

    @Test
    void testPublicGenericClass() {
        final SOURCE = '''
            public class MyClass<T> {
                T someProperty
            }
        '''
        assertSingleViolation(SOURCE, 2, 'public class MyClass<T>', 'The public keyword is unnecessary for classes')
    }

    @Test
    void testPublicGenericMethodWithReturnGenericType() {
        final SOURCE = '''
             class MyClass {
                public <T> T myMethod() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPublicGenericMethodWithVoidReturnType() {
        final SOURCE = '''
             class MyClass {
                public <T> void myMethod(T t) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPublicGenericConstructor() {
        final SOURCE = '''
             class MyClass<T, K> {
                public MyClass(T t, K k) { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public MyClass(T t, K k) { }', 'The public keyword is unnecessary for constructors')
    }

    @Override
    protected UnnecessaryPublicModifierRule createRule() {
        new UnnecessaryPublicModifierRule()
    }
}
