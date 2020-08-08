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
 * Checks for duplication of constant number literal values.
 * <p/>
 * Set the optional <code>ignoreNumbers</code> property to a comma-separated list (String) of
 * the numbers that should be ignored by this rule (i.e., not cause a violation). This property
 * defaults to "0,1" to ignore the constants zero and one.

 * Set the optional <code>duplicateNumberMinimumValue</code> property to an integer so this rule
 * will ignore literal numbers lower than this parameter (i.e., not cause a violation). There is
 * no default value for this property
 *
 * By default, this rule does not apply to test files.
 *
 * @author Chris Mair
 * @author Nicolas Vuillamy
 */
class DuplicateNumberLiteralRule extends AbstractAstVisitorRule {

    private static final List NUMBER_TYPES = [Number,
            Byte.TYPE,
            Double.TYPE,
            Float.TYPE,
            Integer.TYPE,
            Long.TYPE,
            Short.TYPE]

    String name = 'DuplicateNumberLiteral'
    int priority = 2
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
    String ignoreNumbers = '0,1'
    Integer duplicateNumberMinimumValue

    @Override
    AstVisitor getAstVisitor() {
        def ignoreValuesSet = parseIgnoreValues()
        def additionalChecksClosure = defineAdditionalChecksClosure()
        new DuplicateLiteralAstVisitor(NUMBER_TYPES, ignoreValuesSet, additionalChecksClosure)
    }

    private Set parseIgnoreValues() {
        def strings = ignoreNumbers ? ignoreNumbers.tokenize(',') : []
        def numbers = strings*.trim()
        numbers as Set
    }

    // Define a compare closure if duplicateNumberMinimumValue is defined
    private Closure defineAdditionalChecksClosure() {
        if (duplicateNumberMinimumValue || duplicateNumberMinimumValue == 0) {
            return { node ->
                Integer.valueOf(node.value) >= Integer.valueOf(duplicateNumberMinimumValue)
            }
        }
        null
    }
}
