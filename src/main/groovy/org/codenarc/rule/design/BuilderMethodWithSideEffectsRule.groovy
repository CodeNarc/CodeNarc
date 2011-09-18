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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor

/**
 * A builder method is defined as one that creates objects. As such, they should never be of void return type. If a method is named build, create, or make, then it should always return a value. 
 *
 * @author Hamlet D'Arcy
 */
class BuilderMethodWithSideEffectsRule extends AbstractAstVisitorRule {
    String name = 'BuilderMethodWithSideEffects'
    int priority = 2
    Class astVisitorClass = BuilderMethodWithSideEffectsAstVisitor
    String methodNameRegex = '(create.*|make.*|build.*)'
}

class BuilderMethodWithSideEffectsAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {

        if (node.name ==~ rule.methodNameRegex && node.isVoidMethod()) {
            addViolation(node, "The method '$node.name' is named like a builder method but has a void return type")
        }
    }
}
