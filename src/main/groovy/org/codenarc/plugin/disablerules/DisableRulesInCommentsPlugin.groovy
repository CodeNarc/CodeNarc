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

import org.codenarc.plugin.AbstractCodeNarcPlugin
import org.codenarc.plugin.FileViolations
import org.codenarc.rule.Violation

/**
 * Plugin that enables enablement/disablement of rules (and removing their violations) using comments within the source code
 *
 * @author Chris Mair
 */
class DisableRulesInCommentsPlugin extends AbstractCodeNarcPlugin {

    /**
     * Remove any violations for rules disabled by comments within the source code.
     * @param fileViolations - the FileViolations representing a single source file and its violations
     */
    @Override
    void processViolationsForFile(FileViolations fileViolations) {
        LookupTable lookupTable = new LookupTable(fileViolations.sourceText)

        def disabledViolations = fileViolations.violations.findAll { v -> isViolationDisabled(lookupTable, v) }
        fileViolations.violations.removeAll(disabledViolations)
    }

    private boolean isViolationDisabled(LookupTable lookupTable, Violation violation) {
        def disabledRuleNames = lookupTable.disabledRuleNamesForLineNumber(violation.lineNumber)
        return disabledRuleNames.contains(LookupTable.ALL_RULES) || disabledRuleNames.contains(violation.rule.name)
    }

}
