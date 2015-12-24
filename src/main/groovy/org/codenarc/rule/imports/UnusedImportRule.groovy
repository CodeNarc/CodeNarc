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

import org.codehaus.groovy.ast.ImportNode
import org.codenarc.rule.AbstractRule
import org.codenarc.source.SourceCode
import org.codenarc.util.GroovyVersion

import java.util.regex.Pattern

/**
 * Rule that checks for an unreferenced import
 *
 * @author Chris Mair
 */
class UnusedImportRule extends AbstractRule {

    String name = 'UnusedImport'
    int priority = 3

    void applyTo(SourceCode sourceCode, List violations) {
        processImports(sourceCode, violations)
        processStaticImports(sourceCode, violations)
    }

    private void processImports(SourceCode sourceCode, List violations) {
        sourceCode.ast?.imports?.each { importNode ->
            if (!findReference(sourceCode, importNode.alias, importNode.className)) {
                violations.add(createViolationForImport(sourceCode, importNode, "The [${importNode.className}] import is never referenced"))
            }
        }
    }

    private void processStaticImports(SourceCode sourceCode, List violations) {
        if (GroovyVersion.isGroovy1_8_OrGreater()) {
            sourceCode.ast?.staticImports?.each { alias, ImportNode classNode ->
                if (!findReference(sourceCode, alias)) {
                    violations.add(createViolationForImport(sourceCode, classNode.className, alias, "The [${classNode.className}] import is never referenced"))
                }
            }
        } else {
            sourceCode.ast?.staticImportAliases?.each { alias, classNode ->
                if (!findReference(sourceCode, alias)) {
                    violations.add(createViolationForImport(sourceCode, classNode.name, alias, "The [${classNode.name}] import is never referenced"))
                }
            }
        }
    }

    private findReference(SourceCode sourceCode, String alias, String className = null) {
        def aliasSameAsNonQualifiedClassName = className && className.endsWith(alias)
        sourceCode.lines.find { line ->
            String aliasPattern = Pattern.quote(alias)
            if (!isImportStatementForAlias(line, aliasPattern)) {
                def aliasCount = countUsage(line, aliasPattern)
                return aliasSameAsNonQualifiedClassName ?
                        aliasCount && aliasCount > countUsage(line, Pattern.quote(className)) : aliasCount
            }
        }
    }

    private isImportStatementForAlias(String line, String pattern) {
        final IMPORT_PATTERN = /import\s+.*/ + pattern
        line =~ IMPORT_PATTERN
    }

    private countUsage(String line, String pattern) {
        // Valid class name contains the following chars a-zA-Z0-9_$
        // Define boundary as
        //      right char is valid for class name
        //      left char is not valid for class name
        // or vice versa.
        // Use boundary before and after class name.
        // Yep, \b is much shorter :)
        def regexp = /(?:(?=[a-zA-Z0-9_\$])(?<![a-zA-Z0-9_\$])|(?<=[a-zA-Z0-9_\$])(?![a-zA-Z0-9_\$]))/ +
                /${pattern}/ +
                /(?:(?=[a-zA-Z0-9_\$])(?<![a-zA-Z0-9_\$])|(?<=[a-zA-Z0-9_\$])(?![a-zA-Z0-9_\$]))/
        (line =~ regexp).count
    }
}
