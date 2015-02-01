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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.EmptyExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Multiple return values can be used to set several variables at once. To use multiple return values, the left hand
 * side of the assignment must be enclosed in parenthesis. If not, then you are not using multiple return values,
 * you're only assigning the last element.
 *
 * @author Hamlet D'Arcy
 */
class ConfusingMultipleReturnsRule extends AbstractAstVisitorRule {
    String name = 'ConfusingMultipleReturns'
    int priority = 2
    Class astVisitorClass = ConfusingMultipleReturnsAstVisitor
}

class ConfusingMultipleReturnsAstVisitor extends AbstractAstVisitor {

    Set<DeclarationExpression> declarations = [] as Set
    
    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        declarations << expression
        super.visitDeclarationExpression(expression)
    }

    @Override
    protected void visitClassEx(ClassNode node) {
        declarations.clear()  // clear it out for next iteration

        def fields = node.fields.
            groupBy { FieldNode f -> f.lineNumber }.
            values().
            findAll { Collection c -> c.size() > 1 }

        for (Collection<FieldNode> nodes : fields) {
            nodes.sort { it.columnNumber }
            addViolationsForMultipleFieldDeclarations(nodes)
        }
        super.visitClassEx(node)
    }

    @Override
    protected void visitClassComplete(ClassNode node) {

        def declarationsOnSingleLine = declarations.
            groupBy { DeclarationExpression d -> d.lineNumber }.
            values().
            findAll { Collection c -> c.size() > 1 }

        for (Collection<DeclarationExpression> groupedNodes : declarationsOnSingleLine) {
            groupedNodes.sort { it.columnNumber }
            addViolationForMultipleDeclarations(groupedNodes)
        }

        declarations.clear()
        super.visitClassComplete(node)
    }

    private addViolationsForMultipleFieldDeclarations(Collection<FieldNode> nodes) {
        // all the declarations are on the same line... does the last one have an initial expression and the others do not?
        if (nodes[-1].initialExpression != null) {
            nodes.remove(nodes[-1]) // remove last node
            if (nodes.every { FieldNode f -> f.initialExpression == null }) {
                for (def v: nodes) {
                    addViolation(v, "Confusing declaration in class $currentClassName. The field '$v.name' is initialized to null")
                }
            }
        }
    }

    private addViolationForMultipleDeclarations(Collection<DeclarationExpression> groupedNodes) {
        // all the declarations are on the same line... does the last one have an initial expression and the others do not?
        def lastExpression = groupedNodes[-1]
        if (!(lastExpression.rightExpression instanceof EmptyExpression)) {
            groupedNodes.remove(lastExpression) // remove last node b/c it has an expression
            for (def declaration: groupedNodes) {
                def rhs = declaration.rightExpression
                if (rhs instanceof EmptyExpression) {
                    addViolation(declaration, "Confusing declaration in class $currentClassName. The variable '$declaration.leftExpression.text' is initialized to null")
                }
            }
        }
    }
}
