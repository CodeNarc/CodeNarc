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
class SourceCodeCriteriaTest extends AbstractTest {
    static final NAME = 'MyTest.groovy'
    static final PATH = "src/$NAME"
    static final MATCH = /.*Test\.groovy/
    static final NO_MATCH = /.*Other\.groovy/
    static final OTHER_NAME = 'OtherClass.groovy'
    static final ANYTHING = 'abc'
    private sourceCode

    void testMatches_NullPathAndName() {
        assert new SourceCodeCriteria().matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilesMatching:ANYTHING).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilesMatching:ANYTHING).matches(sourceCode)
    }

    void testMatches_Path() {
        sourceCode.path = PATH
        assert new SourceCodeCriteria().matches(sourceCode)
        assert new SourceCodeCriteria(applyToFilesMatching:MATCH).matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilesMatching:NO_MATCH).matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFilesMatching:NO_MATCH).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilesMatching:MATCH, doNotApplyToFilesMatching:MATCH).matches(sourceCode)
    }

    void testMatches_Name() {
        sourceCode.name = NAME
        assert new SourceCodeCriteria().matches(sourceCode)
        assert new SourceCodeCriteria(applyToFilenames:NAME).matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilenames:OTHER_NAME).matches(sourceCode)
        assert new SourceCodeCriteria(applyToFilenames:"$OTHER_NAME,$NAME").matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilenames:"File2.groovy,$OTHER_NAME").matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFilenames:OTHER_NAME).matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFilenames:NAME).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilenames:NAME, doNotApplyToFilenames:NAME).matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFilenames:"$OTHER_NAME,$NAME").matches(sourceCode)
    }

    void testMatches_Name_Wildcards() {
        sourceCode.name = NAME
        assert new SourceCodeCriteria(applyToFilenames:'*.groovy').matches(sourceCode)
        assert new SourceCodeCriteria(applyToFilenames:'MyT?st.groovy').matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilenames:'*.ruby').matches(sourceCode)
        assert new SourceCodeCriteria(applyToFilenames:"$OTHER_NAME,My*.groovy").matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFilenames:'*View.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFilenames:'My*.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilenames:'My*.groovy', doNotApplyToFilenames:'MyT?st.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFilenames:"$OTHER_NAME,My*.groovy").matches(sourceCode)
    }

    void testMatches_NameAndPath() {
        sourceCode.name = NAME
        sourceCode.path = PATH
        assert new SourceCodeCriteria().matches(sourceCode)
        assert new SourceCodeCriteria(applyToFilenames:NAME, applyToFilesMatching:MATCH).matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilesMatching:NO_MATCH, doNotApplyToFilenames:OTHER_NAME).matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFilenames:OTHER_NAME, applyToFilesMatching:NO_MATCH).matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFilenames:NAME, applyToFilesMatching:MATCH).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilesMatching:MATCH, applyToFilenames:NAME, doNotApplyToFilenames:"Xyz.groovy,$NAME").matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilenames:NAME, doNotApplyToFilesMatching:MATCH).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilenames:NAME, doNotApplyToFilesMatching:MATCH).matches(sourceCode)
    }

    void setUp() {
        super.setUp()
        sourceCode = new SourceString("class ABC { }")
    }
}