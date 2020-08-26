/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.plugin.disablerules

import org.codenarc.plugin.CodeNarcPlugin
import org.codenarc.plugin.FileViolations
import org.codenarc.results.FileResults
import org.codenarc.rule.Rule
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for DisableRulesInCommentsPlugin
 */
class DisableRulesInCommentsPluginTest extends AbstractTestCase {

    private static final Rule RULE1 = new StubRule(name:'Rule1')
    private static final Violation VIOLATION1 = new Violation(rule: RULE1, lineNumber: 1)
    private static final Violation VIOLATION2 = new Violation(rule: RULE1, lineNumber: 2)
    private static final Violation VIOLATION3 = new Violation(rule: RULE1, lineNumber: 3)
    private static final Violation VIOLATION4 = new Violation(rule: RULE1, lineNumber: 4)
    private static final Violation VIOLATION5 = new Violation(rule: RULE1, lineNumber: 5)
    private static final Violation VIOLATION_NULL_LINE_NUMBER = new Violation(rule: RULE1, lineNumber: null)

    private static final String SOURCE = '''
        class MyClass {
            def value = 123
            void doStuff() {
                println 123
            }
        }
        '''

    private DisableRulesInCommentsPlugin filter = new DisableRulesInCommentsPlugin()
    private String sourceText
    private List<Violation> violations = [VIOLATION1, VIOLATION2, VIOLATION3, VIOLATION4, VIOLATION5]

    @Test
    void test_ImplementsCodeNarcPlugin() {
        assert filter instanceof CodeNarcPlugin
    }

    @Test
    void test_processViolationsForFile_IndividualRules_DisableAll() {
        sourceText = '''
            class MyClass {         /* codenarc-disable Rule2, Rule1, OtherRule  */
                def value = 123
                void doStuff() {
                    println 123
                }
            }
        '''.trim()
        assertNoViolationsEnabled()
    }

    @Test
    void test_processViolationsForFile_IndividualRules_Disable_AndThenEnable() {
        sourceText = '''
            class MyClass {
                def value = 123     //  codenarc-disable Rule2, Rule1, OtherRule
                void doStuff() {
                    println 123     //codenarc-enable Rule1, OtherRule
                }
            }
        '''.trim()
        assertViolationsThatAreEnabled([VIOLATION1, VIOLATION4, VIOLATION5])
    }

    @Test
    void test_processViolationsForFile_IndividualRules_Disable_AndThenEnableOtherRules() {
        sourceText = '''
            class MyClass {
                def value = 123     //  codenarc-disable Rule2, Rule1, OtherRule
                void doStuff() {
                    println 123//codenarc-enable OtherRule, Rule2
                }
            }
        '''.trim()
        assertViolationsThatAreEnabled([VIOLATION1])
    }

    @Test
    void test_processViolationsForFile_IndividualRules_DisableOtherRules() {
        sourceText = '''
            class MyClass {
                def value = 123     //  codenarc-disable Rule2, OtherRule
                void doStuff() {
                    println 123     //codenarc-enable OtherRule
                }
            }
        '''.trim()
        assertAllViolationsEnabled()
    }

    @Test
    void test_processViolationsForFile_IndividualRules_Disable_AndThenEnableAll() {
        sourceText = '''
            class MyClass {
                def value = 123
                //  codenarc-disable Rule1
                void doStuff() {
                    println 123         // codenarc-enable
                }
            }
        '''.trim()
        assertViolationsThatAreEnabled([VIOLATION1, VIOLATION2, VIOLATION5])
    }

    @Test
    void test_processViolationsForFile_Disable_AndThenEnable() {
        sourceText = '''
            class MyClass {         //   codenarc-disable
                def value = 123
                void doStuff() {    //codenarc-enable
                    println 123
                }
            }
        '''.trim()
        assertViolationsThatAreEnabled([VIOLATION3, VIOLATION4, VIOLATION5])
    }

    @Test
    void test_processViolationsForFile_Disable_AndThenEnableAfterLastViolation() {
        sourceText = '''
            class MyClass {
                def value = 123     /*codenarc-disable*/
                void doStuff() {
                    println 123
                }
                // This one is after all of the violations
                //codenarc-enable
            }
        '''.trim()
        assertViolationsThatAreEnabled([VIOLATION1])
    }

    @Test
    void test_processViolationsForFile_DisableAllViolations() {
        sourceText = '// codenarc-disable' + SOURCE
        assertNoViolationsEnabled()

        sourceText = '    /*codenarc-disable*/' + SOURCE
        assertNoViolationsEnabled()
    }

    @Test
    void test_processViolationsForFile_DisableLine() {
        sourceText = '''
            class MyClass {         // codenarc-disable-line
                def value = 123     /*codenarc-disable-line Rule2, OtherRule*/
                void doStuff() {
                    println 123     //codenarc-disable-line Rule2, Rule1, OtherRule
                }
            }
        '''.trim()
        assertViolationsThatAreEnabled([VIOLATION2, VIOLATION3, VIOLATION5])
    }

    @Test
    void test_processViolationsForFile_NoDisableComments() {
        sourceText = '''
            // codenarc-enable
            class MyClass {         // some other comment
                def value = 123     /* codenarc-enable Rule1, Rule2 */
                void doStuff() {
                    println 123     // other comment
                }
            /* codenarc-enable */
            }
            '''
        assertAllViolationsEnabled()
    }

    @Test
    void test_processViolationsForFile_ViolationWithNullLineNumber() {
        violations = [VIOLATION_NULL_LINE_NUMBER]
        sourceText = SOURCE
        assertViolationsThatAreEnabled([VIOLATION_NULL_LINE_NUMBER])
    }

    @Test
    void test_processViolationsForFile_NoComments() {
        sourceText = '''
            println 123
            '''
        assertAllViolationsEnabled()
    }

    // Helper methods

    private void assertAllViolationsEnabled() {
        assertViolationsThatAreEnabled(violations)
    }

    private void assertNoViolationsEnabled() {
        assertViolationsThatAreEnabled([])
    }

    private void assertViolationsThatAreEnabled(List<Violation> enabledViolations) {
        FileViolations fileViolations = createFileViolations()

        filter.processViolationsForFile(fileViolations)

        assert fileViolations.violations.size() == enabledViolations.size()
        assert fileViolations.violations == enabledViolations
    }

    private FileViolations createFileViolations() {
        SourceString sourceCode = new SourceString(sourceText)
        FileResults fileResults = new FileResults('path', violations, sourceCode)
        return new FileViolations(fileResults)
    }

}
