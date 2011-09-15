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

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * An interface should be used only to model a behaviour of a class: using an interface as a container of constants is a poor usage pattern.
 *
 * @author 'Hamlet D'Arcy'
 */
class ConstantsOnlyInterfaceRule extends AbstractAstVisitorRule {
    String name = 'ConstantsOnlyInterface'
    int priority = 2
    Class astVisitorClass = ConstantsOnlyInterfaceAstVisitor
}

class ConstantsOnlyInterfaceAstVisitor extends AbstractAstVisitor {
    @Override
    protected void visitClassEx(ClassNode node) {

        if (node.isInterface() && node.fields && !node.methods) {
            addViolation(node, "The interface $node.name has only fields and no methods defined")
        }
        super.visitClassEx(node)
    }
}
