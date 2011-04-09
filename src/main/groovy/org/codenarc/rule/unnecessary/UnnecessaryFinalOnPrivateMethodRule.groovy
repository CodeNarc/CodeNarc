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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.MethodNode
import java.lang.reflect.Modifier

/**
 * A private method is marked final. Private methods cannot be overridden, so marking it final is unnecessary. 
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class UnnecessaryFinalOnPrivateMethodRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryFinalOnPrivateMethod'
    int priority = 2
    Class astVisitorClass = UnnecessaryFinalOnPrivateMethodAstVisitor
}

class UnnecessaryFinalOnPrivateMethodAstVisitor extends AbstractAstVisitor {
    @Override
    void visitMethodEx(MethodNode node) {

        if (Modifier.isFinal(node.modifiers) && Modifier.isPrivate(node.modifiers)) {
            addViolation(node, "The '$node.name' method is both private and final")
        }
        super.visitMethodEx(node)
    }


}
