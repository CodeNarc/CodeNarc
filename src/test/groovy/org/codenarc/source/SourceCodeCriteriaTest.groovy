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

import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

/**
 * Tests for SourceCodeUtil
 *
 * @author Chris Mair
  */
class SourceCodeCriteriaTest extends AbstractTestCase {

    private static final NAME = 'MyTest.groovy'
    private static final PATH = "src/$NAME"
    private static final MATCH = /.*Test\.groovy/
    private static final NO_MATCH = /.*Other\.groovy/
    private static final OTHER_NAME = 'OtherClass.groovy'
    private static final ANYTHING = 'abc'

    private sourceCode

    @Test
    void testMatches_NullPathAndName() {
        assert new SourceCodeCriteria().matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilesMatching:ANYTHING).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilesMatching:ANYTHING).matches(sourceCode)
    }

    @Test
    void testMatches_Path() {
        sourceCode.path = PATH
        assert new SourceCodeCriteria().matches(sourceCode)
        assert new SourceCodeCriteria(applyToFilesMatching:MATCH).matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilesMatching:NO_MATCH).matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFilesMatching:NO_MATCH).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilesMatching:MATCH, doNotApplyToFilesMatching:MATCH).matches(sourceCode)
    }

    @Test
    void testMatches_Name() {
        sourceCode.name = NAME
        assert new SourceCodeCriteria().matches(sourceCode)
        assert new SourceCodeCriteria(applyToFileNames:NAME).matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFileNames:OTHER_NAME).matches(sourceCode)
        assert new SourceCodeCriteria(applyToFileNames:"$OTHER_NAME,$NAME").matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFileNames:"File2.groovy,$OTHER_NAME").matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFileNames:OTHER_NAME).matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFileNames:NAME).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFileNames:NAME, doNotApplyToFileNames:NAME).matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFileNames:"$OTHER_NAME,$NAME").matches(sourceCode)
    }

    @Test
    void testMatches_Name_Wildcards() {
        sourceCode.name = NAME
        assert new SourceCodeCriteria(applyToFileNames:'*.groovy').matches(sourceCode)
        assert new SourceCodeCriteria(applyToFileNames:'MyT?st.groovy').matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFileNames:'*.ruby').matches(sourceCode)
        assert new SourceCodeCriteria(applyToFileNames:"$OTHER_NAME,My*.groovy").matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFileNames:'*View.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFileNames:'My*.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFileNames:'My*.groovy', doNotApplyToFileNames:'MyT?st.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFileNames:"$OTHER_NAME,My*.groovy").matches(sourceCode)
    }

    @Test
    void testMatches_NameAndPath() {
        sourceCode.name = NAME
        sourceCode.path = PATH
        assert new SourceCodeCriteria().matches(sourceCode)
        assert new SourceCodeCriteria(applyToFileNames:NAME, applyToFilesMatching:MATCH).matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFilesMatching:NO_MATCH, doNotApplyToFileNames:OTHER_NAME).matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFileNames:OTHER_NAME, applyToFilesMatching:NO_MATCH).matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFileNames:NAME, applyToFilesMatching:MATCH).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFilesMatching:MATCH, applyToFileNames:NAME, doNotApplyToFileNames:"Xyz.groovy,$NAME").matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFileNames:NAME, doNotApplyToFilesMatching:MATCH).matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFileNames:NAME, doNotApplyToFilesMatching:MATCH).matches(sourceCode)
    }

    @Test
    void testMatches_NameAndPath_ApplyToFileNamesSpecifiesPath() {
        sourceCode.name = NAME
        sourceCode.path = PATH
        assert new SourceCodeCriteria(applyToFileNames:'**/*.groovy').matches(sourceCode)
        assert new SourceCodeCriteria(applyToFileNames:'src/MyT?st.groovy').matches(sourceCode)
        assert new SourceCodeCriteria(applyToFileNames:'*/MyT?st.groovy').matches(sourceCode)
        assert new SourceCodeCriteria(doNotApplyToFileNames:'**/*.ruby').matches(sourceCode)
        assert new SourceCodeCriteria(applyToFileNames:"$OTHER_NAME,**/My*.groovy").matches(sourceCode)

        assert !new SourceCodeCriteria(applyToFileNames:'**/*View.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFileNames:'**/My*.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(applyToFileNames:'src/My*.groovy', doNotApplyToFileNames:'MyT?st.groovy').matches(sourceCode)
        assert !new SourceCodeCriteria(doNotApplyToFileNames:"$OTHER_NAME,src/**My*.groovy").matches(sourceCode)
    }

    @Before
    void setUpSourceCodeCriteriaTest() {
        sourceCode = new SourceString('class ABC { }')
    }
}
