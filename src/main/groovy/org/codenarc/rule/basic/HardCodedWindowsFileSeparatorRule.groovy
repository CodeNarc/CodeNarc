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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.*

/**
 * This rule finds usages of a Windows file separator within the constructor call of a File object. It is better to use the Unix file separator or use the File.separator constant. 
 *
 * @author Hamlet D'Arcy
 */
class HardCodedWindowsFileSeparatorRule extends AbstractAstVisitorRule {
    String name = 'HardCodedWindowsFileSeparator'
    int priority = 2
    Class astVisitorClass = HardCodedWindowsFileSeparatorAstVisitor
}

class HardCodedWindowsFileSeparatorAstVisitor extends AbstractAstVisitor {

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (isFirstVisit(call)) {

            if (AstUtil.isConstructorCall(call, 'File') && call.arguments instanceof ArgumentListExpression) {
                for (Expression exp : call.arguments.expressions) {
                    addViolationForWindowsSeparator(exp)
                }
            }
            super.visitConstructorCallExpression(call)
        }
    }

    private void addViolationForWindowsSeparator(Expression expression) {
        if (expression instanceof ConstantExpression && expression.value instanceof String) {
            if (expression.value.contains('\\')) {
                addViolation(expression, 'The windows file separator is not portable')
            }
        } else if (expression instanceof GStringExpression) {
            if (expression.strings && expression.strings[0].value.contains('\\')) {
                addViolation(expression, 'The windows file separator is not portable')
            }
        }
    }
}
