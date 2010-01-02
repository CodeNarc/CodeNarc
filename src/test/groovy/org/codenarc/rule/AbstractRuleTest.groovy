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

import org.codenarc.source.SourceString
import org.codenarc.source.SourceCode

/**
 * Tests for the AbstractRule class
 *
 * @author Chris Mair
 * @version $Revision: 257 $ - $Date: 2009-12-25 17:07:22 -0500 (Fri, 25 Dec 2009) $
 */
class AbstractRuleTest extends AbstractRuleTestCase {
    private static final NAME = 'Rule123'
    private static final PRIORITY = 2
    private static final SOURCE = 'class MyClass { }'
    private static final FILENAME = 'MyTest.groovy'
    private static final MATCH = /.*Test\.groovy/
    private static final NO_MATCH = /.*Other\.groovy/
    def skipTestThatUnrelatedCodeHasNoViolations

    void testToString() {
        assertContainsAll(rule.toString(), ['TestPathRule', NAME, PRIORITY.toString()])
    }

    void testName() {
        rule.name = 'abc'
        assert rule.getName() == 'abc'
    }

    void testDescription() {
        assert rule.description == null
        rule.description = 'abc'
        assert rule.getDescription() == 'abc'
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

//--------
    void testApplyToFileNames() {
        rule.applyToFileNames = FILENAME
        assertSingleViolation(SOURCE)
        rule.applyToFileNames = "Xxx.groovy"
        assertNoViolations(SOURCE)
    }

    void testApplyToFileNames_Wildcards() {
        rule.applyToFileNames = 'My*.groovy'
        assertSingleViolation(SOURCE)
        rule.applyToFileNames = "MyTest??.groovy"
        assertNoViolations(SOURCE)
    }

    void testDoNotApplyToFileNames() {
        rule.doNotApplyToFileNames = "Xxx.groovy"
        assertSingleViolation(SOURCE)
        rule.doNotApplyToFileNames = FILENAME
        assertNoViolations(SOURCE)
    }

    void testDoNotApplyToFileNames_Wildcards() {
        rule.doNotApplyToFileNames = "MyTest??.groovy"
        assertSingleViolation(SOURCE)
        rule.doNotApplyToFileNames = 'My*.gr*'
        assertNoViolations(SOURCE)
    }

    void testBothApplyToFileNamesAndDoNotApplyToFileNames() {
        rule.applyToFileNames = FILENAME             // apply = YES
        rule.doNotApplyToFileNames = FILENAME        // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToFileNames = "Xxx.groovy"         // apply = NO
        rule.doNotApplyToFileNames = FILENAME        // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToFileNames = FILENAME             // apply = YES
        rule.doNotApplyToFileNames = "Xxx.groovy"    // doNotApply = NO
        assertSingleViolation(SOURCE)

        rule.applyToFileNames = "Xxx.groovy"         // apply = NO
        rule.doNotApplyToFileNames = "Xxx.groovy"    // doNotApply = NO
        assertNoViolations(SOURCE)
    }
//--------
    void testApplyToFileNamesAndDoNotApplyToRegex() {
        rule.applyToFileNames = FILENAME             // apply filename = YES
        rule.doNotApplyToFilesMatching = MATCH       // doNotApply regex = YES
        assertNoViolations(SOURCE)

        rule.applyToFileNames = "Xxx.groovy"         // apply filename = NO
        rule.doNotApplyToFilesMatching = MATCH       // doNotApply regex = YES
        assertNoViolations(SOURCE)
    }

    void testApplyToRegexAndDoNotApplyToFileNames() {
        rule.applyToFilesMatching = MATCH            // apply regex = YES
        rule.doNotApplyToFileNames = "Xxx.groovy"    // doNotApply filename = NO
        assertSingleViolation(SOURCE)

        rule.applyToFilesMatching = NO_MATCH         // apply regex = NO
        rule.doNotApplyToFileNames = FILENAME        // doNotApply filename = YES
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ViolationMessageIsNotSet() {
        def violations = applyRuleTo(SOURCE)
        assert violations[0].message == FILENAME
    }

    void testApplyTo_ViolationMessageIsSetToEmpty() {
        rule.violationMessage = ''
        def violations = applyRuleTo(SOURCE)
        assert violations[0].message == ''
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

    void testCreateViolation() {
        def v = rule.createViolation(23, "src", "msg")
        assert v.lineNumber == 23
        assert v.sourceLine == 'src'
        assert v.message == 'msg'
    }

    void testCreateViolation_Defaults() {
        def v = rule.createViolation(99)
        assert v.lineNumber == 99
        assert v.sourceLine == null
        assert v.message == null
    }

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

    void testSourceLineAndNumberForImport() {
        final SOURCE = '''
            import a.b.MyClass
            import a.b.MyClass as Boo
            // some comment
            import a.pkg1.MyOtherClass as MOC
        '''
        def sourceCode = new SourceString(SOURCE)
        def ast = sourceCode.ast
        assert rule.sourceLineAndNumberForImport(sourceCode, ast.imports[0]) == [sourceLine:'import a.b.MyClass', lineNumber:2]
        assert rule.sourceLineAndNumberForImport(sourceCode, ast.imports[1]) == [sourceLine:'import a.b.MyClass as Boo', lineNumber:3]
        assert rule.sourceLineAndNumberForImport(sourceCode, ast.imports[2]) == [sourceLine:'import a.pkg1.MyOtherClass as MOC', lineNumber:5]

        // Not found
        def otherSourceCode = new SourceString('def v = 1')
        assert rule.sourceLineAndNumberForImport(otherSourceCode, ast.imports[0]) == [sourceLine:'import a.b.MyClass as MyClass', lineNumber:null]
    }

    void testSourceLineAndNumberForImport_ClassNameAndAlias() {
        final SOURCE = '''
            import a.b.MyClass
            import a.b.MyClass as Boo
            // some comment
            import a.pkg1.MyOtherClass as MOC
        '''
        def sourceCode = new SourceString(SOURCE)
        assert rule.sourceLineAndNumberForImport(sourceCode, 'a.b.MyClass', 'MyClass') == [sourceLine:'import a.b.MyClass', lineNumber:2]
        assert rule.sourceLineAndNumberForImport(sourceCode, 'a.b.MyClass', 'Boo') == [sourceLine:'import a.b.MyClass as Boo', lineNumber:3]
        assert rule.sourceLineAndNumberForImport(sourceCode, 'a.pkg1.MyOtherClass', 'MOC') == [sourceLine:'import a.pkg1.MyOtherClass as MOC', lineNumber:5]

        // Not found
        def otherSourceCode = new SourceString('def v = 1')
        assert rule.sourceLineAndNumberForImport(otherSourceCode, 'a.b.MyClass', 'MyClass') == [sourceLine:'import a.b.MyClass as MyClass', lineNumber:null]
    }

    //--------------------------------------------------------------------------
    // Tests for deprecated properties/methods
    //--------------------------------------------------------------------------

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

    //--------------------------------------------------------------------------
    // Setup and helper methods
    //--------------------------------------------------------------------------

    void setUp() {
        super.setUp()
        sourceCodePath = FILENAME
        sourceCodeName = FILENAME
    }

    protected Rule createRule() {
        return new TestPathRule(name:NAME, priority:PRIORITY)
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