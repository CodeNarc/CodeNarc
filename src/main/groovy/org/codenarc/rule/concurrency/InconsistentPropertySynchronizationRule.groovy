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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * Class contains similarly-named get and set methods where the set method is synchronized and the get method is not, or the get method is synchronized and the set method is not. 
 *
 * @author 'Hamlet D'Arcy'
 */
class InconsistentPropertySynchronizationRule extends AbstractAstVisitorRule {
    String name = 'InconsistentPropertySynchronization'
    int priority = 2
    Class astVisitorClass = InconsistentPropertySynchronizationAstVisitor
}

class InconsistentPropertySynchronizationAstVisitor extends AbstractMethodVisitor {

    private final Map<String, MethodNode> synchronizedMethods = [:]
    private final Map<String, MethodNode> unsynchronizedMethods = [:]

    @Override
    void visitMethod(MethodNode node) {
        if (node.name.startsWith('get') && node.name.size() > 3 && AstUtil.getParameterNames(node).isEmpty()) {
            // is a getter
            def propName = node.name[3..-1]
            saveMethodInfo(node)
            // does the enclosing class have this property?
            if (AstUtil.classNodeHasProperty(currentClassNode, extractPropertyName(node))) {
                addViolationOnMismatch([node.name], "set$propName")
            }
        }

        if (node.name.startsWith('is') && node.name.size() > 2 && AstUtil.getParameterNames(node).isEmpty()) {
            // is a getter
            def propName = node.name[2..-1]
            saveMethodInfo(node)
            if (AstUtil.classNodeHasProperty(currentClassNode, extractPropertyName(node))) {
                addViolationOnMismatch([node.name], "set$propName")
            }
        }

        if (node.name.startsWith('set') && node.name.size() > 3 && AstUtil.getParameterNames(node).size() == 1) {
            // is a setter
            def propName = node.name[3..-1]
            saveMethodInfo(node)
            if (AstUtil.classNodeHasProperty(currentClassNode, extractPropertyName(node))) {
                addViolationOnMismatch(["get$propName", "is$propName"], node.name)
            }
        }
    }

    private static String extractPropertyName(MethodNode node) {
        if (node.name.startsWith('get') || node.name.startsWith('set')) {
            if (node.name.length() == 4) {
                return node.name[3..-1].toLowerCase()
            }
            return node.name[3].toLowerCase() + node.name[4..-1]
        } else if (node.name.startsWith('is')) {
            if (node.name.length() == 3) {
                return node.name[2..-1].toLowerCase()
            }
            return node.name[2].toLowerCase() + node.name[3..-1]
        }
        throw new IllegalArgumentException("MethodNode $node.name is not a getter/setter/izzer")
    }

    private saveMethodInfo(MethodNode node) {
        if (Modifier.isSynchronized(node.modifiers)) {
            synchronizedMethods.put(node.name, node)
        } else if (AstUtil.getAnnotation(node, 'Synchronized')) {
            synchronizedMethods.put(node.name, node)
        } else if (AstUtil.getAnnotation(node, 'groovy.transform.Synchronized')) {
            synchronizedMethods.put(node.name, node)
        } else {
            unsynchronizedMethods.put(node.name, node)
        }
    }

    @SuppressWarnings('CyclomaticComplexity')
    private void addViolationOnMismatch(List rawGetterNames, String setterName) {
        def getterNames = rawGetterNames*.toString() // force GString into strings

        if (containsKey(synchronizedMethods, getterNames) && unsynchronizedMethods.containsKey(setterName)) {
            def getterName = getFirstValue(synchronizedMethods, getterNames).name
            MethodNode node = unsynchronizedMethods.get(setterName)
            addViolation(node, "The getter method $getterName is synchronized but the setter method $setterName is not")
        } else if (containsKey(unsynchronizedMethods, getterNames) && synchronizedMethods.containsKey(setterName)) {
            def getterName = getFirstValue(unsynchronizedMethods, getterNames).name
            MethodNode node = unsynchronizedMethods.get(getterName)
            addViolation(node, "The setter method $setterName is synchronized but the getter method $getterName is not")
        } else if (containsKey(synchronizedMethods, getterNames)) {
            // perhaps the owner didn't define the method
            def getterName = getFirstValue(synchronizedMethods, getterNames).name
            MethodNode node = synchronizedMethods.get(getterName)
            if (!currentClassNode?.methods?.find { it?.name == setterName && it?.parameters?.length == 1 }) {
                addViolation(node, "The getter method $getterName is synchronized but the setter method $setterName is not")
            }
        } else if (synchronizedMethods.containsKey(setterName)) {
            // the setter is synchronized, perhaps the getter was never defined?
            if (!currentClassNode?.methods?.find { getterNames.contains(it?.name) && it?.parameters?.length == 0 }) {
                MethodNode node = synchronizedMethods.get(setterName)
                addViolation(node, "The setter method $setterName is synchronized but the getter method ${getterNames.size() == 1 ? getterNames[0] : getterNames} is not")
            }
        }
    }

    private static boolean containsKey(Map methodList, List<String> keys) {
        methodList.find { it.key in keys } != null
    }
    private static MethodNode getFirstValue(Map<String, MethodNode> methodList, List<String> keys) {
        methodList.find { it.key in keys }.value
    }
}
