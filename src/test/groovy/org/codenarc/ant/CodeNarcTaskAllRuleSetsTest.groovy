/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.ant

import org.apache.tools.ant.Project
import org.apache.tools.ant.types.FileSet
import org.codenarc.ruleset.RuleSets
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

/**
 * Run the CodeNarc Ant Task against a portion of the CodeNarc source using all predefined RuleSets.
 *
 * @author Chris Mair
 */
class CodeNarcTaskAllRuleSetsTest extends AbstractTestCase {

    private static final BASE_DIR = 'src'
    private static final RULESET_FILES = RuleSets.ALL_RULESET_FILES.join(',')
    private static final REPORT_FILE = 'target/CodeNarcTaskAllRuleSetsReport.html'

    private codeNarcTask
    private fileSet
    private outputFile

    @Test
    void testExecute_MultipleRuleSetFiles() {
        codeNarcTask.addFileset(fileSet)
        codeNarcTask.execute()
        assertReportFileExists()
    }

    @Before
    void setUpCodeNarcTaskAllRuleSetsTest() {

        def project = new Project(basedir:'.')
        fileSet = new FileSet(dir:new File(BASE_DIR), project:project)
        fileSet.setIncludes('main/groovy/org/codenarc/rule/basic/*.groovy')

        codeNarcTask = new CodeNarcTask(project:project)
        codeNarcTask.addConfiguredReport(new Report(type:'html', toFile:REPORT_FILE))
        codeNarcTask.ruleSetFiles = RULESET_FILES
        outputFile = new File(REPORT_FILE)
    }

    private void assertReportFileExists() {
        assert outputFile.exists()
    }

}
