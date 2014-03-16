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

import org.codenarc.rule.AbstractRule
import org.codenarc.source.SourceCode
import org.codenarc.util.ImportUtil

/**
 * Checks for an import of a class that is within the same package as the importing class.
 *
 * @author Chris Mair
  */
@SuppressWarnings('DuplicateLiteral')
class ImportFromSamePackageRule extends AbstractRule {
    String name = 'ImportFromSamePackage'
    int priority = 3

    void applyTo(SourceCode sourceCode, List violations) {
        def hasImports = sourceCode.ast?.imports || sourceCode.ast?.starImports
        if (hasImports && sourceCode.ast.packageName) {
            def rawPackage = sourceCode.ast.packageName
            def filePackageName = rawPackage.endsWith('.') ? rawPackage[0..-2] : rawPackage
            ImportUtil.getNonStaticImportsSortedByLineNumber(sourceCode).each { importNode ->
                def importPackageName = ImportUtil.packageNameForImport(importNode)
                if (importPackageName == filePackageName && !hasAlias(importNode)) {
                    violations.add(createViolationForImport(sourceCode, importNode))
                }
            }
        }
    }

    private boolean hasAlias(importNode) {
        importNode.className && importNode.alias != getClassNameNoPackage(importNode.className)
    }

    private String getClassNameNoPackage(String className) {
        def indexOfLastPeriod = className.lastIndexOf('.')
        (indexOfLastPeriod == -1) ? className : className[indexOfLastPeriod + 1..-1]
    }

}
