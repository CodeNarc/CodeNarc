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

/**
 * Class contains similarly-named get and set methods where one method of the pair is marked either @WithReadLock or @WithWriteLock and the other is not locked at all.
 *
 * @author 'Hamlet D'Arcy'
 */
class InconsistentPropertyLockingRule extends AbstractAstVisitorRule {
    String name = 'InconsistentPropertyLocking'
    int priority = 2
    Class astVisitorClass = InconsistentPropertyLockingAstVisitor
}

class InconsistentPropertyLockingAstVisitor extends AbstractMethodVisitor {

    private final Map<String, MethodNode> guardedMethods = [:]
    private final Map<String, MethodNode> unguardedMethods = [:]

    @Override
    void visitMethod(MethodNode node) {
        if (node.name.startsWith('get') && node.name.size() > 3 && AstUtil.getParameterNames(node).isEmpty()) {
            // is a getter
            def propName = node.name[3..-1]
            saveMethodInfo(node)
            addViolationOnMismatch(node.name, "set$propName")
        }

        if (node.name.startsWith('is') && node.name.size() > 2 && AstUtil.getParameterNames(node).isEmpty()) {
            // is a getter
            def propName = node.name[2..-1]
            saveMethodInfo(node)
            addViolationOnMismatch(node.name, "set$propName")
        }

        if (node.name.startsWith('set') && node.name.size() > 3 && AstUtil.getParameterNames(node).size() == 1) {
            // is a setter
            def propName = node.name[3..-1]
            saveMethodInfo(node)
            addViolationOnMismatch("get$propName", node.name)
            addViolationOnMismatch("is$propName", node.name)
        }
    }

    private saveMethodInfo(MethodNode node) {

        if (isWriteLocked(node) || isReadLocked(node)) {
            guardedMethods.put(node.name, node)
        } else {
            unguardedMethods.put(node.name, node)
        }
    }

    private void addViolationOnMismatch(String getterName, String setterName) {
        if (guardedMethods.containsKey(getterName) && unguardedMethods.containsKey(setterName)) {
            MethodNode unguardedNode = unguardedMethods.get(setterName)
            MethodNode guardedNode = guardedMethods.get(getterName)
            def lockType = getGuardName(guardedNode)
            addViolation(unguardedNode, "The getter method $getterName is marked @$lockType but the setter method $setterName is not locked")
        }
        if (unguardedMethods.containsKey(getterName) && guardedMethods.containsKey(setterName)) {
            MethodNode unguardedNode = unguardedMethods.get(getterName)
            MethodNode guardedNode = guardedMethods.get(setterName)
            def lockType = getGuardName(guardedNode)
            addViolation(unguardedNode, "The setter method $setterName is marked @$lockType but the getter method $getterName is not locked")
        }
    }

    private static String getGuardName(MethodNode guardedNode) {
        if (isWriteLocked(guardedNode)) {
            return 'WithWriteLock'
        }
        if (isReadLocked(guardedNode)) {
            return 'WithReadLock'
        }
        ''
    }

    private static boolean isWriteLocked(MethodNode node) {
        AstUtil.getAnnotation(node, 'WithWriteLock') || AstUtil.getAnnotation(node, 'groovy.transform.WithWriteLock')
    }

    private static boolean isReadLocked(MethodNode node) {
        AstUtil.getAnnotation(node, 'WithReadLock') || AstUtil.getAnnotation(node, 'groovy.transform.WithReadLock')
    }
}
