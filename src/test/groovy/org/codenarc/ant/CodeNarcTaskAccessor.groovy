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

import org.apache.tools.ant.types.FileSet

/**
 * Test-only class for accessing protected members of CodeNarcTask from test classes in other packages
 *
 * @author Chris Mair
 */
class CodeNarcTaskAccessor {

    private final CodeNarcTask codeNarcTask

    CodeNarcTaskAccessor(CodeNarcTask codeNarcTask) {
        assert codeNarcTask
        this.codeNarcTask = codeNarcTask
    }

    String getRuleSetFiles() {
        codeNarcTask.ruleSetFiles
    }

    List getReportWriters() {
        codeNarcTask.reportWriters
    }

    FileSet getFileSet() {
        codeNarcTask.fileSet
    }

    def getRuleSet() {
        codeNarcTask.ruleSet
    }

}
