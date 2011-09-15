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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor

/**
 * If Spock's @IgnoreRest on any method, all non-annotated test methods are not executed. This behaviour is almost always
 * unintended. It's fine to use @IgnoreRest locally during development, but when committing code, it should be removed.
 *
 * @author Jan Ahrens
 * @author Stefan Armbruster
  */
class SpockIgnoreRestUsedRule extends AbstractAstVisitorRule {
    String name = 'SpockIgnoreRestUsed'
    int priority = 2
    Class astVisitorClass = SpockIgnoreRestUsedAstVisitor
}

class SpockIgnoreRestUsedAstVisitor extends AbstractMethodVisitor {

    private final static ACC_PUBLIC = 1
    private final static CANDIDATE_SUPER_CLASSNODES = ['spock.lang.Specification', 'Specification'].collect {new ClassNode(it, ACC_PUBLIC, null)}

    @Override
    void visitMethod(MethodNode node) {
        if (CANDIDATE_SUPER_CLASSNODES.any { node.declaringClass.isDerivedFrom(it)}) {

            def hasIgnoreRest = node.annotations.any {
                it.classNode.nameWithoutPackage == 'IgnoreRest'
            }
            if (hasIgnoreRest) {
                addViolation(node, "The method '$node.name' in class $node.declaringClass.name uses @IgnoreRest")
            }
        }
    }
}
