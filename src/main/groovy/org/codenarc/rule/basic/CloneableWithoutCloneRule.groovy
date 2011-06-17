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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.Parameter

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

    @Override
    void visitClassEx(ClassNode node) {
        // is this class a Cloneable?
        def cloneableClassNode = ClassHelper.make(Cloneable)
        def isCloneable = node.interfaces.find {
            it == cloneableClassNode || it.name == 'Cloneable'
        }
        if (isCloneable) {
            if (!hasCloneMethod(node)) {
                addViolation(node, "The class $node.name implements Cloneable but does not define a proper clone() method")
            }
        }
        super.visitClassEx(node)
    }

    private boolean hasCloneMethod(ClassNode classNode) {
        classNode.getDeclaredMethod('clone', [] as Parameter[])
    }
}
