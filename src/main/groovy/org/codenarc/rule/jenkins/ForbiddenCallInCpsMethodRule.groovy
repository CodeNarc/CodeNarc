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

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Some methods from the standard library cannot be CPS transformed and therefore must not be called from CPS transformed methods in Jenkins
 *
 * @author Daniel Zänker
 */
class ForbiddenCallInCpsMethodRule extends AbstractAstVisitorRule {

    String name = 'ForbiddenCallInCpsMethod'
    int priority = 2
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
    Class astVisitorClass = ForbiddenCallInCpsMethodAstVisitor
}

class ForbiddenCallInCpsMethodAstVisitor extends AbstractAstVisitor {
    protected List<Map> forbiddenMethods = [
        [type: 'java.lang.Iterable', name: 'sort', parameters: ['groovy.lang.Closure']],
        [type: 'java.lang.Iterable', name: 'toSorted', parameters: ['groovy.lang.Closure']],
        [type: 'java.lang.CharSequence', name: 'eachLine', parameters: ['groovy.lang.Closure']],
        [type: 'java.lang.CharSequence', name: 'eachLine', parameters: ['java.lang.Integer', 'groovy.lang.Closure']]
    ]

    @Override
    protected void visitConstructorOrMethod(MethodNode method, boolean isConstructor) {
        boolean isCpsContext = JenkinsUtil.isCpsMethod(method, isConstructor)
        if (isCpsContext) {
            super.visitConstructorOrMethod(method, isConstructor)
        }
    }

    private Map findForbiddenMethod(MethodCallExpression call) {
        ClassNode type = JenkinsUtil.getReceiverType(call)
        List<String> allInterfaceNames = type.allInterfaces*.name
        List<String> allTypeNames = allInterfaceNames + type.name
        List<ClassNode> wrappedArgumentTypes = AstUtil.getMethodArguments(call).collect { ClassHelper.getWrapper(it.type) }

        for (Map forbiddenMethod : forbiddenMethods) {
            if (allTypeNames.contains(forbiddenMethod.type)) {
                boolean methodMatches = call.methodAsString == forbiddenMethod.name &&
                    wrappedArgumentTypes*.name == forbiddenMethod.parameters
                if (methodMatches) {
                    return forbiddenMethod
                }
            }
        }
        return [:]
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        Map method = findForbiddenMethod(call)
        if (!method.isEmpty()) {
            addViolation(call, "Method ${method.type}.${method.name}(${method.parameters.join(', ')}) is forbidden in CPS transformed methods")
        }

        super.visitMethodCallExpression(call)
    }
}
