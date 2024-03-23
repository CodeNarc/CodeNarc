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
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor

/**
 * Overridden methods of the standard library (e.g. from java.lang.Object) are often called from there and therefore must not be CPS transformed in Jenkins
 *
 * @author Daniel Zänker
 */
class OverridesNotNonCpsRule extends AbstractAstVisitorRule {

    String name = 'OverridesNotNonCps'
    int priority = 2
    Class astVisitorClass = OverridesNotNonCpsAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
    String applyToFileNames = 'Jenkinsfile'
}

class OverridesNotNonCpsAstVisitor extends AbstractMethodVisitor {
    final static List<Map> OBJECT_METHODS = [
        [name: 'clone', parameters: []],
        [name: 'equals', parameters: [ClassHelper.make(Object)]],
        [name: 'finalize', parameters: []],
        [name: 'getClass', parameters: []],
        [name: 'hashCode', parameters: []],
        [name: 'notify', parameters: []],
        [name: 'notifyAll', parameters: []],
        [name: 'toString', parameters: []],
        [name: 'wait', parameters: []],
        [name: 'wait', parameters: [ClassHelper.make(Long)]],
        [name: 'wait', parameters: [ClassHelper.make(Long), ClassHelper.make(Integer)]],
    ]

    private static boolean isObjectMethod(MethodNode methodNode) {
        List<ClassNode> wrappedTypes = methodNode.parameters.collect { ClassHelper.getWrapper(it.type) }
        Map anyObjectMethod = OBJECT_METHODS.find {
            it.name == methodNode.name && it.parameters == wrappedTypes
        }
        return anyObjectMethod != null
    }

    @Override
    void visitMethod(MethodNode method) {
        if (isObjectMethod(method) && JenkinsUtil.isCpsMethod(method, false)) {
            addViolation(method, 'Overridden methods from Object should not be CPS transformed')
        }
    }
}
