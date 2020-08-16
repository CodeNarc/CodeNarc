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
 * @author Chris Mair
 */
class DisableRulesInCommentsPlugin extends AbstractCodeNarcPlugin {

    private static final String CODENARC_DISABLE = 'codenarc-disable'
    private static final String CODENARC_ENABLE = 'codenarc-enable'
    private static final String ALL_RULES = '#ALL#'
    private static final Set<String> EMPTY = []

    private static class Data {
        private final FileViolations fileViolations
        private final Map<Integer, Set<String>> disabledRulesByLine

        Data(FileViolations fileViolations) {
            this.fileViolations = fileViolations
            this.disabledRulesByLine = [:]
        }
    }

    /**
     * Remove any violations for rules disabled by comments within the source code.
     * @param fileViolations - the FileViolations representing a single source file and its violations
     */
    @Override
    void processViolationsForFile(FileViolations fileViolations) {
        Data data = new Data(fileViolations)
        buildLookupTable(data)

        def disabledViolations = fileViolations.violations.findAll { v -> isViolationDisabled(data, v) }
        fileViolations.violations.removeAll(disabledViolations)
    }

    protected static Set<String> parseRuleNames(String line, String codeNarcToken) {
        int index = line.indexOf(codeNarcToken)
        if (index == -1) {
            return EMPTY
        }
        int startIndex = index + codeNarcToken.length()
        String restOfLine = line.substring(startIndex)
        restOfLine = restOfLine.replaceAll(/\*\//, '')
        def names = restOfLine.tokenize(',')
        return names*.trim() as Set
    }

    private boolean isViolationDisabled(Data data, Violation violation) {
        def disabledRuleNames = data.disabledRulesByLine[violation.lineNumber] ?: EMPTY
        return disabledRuleNames.contains(ALL_RULES) || disabledRuleNames.contains(violation.rule.name)
    }

    private void buildLookupTable(Data data) {
        String sourceText = data.fileViolations.sourceText
        boolean isDisablingAllRules = false
        Set<String> currentlyDisabledRuleNames = []
        sourceText.eachLine { line, lineNumber0 ->
            int lineNumber = lineNumber0 + 1
            if (line.contains(CODENARC_DISABLE)) {
                def ruleNames = parseRuleNames(line, CODENARC_DISABLE)
                if (ruleNames) {
                    currentlyDisabledRuleNames.addAll(ruleNames)
                }
                else {
                    isDisablingAllRules = true
                }
            }
            if (line.contains(CODENARC_ENABLE)) {
                def ruleNames = parseRuleNames(line, CODENARC_ENABLE)
                if (ruleNames) {
                    currentlyDisabledRuleNames.removeAll(ruleNames)
                }
                else {
                    currentlyDisabledRuleNames.clear()
                    isDisablingAllRules = false
                }
            }
            if (isDisablingAllRules) {
                data.disabledRulesByLine[lineNumber] = [ALL_RULES]
            }
            else {
                if (currentlyDisabledRuleNames) {
                    data.disabledRulesByLine[lineNumber] = currentlyDisabledRuleNames.clone()
                }
            }
        }
    }

}
