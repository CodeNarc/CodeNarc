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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * If a variable has a visibility modifier or a type declaration, then the def keyword is unneeded.
 * For instance 'def private n = 2' is redundant and can be simplified to 'private n = 2'.
 *
 * @author 'René Scheibe'
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class UnnecessaryDefInVariableDeclarationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryDefInVariableDeclaration'
    int priority = 3
    Class astVisitorClass = UnnecessaryDefInVariableDeclarationAstVisitor
}

class UnnecessaryDefInVariableDeclarationAstVisitor extends AbstractAstVisitor {
    @Override
    void visitFieldEx(FieldNode node) {
        checkViolations(node)

        super.visitFieldEx(node)
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        if (!(statement.expression instanceof DeclarationExpression)) { return }
        if (!(statement.expression.leftExpression instanceof VariableExpression)) { return }

        checkViolations(statement.expression)

        super.visitExpressionStatement(statement)
    }

    private checkViolations(ASTNode node) {
        String declaration = getDeclaration(node)

        if (contains(declaration, 'def')) {
            if (contains(declaration, 'private')) {
                addViolation(node, 'The def keyword is unneeded when a variable is marked private')
            } else if (contains(declaration, 'protected')) {
                addViolation(node, 'The def keyword is unneeded when a variable is marked protected')
            } else if (contains(declaration, 'public')) {
                addViolation(node, 'The def keyword is unneeded when a variable is marked public')
            } else if (contains(declaration, 'static')) {
                addViolation(node, 'The def keyword is unneeded when a variable is marked static')
            } else if (contains(declaration, 'final')) {
                addViolation(node, 'The def keyword is unneeded when a variable is marked final')
            } else if (contains(declaration, 'transient')) {
                addViolation(node, 'The def keyword is unneeded when a variable is marked transient')
            } else if (contains(declaration, 'volatile')) {
                addViolation(node, 'The def keyword is unneeded when a variable is marked volatile')
            } else if (contains(declaration, 'Object')) {
                addViolation(node, 'The def keyword is unneeded when a variable is of type Object')
            }
        }
    }

    private String getDeclaration(AnnotatedNode node) {
        if ([node.lineNumber, node.lastLineNumber, node.columnNumber, node.lastColumnNumber].any{ it < 1 }) {
            return ''
        }

        String acc = ''
        for (lineIndex in (node.lineNumber-1 .. node.lastLineNumber-1)) {
            // the raw line is required to apply columnNumber and lastColumnNumber
            def line = getRawLine(sourceCode, lineIndex)

            // extract the relevant part of the first line
            if (lineIndex == node.lineNumber - 1) {
                int nonRelevantColumns = node.columnNumber - 1
                line = line.replaceFirst(".{$nonRelevantColumns}", ' ' * nonRelevantColumns) // retain the line length as it's important when using lastColumnNumber
            }

            // extract the relevant part of the last line
            if (lineIndex == node.lastLineNumber - 1) {
                def stopIndex = node.lastColumnNumber < line.size() ? node.lastColumnNumber - 2 : line.size() - 1
                line = line[0..stopIndex]
            }

            if (line.contains('=')) {
                acc += line[0..<line.indexOf('=')]
                break
            } else {
                acc += line + ' '
            }
        }
        acc
    }

    private boolean contains(String declaration, String modifier) {
        declaration?.startsWith(modifier) || declaration?.contains(' ' + modifier + ' ')
    }
}
