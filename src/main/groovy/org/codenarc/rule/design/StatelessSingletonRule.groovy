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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * There is no benefit in creating a stateless Singleton. Make a new instance with the new keyword instead.
 *
 * @author 'Victor Savkin'
  */
class StatelessSingletonRule extends AbstractAstVisitorRule {
    String name = 'StatelessSingleton'
    int priority = 2
    Class astVisitorClass = StatelessSingletonAstVisitor
    String instanceRegex = 'instance|_instance'
}

class StatelessSingletonAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassComplete(ClassNode node) {
        if (isSingleton(node) && !doesExtendClass(node) && !hasState(node)) {
            addViolation node, 'There is no point in creating a stateless Singleton. ' +
                "Make a new instance of '${node.nameWithoutPackage}' with the new keyword instead."
        }
        super.visitClassComplete node
    }

    private isSingleton(classNode) {
        hasSingletonAnnotation(classNode) ||
            hasOneStaticFieldOfItself(classNode) ||
            hasOneStaticFieldNamedInstance(classNode)
    }

    private static doesExtendClass(classNode) {
        classNode.superClass.name != Object.name
    }

    private hasState(classNode) {
        classNode.fields.any {
            it.type.name != classNode.name && !(it.name ==~ rule.instanceRegex)
        }
    }

    private static hasOneStaticFieldOfItself(classNode) {
        classNode.fields.findAll {
            it.static && it.type.name == classNode.name
        }.size() == 1
    }

    private hasOneStaticFieldNamedInstance(classNode) {
        classNode.fields.findAll {
            it.static && it.type.name == Object.name && it.name ==~ rule.instanceRegex
        }.size() == 1
    }

    private static hasSingletonAnnotation(classNode) {
        classNode?.annotations?.any { it.classNode.name == Singleton.name }
    }
}
