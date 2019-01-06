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
 * Checks for empty @see tags within javadoc.
 *
 * @author Chris Mair
 */
class JavadocEmptySeeTagRule extends AbstractAstVisitorRule {

    private static final String REGEX =  JAVADOC_START + JAVADOC_ANY_LINES + RELUCTANT + group(JAVADOC_LINE_PREFIX + /\@see/) + OPTIONAL_WHITESPACE + NEW_LINE

    String name = 'JavadocEmptySeeTag'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        def matcher = sourceCode.getText() =~ REGEX
        while (matcher.find()) {
            String sourceLine = matcher.group(2).trim()
            int lineNumber = sourceCode.getLineNumberForCharacterIndex(matcher.end()) - 1
            violations.add(new Violation(rule:this, lineNumber:lineNumber, sourceLine:sourceLine,
                    message:'The javadoc @see tag is empty'))
        }
    }

}
