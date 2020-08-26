/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.plugin.disablerules

import org.codenarc.AnalysisContext
import org.codenarc.report.ReportWriter
import org.codenarc.results.Results
import org.codenarc.rule.Violation
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Integration test for disabling rules in comments; uses the Groovy AntBuilder.
 *
 * @author Chris Mair
 */
class DisableRulesInComments_AntTest extends AbstractTestCase {

    private static final RULESET_FILE = 'disable-rules-in-comments/disabling-rules.ruleset'

    private static class DisableRulesTestReportWriter implements ReportWriter {
        static List<Violation> violations

        @SuppressWarnings('AssignmentToStaticFieldFromInstanceMethod')
        @Override
        void writeReport(AnalysisContext analysisContext, Results results) {
            violations = results.violations
        }
    }

    @Test
    void test_Execute_UsingAntBuilder() {
        def ant = new AntBuilder()

        ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')

        ant.codenarc(ruleSetFiles:RULESET_FILE) {
            fileset(dir:'src/test/resources/disable-rules-in-comments') {
                include(name:'**/*.groovy')
            }
            report(type:DisableRulesTestReportWriter.name) {
            }
            report(type:'ide') {
            }
        }
        log("violations=${DisableRulesTestReportWriter.violations}")
        assert DisableRulesTestReportWriter.violations.empty
    }

}
