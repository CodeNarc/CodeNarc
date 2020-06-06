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

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MissingBlankLineAfterImports'
    }

    @Test
    void test_BlankLineAfterImports_NoViolation() {
        final SOURCE = '''\
            package org.codenarc

            import org.codenarc.rule.Rule
            import org.codenarc.rule.StubRule

            class MyClass {
                    def go() { /* ... */ }
            }
            '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @SuppressWarnings('MissingBlankLineAfterImports')
    @Test
    void test_NoLinesAfterImports_Violation() {
        final SOURCE = '''
            package org.codenarc

            import org.codenarc.rule.Rule
            import org.codenarc.rule.StubRule
            class MyClass {
                    void go() { /* ... */ }
            }'''.stripIndent()
        assertSingleViolation(SOURCE, 6, 'class MyClass {', 'Missing blank line after imports in file null')
    }

    @Test
    void test_PackageInfo_NothingAfter_LastImport_NoViolation() {
        final SOURCE = '''
            package com.a.random.pkg.nothing.to.see.here

            import com.very.very.important.pkg
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_PackageInfo_NotABlankLineAfterLastImport_NoViolation() {
        final SOURCE = '''
            package com.a.random.pkg.nothing.to.see.here

            import com.very.very.important.pkg
            // comment
        '''.stripIndent()
        assertSingleViolation(SOURCE, 5, '// comment', 'Missing blank line after imports')
    }

    @Test
    void test_PackageInfo_NoViolation() {
        final SOURCE = '''
            package com.a.random.pkg.nothing.to.see.here

            import com.very.very.important.pkg

        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Override
    protected MissingBlankLineAfterImportsRule createRule() {
        new MissingBlankLineAfterImportsRule()
    }
}
