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

import org.codenarc.rule.AbstractRule
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil
import org.codenarc.util.ImportUtil

/**
 * Rule that checks for non-static imports from any packages that are
 * automatically imported by Groovy, including:
 * <ul>
 *   <li>java.io</li>
 *   <li>java.lang</li>
 *   <li>java.net</li>
 *   <li>java.util</li>
 *   <li>java.math.BigDecimal</li>
 *   <li>java.math.BigInteger</li>
 *   <li>groovy.lang</li>
 *   <li>groovy.util</li>
 * </ul>
 *
 * @author Chris Mair
  */
class UnnecessaryGroovyImportRule extends AbstractRule {
    String name = 'UnnecessaryGroovyImport'
    int priority = 3

    void applyTo(SourceCode sourceCode, List violations) {
        if (sourceCode.ast?.imports || sourceCode.ast?.starImports) {
            ImportUtil.getNonStaticImportsSortedByLineNumber(sourceCode).each { importNode ->
                def importClassName = importNode.className
                def importPackageName = ImportUtil.packageNameForImport(importNode)

                if ((!importNode.alias || importClassName.endsWith(".$importNode.alias")) &&
                        (importPackageName in AstUtil.AUTO_IMPORTED_PACKAGES || importClassName in AstUtil.AUTO_IMPORTED_CLASSES)) {
                    violations.add(createViolationForImport(sourceCode, importNode))
                }
            }
        }
    }

}
