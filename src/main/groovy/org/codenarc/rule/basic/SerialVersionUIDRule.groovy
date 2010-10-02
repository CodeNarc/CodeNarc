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
import org.codehaus.groovy.ast.FieldNode
import java.lang.reflect.Modifier
import org.codehaus.groovy.ast.PropertyNode

/**
 * Rule that checks that serialVersionUID field is static final and type long, and is not a property.
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class SerialVersionUIDRule extends AbstractAstVisitorRule {
    String name = 'SerialVersionUID'
    int priority = 2
    Class astVisitorClass = SerialVersionUIDAstVisitor
}

class SerialVersionUIDAstVisitor extends AbstractAstVisitor {

    def void visitField(FieldNode node) {
        if (node?.name == 'serialVersionUID') {
            if (!Modifier.isStatic(node?.modifiers)) {
                addViolation node, 'serialVersionUID found that is not static. '
            }
            if (!Modifier.isFinal(node?.modifiers)) {
                addViolation node, 'serialVersionUID found that is not final. '
            }
            if (node?.type?.name != 'long') {
                addViolation node, 'serialVersionUID found that is not long. Found: ' + node?.type?.name
            }
        }
    }

    def void visitProperty(PropertyNode node) {
        if (node?.name == 'serialVersionUID') {
            addViolation node, 'serialVersionUID found that is a property. '
        }
    }


}
