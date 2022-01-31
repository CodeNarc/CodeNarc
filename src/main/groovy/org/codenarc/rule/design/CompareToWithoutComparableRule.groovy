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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractEnhanceableAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

/**
 * If you implement a compareTo method then you should also implement the Comparable interface.
 *
 * @author Hamlet D'Arcy
 */
class CompareToWithoutComparableRule extends AbstractEnhanceableAstVisitorRule {
    String name = 'CompareToWithoutComparable'
    int priority = 2
    Class astVisitorClass = CompareToWithoutComparableAstVisitor
}

class CompareToWithoutComparableAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {
        if (AstUtil.isMethodNode(node, 'compareTo', 1, Integer.TYPE)) {
            if (!AstUtil.classNodeImplementsType(node.declaringClass, Comparable)) {
                addViolation(node.declaringClass, "compareTo method at line $node.lineNumber would implement Comparable.compareTo(Object) but the enclosing class does not implement Comparable.")
            }
        }
    }
}
