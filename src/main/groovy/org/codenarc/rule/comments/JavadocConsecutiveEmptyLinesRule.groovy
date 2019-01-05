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
 * Checks for javadoc comments with more than one consecutive empty line
 *
 * @author Chris Mair
 */
class JavadocConsecutiveEmptyLinesRule extends AbstractAstVisitorRule {

    private static final String REGEX = JAVADOC_START + JAVADOC_ANY_LINES + RELUCTANT + JAVADOC_EMPTY_LINE + JAVADOC_EMPTY_LINE

    String name = 'JavadocConsecutiveEmptyLines'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        def matcher = sourceCode.getText() =~ REGEX
        while (matcher.find()) {
            int lineNumber = sourceCode.getLineNumberForCharacterIndex(matcher.end()) - 1
            violations.add(new Violation(rule:this, lineNumber:lineNumber, sourceLine:' * ',
                    message:'The javadoc contains consecutive empty lines'))
        }
    }

}
