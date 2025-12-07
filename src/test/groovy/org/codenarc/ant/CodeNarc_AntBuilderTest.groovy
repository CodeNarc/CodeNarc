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

import static org.codenarc.test.TestUtil.assertContainsAllInOrder
import static org.codenarc.test.TestUtil.assertContainsAll

import groovy.ant.AntBuilder
import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for CodeNarcTask that use the Groovy AntBuilder.
 *
 * @author Chris Mair
 */
class CodeNarc_AntBuilderTest extends AbstractTestCase {

    private static final TITLE = 'Sample Project'
    private static final REPORTS = [
        html: [
            options: [
                title: TITLE,
                outputFile: 'target/AntBuilderTestHtmlReport.html',
            ],
            verify: { reportFile ->
                assertContainsAllInOrder(reportFile.text, [TITLE, 'io', 'Rule Descriptions'])
            },
        ],
        sortable: [
            options: [
                title: TITLE,
                outputFile: 'target/AntBuilderTestSortableHtmlReport.html',
            ],
            verify: { reportFile ->
                assertContainsAllInOrder(reportFile.text, [TITLE, 'io', 'Rule Descriptions'])
            },
        ],
        xml: [
            options: [
                title: TITLE,
                outputFile: 'target/AntBuilderTestXmlReport.xml',
            ],
            verify: { reportFile ->
                assertContainsAllInOrder(reportFile.text, ['<?xml version', TITLE, 'io', '<Rules>'])
            },
        ],
        json: [
            options: [
                title: TITLE,
                outputFile: 'target/AntBuilderTestJsonReport.json',
            ],
            verify: { reportFile ->
                assertContainsAll(reportFile.text, ['"codeNarc": {', '"report": {', '"project": {', TITLE, '"summary": {', '"packages": [', '"rules": ['])
            },
        ],
        text: [
            options: [
                title: TITLE,
                outputFile: 'target/AntBuilderTestTextReport.txt',
            ],
            verify: { reportFile ->
                assertContainsAllInOrder(reportFile.text, ['CodeNarc Report', TITLE, 'codenarc.org'])
            },
        ],
        sarif: [
            options: [
                outputFile: 'target/AntBuilderTestSarifReport.json',
            ],
            verify: { reportFile ->
                assertContainsAll(reportFile.text, ['"$schema": "https://json.schemastore.org/sarif-', '"runs": [', '"tool": {', '"informationUri": "https://codenarc.org"', '"results": ['])
            },
        ],
    ]
    private static final RULESET_FILES = [
            'rulesets/basic.xml',
            'rulesets/groovyism.xml',
            'rulesets/imports.xml',
            'rulesets/size.xml',
            'rulesets/unnecessary.xml',
            ].join(',')

    @Test
    void testAntTask_Execute_UsingAntBuilder() {
        def ant = new AntBuilder()

        ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')

        ant.codenarc(ruleSetFiles:RULESET_FILES) {
            fileset(dir:'src/test/groovy/org/codenarc/rule') {
                include(name:'**/*.groovy')
            }
            addReports(delegate)
        }
        REPORTS.each { type, config ->
            def reportFile = new File(config.options.outputFile)
            assert reportFile.exists()
            if (config.verify) {
                config.verify.call(reportFile)
            }
        }
    }

    private void addReports(AntBuilder builder) {
        REPORTS.each { type, config ->
            builder.report(type: type) {
                config.options.each { name, value ->
                    option(name: name, value: value)
                }
            }
        }
    }

}
