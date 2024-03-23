/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.MethodCall
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * CPS transformed methods may not be called from non CPS transformed methods in Jenkins
 *
 * @author Daniel Zänker
 */
class CpsCallFromNonCpsMethodRule extends AbstractAstVisitorRule {

    String name = 'CpsCallFromNonCpsMethod'
    int priority = 2
    Class astVisitorClass = CpsCallFromNonCpsMethodAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
    String applyToFileNames = 'Jenkinsfile'

    String cpsScriptVariableName = 'script'
    List<String> cpsPackages = []
}

class CpsCallFromNonCpsMethodAstVisitor extends AbstractAstVisitor {

    private final static List<String> PIPELINE_STEP_CPS_EXCEPTIONS = ['echo', 'properties', 'getContext']

    private MethodNode currentMethod
    private boolean cpsContext = false

    @Override
    protected void visitMethodEx(MethodNode methodNode) {
        cpsContext = JenkinsUtil.isCpsMethod(methodNode, false)
        currentMethod = methodNode
    }

    @Override
    protected void visitMethodComplete(MethodNode node) {
        currentMethod = null
        cpsContext = false
    }

    @Override
    void visitConstructor(ConstructorNode constructorNode) {
        cpsContext = JenkinsUtil.isCpsMethod(constructorNode, true)
        currentMethod = constructorNode
        for (Parameter parameter : constructorNode.parameters) {
            if (parameter.hasInitialExpression()) {
                parameter.initialExpression.visit(this)
            }
        }
        super.visitConstructor(constructorNode)
        currentMethod = null
        cpsContext = false
    }
    private ClassNode getReceiverClass(MethodCall call, boolean implicit) {
        if (implicit) {
            return currentClassNode
        }
        return JenkinsUtil.getReceiverType(call)
    }

    private boolean isCallOnScriptVariable(MethodCall call) {
        if (call.receiver instanceof VariableExpression) {
            VariableExpression variableExpression = (VariableExpression) call.receiver
            return variableExpression.name == rule.cpsScriptVariableName
        }
        return false
    }

    private static boolean isCpsPipelineStep(MethodCall call) {
        return call.methodAsString !in PIPELINE_STEP_CPS_EXCEPTIONS
    }

    private boolean isInCpsPackage(MethodNode methodNode) {
        if (methodNode.declaringClass.packageName) {
            return rule.cpsPackages.any { String packageName -> methodNode.declaringClass.packageName.startsWith(packageName) }
        }
        // assume always CPS for default package (e.g. Jenkinsfile)
        return true
    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        checkMethodCall(call, !call.receiver)
        super.visitStaticMethodCallExpression(call)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        checkMethodCall(call, call.implicitThis)
        super.visitMethodCallExpression(call)
    }

    private <T extends ASTNode & MethodCall> void checkMethodCall(T call, boolean implicit) {
        if (cpsContext) {
            return
        }

        if (isCallOnScriptVariable(call) && isCpsPipelineStep(call)) {
            if (currentMethod) {
                addViolation(call, "The method ${call.methodAsString} is a CPS transformed pipeline step and may not be called from non-CPS transformed method ${currentClassNode.name}.${currentMethod.name}")
            } else {
                addViolation(call, "The method ${call.methodAsString} is a CPS transformed pipeline step and may not be called from non-CPS transformed initialization code in ${currentClassNode.name}")
            }
            return
        }

        ClassNode receiverClass = getReceiverClass(call, implicit)
        if (receiverClass == ClassHelper.DYNAMIC_TYPE) {
            // can't check methods for dynamic type
            return
        }

        String methodName = call.methodAsString
        if (receiverClass.getMethods(methodName).any { JenkinsUtil.isCpsMethod(it, it instanceof ConstructorNode) && isInCpsPackage(it) }) {
            if (currentMethod) {
                addViolation(call, "The method ${receiverClass.name}.${methodName} is CPS transformed and may not be called from non-CPS transformed method ${currentClassNode.name}.${currentMethod.name}")
            } else {
                addViolation(call, "The method ${receiverClass.name}.$methodName is CPS transformed and may not be called from non-CPS transformed initialization code in ${currentClassNode.name}")
            }
        }
    }
}
