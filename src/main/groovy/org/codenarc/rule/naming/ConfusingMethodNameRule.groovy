/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * This rule traps the condition where two methods or closures differ only by their capitalization.
 *
 * @author Hamlet D'Arcy
 * @author Hubert 'Mr. Haki' Klein Ikkink
  */
class ConfusingMethodNameRule extends AbstractAstVisitorRule {
    String name = 'ConfusingMethodName'
    int priority = 2
    Class astVisitorClass = ConfusingMethodNameAstVisitor
}

class ConfusingMethodNameAstVisitor extends AbstractAstVisitor {

    void visitClassEx(ClassNode node) {
        node.visitContents(new ScopedConfusingMethodNameAstVisitor(this))
    }
}

class ScopedConfusingMethodNameAstVisitor extends AbstractAstVisitor {
    def lowercaseMethodNames = [] as Set
    def lowercaseMethodNamesWithParameterTypes = [] as Set
    def lowercaseClosureNames = [] as Set
    def lowercaseFieldNames = [:]
    def parent

    ScopedConfusingMethodNameAstVisitor(AbstractAstVisitor parent) {
        this.parent = parent
        this.rule = parent.rule
    }

    void visitMethodEx(MethodNode node) {
        String methodName = node.getName().toLowerCase()
        String parameterInfo = getParameterDefinitionAsString(node)
        String methodNameWithParameters = node.name.toLowerCase() + parameterInfo

        if (lowercaseClosureNames.contains(methodName)) {
            parent.addViolation(node, 'Found very confusing method name. ' +
                    'Conflicts with a similar closure name. ' +
                    "Found method : $node.name $parameterInfo")
        } else if (lowercaseMethodNamesWithParameterTypes.contains(methodNameWithParameters)) {
            parent.addViolation(node, "Found very confusing method name: $node.name $parameterInfo")
        } else if (lowercaseFieldNames.any { it.key == methodName }) {
            parent.addViolation(node, "The method name $node.name is similar to the field name ${lowercaseFieldNames[methodName]}")
        }
        lowercaseMethodNames.add(methodName)
        lowercaseMethodNamesWithParameterTypes.add(methodNameWithParameters)

        super.visitMethodEx node
    }

    void visitField(FieldNode node) {
        if (AstUtil.isClosureDeclaration(node)) {
            String methodName = node.name.toLowerCase()

            if (lowercaseClosureNames.contains(methodName)) {
                parent.addViolation(node, "Found very confusing closure name: $node.name")
            }

            lowercaseClosureNames.add(methodName)
        } else {
            String fieldName = node.name.toLowerCase()
            lowercaseFieldNames[fieldName] = node.name
        }
        super.visitField(node)
    }

    void visitClassEx(ClassNode node) {
        parent.visitClassEx(node)
    }

    private static String getParameterDefinitionAsString(MethodNode node) {
        Parameter[] parameters = node?.getParameters()
        '(' + parameters?.collect { it?.type?.toString() }?.join(', ') + ')'
    }
}
