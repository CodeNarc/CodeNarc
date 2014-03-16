/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.generic

import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Checks for a specified illegal regular expression within the source code.
 * <p/>
 * The <code>regex</code> property specifies the regular expression to check for. If null or empty, do nothing.
 * <p/>
 * A RuleSet can contain any number of instances of this rule, but each should be configured
 * with a unique rule name, regex, violationMessage and (optionally) customized priority.
 *
 * @author Chris Mair
  */
class IllegalRegexRule extends AbstractRule {
    String name = 'IllegalRegex'
    int priority = 3
    String regex

    boolean isReady() {
        regex
    }

    void applyTo(SourceCode sourceCode, List violations) {
        def matcher = sourceCode.getText() =~ regex
        while (matcher.find()) {
            def lineNumber = sourceCode.getLineNumberForCharacterIndex(matcher.start())
            violations.add(new Violation(rule:this, lineNumber:lineNumber, sourceLine:matcher.group(),
                    message:"Match found for illegal regular expression \"$regex\""))
        }
    }

}
