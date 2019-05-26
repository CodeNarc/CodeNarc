/*
 * Copyright 2019 the original author or authors.
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

import static org.codenarc.rule.comments.CommentsUtil.*

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Abstract superclass for rules that checks for missing descriptions on javadoc tags.
 *
 * @author Chris Mair
 */
abstract class AbstractJavadocMissingDescriptionRule extends AbstractAstVisitorRule {

    int priority = 3
    boolean allowMultiline = false

    private final String regex = buildRegex()

    protected abstract String getTag()

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        def matcher = sourceCode.getText() =~ regex
        while (matcher.find()) {
            String sourceLine = matcher.group(2).trim()
            int lineNumber = sourceCode.getLineNumberForCharacterIndex(matcher.end()) - 1

            boolean isTagContinuedOnNextLine = allowMultiline && hasTextOnNextLine(sourceCode, lineNumber)
            if (!isTagContinuedOnNextLine) {
                violations.add(new Violation(rule: this, lineNumber: lineNumber, sourceLine: sourceLine,
                        message: "The description for the javadoc ${getTag()} tag is missing"))
            }
        }
    }

    private String buildRegex() {
        return JAVADOC_START + JAVADOC_ANY_LINES + group(JAVADOC_LINE_PREFIX + getTag() + WHITESPACE + NON_WHITESPACE_CHARS) + OPTIONAL_WHITESPACE + NEW_LINE
    }

}
