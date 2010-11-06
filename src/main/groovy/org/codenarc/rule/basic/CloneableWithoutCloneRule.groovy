/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ClassHelper

/**
 * A class that implements Cloneable should define a clone() method.
 * 
 * @author Hamlet D'Arcy & Rene Groeschke
 * @version $Revision$ - $Date$
 */
class CloneableWithoutCloneRule extends AbstractAstVisitorRule {

    String name = 'CloneableWithoutClone'
    int priority = 2
    Class astVisitorClass = CloneableWithoutCloneAstVisitor
}


class CloneableWithoutCloneAstVisitor extends AbstractAstVisitor  {

    private boolean hasCloneMethod
    
    def void visitClassEx(ClassNode node) {
        // is this class a Clonable?
        def cloneableClassNode = ClassHelper.make(Cloneable)
        def isCloneable = node.interfaces.find {
            it == cloneableClassNode || it.name == "Cloneable"
        }
        if (isCloneable) {
            if (!hasCloneMethod) {
                addViolation(node)
            }
        }
        super.visitClassEx(node);
    }

    protected void visitConstructorOrMethodEx(MethodNode node, boolean isConstructor) {
        // is this method a clone method?
        if ((node.name == "clone") && (!node.parameters)) {
            hasCloneMethod = true
        }
        super.visitConstructorOrMethodEx(node, isConstructor);
    }
}
