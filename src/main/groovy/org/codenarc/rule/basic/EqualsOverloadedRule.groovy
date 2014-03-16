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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

/**
 * The class has an equals method, but the parameter of the method is not of type Object. It is not overriding equals but instead overloading it. 
 *
 * @author Hamlet D'Arcy,
 * @author Artur Gajowy
 * @author Marcin Smialek
 *
 */
class EqualsOverloadedRule extends AbstractAstVisitorRule {
    String name = 'EqualsOverloaded'
    int priority = 2
    Class astVisitorClass = EqualsOverloadedAstVisitor
}

class EqualsOverloadedAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {
        
        if (AstUtil.isMethodNode(node, 'equals')) {
            addViolationIfOverloaded(node)
        }
    }

    private void addViolationIfOverloaded(MethodNode method) {

        if (!(isEqualsMethodWithOneArgument(method) && onlyArgumentHasTypeObject(method))) {
            addViolation(method, "The class $method.declaringClass.name overloads the equals method, it does not override it.")
        }
    }

    private static boolean isEqualsMethodWithOneArgument(MethodNode method) {
        AstUtil.isMethodNode(method, 'equals', 1)
    }

    private static boolean onlyArgumentHasTypeObject(MethodNode method) {
        if (method.parameters[0].type.name == 'Object') { return true }
        if (method.parameters[0].type.name == 'java.lang.Object') { return true }
        false
    }

}
