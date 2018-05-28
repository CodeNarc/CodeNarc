/*
 * Copyright 2018 the original author or authors.
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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Enforce that all public methods are above protected and private methods.
 *
 * @author Chris Mair
 */
class PublicMethodsBeforeNonPublicRule extends AbstractAstVisitorRule {

    String name = 'PublicMethodsBeforeNonPublic'
    int priority = 3
    Class astVisitorClass = PublicMethodsBeforeNonPublicAstVisitor
}

class PublicMethodsBeforeNonPublicAstVisitor extends AbstractAstVisitor {

    private boolean hasDeclaredNonPublicMethod = false

    @Override
    protected void visitMethodComplete(MethodNode node) {
        if (node.public) {
            if (hasDeclaredNonPublicMethod) {
                addViolation(node, "The public method $node.name in class $currentClassName is declared after a non-public method")
            }
        }
        else {
            this.hasDeclaredNonPublicMethod = true
        }
        super.visitMethodComplete(node)
    }
}
