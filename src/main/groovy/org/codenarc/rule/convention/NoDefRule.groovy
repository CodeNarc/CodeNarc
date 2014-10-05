/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

import java.util.regex.Pattern

/**
 * Def keyword is overused and should be replaced with specific type.
 * <p/>
 * The <code>excludeRegex</code> property optionally specifies regex
 * to find text which could occur immediately after def.
 *
 * @author Dominik Przybysz
 */
class NoDefRule extends AbstractRule {
    String name = 'NoDef'
    int priority = 3
    protected static final String MESSAGE = 'def should not be used'
    String excludeRegex

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        Pattern excludeFilter = excludeRegex ? ~/.*def\s+$excludeRegex.*/ : null
        sourceCode.lines.eachWithIndex {
            String line, int idx ->
                if (line.contains('def ') && (!excludeFilter || !(line ==~ excludeFilter))) {
                    violations << createViolation(idx + 1, line.trim(), MESSAGE)
                }
        }
    }
}
