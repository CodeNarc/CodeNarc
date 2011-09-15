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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor

/**
 * Rule that checks that the names of the most commonly overridden methods: equals,
 * hashCode and toString, are correct.
 *
 * @author @Hackergarten
 * @author Hamlet D'Arcy
 */
class ObjectOverrideMisspelledMethodNameRule extends AbstractAstVisitorRule {
    String name = 'ObjectOverrideMisspelledMethodName'
    int priority = 2
    Class astVisitorClass = ObjectOverrideMisspelledMethodNameAstVisitor
}

class ObjectOverrideMisspelledMethodNameAstVisitor extends AbstractMethodVisitor {

    @Override
    @SuppressWarnings('DuplicateLiteral')
    void visitMethod(MethodNode node) {
        checkForExactMethodName(node, 'equal', ['Object'], 'equals')
        checkForMethodNameWithIncorrectCase(node, 'equals', ['Object'])
        checkForMethodNameWithIncorrectCase(node, 'hashCode', [])
        checkForMethodNameWithIncorrectCase(node, 'toString', [])
    }

    private void checkForMethodNameWithIncorrectCase(MethodNode node, String targetMethodName, List parameterTypes) {
        def actualMethodName = node?.name
        if (actualMethodName?.toLowerCase() == targetMethodName.toLowerCase()
                && actualMethodName != targetMethodName
                && node?.parameters*.type.name == parameterTypes) {
            addViolation node, "Trying to override the $targetMethodName method using the name [$actualMethodName]"
        }
    }

    private void checkForExactMethodName(MethodNode node, String targetMethodName, List parameterTypes, String overridingMethodName) {
        def actualMethodName = node?.name
        if (actualMethodName == targetMethodName && node?.parameters*.type.name == parameterTypes) {
            addViolation node, "Trying to override the $overridingMethodName method using the name [$actualMethodName]"
        }
    }

}
