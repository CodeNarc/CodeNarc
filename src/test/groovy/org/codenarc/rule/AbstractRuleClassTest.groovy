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
import org.codenarc.source.SourceCode

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

    void testName() {
        rule.name = 'abc'
        assert rule.getName() == 'abc'
    }

    void testPriority() {
        rule.priority = 1
        assert rule.getPriority() == 1
    }

    void testIsReady_DefaultsToTrue() {
        assert rule.ready == true
    }

    void testIsReady() {
        rule = new NotReadyRule()
        assert rule.isReady() == false
        assertNoViolations(SOURCE)
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

    void testApplyToFilenames_Wildcards() {
        rule.applyToFilenames = 'My*.groovy'
        assertSingleViolation(SOURCE)
        rule.applyToFilenames = "MyTest??.groovy"
        assertNoViolations(SOURCE)
    }

    void testDoNotApplyToFilenames() {
        rule.doNotApplyToFilenames = "Xxx.groovy"
        assertSingleViolation(SOURCE)
        rule.doNotApplyToFilenames = FILENAME
        assertNoViolations(SOURCE)
    }

    void testDoNotApplyToFilenames_Wildcards() {
        rule.doNotApplyToFilenames = "MyTest??.groovy"
        assertSingleViolation(SOURCE)
        rule.doNotApplyToFilenames = 'My*.gr*'
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

    void testApplyTo_ViolationMessageIsNotSet() {
        def violations = applyRuleTo(SOURCE)
        assert violations[0].message == FILENAME
    }

    void testApplyTo_ViolationMessageIsSetToEmpty() {
        rule.violationMessage = ''
        def violations = applyRuleTo(SOURCE)
        assert violations[0].message == FILENAME
    }

    void testApplyTo_ViolationMessageIsSet() {
        rule.violationMessage = 'abc'
        rule.numberOfViolations = 2
        def violations = applyRuleTo(SOURCE)
        assert violations[0].message == 'abc'
        assert violations[1].message == 'abc'
    }

    void testApplyTo_Error() {
        rule = new ExceptionRule(new Exception('abc'))
        shouldFailWithMessageContaining('abc') { applyRuleTo(SOURCE) }
    }

    void testSourceLineAndNumberForImport() {
        final SOURCE = '''
            import a.b.MyClass
            import a.b.MyClass as Boo
            // some comment
            import a.pkg1.MyOtherClass as MOC
        '''
        def sourceCode = new SourceString(SOURCE)
        def ast = sourceCode.ast
        println ast.imports.collect { it.alias }
        assert rule.sourceLineAndNumberForImport(sourceCode, ast.imports[0]) == [sourceLine:'import a.b.MyClass', lineNumber:2]
        assert rule.sourceLineAndNumberForImport(sourceCode, ast.imports[1]) == [sourceLine:'import a.b.MyClass as Boo', lineNumber:3]
        assert rule.sourceLineAndNumberForImport(sourceCode, ast.imports[2]) == [sourceLine:'import a.pkg1.MyOtherClass as MOC', lineNumber:5]

        // Not found
        def otherSourceCode = new SourceString('def v = 1')
        assert rule.sourceLineAndNumberForImport(otherSourceCode, ast.imports[0]) == [sourceLine:'import a.b.MyClass as MyClass', lineNumber:null]
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

class NotReadyRule extends TestPathRule {
    boolean isReady() {
        return false
    }
}

class ExceptionRule extends AbstractRule {
    String name = 'Exception'
    int priority = 1
    Throwable throwable

    ExceptionRule(Throwable throwable) {
        this.throwable = throwable
    }

    void applyTo(SourceCode sourceCode, List violations) {
        throw throwable
    }
}