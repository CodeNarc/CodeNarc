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
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * If a method has a visibility modifier, then the def keyword is unneeded. For instance 'def private method() {}' is redundant and can be simplified to 'private method() {}'.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class UnnecessaryDefInMethodDeclarationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryDefInMethodDeclaration'
    int priority = 3
    Class astVisitorClass = UnnecessaryDefInMethodDeclarationAstVisitor
}

class UnnecessaryDefInMethodDeclarationAstVisitor extends AbstractAstVisitor {
    @Override
    void visitMethodEx(MethodNode node) {
        String declaration = getDeclaration(node)
        if (declaration?.startsWith('def ') || declaration?.contains(' def ')) {
            if (declaration?.startsWith('private ') || declaration?.contains(' private ')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked private')
            } else if (declaration?.startsWith('protected ') || declaration?.contains(' protected ')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked protected')
            } else if (declaration?.startsWith('public ') || declaration?.contains(' public ')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked public')
            } else if (declaration?.startsWith('static ') || declaration?.contains(' static ')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked static')
            } else if (declaration?.startsWith('Object ') || declaration?.contains(' Object ')) {
                addViolation(node, 'The def keyword is unneeded when a method returns the Object type')
            }
        }
        super.visitMethodEx(node)
    }

    private String getDeclaration(ASTNode node) {
        if (node.lineNumber < 0) {
            return ''
        }

        def current = node.lineNumber - 1
        String acc = ''
        while (current <= node.lastLineNumber) {
            def line = sourceCode.line(current)
            if (line?.contains('{')) {
                return acc + line[0..(line.indexOf('{'))]
            }
            acc = acc + line + ' '
            current++
        }
        acc
    }
}
