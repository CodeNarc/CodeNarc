/*
 * Copyright 2013 the original author or authors.
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
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.util.AstUtil

/**
 * The method clone() should only be declared if the class implements the Cloneable interface.
 *
 * @author ArturGajowy
 */
class CloneWithoutCloneableRule extends AbstractAstVisitorRule {
    String name = 'CloneWithoutCloneable'
    int priority = 2
    Class astVisitorClass = CloneWithoutCloneableAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
}

class CloneWithoutCloneableAstVisitor extends AbstractAstVisitor {
    
    @Override
    protected void visitClassEx(ClassNode node) {
        def cloneMethod = cloneMethod(node)
        if (cloneMethod && !isCloneable(node)) {
            addViolation(cloneMethod, "Class $node.name declares a clone() method, but does not implement java.lang.Cloneable interface")
        }
    }

    private MethodNode cloneMethod(ClassNode classNode) {
        classNode.getDeclaredMethod('clone', [] as Parameter[])
    }

    private boolean isCloneable(ClassNode classNode) {
        AstUtil.classNodeImplementsType(classNode, Cloneable)
    }
}
