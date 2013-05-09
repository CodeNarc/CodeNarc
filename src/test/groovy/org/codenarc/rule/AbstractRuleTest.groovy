/*
 * Copyright 2009 the original author or authors.
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

import org.codehaus.groovy.control.Phases
import org.codenarc.source.SourceCode
import org.codenarc.source.SourceString
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.assertContainsAll
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for the AbstractRule class
 *
 * @author Chris Mair
 */
class AbstractRuleTest extends AbstractRuleTestCase {

    private static final NAME = 'Rule123'
    private static final PRIORITY = 2
    private static final SOURCE = 'class MyClass { }'
    private static final FILENAME = 'MyTest.groovy'
    private static final PATH = 'org/codenarc/MyTest.groovy'
    private static final MATCH = /.*Test\.groovy/
    private static final NO_MATCH = /.*Other\.groovy/

    static skipTestThatUnrelatedCodeHasNoViolations
    static skipTestThatInvalidCodeHasNoViolations

    @Test
    void testToString() {
        assertContainsAll(rule.toString(), ['FakePathRule', NAME, PRIORITY.toString()])
    }

    @Test
    void testName() {
        rule.name = 'abc'
        assert rule.getName() == 'abc'
    }

    @Test
    void testDescription() {
        assert rule.description == null
        rule.description = 'abc'
        assert rule.getDescription() == 'abc'
    }

    @Test
    void testPriority() {
        rule.priority = 1
        assert rule.getPriority() == 1
    }

    @Test
    void testIsReady_DefaultsToTrue() {
        assert rule.ready
    }

