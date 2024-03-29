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
package org.codenarc.rule.comments

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.source.SourceString
import org.junit.jupiter.api.Test

/**
 * Tests for ClassJavadocRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class ClassJavadocRuleTest extends AbstractRuleTestCase<ClassJavadocRule> {

    static skipTestThatUnrelatedCodeHasNoViolations

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassJavadoc'
        assert !rule.applyToNonMainClasses
    }

    @Test
    void testIgnoresNonMainClasses() {
        final SOURCE = '''
            /**
             * Javadoc
             */
            class MyClass {}

            class OtherClass {}
        '''
        sourceCodeName = 'MyClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testHasJavadoc_DefaultPackage_NoViolations() {
        final SOURCE = '''
            /**
             * Javadoc
             */
            class TestClass {
            }
        '''
        sourceCodeName = 'TestClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testHasJavadoc_IgnoresBlankLinesBetweenJavadocAndClass_NoViolations() {
        final SOURCE = '''
            /**
             * Javadoc
             */

            class TestClass {
            }
        '''
        sourceCodeName = 'TestClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testHasJavadoc_WithinPackage_NoViolations() {
        final SOURCE = '''
            package org.example

            /**
             * Javadoc
             */
            class TestClass {
            }
        '''
        sourceCodeName = 'TestClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testHasInlineJavadoc_WithinPackage_NoViolations() {
        final SOURCE = '''
            package org.example

            /** Javadoc */
            class TestClass {
            }
        '''
        sourceCodeName = 'TestClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testHasJavadoc_WithinPackage_WithRegularComment_NoViolations() {
        final SOURCE = '''
            package org.example

            /**
             * Javadoc
             */
            // Not javadoc
            class TestClass {
            }
        '''
        sourceCodeName = 'TestClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testHasJavadoc_WithinPackage_WithAnnotation_NoViolations() {
        final SOURCE = '''
            package org.example

            /**
             * Javadoc
             */
            @ToString
            class TestClass {
            }
        '''
        sourceCodeName = 'TestClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testHasJavadoc_WithinPackage_WithCommentedAnnotation_NoViolations() {
        final SOURCE = '''
            package org.example

            /**
             * Javadoc
             */
            //@ToString
            class TestClass {
            }
        '''
        sourceCodeName = 'TestClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testMissingJavadoc_WithinPackage_Violation() {
        final SOURCE = '''
            package org.example

            // Not javadoc
            class TestClass {
            }
        '''
        sourceCodeName = 'TestClass.Groovy'
        assertViolations(SOURCE,
            [line:5, source:'class TestClass', message:'Class org.example.TestClass missing Javadoc'])
    }

    @Test
    void testApplyToNonMainPackages_Violations() {
        final SOURCE = '''
            package org.example

            class MyClass {
            }

            // Not javadoc
            class OtherClass {
            }
        '''
        rule.applyToNonMainClasses = true
        sourceCodeName = 'MyClass.groovy'

        assertViolations(SOURCE,
                [line: 4, source: 'class MyClass', message: 'Class org.example.MyClass missing Javadoc'],
                [line: 8, source: 'class OtherClass', message: 'Class org.example.OtherClass missing Javadoc'])
    }

    @Test
    void testSourceCodeNameWithoutExtension() {
        final SOURCE = 'println'
        assert rule.sourceCodeNameWithoutExtension(new SourceString(SOURCE, null, null)) == null
        assert rule.sourceCodeNameWithoutExtension(new SourceString(SOURCE, null, '')) == ''
        assert rule.sourceCodeNameWithoutExtension(new SourceString(SOURCE, null, 'abc')) == 'abc'
        assert rule.sourceCodeNameWithoutExtension(new SourceString(SOURCE, null, 'abc.groovy')) == 'abc'
    }

    @Override
    protected ClassJavadocRule createRule() {
        new ClassJavadocRule()
    }
}
