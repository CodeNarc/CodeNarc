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

/**
 * Manages a lookup table for disabled rules within a single source file
 *
 * @author Chris Mair
 */
class LookupTable {

    protected static final String ALL_RULES = '#ALL#'
    private static final String CODENARC_DISABLE = 'codenarc-disable'
    private static final String CODENARC_DISABLE_LINE = 'codenarc-disable-line'
    private static final String CODENARC_ENABLE = 'codenarc-enable'
    private static final Set<String> EMPTY = []

    private final String sourceText
    private final Map<Integer, Set<String>> disabledRulesByLine = [:]
    private final Set<String> currentlyDisabledRuleNames = []
    private final Set<String> thisLineDisabledRuleNames = []

    private boolean isDisablingAllRules = false

    LookupTable(String sourceText) {
        this.sourceText = sourceText
        buildLookupTable()
    }

    Set<String> disabledRuleNamesForLineNumber(Integer lineNumber) {
        return disabledRulesByLine[lineNumber] ?: EMPTY
    }

    private void buildLookupTable() {
        sourceText.eachLine { line, lineNumber0 ->
            int lineNumber = lineNumber0 + 1
            checkForCodeNarcDisable(line)
            checkForCodeNarcEnable(line)
            setDisabledRulesByLine(lineNumber)
        }
    }

    private void checkForCodeNarcDisable(String line) {
        thisLineDisabledRuleNames.clear()
        if (line.contains(CODENARC_DISABLE_LINE)) {
            def ruleNames = parseRuleNames(line, CODENARC_DISABLE_LINE)
            if (ruleNames) {
                thisLineDisabledRuleNames.addAll(ruleNames)
            } else {
                thisLineDisabledRuleNames << ALL_RULES
            }
        }
        else if (line.contains(CODENARC_DISABLE)) {
            def ruleNames = parseRuleNames(line, CODENARC_DISABLE)
            if (ruleNames) {
                currentlyDisabledRuleNames.addAll(ruleNames)
            } else {
                isDisablingAllRules = true
            }
        }
    }

    private void checkForCodeNarcEnable(String line) {
        if (line.contains(CODENARC_ENABLE)) {
            def ruleNames = parseRuleNames(line, CODENARC_ENABLE)
            if (ruleNames) {
                currentlyDisabledRuleNames.removeAll(ruleNames)
            } else {
                currentlyDisabledRuleNames.clear()
                isDisablingAllRules = false
            }
        }
    }

    private void setDisabledRulesByLine(int lineNumber) {
        if (isDisablingAllRules) {
            disabledRulesByLine[lineNumber] = [ALL_RULES]
        } else {
            def disabled = [] as Set
            disabled.addAll(thisLineDisabledRuleNames)
            disabled.addAll(currentlyDisabledRuleNames)
            disabledRulesByLine[lineNumber] = disabled
        }
    }

    protected static Set<String> parseRuleNames(String line, String codeNarcToken) {
        int index = line.indexOf(codeNarcToken)
        if (index == -1) {
            return EMPTY
        }
        int startIndex = index + codeNarcToken.length()
        String rawRestOfLine = line.substring(startIndex)
        String restOfLine = rawRestOfLine.trim().replaceAll(/\*\//, '')
        def names = restOfLine.tokenize(',')
        return names*.trim() as Set
    }

}
