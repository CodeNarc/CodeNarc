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
package org.codenarc.source

import org.codenarc.test.AbstractTest

/**
 * Tests for SourceCodeUtil
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SourceCodeUtilTest extends AbstractTest {
    static final NAME = 'MyTest.groovy'
    static final PATH = "src/$NAME"
    static final MATCH = /.*Test\.groovy/
    static final NO_MATCH = /.*Other\.groovy/
    static final OTHER_NAME = 'OtherClass.groovy'
    static final ANYTHING = 'abc'
    private sourceCode

    void testShouldApplyTo_NullPathAndName() {
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [:])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilesMatching:ANYTHING])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilesMatching:ANYTHING])
    }

    void testShouldApplyTo_Path() {
        sourceCode.path = PATH
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [:])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilesMatching:MATCH])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilesMatching:NO_MATCH])

        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilesMatching:NO_MATCH])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilesMatching:MATCH, doNotApplyToFilesMatching:MATCH])
    }

    void testShouldApplyTo_Name() {
        sourceCode.name = NAME
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [:])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:NAME])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilenames:OTHER_NAME])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:"$OTHER_NAME,$NAME"])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilenames:"File2.groovy,$OTHER_NAME"])

        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:OTHER_NAME])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilenames:NAME])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:NAME, doNotApplyToFilenames:NAME])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilenames:"$OTHER_NAME,$NAME"])
    }

    void testShouldApplyTo_Name_Wildcards() {
        sourceCode.name = NAME
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:'*.groovy'])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:'MyT?st.groovy'])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilenames:'*.ruby'])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:"$OTHER_NAME,My*.groovy"])

        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:'*View.groovy'])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilenames:'My*.groovy'])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:'My*.groovy', doNotApplyToFilenames:'MyT?st.groovy'])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilenames:"$OTHER_NAME,My*.groovy"])
    }

    void testShouldApplyTo_NameAndPath() {
        sourceCode.name = NAME
        sourceCode.path = PATH
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [:])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:NAME, applyToFilesMatching:MATCH])
        assert SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilesMatching:NO_MATCH, doNotApplyToFilenames:OTHER_NAME])

        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:OTHER_NAME, applyToFilesMatching:NO_MATCH])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [doNotApplyToFilenames:NAME, applyToFilesMatching:MATCH])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilesMatching:MATCH, applyToFilenames:NAME, doNotApplyToFilenames:"Xyz.groovy,$NAME"])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:NAME, doNotApplyToFilesMatching:MATCH])
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, [applyToFilenames:NAME, doNotApplyToFilesMatching:MATCH])
    }

    void setUp() {
        super.setUp()
        sourceCode = new SourceString("class ABC { }")
    }
}