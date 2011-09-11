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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * If a variable has a visibility modifier or a type declaration, then the def keyword is unneeded.
 * For instance 'def private n = 2' is redundant and can be simplified to 'private n = 2'.
 *
 * @author 'René Scheibe'
  */
class UnnecessaryDefInVariableDeclarationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryDefInVariableDeclaration'
    int priority = 3
    Class astVisitorClass = UnnecessaryDefInVariableDeclarationAstVisitor
}

class UnnecessaryDefInVariableDeclarationAstVisitor extends AbstractAstVisitor {
    
    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        def node = statement.expression

        if (!(node instanceof DeclarationExpression)) { return }
        if (!(node.leftExpression instanceof VariableExpression)) { return }

        String declaration = AstUtil.getDeclaration(node, sourceCode)
        if (declaration.contains('=')) {
            declaration = declaration[0..(declaration.indexOf('='))] // ignore everything to the right of equals
        }

        if (contains(declaration, 'def')) {
            if (contains(declaration, 'private')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is marked private")
            } else if (contains(declaration, 'protected')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is marked protected")
            } else if (contains(declaration, 'public')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is marked public")
            } else if (contains(declaration, 'static')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is marked static")
            } else if (contains(declaration, 'final')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is marked final")
            } else if (contains(declaration, 'transient')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is marked transient")
            } else if (contains(declaration, 'volatile')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is marked volatile")
            } else if (contains(declaration, 'Object')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is of type Object")
            } else if (node.leftExpression.type != ClassHelper.DYNAMIC_TYPE) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a variable is declared with a type")
            }
        }

        super.visitExpressionStatement(statement)
    }

    static private boolean contains(String declaration, String modifier) {
        return declaration?.startsWith(modifier) || declaration?.contains(' ' + modifier + ' ')
    }
}
