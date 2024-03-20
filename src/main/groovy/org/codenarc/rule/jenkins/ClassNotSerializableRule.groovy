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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Classes in Jenkins pipeline libraries and Jenkinsfiles should generally implement the Serializable interface because every expression/variable used in a CPS transformed method can potentially be serialized
 *
 * @author Daniel Zänker
 */
class ClassNotSerializableRule extends AbstractAstVisitorRule {

    String name = 'ClassNotSerializable'
    int priority = 2
    Class astVisitorClass = ClassNotSerializableAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
    String applyToFileNames = 'Jenkinsfile'
}

class ClassNotSerializableAstVisitor extends AbstractAstVisitor {
    @Override
    protected void visitClassEx(ClassNode classNode) {
        if (classNode.script || classNode.interface) {
            return
        }
        if (!AstUtil.classNodeImplementsType(classNode, Serializable)) {
            addViolation(classNode, "Class ${classNode.name} is not Serializable")
        }
    }
}
