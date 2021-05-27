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
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil

import java.util.regex.Pattern

/**
 * Rule that checks for an unreferenced import
 *
 * @author Chris Mair
 */
class UnusedImportRule extends AbstractRule {

    String name = 'UnusedImport'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        processImports(sourceCode, violations)
        processStaticImports(sourceCode, violations)
    }

    private void processImports(SourceCode sourceCode, List violations) {
        sourceCode.ast?.imports?.each { importNode ->
            if (!findReference(sourceCode, importNode.alias, importNode.className) && !AstUtil.isFromGeneratedSourceCode(importNode)) {
                violations.add(createViolationForImport(sourceCode, importNode, "The [${importNode.className}] import is never referenced"))
            }
        }
    }

    private void processStaticImports(SourceCode sourceCode, List violations) {
        sourceCode.ast?.staticImports?.each { alias, ImportNode classNode ->
            if (!findReference(sourceCode, alias) && !AstUtil.isFromGeneratedSourceCode(classNode)) {
                violations.add(createViolationForImport(sourceCode, classNode.className, alias, "The [${classNode.className}] import is never referenced"))
            }
        }
    }

    private String findReference(SourceCode sourceCode, String alias, String className = null) {
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

    private boolean isImportStatementForAlias(String line, String pattern) {
        final IMPORT_PATTERN = /import\s+.*/ + pattern
        line =~ IMPORT_PATTERN
    }

    private int countUsage(String line, String pattern) {
        final INVALID = '[^a-zA-Z0-9_\\$]'
        def regexp = /($INVALID|^|\$)${pattern}($INVALID|$)/
        return (line =~ regexp).count
    }
}
