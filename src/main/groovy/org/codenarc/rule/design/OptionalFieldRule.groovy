/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Do not use an Optional as a field type.
 *
 * @author Chris Mair
 */
class OptionalFieldRule extends AbstractAstVisitorRule {

    String name = 'OptionalField'
    int priority = 2
    Class astVisitorClass = OptionalFieldAstVisitor
}

class OptionalFieldAstVisitor extends AbstractAstVisitor {

    @Override
    void visitField(FieldNode node) {
        if (node.type.name == 'Optional') {
            String message = "The field ${node.name} in class ${currentClassName} is an Optional"
            addViolation(node, message)
        }

        super.visitField(node)
    }
}
