/*
 * Copyright 2019 the original author or authors.
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
import org.junit.Test

/**
 * Abstract superclass for Tests for JavadocMissingXxxDescriptionRule classes
 *
 * @author Chris Mair
 */
abstract class AbstractJavadocMissingDescriptionRuleTestCase<T extends AbstractJavadocMissingDescriptionRule> extends AbstractRuleTestCase<T> {

    protected String sourceWithViolations = """
        class MyClass {
            /**
             * Return the calculated count of some stuff.
             *
             * ${getTag()} name
             *
             * NOTE: Only the first occurrence of an empty @return tag is found.
             *       So the following line is not flagged as a violation!!!
             * ${getTag()} name2
             */
            int countThings(int startIndex) { }

            /**
             *${getTag()} otherName
             */
            String otherMethod() { }
        }
        """

    protected abstract String getTag()

    protected String getRuleName() {
        return rule.name
    }
    protected String getViolationMessage() {
        return "The description for the javadoc ${getTag()} tag is missing"
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == getRuleName()
        assert rule.allowMultiline == false
    }

    @Test
    void test_JavadocWithoutEmptyTag_NoViolations() {
        final SOURCE = """
            class MyClass {
                /**
                 * Return the calculated count of some stuff.
                 * ${getTag()} name description
                 * @param endIndex the ending index
                 * @return the full count
                 */
                int countThings(int endIndex) {
                }
            }
        """
        assertNoViolations(SOURCE)
    }

    @Test
    void test_RegularComments_NoViolations() {
        final SOURCE = """
            /**
             * Initialize starting values.
             * Do not call this ... ever!!
             */
            void initialize() { }

            /*
             * Some comment
             * ${getTag()} name
             */
            void doThings(int startIndex) { }

            /*
                * ${getTag()} name
                void otherMethod() {
                    doThings(5);
                }
            */
        """
        assertNoViolations(SOURCE)
    }

    @Test
    void test_JavadocWithEmptyTag_Violations() {
        assertViolations(sourceWithViolations,
                [line:6, source:"* ${getTag()} name", message:getViolationMessage()],
                [line:15, source:"${getTag()} otherName", message:getViolationMessage()])
    }

    @Test
    void test_JavadocWithEmptyTag_WindowsLineEndings_Violations() {
        final SOURCE = sourceWithViolations.replace('\n', '\r\n')
        assertViolations(SOURCE,
                [line:6, source:"* ${getTag()} name", message:getViolationMessage()],
                [line:15, source:"${getTag()} otherName", message:getViolationMessage()])
    }

    @Test
    void test_JavadocWithMultilineTag_allowMultiline_NoViolations() {
        final SOURCE = """
            class MyClass {
                /**
                 * Return the calculated count of some stuff.
                 * ${getTag()} name
                 *    the full count
                 */
                int countThings() {
                }
            }
        """
        rule.allowMultiline = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_JavadocWithMultilineTag_allowMultiline_Violations() {
        final SOURCE = """
            class MyClass {
                /**
                 * ${getTag()} name
                 * @see MyOtherClass
                 */
                int method1() { }

                /**
                 * ${getTag()} name
                 */
                int method2() { }

                /**
                 *${getTag()} name
                 *
                 * Oh and another thing...
                 */
                int method3() { }
            }
        """
        rule.allowMultiline = true
        assertViolations(SOURCE,
                [line:4, source:"* ${getTag()} name", message:getViolationMessage()],
                [line:10, source:"* ${getTag()} name", message:getViolationMessage()],
                [line:15, source:"*${getTag()} name", message:getViolationMessage()])
    }

}
