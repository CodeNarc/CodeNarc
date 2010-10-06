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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.MethodNode

/**
 * Rule that checks that the names of the most commonly overridden methods: equals,
 * hashCode and toString, are correct.
 *
 * @author @Hackergarten
 */
class ObjectOverrideMisspelledMethodNameRule extends AbstractAstVisitorRule {
    String name = 'ObjectOverrideMisspelledMethodName'
    int priority = 2
    Class astVisitorClass = ObjectOverrideMisspelledMethodNameAstVisitor
}

class ObjectOverrideMisspelledMethodNameAstVisitor extends AbstractAstVisitor {

    void visitMethod(MethodNode node) {
        checkEqual(node)
        checkEquals(node)
        checkHashCode(node)
        checkToString(node)
    }

    private void checkEqual(MethodNode node) {
        def methodName = node?.name
        if(methodName == 'equal'
                && node?.parameters.size() == 1
                && node?.parameters[0].type.name == 'Object') {
            addViolation node, 'Trying to override the equals method using the name [$methodName]'
        }
    }

    private void checkEquals(MethodNode node) {
        def methodName = node?.name
        if(methodName?.toLowerCase() == 'equals'
                && methodName != 'equals'
                && node?.parameters.size() == 1
                && node?.parameters[0].type.name == 'Object') {
            addViolation node, 'Trying to override the equals method using the name [$methodName]'
        }
    }

    private void checkHashCode(MethodNode node) {
        def methodName = node?.name
        if(methodName?.toLowerCase() == 'hashcode' && methodName != 'hashCode') {
            addViolation node, 'Trying to override the hashCode method using the name [$methodName]'
        }
    }

    private void checkToString(MethodNode node) {
        def methodName = node?.name
        if(methodName?.toLowerCase() == 'tostring'
                && methodName != 'toString'
                && node?.parameters.size() == 0) {
            addViolation node, 'Trying to override the toString method using the name [$methodName]'
        }
    }
}
