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

import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Check for javadoc comments with an empty top line
 *
 * @author Chris Mair
 */
class JavadocEmptyFirstLineRule extends AbstractRule {

    private static final String REGEX = /\/\*\*\s*\n\s*\*\s*\n/

    String name = 'JavadocEmptyFirstLine'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        def matcher = sourceCode.getText() =~ REGEX
        while (matcher.find()) {
            int lineNumber = sourceCode.getLineNumberForCharacterIndex(matcher.start())
            violations.add(new Violation(rule:this, lineNumber:lineNumber, sourceLine:'/** ..',
                    message:'The first line of the javadoc is empty'))
        }
    }

}
