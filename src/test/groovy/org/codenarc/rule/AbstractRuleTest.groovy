/*
 * Copyright 2008 the original author or authors.
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

import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTest

/**
 * Abstract superclass for tests of Rule classes
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractRuleTest extends AbstractTest {
    protected Rule rule

    // Subclasses can optionally set these to set the name or path of the SourceCode object created
    protected String sourceCodeName
    protected String sourceCodePath

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
        assert violations.size() == 2
        assertViolation(violations[0], lineNumber1, sourceLineText1)
        assertViolation(violations[1], lineNumber2, sourceLineText2)
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
        assert violations.size() == 1
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
        assert violations.size() == 1
        assert closure(violations[0]), "Closure failed for ${violations[0]}"
    }

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in no violations.
     * @param source - the full source code to which the rule is applied, as a String
     */
    void assertNoViolations(String source) {
        def violations = applyRuleTo(source)
        assert violations.empty, violations
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
        assert violation.lineNumber == lineNumber
        if (sourceLineText) {
            assert violation.sourceLine.contains(sourceLineText), "sourceLineText=[$sourceLineText]"
        }
        if (messageText) {
            assert violation.message, "The violation message was null"
            if (messageText instanceof Collection) {
                assertContainsAll(violation.message, messageText)
            }
            else {
                assert violation.message.contains(messageText), "messageText=[$messageText]"
            }
        }
    }

    /**
     * Apply the current Rule to the specified source (String) and return the resulting List of Violations.
     * @param source - the full source code to which the rule is applied, as a String
     */
    protected List applyRuleTo(String source) {
        def sourceCode = new SourceString(source, sourceCodePath, sourceCodeName)
        def violations = rule.applyTo(sourceCode)
        log("violations=$violations")
        return violations
    }

    void setUp() {
        super.setUp()
        this.rule = createRule()
    }

}