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
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * This rule finds instances of method parameters not being used. It does not analyze private methods (that is done by the UnusedPrivateMethodParameter rule) or methods marked @Override. 
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class UnusedMethodParameterRule extends AbstractAstVisitorRule {
    String name = 'UnusedMethodParameter'
    int priority = 2
    String ignoreRegex = 'ignore|ignored'
    String ignoreClassRegex = '.*Category'
    Class astVisitorClass = UnusedMethodParameterAstVisitor
}

class UnusedMethodParameterAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {

        if (!currentClassNode.isInterface() && !node.isAbstract() && !(currentClassName ==~ rule.ignoreClassRegex)
            && !Modifier.isPrivate(node.modifiers) && AstUtil.getAnnotation(node, 'Override') == null
            && !isMainMethod(node)) {
            
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

    private static boolean isMainMethod(MethodNode node) {
        AstUtil.isMethodNode(node, 'main', 1) &&
            (node.parameters[0].dynamicTyped || node.parameters[0].type.name in ['String[]', '[Ljava.lang.String;']) &&
            (node.isVoidMethod() || node.dynamicReturnType) &&
            node.isStatic()
    }

    private List<ClassNode> getAnonymousClasses() {
        sourceCode.ast.classes.findAll {
            it instanceof InnerClassNode && it.anonymous
        }
    }
}
