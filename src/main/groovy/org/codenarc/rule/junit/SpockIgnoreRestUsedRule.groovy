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
import org.codenarc.util.WildcardPattern

/**
 * If Spock's @IgnoreRest on any method, all non-annotated test methods are not executed. This behaviour is almost always
 * unintended. It's fine to use @IgnoreRest locally during development, but when committing code, it should be removed.
 *
 * @author Jan Ahrens
 * @author Stefan Armbruster
 * @author Chris Mair
  */
class SpockIgnoreRestUsedRule extends AbstractAstVisitorRule {

    String name = 'SpockIgnoreRestUsed'
    int priority = 2
    String specificationSuperclassNames = '*Specification'
    String specificationClassNames = null
    Class astVisitorClass = SpockIgnoreRestUsedAstVisitor
}

class SpockIgnoreRestUsedAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitClass(ClassNode node) {
        def superClassPattern = new WildcardPattern(rule.specificationSuperclassNames)
        def classNamePattern = new WildcardPattern(rule.specificationClassNames, false)
        if (superClassPattern.matches(node.superClass.name) || classNamePattern.matches(node.name)) {
            super.visitClass(node)
        }
    }

    @Override
    void visitMethod(MethodNode node) {
        def hasIgnoreRest = node.annotations.any {
            it.classNode.nameWithoutPackage == 'IgnoreRest'
        }
        if (hasIgnoreRest) {
            addViolation(node, "The method '$node.name' in class $node.declaringClass.name uses @IgnoreRest")
        }
    }
}
