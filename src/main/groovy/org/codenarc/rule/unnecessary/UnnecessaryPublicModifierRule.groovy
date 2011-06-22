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
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.apache.log4j.Logger
import org.codehaus.groovy.ast.ConstructorNode

/**
 * The 'public' modifier is not required on methods, constructors or classes.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class UnnecessaryPublicModifierRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryPublicModifier'
    int priority = 3
    Class astVisitorClass = UnnecessaryPublicModifierAstVisitor
}

class UnnecessaryPublicModifierAstVisitor extends AbstractAstVisitor {
    private static final LOG = Logger.getLogger(UnnecessaryPublicModifierAstVisitor)

    @Override
    protected void visitClassEx(ClassNode node) {
        checkDeclaration(node, 'classes')
        super.visitClassEx(node)
    }

    @Override
    void visitMethodEx(MethodNode node) {
        checkDeclaration(node, 'methods')
        super.visitMethodEx(node)
    }

    @Override
    def void visitConstructorEx(ConstructorNode node) {
        checkDeclaration(node, 'constructors')
        super.visitConstructorEx(node)
    }

    private void checkDeclaration(node, String nodeType) {
        String declaration = getDeclaration(node)
        if (getDeclaration(node)?.startsWith('public ')) {
            addViolation(node, "The public keyword is unnecessary for $nodeType")
        }
        else if (declaration?.contains(' public ')) {
            addViolation(node, "The public keyword is unnecessary for $nodeType")
        }
    }

    private String getDeclaration(ASTNode node) {
        if (node.lineNumber < 0) {
            return ''
        }

        def current = node.lineNumber - 1
        String acc = ''
        while (current <= node.lastLineNumber) {
            def line = sourceCode.line(current)
            if (line == null) {
                LOG.warn("${rule.name} cannot find source code line $current in ${sourceCode.name}. Scanning lines ${node.lineNumber} to ${node.lastLineNumber}.")
                return ''
            } else if (line.contains('{')) {
                return acc + line[0..(line.indexOf('{'))]
            }
            acc = acc + line + ' '
            current++
        }
        acc
    }
}
