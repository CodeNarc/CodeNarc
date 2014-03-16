/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * This rule finds classes marked final that contain protected methods and fields. If a class is final then it may not be subclassed,
 * and there is therefore no point in having a member with protected visibility. Either the class should not be final or
 * the member should be private or protected.
 *
 * @author Hamlet D'Arcy
 */
class FinalClassWithProtectedMemberRule extends AbstractAstVisitorRule {
    String name = 'FinalClassWithProtectedMember'
    int priority = 2
    Class astVisitorClass = FinalClassWithProtectedMemberAstVisitor
}

class FinalClassWithProtectedMemberAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        if (node.isProtected()) {
            if (Modifier.isFinal(node.declaringClass.modifiers) && !AstUtil.getAnnotation(node, 'Override')) {
                addViolation node, "The method $node.name has protected visibility but the enclosing class $node.declaringClass.name is marked final"
            }
        }
        super.visitConstructorOrMethod(node, isConstructor)
    }

    @Override
    void visitField(FieldNode node) {
        if (isProtected(node)) {
            if (Modifier.isFinal(node.declaringClass.modifiers)) {
                addViolation node, "The field $node.name has protected visibility but the enclosing class $node.declaringClass.name is marked final"
            }
        }
        super.visitField(node)
    }

    private static boolean isProtected(node) {
        Modifier.isProtected(node.modifiers)
    }
}
