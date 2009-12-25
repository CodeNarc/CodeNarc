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
package org.codenarc.ant

import org.codenarc.test.AbstractTestCase

/**
 * Tests for CodeNarcTask that use the Groovy AntBuilder.
 *
 * @author Chris Mair
 * @version $Revision: 30 $ - $Date: 2009-12-14 22:49:35 -0500 (Mon, 14 Dec 2009) $
 */
class CodeNarc_AntBuilderTest extends AbstractTestCase {

    private static final HTML = 'html'
    private static final REPORT_FILE = 'AntBuilderTestReport.html'
    private static final TITLE = 'Sample'
    private static final RULESET_FILES = [
            'rulesets/basic.xml',
            'rulesets/braces.xml'].join(',')

    void testAntTask_Execute_UsingAntBuilder() {
        def ant = new AntBuilder()

        ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')

        ant.codenarc(ruleSetFiles:RULESET_FILES) {
           fileset(dir:'src/main/groovy') {
               include(name:"**/*.groovy")
           }
           report(type:HTML, title:TITLE, toFile:REPORT_FILE)
//               option(name:'title', value:TITLE)
//               option(name:'outputFile', value:REPORT_FILE)
//           }
        }
        verifyReportFile()
    }

    private void verifyReportFile() {
        def file = new File(REPORT_FILE)
        assert file.exists()
        assertContainsAllInOrder(file.text, [TITLE, 'org/codenarc', 'Rule Descriptions'])
    }
}