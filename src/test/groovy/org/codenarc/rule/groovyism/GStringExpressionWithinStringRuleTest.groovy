/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for GStringExpressionWithinStringRule
 *
 * @author Chris Mair
 */
class GStringExpressionWithinStringRuleTest extends AbstractRuleTestCase<GStringExpressionWithinStringRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GStringExpressionWithinString'
    }

    @Test
    void testSingleQuoteStrings_WithoutGStringExpressions_NoViolations() {
        final SOURCE = '''
            def str1 = '123'
            def str2 = 'abc def ghi'
            def str3 = 'abc ${ ghu'
            def str4 = 'abc $ghu    }'
            def str5 = 'abc {123}'
            def str6 = 'abc $}'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleQuoteStrings_WithQuasiGStringExpressionInAnnotation_NoViolations() {
        final SOURCE = '''
            class SomeClass {
                @SomeAnnotationOnField('${sample.property1}')
                String sampleProperty

                @SomeAnnotationOnMethod('${sample.property2}')
                void method() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleQuoteStrings_WithQuasiGStringExpressionInAnnotation_AnnotatedMethodInAnnotatedClass_NoViolations() {
        final SOURCE = '''
            @SomeAnnotationOnClass('${sample.property1}')
            class SomeClass {
                @SomeAnnotationOnField('${sample.property2}')
                String sampleProperty

                @SomeAnnotationOnMethod('${sample.property3}')
                void method() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleQuoteStrings_WithQuasiGStringExpressionInAnnotation_NestedAnnotations_NoViolations() {
        final SOURCE = '''
            @SomeAnnotationOnClass(attribute='${sample.property1}',
                            nested=[@NestedAnnotation('${sample.property2}'),
                                    @NestedAnnotation('${sample.property3}')],
                             someOtherAttribute='${sample.property4}')
            class SomeClass {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleQuoteStrings_WithQuasiGStringExpressionInAnnotation_MultivalueElement_NoViolations() {
        final SOURCE = '''
            @SomeAnnotationOnClass(attribute=['${sample.property1}', '${sample.property2}'])
            class SomeClass {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoubleQuoteStrings_NoViolations() {
        final SOURCE = '''
            def valueToBeReplaced = '123'
            def str1 = "123"
            def str2 = "abc def ghi"
            def str3 = "abc ${count}"
            def str4 = "abc $count    }"
            def str5 = "abc {123}"
            def str6 = "abc ${}"
            def str7 = "total: ${count * 25}"
            def str8 = "$valueToBeReplaced \$valueNotToBeReplaced"
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineGStrings_NoViolations() {
        final SOURCE = '''
            def "plugin does not apply idea plugin"() {
                given:
                buildScript << """
                    task $testTaskName {
                        doLast {
                            println "Has idea plugin: \\${project.plugins.hasPlugin(IdeaPlugin)}"
                        }
                    }
                """

                expect:
                runTask(testTaskName).output.contains('Has idea plugin: false')

                where:
                testTaskName = 'hasIdeaPlugin\'
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleQuoteStrings_WithGStringExpression_Violations() {
        final SOURCE = '''
            def str1 = 'total: ${count}'
            def str2 = 'average: ${total / count}'
        '''
        assertViolations(SOURCE,
            [line:2, source:"def str1 = 'total: \${count}'", message:'\'${count}\''],
            [line:3, source:"def str2 = 'average: \${total / count}'", message:'\'${total / count}\''])
    }

    @Test
    void testSingleQuoteStrings_WithGStringExpressionInAnnotatedMethod_SingleViolation() {
        final SOURCE = '''
            class SomeClass {
                @SomeAnnotationOnMethod('${sample.property}')
                void method() {
                    def str1 = 'total: ${count}'
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, "def str1 = 'total: \${count}'", '\'${count}\'')
    }

    @Override
    protected GStringExpressionWithinStringRule createRule() {
        new GStringExpressionWithinStringRule()
    }
}
