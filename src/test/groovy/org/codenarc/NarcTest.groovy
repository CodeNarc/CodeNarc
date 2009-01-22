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
package org.codenarc

import org.codenarc.Narc
import org.codenarc.report.ReportWriter
import org.codenarc.rule.TestPathRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTest

/**
 * Tests for Narc.
 *
 * @author Chris Mair
 * @version $Revision: 193 $ - $Date: 2009-01-13 21:04:52 -0500 (Tue, 13 Jan 2009) $
 */
class NarcTest extends AbstractTest {
    static final SOURCE_DIR = 'src/test/resources/source' 
    def narc
    def reportResults
    def ruleSet

    void testRun_BaseDirectory() {
        narc.run()
        assert reportResults.numberOfFilesWithViolations == 2
    }

    void testRun_SourceDirectories() {
        narc.baseDirectory = null
        narc.sourceDirectories = [SOURCE_DIR]
        narc.run()
        assert reportResults.numberOfFilesWithViolations == 2
    }

    void testRun_BaseDirectoryAndSourceDirectories() {
        narc.baseDirectory = 'src/test/resources'
        narc.sourceDirectories = ['source', 'sourcewithdirs']
        narc.run()
        assert reportResults.numberOfFilesWithViolations == 7
    }

    void testRun_NullBaseDirectoryAndNullSourceDirectories() {
        narc.baseDirectory = ''
        narc.sourceDirectories = []
        shouldFailWithMessageContaining(['baseDirectory','sourceDirectories']) { narc.run() }
    }

    void testRun_EmptyBaseDirectoryAndEmptySourceDirectories() {
        narc.baseDirectory = null
        narc.sourceDirectories = null
        shouldFailWithMessageContaining(['baseDirectory','sourceDirectories']) { narc.run() }
    }

    void testRun_NullRuleSet() {
        narc.ruleSet = null
        shouldFailWithMessageContaining('ruleSet') { narc.run() }
    }

    void testRun_NullReportWriter() {
        narc.reportWriter = null
        shouldFailWithMessageContaining('reportWriter') { narc.run() }
    }

    void setUp() {
        super.setUp()
        ruleSet = new ListRuleSet([new TestPathRule()])
        narc = new Narc()
        narc.reportWriter = [writeOutReport:{ context, results -> reportResults = results}] as ReportWriter
        narc.baseDirectory = SOURCE_DIR
        narc.ruleSet = ruleSet
    }
}