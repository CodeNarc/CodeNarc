/*
 * Copyright 2020 the original author or authors.
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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Check whether method declarations do not contain unnecessary whitespace between method name and the opening
 * parenthesis for parameter list.
 */
class SpaceAfterMethodDeclarationNameRule extends AbstractAstVisitorRule {

    String name = 'SpaceAfterMethodDeclarationName'
    int priority = 3
    Class astVisitorClass = SpaceAfterMethodDeclarationNameRuleAstVisitor
}

class SpaceAfterMethodDeclarationNameRuleAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        if (node.lineNumber >= 0 && !node.name.contains('(')) {
            def line = sourceLine(node)
            def openingParenthesisIndex = line.indexOf('(', node.columnNumber - 1)

            if (openingParenthesisIndex >= 0 && line[openingParenthesisIndex - 1].trim().empty) {
                def type = isConstructor ? 'constructor' : 'method'
                addViolation(node, "There is trailing whitespace in $type name declaration.")
            }
        }

        super.visitConstructorOrMethod(node, isConstructor)
    }
}
