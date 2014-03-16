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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * Volatile array fields are unsafe because the contents of the array are not treated as volatile. Changing the entire array reference is visible to other threads, but changing an array element is not. 
 *
 * @author Hamlet D'Arcy
 */
class VolatileArrayFieldRule extends AbstractAstVisitorRule {
    String name = 'VolatileArrayField'
    int priority = 2
    Class astVisitorClass = VolatileArrayFieldAstVisitor
}

class VolatileArrayFieldAstVisitor extends AbstractFieldVisitor {
    @Override
    void visitField(FieldNode node) {

        if (Modifier.isVolatile(node.modifiers) && AstUtil.getFieldType(node)?.isArray()) {
            addViolation(node, "The array field $node.name is marked volatile, but the contents of the array will not share the same volatile semantics. Use a different data type")
        }
    }

}
