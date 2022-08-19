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

import static org.codenarc.test.TestUtil.*

import groovy.ant.AntBuilder
import org.apache.tools.ant.BuildException
import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for CodeNarcTask that use the Groovy AntBuilder to test source files with compile errors
 *
 * @author Chris Mair
 */
class CodeNarc_CompileErrorsTest extends AbstractTestCase {

    private static final String TITLE = 'Sample Project'
    private static final String RULESET_FILES = 'rulesets/basic.xml'

    private AntBuilder ant

    @BeforeEach
    void setUp() {
        ant = new AntBuilder()
        ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')
    }

    @Test
    void testAntTask_Execute_SourceFilesWithCompileErrors() {
        ant.codenarc(ruleSetFiles:RULESET_FILES) {
            fileset(dir:'src/test/resources/sourcewitherrors') {
                include(name:'**/*.txt')
            }
            report(type:'ide') {
                option(name:'title', value:TITLE)
            }
        }
        assertNoErrors()
    }

    @Test
    void testAntTask_Execute_SourceFilesWithCompileErrors_failOnError_true() {
        def msg = shouldFail(BuildException) {
            ant.codenarc(ruleSetFiles: RULESET_FILES, failOnError: true) {
                fileset(dir: 'src/test/resources/sourcewitherrors') {
                    include(name: '**/*.txt')
                }
                report(type: 'ide') {
                    option(name: 'title', value: TITLE)
                }
            }
        }
        log(msg)
    }

}
