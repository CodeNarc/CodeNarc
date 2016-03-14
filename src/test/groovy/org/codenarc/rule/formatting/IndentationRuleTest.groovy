/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for IndentationRule
 *
 * @author Rahul Somasunderam
 */
class IndentationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'Indentation'
    }

    @Test
    void testClassWithAttributeAndSimpleMethodsWithNoViolations() {
        final SOURCE = '''\
            |class Mango {
            |
            |    int size
            |
            |    def mangoGetter() { size }
            |
            |    def mangoMethod() {
            |        println "hello"
            |    }
            |
            |}'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassWithAnnotatedField() {
        final SOURCE = '''\
            |class Mango {
            |    @BoringProperty(value = 3)
            |    int size
            |}'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassWithAnnotatedFieldOnSameLine() {
        final SOURCE = '''\
            |class Mango {
            |    @BoringProperty(value = 3) int size
            |}'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassWithAnnotatedMethodOnSameLine() {
        final SOURCE = '''\
            |class Mango {
            |    @BoringProperty(value = 3) int getSize() {
            |        3
            |    }
            |}'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassWithPackage() {
        final SOURCE = '''\
            |package foo.bar
            |
            |class Mango {
            |
            |}'''.stripMargin()
        assertNoViolations(toTabs(SOURCE))
    }

    @Test
    void testClassWithPackageWithGrab() {
        final SOURCE = '''\
            |@Grab('a:b:3.0')
            |package foo.bar
            |
            |class Mango {
            |}'''.stripMargin()
        assertNoViolations(toTabs(SOURCE))
    }

    @Test
    void testClassWithPackageWithGrabViolation() {
        final SOURCE = '''\
            |    @Grab('a:b:3.0')
            |package foo.bar
            |
            |class Mango {
            |}'''.stripMargin()
        assertSingleViolation(SOURCE, 1, "    @Grab('a:b:3.0')", 'Expected indent 0 characters. Actual was 4 characters.')
    }

    @Test
    void testPackageWithViolation() {
        final SOURCE = '''\
            |    package foo.bar
            |
            |class Mango {
            |}'''.stripMargin()
        assertSingleViolation(SOURCE, 1, "    package foo.bar", 'Expected indent 0 characters. Actual was 4 characters.')
    }

    @Test
    void testClassWithImports() {
        final SOURCE = '''\
            |package foo.bar
            |
            |import tarfu.Banana
            |
            |class Mango {
            |}'''.stripMargin()
        assertNoViolations(toTabs(SOURCE))
    }

    @Test
    void testImportViolation() {
        final SOURCE = '''\
            |package foo.bar
            |
            |    import tarfu.Banana
            |
            |class Mango {
            |}'''.stripMargin()
        assertSingleViolation(SOURCE, 3, '    import tarfu.Banana',
                'Expected indent 0 characters. Actual was 4 characters.')
    }

    @Test
    void testImportViolation2() {
        final SOURCE = '''\
            |package foo.bar
            |
            |   import tarfu.Banana
            |
            |class Mango {
            |}'''.stripMargin()
        assertSingleViolation(SOURCE, 3, '   import tarfu.Banana',
                'Expected indent 0 characters. Actual was 3 characters.')
    }

    @Test
    void testClassWithAttributeAndSimpleMethodsWithNoFieldIndent() {
        final SOURCE = '''\
            |class Mango {
            |int size
            |}'''.stripMargin()
        assertSingleViolation(SOURCE, 2, 'int size', 'Expected indent 4 characters. Actual was 0 characters.')
    }

    @Test
    void testStaticInnerClass() {
        final SOURCE = '''\
            |class Mango {
            |    static class Seed {
            |        int size
            |    }
            |}'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testAnonymousInnerClass() {
        final SOURCE = '''\
            |abstract class Mango {
            |    abstract String getType()
            |    static Mango create(final String theType) {
            |        new Mango() {
            |            String getType() {
            |                theType
            |            }
            |        }
            |    }
            |}'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassWithAttributeAndSimpleMethodsWithTooMuchFieldIndent() {
        final SOURCE = '''\
            |class Mango {
            |        int size
            |}'''.stripMargin()
        assertSingleViolation(SOURCE, 2, '        int size',
                'Expected indent 4 characters. Actual was 8 characters.')
    }

    @Test
    void testScriptVariableInOneLine() {
        final SOURCE = '''def arr = [1,2,3]'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testScriptVariableInOneLineWithWrongIndents() {
        final SOURCE = '''    def arr = [1,2,3]'''.stripMargin()
        assertSingleViolation(SOURCE, 1, '    def arr = [1,2,3]',
                'Expected indent 0 characters. Actual was 4 characters.')
    }

    @Test
    void testScriptVariableInTwoLinesWithCollect() {
        final SOURCE = '''\
            |def arr = [1,2,3].collect {
            |    it * 2
            |}
            |'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testSpacesInsteadOfTabs() {
        (rule as IndentationRule).with {
            useTabs = true
            indentSize = 1
            continuationIndentSize = 2
        }
        final SOURCE = '''\
            |package foo.bar
            |
            |import a.b
            |
            |class Mango {
            |    int size
            |}
            |'''.stripMargin()
        assertSingleViolation(SOURCE, 6, '  int size', 'Tabs were expected for indents. Spaces were found')
    }

    @Test
    void testTabsInsteadOfSpaces() {

        final SOURCE = '''\
            |package foo.bar
            |
            |import a.b
            |
            |class Mango {
            |    int size
            |}
            |'''.stripMargin()
        assertSingleViolation(toTabs(SOURCE), 6, '\tint size', 'Spaces were expected for indents. Tabs were found')
    }

    @Test
    void testIndentSize() {
        ((IndentationRule) rule).with {
            indentSize = 4
            continuationIndentSize = 8
        }

        final SOURCE = '''\
            |package foo.bar
            |
            |import a.b
            |
            |class Mango {
            |  int size
            |}
            |'''.stripMargin()
        assertSingleViolation(SOURCE, 6, '  int size', 'Expected indent 4 characters. Actual was 2 characters.')
    }

    @Test
    void testScriptVariableInTwoLines() {
        final SOURCE = '''\
        |def arr =
        |        [1,2,3]
        |'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testScriptVariableWithMultilineList() {
        final SOURCE = '''\
        |def arr = [
        |        1,2,3
        |]
        |'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testScriptVariableWithMultilineListTerminatingEarly() {
        final SOURCE = '''\
        |def arr = [
        |        1,2,3]
        |'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testScriptVariableWithMultilineListOnMultipleLines() {
        final SOURCE = '''\
        |def arr = [
        |        1,
        |        2,
        |        3
        |]
        |'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testScriptVariableInTwoLinesWithLessIndent() {
        final SOURCE = '''\
        |def arr =
        |    [1,2,3]
        |'''.stripMargin()
        assertSingleViolation(SOURCE, 2, '    [1,2,3]',
                'Expected indent 8 characters. Actual was 4 characters.')
    }

    @Test
    void testClassWithAnnotatedMethod() {
        final SOURCE = '''\
        |class Mango {
        |    @BoringProperty(value = 3)
        |    int getSize() {
        |        3
        |    }
        |}'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testAnnotatedClass() {
        final SOURCE = '''\
        |@BoringProperty(value = 3)
        |class Mango {
        |
        |    int getSize() {
        |        3
        |    }
        |}'''.stripMargin()
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new IndentationRule()
    }

    protected static toTabs(String input) {
        input.split('\n').
                collect {
                    it.replaceAll(/^( +)/) { List<String> a ->
                        '\t' * (a[0].length() / 4)
                    }
                }.
                join('\n')

    }
}
