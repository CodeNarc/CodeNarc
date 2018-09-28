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
package org.codenarc.rule.imports

import org.codehaus.groovy.ast.ImportNode
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Checks for wildcard (star) imports. If the ignoreStaticImports property is true, then do not check static imports.
 *
 * @author Kyle Boon
 * @author Chris Mair
 */
class NoWildcardImportsRule extends AbstractImportRule {

    private static final String MESSAGE = 'Wildcard (star) import'

    String name = 'NoWildcardImports'
    int priority = 3
    boolean ignoreStaticImports = false
    boolean ignoreImports = false

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        if (!ignoreImports) {
            sourceCode.ast?.starImports.each { ImportNode importNode ->
                violations.add(createViolationForImport(sourceCode, importNode, MESSAGE))
            }
        }

        if (!ignoreStaticImports) {
            sourceCode.ast?.staticStarImports.each { String alias, ImportNode importNode ->
                violations.add(createViolationForImport(sourceCode, importNode, MESSAGE))
            }
        }
    }

}
