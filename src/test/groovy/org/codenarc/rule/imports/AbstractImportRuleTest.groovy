/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.rule.imports

import org.codenarc.rule.AbstractRuleTest

/**
 * Abstract superclass for tests of import-related Rule classes
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractImportRuleTest extends AbstractRuleTest {

    /**
     * Apply the current Rule to the specified source (String) and assert that it results
     * in one violation for each of the class names in the specified importedClasses List.
     * @param source - the full source code to which the rule is applied, as a String
     * @param importedClasses - the List of class names (including package), one for each expected violation
     */
    protected void assertImportViolations(String source, List importedClasses) {
        def violations = applyRuleTo(source)
        assert violations.size() == importedClasses.size(), "${violations.size()} violations"
        importedClasses.eachWithIndex { importedClass, index ->
            assert violations[index], "No violation for index $index"
            assertViolation(violations[index], null, importedClass)
        }
    }

}