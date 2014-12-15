/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule

import org.codenarc.analyzer.StringSourceAnalyzer
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.source.CustomCompilerPhaseSourceDecorator
import org.codenarc.source.SourceCode
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

import static org.codenarc.test.TestUtil.assertContainsAll

/**
 * Abstract superclass for tests of Rule classes
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
@SuppressWarnings('DuplicateLiteral')
abstract class AbstractRuleTestCase extends AbstractTestCase {

    protected static final CONSTRUCTOR_METHOD_NAME = '<init>'
    protected static final DEFAULT_TEST_FILES = AbstractAstVisitorRule.DEFAULT_TEST_FILES
    protected static final DEFAULT_TEST_CLASS_NAMES = AbstractAstVisitorRule.DEFAULT_TEST_CLASS_NAMES
    protected Rule rule

    // Subclasses can optionally set these to set the name or path of the SourceCode object created
    protected String sourceCodeName
    protected String sourceCodePath

    //--------------------------------------------------------------------------
    // Common Tests - Run for all concrete subclasses
    //--------------------------------------------------------------------------

    /**
     * Make sure that code unrelated to the rule under test causes no violations.
     * Subclasses can skip this rule by defining a property named 'skipTestThatUnrelatedCodeHasNoViolations'.
     */
    @Test
    void testThatUnrelatedCodeHasNoViolations() {
        final SOURCE = 'class MyClass { }'
        if (!getProperties().keySet().contains('skipTestThatUnrelatedCodeHasNoViolations')) {
            assertNoViolations(SOURCE)
        }
    }

    @Test
    void testThatInvalidCodeHasNoViolations() {
        final SOURCE = '''
            @will not compile@ &^%$#
        '''
        if (!getProperties().keySet().contains('skipTestThatInvalidCodeHasNoViolations')) {
            // Verify no errors/exceptions
            def sourceCode = prepareSourceCode(SOURCE)
            assert rule.applyTo(sourceCode).empty
        }
    }

    @Test
    void testThatApplyToFilesMatchingValuesAreValidRegex() {
        assertValidRegex(rule.applyToFilesMatching, 'applyToFilesMatching')
        assertValidRegex(rule.doNotApplyToFilesMatching, 'doNotApplyToFilesMatching')
    }

    private void assertValidRegex(String regex, String name) {
        if (regex) {
            try {
                Pattern.compile(regex)
            }
            catch(PatternSyntaxException e) {
                fail("The $name value [$regex] is not a valid regular expression: $e")
            }
        }
    }

    //--------------------------------------------------------------------------
    // Abstract Method Declarations - Must be implemented by concrete subclasses
    //--------------------------------------------------------------------------

    /**
     * Create and return a new instance of the Rule class to be tested.
     * @return a new Rule instance
     */
    protected abstract Rule createRule()

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in two violations with the specified line numbers and containing the specified source text values.
     * @param source - the full source code to which the rule is applied, as a String
     * @param lineNumber1 - the expected line number in the first violation
     * @param sourceLineText1 - the text expected within the sourceLine of the first violation
     * @param lineNumber2 - the expected line number in the second violation
     * @param sourceLineText2 - the text expected within the sourceLine of the second violation
     */
    protected void assertTwoViolations(String source,
            Integer lineNumber1, String sourceLineText1,
            Integer lineNumber2, String sourceLineText2) {
        def violations = applyRuleTo(source)
        violations.sort { it.lineNumber }
        assert violations.size() == 2, "Expected 2 violations\nFound ${violations.size()}: \n${violations.join('\n')}\n"
        assertViolation(violations[0], lineNumber1, sourceLineText1)
        assertViolation(violations[1], lineNumber2, sourceLineText2)
    }

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in the violations specified inline within the source.<p>
     *
     * Inline violations can be specified either by using the {@link #inlineViolation(java.lang.String)} method
     * or simply by prefixing a violation message with a '#'. Multiple inline violations per line are allowed.<p>
     *
     * One can prevent a '#' character from starting a violation message by escaping it with a '\' character
     * (keep in mind that most of Groovy's string literal syntax demands the '\' to be escaped itself,
     * as a '\\' sequence).<p> 
     *
     * For every source line all text after the first non-escaped '#' character is part of some inline violation message
     * (with the sole exception of the first line of a Groovy script beginning with a shebang).
     * More precisely, every '#' character that is neither escaped nor part of a shebang starts an inline violation that
     * spans to the end of its line or until next non-escaped '#' character.<p>
     *     
     * See the {@link #inlineViolation(java.lang.String)} method.<br>    
     * See the {@link #removeInlineViolations(java.lang.String)} method.<br>    
     *
     * @param source - the full source code to which the rule is applied annotated with inline violations, as a String
     */
    protected void assertInlineViolations(String annotatedSource) {
        def parseResult = new InlineViolationsParser().parse(annotatedSource)
        def violationsMap = parseResult.violations as Map[]
        assertViolations(parseResult.source, violationsMap)
        assert violationsMap, 'There must be at least one inline violation specified. If no violations are intended, then use assertNoViolations() instead'
    }

    /**
     * Prepares an inline violation with a given message, escaping all '#' characters and preventing accidental 
     * escaping of next inline violation's start when the message ends with a '\' character.
     *
     * @param violationMessage message for the inline violation
     * @return a String that will be interpreted as an inline violation by the 
     * {@link #assertInlineViolations(java.lang.String)} method 
     */
    protected static String inlineViolation(String violationMessage) {
        return InlineViolationsParser.inlineViolation(violationMessage)
    }

    /**
     * Removes all inline violations from a source.
     *
     * @param annotatedSource source possibly containing inline violations
     * @return the given source with inline violations removed
     */
    protected static String removeInlineViolations(String annotatedSource) {
        return new InlineViolationsParser().parse(annotatedSource).source
    }

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in the violations specified in violationMaps.
     * @param source - the full source code to which the rule is applied, as a String
     * @param violationMaps - a list (array) of Maps, each describing a single violation.
     *      Each element in the map can contain a lineNumber, sourceLineText and messageText entries.
     */
    protected void assertViolations(String source, Map[] violationMaps) {
        def rawViolations = applyRuleTo(source)
        rawViolations.sort { v -> v.lineNumber }
        assert rawViolations.size() == violationMaps.size(), "Expected ${violationMaps.size()} violations\nFound ${rawViolations.size()}: \n    ${rawViolations.join('\n    ')}\n"
        violationMaps.eachWithIndex { violationMap, index ->
            assert violationMap.keySet().every { key -> key in ['lineNumber', 'sourceLineText', 'messageText'] }, "violationMap keys must be 'lineNumber', 'sourceLineText' and/or 'messageText'"
            assertViolation(rawViolations[index], violationMap.lineNumber, violationMap.sourceLineText, violationMap.messageText)
        }
    }

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in two violations with the specified line numbers and containing the specified source text values.
     * @param source - the full source code to which the rule is applied, as a String
     * @param lineNumber1 - the expected line number in the first violation
     * @param sourceLineText1 - the text expected within the sourceLine of the first violation
     * @param msg1 - the text expected within the message of the first violation; May be a String or List of Strings; Defaults to null;
     * @param lineNumber2 - the expected line number in the second violation
     * @param sourceLineText2 - the text expected within the sourceLine of the second violation
     * @param msg2 - the text expected within the message of the second violation; May be a String or List of Strings; Defaults to null;
     */
    @SuppressWarnings('ParameterCount')
    protected void assertTwoViolations(String source,
            Integer lineNumber1, String sourceLineText1, msg1,
            Integer lineNumber2, String sourceLineText2, msg2) {
        def violations = applyRuleTo(source)
        assert violations.size() == 2, "Expected 2 violations\nFound ${violations.size()}: \n${violations.join('\n')}\n"
        assertViolation(violations[0], lineNumber1, sourceLineText1, msg1)
        assertViolation(violations[1], lineNumber2, sourceLineText2, msg2)
    }

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in a single violation with the specified line number and containing the specified source text.
     * @param source - the full source code to which the rule is applied, as a String
     * @param lineNumber - the expected line number in the resulting violation; defaults to null
     * @param sourceLineText - the text expected within the sourceLine of the resulting violation; defaults to null
     * @param messageText - the text expected within the message of the resulting violation; May be a String or List of Strings; Defaults to null;
     */
    protected void assertSingleViolation(String source, Integer lineNumber=null, String sourceLineText=null, messageText=null) {
        def violations = applyRuleTo(source)
        assert violations.size() == 1, "Expected 1 violation\nFound ${violations.size()}: \n${violations.join('\n')}\n  for sourceLineText: [$sourceLineText]"
        assertViolation(violations[0], lineNumber, sourceLineText, messageText)
    }

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in a single violation and that the specified closure returns true.
     * @param source - the full source code to which the rule is applied, as a String; defaults to null
     * @param closure - the closure to apply to the violation; takes a single Violation parameter
     */
    protected void assertSingleViolation(String source, Closure closure) {
        def violations = applyRuleTo(source)
        assert violations.size() == 1, "Expected 1 violation\nFound ${violations.size()}: \n${violations.join('\n')}\n"
        assert closure(violations[0]), "Closure failed for ${violations[0]}"
    }

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in no violations.
     * @param source - the full source code to which the rule is applied, as a String
     */
    protected void assertNoViolations(String source) {
        def violations = applyRuleTo(source)
        assert violations.empty, "Expected no violations, but got ${violations.size()}: \n${violations.join('\n')}\n"
    }

    /**
     * Assert that the specified violation is for the current rule, and has expected line number
     * and contains the specified source text and message text.
     * @param violation - the Violation
     * @param lineNumber - the expected line number in the resulting violation
     * @param sourceLineText - the text expected within the sourceLine of the resulting violation; may be null
     * @param messageText - the text expected within the message of the resulting violation; May be a String or List of Strings; Defaults to null;
     */
    protected void assertViolation(
                            Violation violation,
                            Integer lineNumber,
                            String sourceLineText,
                            messageText=null) {
        assert violation.rule == rule
        assert violation.lineNumber == lineNumber : "Wrong line number for violation: \n$violation\nExpected: $lineNumber\nFound:    $violation.lineNumber\n"
        if (sourceLineText) {
            assert violation.sourceLine
            assert violation.sourceLine.contains(sourceLineText), """Problem with source text:
expected to contain:  $sourceLineText
actual:               $violation.sourceLine
"""
        }
        if (messageText) {
            assert violation.message, 'The violation message was null'
            if (messageText instanceof Collection) {
                assertContainsAll(violation.message, messageText)
            }
            else {
                assert violation.message.contains(messageText), "\nExpected message text: [$messageText]\nFound message text:    [$violation.message]\n"
            }
        }
    }

    /**
     * Apply the current Rule to the specified source (String) and return the resulting List of Violations.
     * @param source - the full source code to which the rule is applied, as a String
     */
    protected List applyRuleTo(String source) {
        def sourceCode = prepareSourceCode(source)
        assert sourceCode.valid
        def violations = rule.applyTo(sourceCode)
        log("violations=$violations")
        violations
    }

    private SourceCode prepareSourceCode(String source) {
        def sourceCode = new SourceString(source, sourceCodePath, sourceCodeName)
        if (rule.compilerPhase != SourceCode.DEFAULT_COMPILER_PHASE) {
            sourceCode = new CustomCompilerPhaseSourceDecorator(sourceCode, rule.compilerPhase)
        }
        sourceCode
    }

    /**
     * Apply the current Rule to the specified source (String) and return the resulting List of Violations.
     * @param source - the full source code to which the rule is applied, as a String
     */
    protected List manuallyApplyRule(String source) {
        def analyzer = new StringSourceAnalyzer(source)
        assert analyzer.source.valid
        def results = analyzer.analyze(new ListRuleSet([rule]))
        results.violations
    }

    @Before
    void setUpAbstractRuleTestCase() {
        this.rule = createRule()
    }

}
