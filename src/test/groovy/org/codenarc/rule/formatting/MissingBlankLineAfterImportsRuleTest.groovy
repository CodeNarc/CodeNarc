/*
 * Copyright 2014 the original author or authors.
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
import org.junit.Test

/**
 * Tests for MissingBlankLineAfterImportsRule
 */
class MissingBlankLineAfterImportsRuleTest extends AbstractRuleTestCase<MissingBlankLineAfterImportsRule> {

    private static final IMPORT_TYPES = [
        'import java.lang.Math', // normal
        'import java.lang.*', // star
        'import static java.lang.Math.PI', // static
        'import static java.lang.Math.*', // static star
    ]

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MissingBlankLineAfterImports'
    }

    @Test
    void test_BlankLineAfterLastImport_NoViolation() {
        IMPORT_TYPES.each { lastImport ->
            final SOURCE = """\
            package org.codenarc

            import org.codenarc.rule.Rule
            $lastImport

            class MyClass { }
            """.stripIndent()
            assertNoViolations(SOURCE)
        }
    }

    @Test
    void test_NoBlankLineAfterLastImport_Violation() {
        IMPORT_TYPES.each { lastImport ->
            final SOURCE = """\
            package org.codenarc

            import org.codenarc.rule.Rule
            $lastImport
            class MyClass { }
            """.stripIndent()
            assertSingleViolation(SOURCE, 5, 'class MyClass { }', 'Missing blank line after imports in file null')
        }
    }

    @Test
    void test_PackageInfo_NothingAfterLastImport_NoViolation() {
        IMPORT_TYPES.each { lastImport ->
            final SOURCE = """\
                package org.codenarc

                $lastImport
                """.stripIndent()
            assertNoViolations(SOURCE)
        }
    }

    @Test
    void test_PackageInfo_BlankLineAfterLastImport_NoViolation() {
        IMPORT_TYPES.each { lastImport ->
            final SOURCE = """\
                package org.codenarc

                $lastImport

                """.stripIndent()
            assertNoViolations(SOURCE)
        }
    }

    @Test
    void test_PackageInfo_NoBlankLineAfterLastImport_Violation() {
        IMPORT_TYPES.each { lastImport ->
            final SOURCE = """\
                package org.codenarc

                $lastImport
                // comment
                """.stripIndent()
            assertSingleViolation(SOURCE, 4, '// comment', 'Missing blank line after imports')
        }
    }

    @Override
    protected MissingBlankLineAfterImportsRule createRule() {
        new MissingBlankLineAfterImportsRule()
    }
}
