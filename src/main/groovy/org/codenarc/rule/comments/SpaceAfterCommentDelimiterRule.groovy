/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.comments

import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Checks that there is whitespace after comment characters: // and /*
 *
 * @author Chris Mair
 */
class SpaceAfterCommentDelimiterRule extends AbstractRule {

    private static final String SLASH_SLASH = /[^\:\$]\/\/\w/
    private static final String SLASH_STAR = /\/\*\w/
    private static final String SLASH_STAR_STAR = /[^\/]\/\*\*\w/
    private static final String REGEX = SLASH_SLASH + '|' + SLASH_STAR + '|' + SLASH_STAR_STAR
    private static final String MESSAGE = 'The comment does not begin with space or whitespace'

    String name = 'SpaceAfterCommentDelimiter'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        def matcher = sourceCode.getText() =~ REGEX
        while (matcher.find()) {
            int lineNumber = sourceCode.getLineNumberForCharacterIndex(matcher.end())
            String sourceLine = sourceCode.line(lineNumber - 1)

            // Heuristic to avoid false positives when a string contains single quote (') or double quote (")
            if (!sourceLine.contains("'") && !sourceLine.contains('"')) {
                violations.add(new Violation(rule: this, lineNumber: lineNumber, sourceLine: sourceLine, message: MESSAGE))
            }
        }
    }
}
