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
package org.codenarc.plugin

import org.codenarc.report.ReportWriter
import org.codenarc.rule.Rule

/**
 * Abstract superclass for CodeNarcPlugin implementations. Provides null (do-nothing) implementations for each method.
 * This class is subject to change.
 */
@SuppressWarnings(['EmptyMethodInAbstractClass', 'AbstractClassWithoutAbstractMethod'])
abstract class AbstractCodeNarcPlugin implements CodeNarcPlugin {

    @Override
    void initialize() {
        // Do nothing
    }

    @Override
    void processRules(List<Rule> rules) {
        // Do nothing
    }

    @Override
    void processViolationsForFile(FileViolations fileViolations) {
        // Do nothing
    }

    @Override
    void processReports(List<ReportWriter> reportWriters) {
        // Do nothing
    }

}
