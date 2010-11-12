/*
 * Copyright 2009 the original author or authors.
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

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * This rule checks for the explicit instantiation of an ArrayList. In Groovy, it is best to write "new ArrayList()" as "[]", which creates the same object.
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class ExplicitArrayListInstantiationRule extends AbstractAstVisitorRule {
    String name = 'ExplicitArrayListInstantiation'
    int priority = 2
    Class astVisitorClass = ExplicitArrayListInstantiationAstVisitor
}

class ExplicitArrayListInstantiationAstVisitor extends AbstractAstVisitor {

    def void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (isFirstVisit(call) && call?.type?.name == 'ArrayList') {
            addViolation call
        }
        super.visitConstructorCallExpression call
    }
}
