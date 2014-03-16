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

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * An empty method in an abstract class should be abstract instead, as developer may rely on this empty implementation rather than code the appropriate one.
 *
 * @author Hamlet D'Arcy
 */
class EmptyMethodInAbstractClassRule extends AbstractAstVisitorRule {
    String name = 'EmptyMethodInAbstractClass'
    int priority = 2
    Class astVisitorClass = EmptyMethodInAbstractClassAstVisitor
}

class EmptyMethodInAbstractClassAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {

        if (isAbstract(node.declaringClass) && !isAbstract(node) && !node.isPrivate()) {
            if (node.returnType.name == 'void') {
                // check for empty statement
                if (node.code instanceof BlockStatement && !node.code.statements) {
                    addViolation (node, "The method $node.name in abstract class $node.declaringClass.name is empty. Consider making it abstract")
                }
            } else if (node.code instanceof BlockStatement && node.code.statements.size() == 1) {
                // check for 'return null' statement
                def code = node.code.statements[0]
                if (code instanceof ExpressionStatement && AstUtil.isNull(code.expression)) {
                    addViolation (node, "The method $node.name in abstract class $node.declaringClass.name contains no logic. Consider making it abstract")
                } else if (code instanceof ReturnStatement && AstUtil.isNull(code.expression)) {
                    addViolation (node, "The method $node.name in abstract class $node.declaringClass.name contains no logic. Consider making it abstract")
                }
            }
        }
    }

    private static boolean isAbstract(node) {
        return Modifier.isAbstract(node.modifiers)
    }

}
