/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.size

import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks if the number of parameters in method/constructor exceeds the number of parameters
 * specified by the maxParameters property.
 *
 * @author Maciej Ziarko
 */
class ParameterCountRule extends AbstractAstVisitorRule {

    private static final DEFAULT_MAX_PARAMETER = 5

    String name = 'ParameterCount'
    int priority = 2
    Class astVisitorClass = ParameterCountAstVisitor
    int maxParameters = DEFAULT_MAX_PARAMETER

    void setMaxParameters(int maxParameters) {
        if (maxParameters < 1) {
            throw new IllegalArgumentException("maxParameters property of $name rule must be a positive integer!")
        }
        this.maxParameters = maxParameters
    }
}

class ParameterCountAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        checkParametersCount(node)
    }

    private void checkParametersCount(MethodNode node) {
        if (node.parameters.size() > rule.maxParameters) {
            addViolation(node, "Number of parameters in ${getName(node)} exceeds maximum allowed (${rule.maxParameters}).")
        }
    }

    private String getName(MethodNode methodNode) {
        return "method ${methodNode.declaringClass.name}.${methodNode.name}"
    }

    private String getName(ConstructorNode constructorNode) {
        return "constructor of class ${constructorNode.declaringClass.name}"
    }
}
