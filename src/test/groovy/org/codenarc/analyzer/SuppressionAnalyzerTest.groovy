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
package org.codenarc.analyzer

import org.codenarc.rule.MockRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for SuppressionAnalyzer
 */
class SuppressionAnalyzerTest extends AbstractTestCase {

    @Test
    void testNone() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''
            println 4
        '''))

        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
    }

    @Test
    void testPackage() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')
            @SuppressWarnings(['Rule2', 'Rule3'])
            package foo

            println 4
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
    }

    @Test
    void testImport() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')
            @SuppressWarnings(['Rule2', 'Rule3'])
            import java.lang.Integer

            @SuppressWarnings('Rule4')
            import java.lang.Float

            @SuppressWarnings(['Rule5', 'Rule6'])
            import java.lang.String

            println 4
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule5'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule6'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule7'))
    }

    @Test
    void testStarImport() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')
            @SuppressWarnings(['Rule2', 'Rule3'])
            import java.lang.*

            @SuppressWarnings('Rule4')
            import java.io.*

            @SuppressWarnings(['Rule5', 'Rule6'])
            import java.util.*

            println 4
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule5'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule6'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule7'))
    }

    @Test
    void testStaticStarImport() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')
            @SuppressWarnings(['Rule2', 'Rule3'])
            import static java.lang.*

            @SuppressWarnings('Rule4')
            import static java.io.*

            @SuppressWarnings(['Rule5', 'Rule6'])
            import static java.util.*

            println 4
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule5'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule6'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule7'))
    }

    @Test
    void testSingleClass() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')
            @SuppressWarnings(['Rule2', 'Rule3'])
            class MyClass { }
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
    }

    @Test
    void testTwoClassesClass() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')
            @SuppressWarnings(['Rule2', 'Rule3'])
            class MyClass { }

            class MyClass2 { }
        '''))

        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 6))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 7))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 8))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 9))
    }

    @Test
    void testFields() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            class MyClass {
                @SuppressWarnings('Rule1')
                private String myField = """
                           ... multiline content
                    """
            }
        '''))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 6))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 7))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 8))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 9))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 10))
    }

    @Test
    void testProperties() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            class MyClass {
                @SuppressWarnings('Rule1')
                def myProperty = """
                           ... multiline content
                    """
            }
        '''))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 6))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 7))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 8))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 9))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 10))
    }

    @Test
    void testMethods() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            class MyClass {
                @SuppressWarnings('Rule1')
                private String myMethod() {
                    """
                           ... multiline content
                    """
                }

                @SuppressWarnings('Rule1')
                private String myMethod2() {
                    """
                           ... multiline content
                    """
                }
            }
        '''))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 6))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 7))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 8))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 9))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 10))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 11))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 12))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 13))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 14))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 15))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 16))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 17))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 18))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 19))
    }

    @Test
    void testCompilationFails() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''
            class XYZ ^&**(
        '''))

        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
    }

    private static Violation violationFor(String ruleName, int lineNumber) {
        new Violation(rule: new MockRule(name: ruleName), lineNumber: lineNumber)
    }
}
