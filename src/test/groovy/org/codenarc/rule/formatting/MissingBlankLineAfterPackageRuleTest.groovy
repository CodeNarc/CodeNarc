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
import org.junit.jupiter.api.Test

/**
 * Tests for MissingBlankLineAfterPackageRule
 *
 * @author Joe Sondow
 */
@SuppressWarnings('MissingBlankLineAfterImports')
class MissingBlankLineAfterPackageRuleTest extends AbstractRuleTestCase<MissingBlankLineAfterPackageRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MissingBlankLineAfterPackage'
    }

    @Test
    void testSuccessScenarioOnlyPackage() {
        final SOURCE = '''\
            /** some package comment */
            package org.codenarc
            '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioWithoutPackage() {
        final SOURCE = '''\
            class MyClass {
                    def go() { /* ... */ }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioWithPackage() {
        final SOURCE = '''\
            package org.codenarc

            class MyClass {
                    def go() { /* ... */ }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @SuppressWarnings('ConsecutiveBlankLines')
    @Test
    void testSuccessScenarioTwoBlankLinesAfterPackage() {
        final SOURCE = '''\
            package org.codenarc


            class MyClass {
                    def go() { /* ... */ }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioPackageThenBlankThenImport() {
        final SOURCE = '''\
            package org.codenarc

            import java.util.Date
            class MyClass {
                    def go() { /* ... */ }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void testAnnotationBeforePackage() {
        final SOURCE = '''\
            @RandomPackageAnnotation
            package com.random.pkg.of.no.consequence

            class MyClass {
                    def go() { /* ... */ }
            }
        '''.stripIndent()

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoLinesBetweenPackageAndImports() {
        final SOURCE = '''\
            package org.codenarc
            import java.util.Date

            class MyClass {
                    void go() { /* ... */ }
            }'''.stripIndent()
        assertSingleViolation(SOURCE, 1, 'import java.util.Date',
            'Missing blank line after package statement in file null')
    }

    @Test
    void testNoLinesBetweenPackageAndImportsAndClass() {
        final SOURCE = '''\
            package org.codenarc
            import java.util.Date
            class MyClass {
                    void go() { /* ... */ }
            }'''.stripIndent()
        assertSingleViolation(SOURCE, 1, 'import java.util.Date',
            'Missing blank line after package statement in file null')
    }

    @Test
    void testNoLinesBetweenPackageAndClassJavadoc() {
        final SOURCE = '''\
            package org.codenarc
            /**
             * A very special class
             */
            class MyClass {
                    void go() { /* ... */ }
            }'''.stripIndent()
        assertSingleViolation(SOURCE, 1, '/**', 'Missing blank line after package statement in file null')
    }

    @Test
    void testNoLinesBetweenPackageAndClass() {
        final SOURCE = '''\
            package org.codenarc
            class MyClass {
                    void go() { /* ... */ }
            }'''.stripIndent()
        assertSingleViolation(SOURCE, 1, 'class MyClass {', 'Missing blank line after package statement in file null')
    }

    @Test
    void testAnnotationBeforePackageAndNoLinesAfterPackage() {
        final SOURCE = '''\
            @RandomPackageAnnotation
            package com.random.pkg.of.no.consequence
            class MyClass {
                    def go() { /* ... */ }
            }
        '''.stripIndent()

        assertSingleViolation(SOURCE, 1, 'class MyClass {', 'Missing blank line after package statement in file null')
    }

    @Override
    protected MissingBlankLineAfterPackageRule createRule() {
        new MissingBlankLineAfterPackageRule()
    }
}
