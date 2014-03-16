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
package org.codenarc.rule.exceptions

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Errors are system exceptions. Do not extend them.
 *
 * @author Hamlet D'Arcy
  */
class ExceptionExtendsErrorRule extends AbstractAstVisitorRule {
    String name = 'ExceptionExtendsError'
    int priority = 2
    Class astVisitorClass = ExceptionExtendsErrorAstVisitor
}

class ExceptionExtendsErrorAstVisitor extends AbstractAstVisitor {
    @Override
    protected void visitClassEx(ClassNode node) {
        if (AstUtil.classNodeImplementsType(node, Error)) {
            addViolation(node, "The class $node.name extends Error, which is meant to be used only as a system exception")
        }
        super.visitClassEx(node)
    }

}
