/*
 * Copyright 2011 the original author or authors.
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
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.captureSystemOut

/**
 * Run the CodeNarc Ant Task against a portion of the CodeNarc source using a custom, predefined RuleSet.
 *
 * @author Chris Mair
 */
class CodeNarcTask_CustomRuleSetTest extends AbstractTestCase {

    private static final BASE_DIR = 'src'
    private static final RULESET_FILES = 'rulesets/CustomRuleSet.groovy'

    private codeNarcTask
    private fileSet

    @Test
    void testExecute_UseCustomRuleSet() {
        codeNarcTask.addFileset(fileSet)
        def output = captureSystemOut {
            codeNarcTask.execute()
        }
        log("output: $output")
        assert output.contains('CyclomaticComplexity')
    }

    @Before
    void setUpCodeNarcTask_CustomRuleSetTest() {

        def project = new Project(basedir:'.')
        fileSet = new FileSet(dir:new File(BASE_DIR), project:project)
        fileSet.setIncludes('main/groovy/org/codenarc/rule/dry/*.groovy')

        codeNarcTask = new CodeNarcTask(project:project)
        codeNarcTask.addConfiguredReport(new Report(type:'console'))
        codeNarcTask.ruleSetFiles = RULESET_FILES
    }

}
