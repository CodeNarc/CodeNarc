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

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern

/**
 * Checks that field types are explicitly specified (and not using def)
 *
 * @author Chris Mair
 */
class FieldTypeRequiredRule extends AbstractAstVisitorRule {

    String name = 'FieldTypeRequired'
    int priority = 3
    Class astVisitorClass = FieldTypeRequiredAstVisitor
    String ignoreFieldNames

}

class FieldTypeRequiredAstVisitor extends AbstractAstVisitor {

    @Override
    void visitField(FieldNode node) {
        if (node.isDynamicTyped() && !isIgnoredFieldName(node)) {
            addViolation(node, $/The type is not specified for field "$node.name"/$)
        }

        super.visitField(node)
    }

    private boolean isIgnoredFieldName(FieldNode node) {
        new WildcardPattern(rule.ignoreFieldNames, false).matches(node.name)
    }

}