    @Test
    void testIsReady() {
        rule = new NotReadyRule()
        assert !rule.isReady()
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnabled() {
        assertSingleViolation(SOURCE)
        rule.enabled = false
        assertNoViolations(SOURCE)
    }       
    
    @Test
    void testValidatesAstCompilerPhase() {
        rule = new FakePathRule() {
            int compilerPhase = Phases.SEMANTIC_ANALYSIS
        }
        def source = new SourceString(SOURCE)
        assert source.astCompilerPhase == SourceCode.DEFAULT_COMPILER_PHASE
        shouldFailWithMessageContaining(IllegalArgumentException, 'SourceCode with AST compiler phase') {
            rule.applyTo(source)
        }
    }

    @Test
    void testApplyToFilesMatching() {
        rule.applyToFilesMatching = MATCH
        assertSingleViolation(SOURCE)
        rule.applyToFilesMatching = NO_MATCH
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoNotApplyToFilesMatching() {
        rule.doNotApplyToFilesMatching = NO_MATCH
        assertSingleViolation(SOURCE)
        rule.doNotApplyToFilesMatching = MATCH
        assertNoViolations(SOURCE)
    }

    @Test
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

    @Test
    void testApplyToFileNames_FilenameOnly() {
        rule.applyToFileNames = FILENAME
        assertSingleViolation(SOURCE)

        rule.applyToFileNames = 'Xxx.groovy'
        assertNoViolations(SOURCE)

        rule.applyToFileNames = 'My*.groovy'
        assertSingleViolation(SOURCE)

        rule.applyToFileNames = 'MyTest??.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyToFileNames_WithPath() {
        rule.applyToFileNames = 'org/codenarc/MyTest.groovy'
        assertSingleViolation(SOURCE)

        rule.applyToFileNames = '**/MyTest.groovy'
        assertSingleViolation(SOURCE)

        rule.applyToFileNames = '**/codenarc/MyTest.groovy'
        assertSingleViolation(SOURCE)

        rule.applyToFileNames = '**org/*/My*.groovy'
        assertSingleViolation(SOURCE)

        rule.applyToFileNames = 'other/**/MyTest.groovy'
        assertNoViolations(SOURCE)

        rule.applyToFileNames = 'org/codenarc/MyOtherTest.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoNotApplyToFileNames_FilenameOnly() {
        rule.doNotApplyToFileNames = 'Xxx.groovy'
        assertSingleViolation(SOURCE)

        rule.doNotApplyToFileNames = FILENAME
        assertNoViolations(SOURCE)

        rule.doNotApplyToFileNames = 'MyTest??.groovy'
        assertSingleViolation(SOURCE)

        rule.doNotApplyToFileNames = 'My*.gr*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoNotApplyToFileNames_WithPath() {
        rule.doNotApplyToFileNames = 'org/codenarc/Xxx.groovy'
        assertSingleViolation(SOURCE)

        rule.doNotApplyToFileNames = PATH
        assertNoViolations(SOURCE)

        rule.doNotApplyToFileNames = 'org/code*rc/MyT??t.groovy' 
        assertNoViolations(SOURCE)

        rule.doNotApplyToFileNames = '*/codenarc/MyOtherTest.groovy'
        assertSingleViolation(SOURCE)

        rule.doNotApplyToFileNames = '**/codenarc/My*.gr*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testBothApplyToFileNamesAndDoNotApplyToFileNames() {
        rule.applyToFileNames = FILENAME             // apply = YES
        rule.doNotApplyToFileNames = FILENAME        // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToFileNames = 'Xxx.groovy'         // apply = NO
        rule.doNotApplyToFileNames = FILENAME        // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToFileNames = FILENAME             // apply = YES
        rule.doNotApplyToFileNames = 'Xxx.groovy'    // doNotApply = NO
        assertSingleViolation(SOURCE)

        rule.applyToFileNames = 'Xxx.groovy'         // apply = NO
        rule.doNotApplyToFileNames = 'Xxx.groovy'    // doNotApply = NO
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyToFileNamesAndDoNotApplyToFilesMatching() {
        rule.applyToFileNames = FILENAME             // apply filename = YES
        rule.doNotApplyToFilesMatching = MATCH       // doNotApply regex = YES
        assertNoViolations(SOURCE)

        rule.applyToFileNames = 'Xxx.groovy'         // apply filename = NO
        rule.doNotApplyToFilesMatching = MATCH       // doNotApply regex = YES
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyToFilesMatchingAndDoNotApplyToFileNames() {
        rule.applyToFilesMatching = MATCH            // apply regex = YES
        rule.doNotApplyToFileNames = 'Xxx.groovy'    // doNotApply filename = NO
        assertSingleViolation(SOURCE)

        rule.applyToFilesMatching = NO_MATCH         // apply regex = NO
        rule.doNotApplyToFileNames = FILENAME        // doNotApply filename = YES
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ViolationMessageIsNotSet() {
        def violations = applyRuleTo(SOURCE)
        assert violations[0].message == PATH
    }

    @Test
    void testApplyTo_ViolationMessageIsSetToEmpty() {
        rule.violationMessage = ''
        def violations = applyRuleTo(SOURCE)
        assert violations[0].message == ''
    }

    @Test
    void testApplyTo_ViolationMessageIsSet() {
        rule.violationMessage = 'abc'
        rule.numberOfViolations = 2
        def violations = applyRuleTo(SOURCE)
        assert violations[0].message == 'abc'
        assert violations[1].message == 'abc'
    }

    @Test
    void testApplyTo_Error() {
        rule = new ExceptionRule(new Exception('abc'))
        shouldFailWithMessageContaining('abc') { applyRuleTo(SOURCE) }
    }

    @Test
    void testCreateViolation() {
        def v = rule.createViolation(23, 'src', 'msg')
        assert v.lineNumber == 23
        assert v.sourceLine == 'src'
        assert v.message == 'msg'
    }

    @Test
    void testCreateViolation_Defaults() {
        def v = rule.createViolation(99)
        assert v.lineNumber == 99
        assert v.sourceLine == null
        assert v.message == null
    }

    @Test
    void testCreateViolation_ASTNode() {
        final SOURCE = '''
            class MyClass {
                int count
            }
        '''
        def sourceCode = new SourceString(SOURCE)
        def classNode = sourceCode.ast.classes[0]
        def v = rule.createViolation(sourceCode, classNode)
        assert v.lineNumber == 2
        assert v.sourceLine == 'class MyClass {'
        assert v.message == null
    }

    //--------------------------------------------------------------------------
    // Setup and helper methods
    //--------------------------------------------------------------------------

    @Before
    void setUpAbstractRuleTest() {
        sourceCodePath = PATH
        sourceCodeName = FILENAME
    }

    protected Rule createRule() {
        new FakePathRule(name:NAME, priority:PRIORITY)
    }

}

class NotReadyRule extends FakePathRule {
    boolean isReady() {
        false
    }
}

class ExceptionRule extends AbstractRule {
    String name = 'Exception'
    int priority = 1
    Throwable throwable

    ExceptionRule(Throwable throwable) {
        this.throwable = throwable
    }

    @Override
    void applyTo(SourceCode sourceCode, List violations) {
        throw throwable
    }
}
