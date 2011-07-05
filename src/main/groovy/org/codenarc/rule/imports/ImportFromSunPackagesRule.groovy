/*
 * Copyright 2011 the original author or authors.
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
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.source.SourceCode

/**
 * Avoid importing anything from the 'sun.*' packages. These packages are not portable and are likely to change.
 *
 * @author 'Hamlet D'Arcy'
  */
class ImportFromSunPackagesRule extends AbstractAstVisitorRule {
    String name = 'ImportFromSunPackages'
    int priority = 2

    void applyTo(SourceCode sourceCode, List violations) {
        sourceCode.ast?.imports?.each { importNode ->
            addViolationForSunImport(importNode, sourceCode, violations)
        }
        sourceCode.ast?.starImports?.each { importNode ->
            addViolationForSunImport(importNode, sourceCode, violations)
        }
    }

    def addViolationForSunImport(ImportNode importNode, SourceCode sourceCode, List violations) {
        def className = importNode.className
        def packageName = importNode.packageName
        if (className?.startsWith('sun.')) {
            String message = "The file imports $className, which is not portable and likely to change"
            violations.add(createViolationForImport(sourceCode, importNode, message))
        } else if (packageName?.startsWith('sun.')) {
            String message = "The file imports ${packageName}*, which is not portable and likely to change"
            violations.add(createViolationForImport(sourceCode, importNode, message))
        }
    }
}
