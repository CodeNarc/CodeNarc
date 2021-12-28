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
 * @author Chris Mair
 */
class UnnecessaryPublicModifierRuleTest extends AbstractRuleTestCase<UnnecessaryPublicModifierRule> {

    private static final String MESSAGE_CLASSES = 'The public keyword is unnecessary for classes'
    private static final String MESSAGE_METHODS = 'The public keyword is unnecessary for methods'
    private static final String MESSAGE_CONSTRUCTORS = 'The public keyword is unnecessary for constructors'

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryPublicModifier'
    }

    @Test
    void test_NoViolations() {
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
    void test_Class() {
        final SOURCE = '''public class MyClass1 {
            }
            
            public class MyClass2 {
            }
        '''
        assertViolations(SOURCE,
                [line:1, source:'public class MyClass1', message: MESSAGE_CLASSES],
                [line:4, source:'public class MyClass2', message: MESSAGE_CLASSES])
    }

    @Test
    void test_Class_Split() {
        final SOURCE = '''public
            class MyClass1
            {
            }
            
            public
            class MyClass2 {
            }
        '''
        assertViolations(SOURCE,
                [line:1, source:'public', message: MESSAGE_CLASSES],
                [line:6, source:'public', message: MESSAGE_CLASSES])
    }

    @Test
    void test_Constructor() {
        final SOURCE = '''
            class MyClass {
                public MyClass() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public MyClass() {}', MESSAGE_CONSTRUCTORS)
    }

    @Test
    void test_Constructor_PublicGeneric() {
        final SOURCE = '''
             class MyClass<T, K> {
                public MyClass(T t, K k) { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public MyClass(T t, K k) { }', MESSAGE_CONSTRUCTORS)
    }

    @Test
    void test_Class_PublicGeneric() {
        final SOURCE = '''
            public class MyClass<T> {
                T someProperty
            }
        '''
        assertSingleViolation(SOURCE, 2, 'public class MyClass<T>', MESSAGE_CLASSES)
    }

    @Test
    void test_Class_AnnotationWithParametersContainingPublic() {
        final SOURCE = '''
            @Special(strings = ['some public value', 'some other value']) 
            class MyClass {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Method_Split() {
        final SOURCE = '''
            class MyClass {
                public
                void myMethod() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public', MESSAGE_METHODS)
    }

    @Test
    void test_Method() {
        final SOURCE = '''
            class MyClass {
                public void myMethod() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public void myMethod()', MESSAGE_METHODS)
    }

    @Test
    void test_Method_PublicGeneric_ReturnGenericType() {
        final SOURCE = '''
             class MyClass {
                public <T> T myMethod() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Method_PublicGeneric_VoidReturnType() {
        final SOURCE = '''
             class MyClass {
                public <T> void myMethod(T t) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Method_AnnotationWithParametersContainingPublic() {
        final SOURCE = '''
            class FakeTest {
                @ParameterizedTest
                @ValueSource(strings = [
                        'some public value',
                        'some other value',
                ])
                void testNothing(final String value) {
                    assertFalse(value.isEmpty())
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Interface() {
        final SOURCE = '''
            interface MyInterface {
                public void myMethod()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public void myMethod()', MESSAGE_METHODS)
    }

    @Override
    protected UnnecessaryPublicModifierRule createRule() {
        new UnnecessaryPublicModifierRule()
    }
}
