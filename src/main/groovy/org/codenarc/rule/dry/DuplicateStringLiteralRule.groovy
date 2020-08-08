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

import java.util.regex.Pattern

/**
 * Code containing duplicate String literals can usually be improved by declaring the String as a constant field.
 * <p>
 * Set the optional <code>ignoreStrings</code> property to a comma-separated list (String) of
 * the strings that should be ignored by this rule (i.e., not cause a violation). This property
 * defaults to "" to ignore empty strings.
 * <p>
 * You can customize the delimiter for the <code>ignoreStrings</code> by setting the <code>ignoreStringsDelimiter</code>, which defaults to ",".
 * <p>
 *
 * Set the optional <code>duplicateStringMinimumLength</code> property to an integer so this rule
 * will ignore literal strings whose length is lower than this parameter (i.e., not cause a violation).
 * There is no default value for this property

 * By default, this rule does not apply to test files.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 * @author Nicolas Vuillamy
 */
class DuplicateStringLiteralRule extends AbstractAstVisitorRule {

    String name = 'DuplicateStringLiteral'
    int priority = 2
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
    String ignoreStrings = ''
    char ignoreStringsDelimiter = ','
    Integer duplicateStringMinimumLength

    @Override
    AstVisitor getAstVisitor() {
        def ignoreValuesSet = parseIgnoreValues()
        def additionalChecksClosure = defineAdditionalChecksClosure()
        new DuplicateLiteralAstVisitor(String, ignoreValuesSet, additionalChecksClosure)
    }

    private Set parseIgnoreValues() {
        if (ignoreStrings == null) {
            return Collections.EMPTY_SET
        }
        String delimiter = ignoreStringsDelimiter as String
        def strings = ignoreStrings.contains(delimiter) ? ignoreStrings.split(Pattern.quote(delimiter)) : [ignoreStrings]
        return strings as Set
    }

    // Define a compare closure if duplicateNumberMinimumValue is sent
    private Closure defineAdditionalChecksClosure() {
        if (duplicateStringMinimumLength || duplicateStringMinimumLength == 0) {
            return { node -> node.value.size() >= duplicateStringMinimumLength }
        }
        null
    }
}
