/*
 * Copyright 2017 the original author or authors.
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
package org.codenarc.rule.enhanced

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks for methods that override a method in a super class or implement a method in an interface but are not annotated with @Override.
 *
 * @author Marcin Erdmann
 */
class MissingOverrideAnnotationRule extends AbstractAstVisitorRule {

    String name = 'MissingOverrideAnnotation'
    int priority = 3
    Class astVisitorClass = MissingOverrideAnnotationAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
}

class MissingOverrideAnnotationAstVisitor extends AbstractAstVisitor {

    private static final ClassNode OVERRIDE = ClassHelper.make(Override)

    private final Deque<Map<String, Set<MethodNode>>> superClassMethodsDeque = [] as LinkedList

    @Override
    protected void visitClassEx(ClassNode node) {
        superClassMethodsDeque.push(findSuperClassMethods(node))
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        superClassMethodsDeque.poll()
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        if (!isAnnotatedWithOverride(node)) {
            def allPossibleParameterSignatures = generateAllPossibleParameterSignatures(node)
            def matchingSuperClassMethods = allPossibleParameterSignatures.findResults { parameters ->
                findMatchingSuperClassMethod(node.name, parameters)
            }
            if (matchingSuperClassMethods.size() == allPossibleParameterSignatures.size()) {
                def declaringClassNames = uniqueAndSortedDeclaringClassNames(matchingSuperClassMethods)
                addViolation(node, "Method '$node.name' is overriding a method in $declaringClassNames but is not annotated with @Override.")
            }
        }
    }

    private String uniqueAndSortedDeclaringClassNames(Collection<MethodNode> superClassMethods) {
        def declaringClassNames = superClassMethods*.declaringClass*.name.unique().sort()
        declaringClassNames.collect { "'$it'" }.join(', ')
    }

    private List<List<Parameter>> generateAllPossibleParameterSignatures(MethodNode methodNode) {
        def defaultValueCount = methodNode.parameters.count { it.hasInitialExpression() }
        (0..defaultValueCount).collect { defaultValuesToRemove ->
            def parameters = methodNode.parameters.toList()
            for(int i = parameters.size() - 1; i >= 0 && defaultValuesToRemove > 0; i--) {
                if (parameters[i].hasInitialExpression()) {
                    parameters.remove(i)
                    defaultValuesToRemove--
                }
            }
            parameters
        }
    }

    private MethodNode findMatchingSuperClassMethod(String name, List<Parameter> parameters) {
        def superClassMethods = superClassMethodsDeque.peek()
        def methodsWithSameName = superClassMethods[name]
        methodsWithSameName.find { superClassMethodNode ->
            parameters*.type == superClassMethodNode.parameters*.type
        }
    }

    private boolean isAnnotatedWithOverride(MethodNode node) {
        node.annotations.find { it.classNode == OVERRIDE }
    }

    private Map<String, Set<MethodNode>> findSuperClassMethods(ClassNode node) {
        def nameToMethods = [:].withDefault { [] as Set }
        def superClassAndInterfacesMethods = node.superClass.allDeclaredMethods + interfaceMethods(node)
        superClassAndInterfacesMethods.each { methodNode ->
            nameToMethods[methodNode.name] << methodNode
        }
        nameToMethods
    }

    private Collection<MethodNode> interfaceMethods(ClassNode node) {
        node.interfaces*.declaredMethodsMap*.values().flatten()
    }
}
