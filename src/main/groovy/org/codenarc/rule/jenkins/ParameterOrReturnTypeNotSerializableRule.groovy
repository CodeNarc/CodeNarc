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

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.rule.serialization.SerializationUtil

/**
 * Every parameter and return type has to implement the Serializable interface in Jenkins
 *
 * @author Daniel Zänker
 */
class ParameterOrReturnTypeNotSerializableRule extends AbstractAstVisitorRule {

    String name = 'ParameterOrReturnTypeNotSerializable'
    int priority = 2
    Class astVisitorClass = ParameterOrReturnTypeNotSerializableAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
    String applyToFileNames = 'Jenkinsfile'
}

class ParameterOrReturnTypeNotSerializableAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode method) {
        method.parameters.each {
            if (!SerializationUtil.isSerializableOrDynamicType(it.type)) {
                addViolation(method, "Parameter ${it.name} of method ${currentClassNode.name}.${method.name} is not Serializable")
            }
        }

        if (!SerializationUtil.isSerializableOrDynamicType(method.returnType)) {
            addViolation(method, "Return type ${method.returnType.name} of method ${currentClassNode.name}.${method.name} is not Serializable")
        }
    }
}
