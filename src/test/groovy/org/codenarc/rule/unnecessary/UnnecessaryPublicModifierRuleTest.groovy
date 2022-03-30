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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
    void RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryPublicModifier'
    }

    @Nested
    class Classes {

        @Test
        void NoViolations() {
            final SOURCE = '''
                class MyClass {
                    void "my public Method"() {}
                    public String field
                }
    
                class publicClass {
                    void publicMyMethod() {}
                    public String field
                }
                
                class publicEmptyClass {}
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void Violations() {
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
        void Split_KnownLimitation() {
            final SOURCE = '''public
                class MyClass1
                {
                }
                
                public
                class MyClass2 {
                }
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void CommentContainingPublic() {
            final SOURCE = '''
                class MyClass { // Should this be public or not?
                }
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void PublicGeneric() {
            final SOURCE = '''
                public class MyClass<T> {
                    T someProperty
                }
            '''
            assertSingleViolation(SOURCE, 2, 'public class MyClass<T>', MESSAGE_CLASSES)
        }

        @Test
        void AnnotationWithParametersContainingPublic() {
            final SOURCE = '''
                @Special(strings = ['some public value', 'some other value']) 
                class MyClass {
                }
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void AnnotationOnSameLine_Violation() {
            final SOURCE = '''
                @Disabled public class MyClass {
                }
            '''
            assertSingleViolation(SOURCE, 2, '@Disabled public class MyClass', MESSAGE_CLASSES)
        }

    }

    @Nested
    class Constructors {

        @Test
        void Violation() {
            final SOURCE = '''
                class MyClass {
                    public MyClass() {}
                }
            '''
            assertSingleViolation(SOURCE, 3, 'public MyClass() {}', MESSAGE_CONSTRUCTORS)
        }

        @Test
        void PublicGeneric() {
            final SOURCE = '''
                 class MyClass<T, K> {
                    public MyClass(T t, K k) { }
                }
            '''
            assertSingleViolation(SOURCE, 3, 'public MyClass(T t, K k) { }', MESSAGE_CONSTRUCTORS)
        }

    }

    @Nested
    class Methods {

        @Test
        void Split_KnownLimitation() {
            final SOURCE = '''
                class MyClass {
                    public
                    void myMethod() {}
                }
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void Violation() {
            final SOURCE = '''
                class MyClass {
                    public void myMethod() {}
                }
            '''
            assertSingleViolation(SOURCE, 3, 'public void myMethod()', MESSAGE_METHODS)
        }

        @Test
        void PublicGeneric_ReturnGenericType() {
            final SOURCE = '''
                 class MyClass {
                    public <T> T myMethod() { }
                }
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void PublicGeneric_VoidReturnType() {
            final SOURCE = '''
                 class MyClass {
                    public <T> void myMethod(T t) { }
                }
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void CommentContainingPublic() {
            final SOURCE = '''
                class MyClass {
                    void myMethod() {   // Should this be public or protected?
                    }
                }
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void AnnotationWithParametersContainingPublic() {
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
        void AnnotationWithOpeningBrace() {
            final SOURCE = '''
                import spock.lang.Requires
                import spock.lang.Specification
                
                class SomeSpec extends Specification {
                    @Requires({ true })
                    def 'asd'() {
                        expect:
                        true
                    }
                }
            '''
            assertNoViolations(SOURCE)
        }

        @Test
        void MethodWithinInterface_Violation() {
            final SOURCE = '''
                interface MyInterface {
                    public void myMethod()
                }
            '''
            assertSingleViolation(SOURCE, 3, 'public void myMethod()', MESSAGE_METHODS)
        }

        @Test
        void AnnotationOnTheSameLine_Violation() {
            final SOURCE = '''
                class MyClass {
                    @Ignore public void myMethod() {}
                }
            '''
            assertSingleViolation(SOURCE, 3, 'public void myMethod()', MESSAGE_METHODS)
        }

        @Test
        void AnnotationOnTheSameLine_NoViolation() {
            final SOURCE = '''
                class MyClass {
                    @Ignore('public') void myMethod() {}    // but @Ignore('public stuff') will cause violation
                }
            '''
            assertNoViolations(SOURCE)
        }

    }

    @Override
    protected UnnecessaryPublicModifierRule createRule() {
        new UnnecessaryPublicModifierRule()
    }
}
