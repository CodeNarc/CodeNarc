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
package org.codenarc.rule.security

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor

/**
 * Finds code that violates secure coding principles for mobile code by declaring a member variable public but not final.
 *
 * @author Hamlet D'Arcy
  */
class NonFinalPublicFieldRule extends AbstractAstVisitorRule {
    String name = 'NonFinalPublicField'
    int priority = 2
    Class astVisitorClass = NonFinalPublicFieldAstVisitor
}

class NonFinalPublicFieldAstVisitor extends AbstractFieldVisitor {
    @Override
    void visitField(FieldNode node) {

        if (node.isPublic() && !node.isFinal()) {
            addViolation(node, "The field $node.name is public but not final, which violates secure coding principles")
        }
    }
}
