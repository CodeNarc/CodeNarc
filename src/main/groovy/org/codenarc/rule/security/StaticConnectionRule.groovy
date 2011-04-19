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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.util.AstUtil
import java.sql.Connection
import java.lang.reflect.Modifier

/**
 * Creates violations when a java.sql.Connection object is used as a static field. Database connections stored in static fields will be shared between threads, which is unsafe and can lead to race conditions.
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class StaticConnectionRule extends AbstractAstVisitorRule {
    String name = 'StaticConnection'
    int priority = 2
    Class astVisitorClass = StaticConnectionAstVisitor
}

class StaticConnectionAstVisitor extends AbstractAstVisitor {
    @Override
    void visitFieldEx(FieldNode node) {

        if (AstUtil.classNodeImplementsType(node.type, Connection) && Modifier.isStatic(node.modifiers)) {
            addViolation(node, "The field $node.name in class $node.owner.name is marked static, meaning the Connection will be shared between threads and will possibly experience race conditions")
        }
        super.visitFieldEx(node)
    }


}
