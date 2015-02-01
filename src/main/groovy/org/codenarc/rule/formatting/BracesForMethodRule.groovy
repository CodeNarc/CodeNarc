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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks the location of the opening brace ({) for constructors and methods. By default, requires them on the same line, but the sameLine property can be set to false to override this.
 *
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author Hamlet D'Arcy
  */
class BracesForMethodRule extends AbstractAstVisitorRule {
    String name = 'BracesForMethod'
    int priority = 2
    Class astVisitorClass = BracesForMethodAstVisitor
    boolean sameLine = true
}

class BracesForMethodAstVisitor extends AbstractAstVisitor {

    @Override
    void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {

        if (node.declaringClass?.isInterface() || node.isAbstract()) {
            return
        }

        String lastLine
        if (node.exceptions) {
            lastLine = lastSourceLineTrimmed(node.exceptions[-1])
        } else if (node.parameters) {
            lastLine = lastSourceLineTrimmed(node.parameters[-1])
        } else {
            lastLine = sourceLineTrimmed(node)
        }

        if (rule.sameLine) {
            if(!(containsOpeningBraceAfterParenthesis(lastLine))) {
                addViolation(node, "Opening brace for the method $node.name should start on the same line")
            }
        } else {
            if (containsOpeningBraceAfterParenthesis(lastLine)) {
                addViolation(node, "Opening brace for the method $node.name should start on a new line")
            }
        }
        super.visitConstructorOrMethod(node, isConstructor)
    }

    private containsOpeningBraceAfterParenthesis(String lastLine) {
        int parenthesisIndex = lastLine?.indexOf(')') ?: 0
        lastLine?.indexOf('{', parenthesisIndex) >= 0
    }
}
