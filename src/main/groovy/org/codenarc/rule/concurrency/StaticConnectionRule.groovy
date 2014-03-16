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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier
import java.sql.Connection

/**
 * Creates violations when a java.sql.Connection object is used as a static field. Database connections stored in static fields will be shared between threads, which is unsafe and can lead to race conditions.
 *
 * @author 'Hamlet D'Arcy'
 */
class StaticConnectionRule extends AbstractAstVisitorRule {
    String name = 'StaticConnection'
    int priority = 2
    Class astVisitorClass = StaticConnectionAstVisitor
}

class StaticConnectionAstVisitor extends AbstractFieldVisitor {
    @Override
    void visitField(FieldNode node) {

        if (AstUtil.classNodeImplementsType(node.type, Connection) && Modifier.isStatic(node.modifiers)) {
            addViolation(node, "The field $node.name is marked static, meaning the Connection will be shared between threads and will possibly experience race conditions")
        }
    }

}
