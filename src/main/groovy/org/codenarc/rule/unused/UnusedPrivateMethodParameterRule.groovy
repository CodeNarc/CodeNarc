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
package org.codenarc.rule.unused

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor

import java.lang.reflect.Modifier

/**
 * Rule that checks for parameters to private methods that are not referenced within the method body.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class UnusedPrivateMethodParameterRule extends AbstractAstVisitorRule {
    String name = 'UnusedPrivateMethodParameter'
    int priority = 2
    String ignoreRegex = 'ignore|ignored'
    Class astVisitorClass = UnusedPrivateMethodParameterAstVisitor
}

class UnusedPrivateMethodParameterAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {

        if (Modifier.isPrivate(node.modifiers)) {
            def unusedParameterNames = node.parameters*.name
            def collector = new ReferenceCollector()
            collector.visitMethod(node)
            getAnonymousClasses().each { ClassNode it ->
                it.visitContents(collector)
            }
            unusedParameterNames.removeAll(collector.references)
            unusedParameterNames.removeAll { it =~ rule.ignoreRegex }
            unusedParameterNames.each { parameterName ->
                addViolation(node, "Method parameter [$parameterName] is never referenced in the method $node.name of class $currentClassName")
            }
        }
    }

    private List<ClassNode> getAnonymousClasses() {
        sourceCode.ast.classes.findAll {
            it instanceof InnerClassNode && it.anonymous
        }
    }
}

