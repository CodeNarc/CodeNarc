/*
 * Copyright 2020 the original author or authors.
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

import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Do not use an Optional as a parameter type for method or constructor.
 *
 * @author Chris Mair
 */
class OptionalMethodParameterRule extends AbstractAstVisitorRule {

    String name = 'OptionalMethodParameter'
    int priority = 2
    Class astVisitorClass = OptionalMethodParameterAstVisitor
}

class OptionalMethodParameterAstVisitor extends AbstractAstVisitor {

    @Override
    void visitMethodEx(MethodNode methodNode) {
        processParameters(methodNode.parameters, methodNode.name)
        super.visitMethodEx(methodNode)
    }

    @Override
    void visitConstructor(ConstructorNode constructorNode) {
        processParameters(constructorNode.parameters, '<init>')
        super.visitConstructor(constructorNode)
    }

    private void processParameters(Parameter[] parameters, String methodName) {
        parameters.each { parameter ->
            if (parameter.type.name == 'Optional') {
                String message = "The parameter named $parameter.name of method $methodName in class $currentClassName is an Optional"
                addViolation(parameter, message)
            }
        }
    }

}
