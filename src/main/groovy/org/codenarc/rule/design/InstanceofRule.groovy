/*
 * Copyright 2014 the original author or authors.
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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.util.WildcardPattern

/**
 * Checks for use of the instanceof operator. Use ignoreTypeNames property to configure ignored type names.
 *
 * @author Chris Mair
 */
class InstanceofRule extends AbstractAstVisitorRule {

    String name = 'Instanceof'
    int priority = 2
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
    String ignoreTypeNames = '*Exception'
    Class astVisitorClass = InstanceofAstVisitor
}

class InstanceofAstVisitor extends AbstractAstVisitor {

    private wildcardPattern

    @Override
    protected void visitClassEx(ClassNode node) {
        wildcardPattern = new WildcardPattern(rule.ignoreTypeNames, false)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        def wildcardPattern = new WildcardPattern(rule.ignoreTypeNames, false)
        if (isFirstVisit(expression)) {
            def op = expression.operation
            String typeName = expression.rightExpression.type.name
            if (op.text == 'instanceof' && !wildcardPattern.matches(typeName)) {
                addViolation(expression, "The instanceof operator is used in class $currentClassName")
            }
        }
        super.visitBinaryExpression(expression)
    }
}
