/*
 * Copyright 2018 the original author or authors.
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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Enforce that all static fields are above all instance fields within a class
 *
 * @author Chris Mair
 */
class StaticFieldsBeforeInstanceFieldsRule extends AbstractAstVisitorRule {

    String name = 'StaticFieldsBeforeInstanceFields'
    int priority = 3
    Class astVisitorClass = StaticFieldsBeforeInstanceFieldsAstVisitor
}

class StaticFieldsBeforeInstanceFieldsAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassComplete(ClassNode node) {
        boolean hasDeclaredInstanceField = false
        def fields = node.getFields()
        fields.each { FieldNode fieldNode ->
            if (fieldNode.static) {
                if (hasDeclaredInstanceField) {
                    addViolation(fieldNode, "The static field $fieldNode.name in class ${node?.name} is declared after an instance field")
                }
            }
            else {
                hasDeclaredInstanceField = true
            }
        }
        super.visitClassComplete(node)
    }

}
