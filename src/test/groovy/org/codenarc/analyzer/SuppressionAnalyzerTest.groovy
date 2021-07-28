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
            @SuppressWarnings(['Rule2', 'CodeNarc.Rule3'])
            package foo

            println 4
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
    }

    @Test
    void testPackage_all() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('all')
            package foo
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
    }

    @Test
    void testPackage_CodeNarc() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('CodeNarc')
            package foo
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
    }

    @Test
    void testImport() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')                  // 3
            @SuppressWarnings(['Rule2', 'Rule3'])       // 4
            import java.lang.Integer                    // 5

            @SuppressWarnings('Rule4')                  // 7
            import java.lang.Float                      // 8

            @SuppressWarnings(['Rule5', 'Rule6'])       // 10
            import java.lang.String                     // 11

            @SuppressWarnings("all")                    // 13
            import java.lang.Math                       // 14

            import java.lang.BigDecimal                 // 16
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule5'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule6'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule7'))   // because of "all"

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 5))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 6))

        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 6))
        assert analyzer.isViolationSuppressed(violationFor('Rule4', 7))
        assert analyzer.isViolationSuppressed(violationFor('Rule4', 8))
        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 9))

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 14))
        assert analyzer.isViolationSuppressed(violationFor('Rule2', 14))
    }

    @Test
    void testStarImport() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')                  // 3
            @SuppressWarnings(['Rule2', 'Rule3'])       // 4
            import java.lang.*                          // 5

            @SuppressWarnings('Rule4')                  // 7
            import java.io.*                            // 8

            @SuppressWarnings(['Rule5', 'Rule6'])       // 10
            import java.util.*                          // 11

            import java.net.*                           // 13
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule5'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule6'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule7'))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 6))

        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 6))
        assert analyzer.isViolationSuppressed(violationFor('Rule4', 7))
        assert analyzer.isViolationSuppressed(violationFor('Rule4', 8))
        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 9))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 13))
        assert !analyzer.isViolationSuppressed(violationFor('Rule2', 13))
    }

    @Test
    void testStaticImport() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')                  // 3
            @SuppressWarnings(['Rule2', 'Rule3'])       // 4
            import static java.lang.Float               // 5

            @SuppressWarnings('Rule4')                  // 7
            import static java.io.File                  // 8

            @SuppressWarnings(['Rule5', 'Rule6'])       // 10
            import static java.util.Map                 // 11

            println 4                                   // 12
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule5'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule6'))
        assert !analyzer.isRuleSuppressed(new MockRule(name: 'Rule7'))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 6))

        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 6))
        assert analyzer.isViolationSuppressed(violationFor('Rule4', 7))
        assert analyzer.isViolationSuppressed(violationFor('Rule4', 8))
        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 9))
    }

    @Test
    void testStaticStarImport() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('Rule1')                  // 3
            @SuppressWarnings(['Rule2', 'Rule3'])       // 4
            import static java.lang.*                   // 5

            @SuppressWarnings('Rule4')                  // 7
            import static java.io.*                     // 8

            @SuppressWarnings(['Rule5', 'Rule6'])       // 10
            import static java.util.*                   // 11

            @SuppressWarnings(['CodeNarc'])             // 13
            import static java.lang.Math.*              // 14
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule4'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule5'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule6'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule7'))   // because of "all"

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 6))

        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 6))
        assert analyzer.isViolationSuppressed(violationFor('Rule4', 7))
        assert analyzer.isViolationSuppressed(violationFor('Rule4', 8))
        assert !analyzer.isViolationSuppressed(violationFor('Rule4', 9))

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 14))
        assert analyzer.isViolationSuppressed(violationFor('Rule2', 14))
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
    void testClass_all() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings('all')
            class MyClass { }
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
    }

    @Test
    void testClass_CodeNarc() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            @SuppressWarnings(['CodeNarc', 'other'])
            class MyClass { }
        '''))

        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule1'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule2'))
        assert analyzer.isRuleSuppressed(new MockRule(name: 'Rule3'))
    }

    @Test
    void testFields() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            class MyClass {                             // 3
                @SuppressWarnings('Rule1')              // 4
                private String myField = """            // 5
                           ... multiline content        // 6
                    """                                 // 7

                @SuppressWarnings('all')                // 9
                private String name1 = 'joe'            // 10

                @SuppressWarnings('CodeNarc')           // 12
                private String name2 = 'joe'            // 13
            }                                           // 14
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

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 10))

        assert analyzer.isViolationSuppressed(violationFor('Rule2', 13))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 14))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 15))
    }

    @Test
    void testProperties() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            class MyClass {                             // 3
                @SuppressWarnings('CodeNarc.Rule1')     // 4
                def myProperty = """                    // 5
                           ... multiline content        // 6
                    """                                 // 7

                @SuppressWarnings('all')                // 9
                private String name1 = 'joe'            // 10

                @SuppressWarnings('CodeNarc')           // 12
                private String name2 = 'joe'            // 13

                @SuppressWarnings('Rule1')              // 15
                def myProperty2 = "xxx"                 // 16
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

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 10))
        assert analyzer.isViolationSuppressed(violationFor('Rule2', 10))

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 13))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 14))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 15))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 16))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 17))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 18))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 19))
    }

    @Test
    void testMethods() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''

            class MyClass {                             // 3
                @SuppressWarnings('Rule1')              // 4
                private String myMethod() {             // 5
                    """                                 // 6
                           ... multiline content        // 7
                    """                                 // 8
                }                                       // 9

                @SuppressWarnings('CodeNarc.Rule1')     // 11
                private String myMethod2() {            // 12
                    """                                 // 13
                           ... multiline content        // 14
                    """                                 // 15
                }                                       // 16

                @SuppressWarnings('all')                // 18
                private String myMethod3() { }          // 19

                @SuppressWarnings('CodeNarc')           // 21
                private String myMethod3() { }          // 22
            }                                           // 23
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

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 19))

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 22))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 24))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 25))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 26))
    }

    @Test
    void testConstructors() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''
            class MyClass {                         // 2
                @SuppressWarnings('Rule1')          // 3
                private MyClass() {                 // 4
                    println 123                     // 5
                }                                   // 6

                @SuppressWarnings('all')            // 8
                private MyClass(int count) { }      // 9

                @SuppressWarnings('CodeNarc')       // 11
                private MyClass(int count) { }      // 12
            }                                       // 13
        '''))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 6))

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 9))

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 12))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 13))
    }

    @Test
    void testDeclarations() {
        def analyzer = new SuppressionAnalyzer(new SourceString('''          // 1
            class MyClass {                                                  // 2
                void run() {                                                 // 3
                    @SuppressWarnings('Rule1')                               // 4
                    int t = 123                                              // 5
                    @SuppressWarnings('Rule1') int u = 123                   // 6
                    int x = u + t                                            // 7
                    println u                                                // 8
                    println x                                                // 9
                }                                                            // 10
                private MyClass()  {                                         // 11
                    @SuppressWarnings('Rule1')                               // 12
                    int t = 123                                              // 13
                    @SuppressWarnings('Rule1') int u = 123                   // 14
                    int x = u + t                                            // 15
                    println u                                                // 16
                    println x                                                // 17

                    @SuppressWarnings('all')                                 // 19
                    String name = 'abc'                                      // 20

                    @SuppressWarnings('CodeNarc')                            // 22
                    String name = 'abc'                                      // 23
                }                                                            // 24
            }                                                                // 25
        '''))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', -1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 0))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 1))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 2))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 3))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 4))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 5))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 6))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 7))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 8))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 9))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 10))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 11))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 12))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 13))
        assert analyzer.isViolationSuppressed(violationFor('Rule1', 14))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 15))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 16))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 17))

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 20))

        assert analyzer.isViolationSuppressed(violationFor('Rule1', 23))

        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 24))
        assert !analyzer.isViolationSuppressed(violationFor('Rule1', 25))
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
