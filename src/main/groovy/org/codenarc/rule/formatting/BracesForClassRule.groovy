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
package org.codenarc.rule.formatting

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil

/**
 * Checks the location of the opening brace ({) for classes. By default, requires them on the same line, but the sameLine property can be set to false to override this.
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
  */
class BracesForClassRule extends AbstractRule {
    String name = 'BracesForClass'
    int priority = 2
    boolean sameLine = true

    @Override
    void applyTo(SourceCode sourceCode, List violations) {

        sourceCode?.ast?.classes?.each { ClassNode classNode ->

            def (lineNumber, sourceLine) = findOpeningBraceLine(sourceCode, classNode)
            // Groovy 1.7 returns -1 as line number for a ClassNode representing an enum.
            // In this case we ignore the rule
            if (lineNumber != -1) {
                applyToClassNode(classNode, lineNumber, sourceLine, violations)
            }
        }
    }

    private applyToClassNode(ClassNode classNode, int lineNumber, String sourceLine, List violations) {
        if (sameLine) {
            if (sourceLine?.startsWith('{')) {
                violations.add(new Violation(
                    rule: this,
                    lineNumber: lineNumber,
                    sourceLine: sourceLine,
                    message: "Opening brace for the ${classNode.isInterface() ? 'interface' : 'class'} $classNode.name should start on the same line"))
            }
        } else {
            if (!sourceLine?.startsWith('{') && !definesAnnotationType(sourceLine)) {
                violations.add(new Violation(
                    rule: this,
                    lineNumber: lineNumber,
                    sourceLine: sourceLine,
                    message: "Opening brace for the ${classNode.isInterface() ? 'interface' : 'class'} $classNode.name should start on a new line"))
            }
        }
    }

    private definesAnnotationType(String sourceLine) {
        sourceLine?.contains('@interface')
    }

    private findOpeningBraceLine(SourceCode sourceCode, ASTNode node) {
        int line = AstUtil.findFirstNonAnnotationLine(node, sourceCode)
        def sourceLine = sourceCode.line(line - 1)
        while (sourceLine != null) {

            if (sourceLine?.contains('{')) {
                return [line, sourceLine]
            }
            line++
            sourceLine = sourceCode.line(line - 1)
        }

        return [line, null]
    }
}
