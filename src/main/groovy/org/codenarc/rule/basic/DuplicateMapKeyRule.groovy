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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * A map literal is created with duplicated key. The map entry will be overwritten.
 *
 * @author 'Łukasz Indykiewicz'
 */
class DuplicateMapKeyRule extends AbstractAstVisitorRule {
    String name = 'DuplicateMapKey'
    int priority = 2
    Class astVisitorClass = DuplicateMapKeyAstVisitor
}

class DuplicateMapKeyAstVisitor extends AbstractAstVisitor {
    @Override
    @SuppressWarnings('UnnecessaryCollectCall')
    void visitMapExpression(MapExpression expression) {

        if(isFirstVisit(expression)) {
            def a = expression.mapEntryExpressions
                    .findAll { it.keyExpression instanceof ConstantExpression }
                    .collect { it.keyExpression }

            a.inject([]) { result, it ->
                if (result.contains(it.value)) {
                    addViolation(it, "Key '${it.value}' is duplicated.")
                } else {
                    result.add(it.value)
                }
                result
            }
            super.visitMapExpression(expression)
        }
    }

}
