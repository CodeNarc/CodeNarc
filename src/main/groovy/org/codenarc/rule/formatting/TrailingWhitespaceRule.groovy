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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Checks that no lines of source code end with whitespace characters.
 *
 * @author Joe Sondow
 * @author Chris Mair
 */
class TrailingWhitespaceRule extends AbstractRule {

    String name = 'TrailingWhitespace'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        sourceCode.lines.eachWithIndex { line, index ->
            if (line && line[-1].isAllWhitespace()) {
                violations.add(createViolation(index + 1, line, 'Line ends with whitespace characters'))
            }
        }
    }
}

