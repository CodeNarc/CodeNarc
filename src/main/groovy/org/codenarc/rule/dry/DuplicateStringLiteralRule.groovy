/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.dry

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AstVisitor

/**
 * Code containing duplicate String literals can usually be improved by declaring the String as a constant field.
 * <p/>
 * Set the optional <code>ignoreStrings</code> property to a comma-separated list (String) of
 * the strings that should be ignored by this rule (i.e., not cause a violation). This property
 * defaults to "" to ignore empty strings.
 *
 * By default, this rule does not apply to test files.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class DuplicateStringLiteralRule extends AbstractAstVisitorRule {
    String name = 'DuplicateStringLiteral'
    int priority = 2
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
    String ignoreStrings = ''

    @Override
    AstVisitor getAstVisitor() {
        def ignoreValuesSet = parseIgnoreValues()
        new DuplicateLiteralAstVisitor(String, ignoreValuesSet)
    }

    private Set parseIgnoreValues() {
        if (ignoreStrings == null) {
            return Collections.EMPTY_SET
        }
        def strings = ignoreStrings.contains(',') ? ignoreStrings.split(',') : [ignoreStrings]
        strings as Set
    }
}
