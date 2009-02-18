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

/**
 * Tests for the AbstractRule class
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbstractRuleClassTest extends AbstractRuleTest {
    static final NAME = 'Rule123'
    static final PRIORITY = 2
    static final SOURCE = 'class MyClass { }'
    static final FILENAME = 'MyTest.groovy'
    static final MATCH = /.*Test\.groovy/
    static final NO_MATCH = /.*Other\.groovy/

    void testToString() {
        assertContainsAll(rule.toString(), ['TestPathRule', NAME, PRIORITY.toString()])
    }

    void testEnabled() {
        assertSingleViolation(SOURCE)
        rule.enabled = false
        assertNoViolations(SOURCE)
    }

    void testApplyToFilesMatching() {
        rule.applyToFilesMatching = MATCH
        assertSingleViolation(SOURCE)
        rule.applyToFilesMatching = NO_MATCH
        assertNoViolations(SOURCE)
    }

    void testDoNotApplyToFilesMatching() {
        rule.doNotApplyToFilesMatching = NO_MATCH
        assertSingleViolation(SOURCE)
        rule.doNotApplyToFilesMatching = MATCH
        assertNoViolations(SOURCE)
    }

    void testBothApplyToFilesMatchingAndDoNotApplyToFilesMatching() {
        rule.applyToFilesMatching = MATCH            // apply = YES
        rule.doNotApplyToFilesMatching = MATCH       // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToFilesMatching = NO_MATCH         // apply = NO
        rule.doNotApplyToFilesMatching = MATCH       // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToFilesMatching = MATCH            // apply = YES
        rule.doNotApplyToFilesMatching = NO_MATCH    // doNotApply = NO
        assertSingleViolation(SOURCE)

        rule.applyToFilesMatching = NO_MATCH         // apply = NO
        rule.doNotApplyToFilesMatching = NO_MATCH    // doNotApply = NO
        assertNoViolations(SOURCE)
    }

    void testApplyToFilenames() {
        rule.applyToFilenames = FILENAME
        assertSingleViolation(SOURCE)
        rule.applyToFilenames = "Xxx.groovy"
        assertNoViolations(SOURCE)
    }

    void testDoNotApplyToFilenames() {
        rule.doNotApplyToFilenames = "Xxx.groovy"
        assertSingleViolation(SOURCE)
        rule.doNotApplyToFilenames = FILENAME
        assertNoViolations(SOURCE)
    }

    void testBothApplyToFilenamesAndDoNotApplyToFilenames() {
        rule.applyToFilenames = FILENAME             // apply = YES
        rule.doNotApplyToFilenames = FILENAME        // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToFilenames = "Xxx.groovy"         // apply = NO
        rule.doNotApplyToFilenames = FILENAME        // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToFilenames = FILENAME             // apply = YES
        rule.doNotApplyToFilenames = "Xxx.groovy"    // doNotApply = NO
        assertSingleViolation(SOURCE)

        rule.applyToFilenames = "Xxx.groovy"         // apply = NO
        rule.doNotApplyToFilenames = "Xxx.groovy"    // doNotApply = NO
        assertNoViolations(SOURCE)
    }

    void testApplyToFilenamesAndDoNotApplyToRegex() {
        rule.applyToFilenames = FILENAME             // apply filename = YES
        rule.doNotApplyToFilesMatching = MATCH       // doNotApply regex = YES
        assertNoViolations(SOURCE)

        rule.applyToFilenames = "Xxx.groovy"         // apply filename = NO
        rule.doNotApplyToFilesMatching = MATCH       // doNotApply regex = YES
        assertNoViolations(SOURCE)
    }

    void testApplyToRegexAndDoNotApplyToFilenames() {
        rule.applyToFilesMatching = MATCH            // apply regex = YES
        rule.doNotApplyToFilenames = "Xxx.groovy"    // doNotApply filename = NO
        assertSingleViolation(SOURCE)

        rule.applyToFilesMatching = NO_MATCH         // apply regex = NO
        rule.doNotApplyToFilenames = FILENAME        // doNotApply filename = YES
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new TestPathRule(name:NAME, priority:PRIORITY)
    }

    void setUp() {
        super.setUp()
        sourceCodePath = FILENAME
        sourceCodeName = FILENAME
    }

}