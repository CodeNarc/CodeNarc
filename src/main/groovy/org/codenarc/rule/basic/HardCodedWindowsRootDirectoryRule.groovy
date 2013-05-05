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
 * This rule find cases where a File object is constructed with a windows-based path. This is not portable, and using the File.listRoots() method is a better alternative. 
 *
 * @author Hamlet D'Arcy
 */
class HardCodedWindowsRootDirectoryRule extends AbstractAstVisitorRule {
    String name = 'HardCodedWindowsRootDirectory'
    int priority = 2
    Class astVisitorClass = HardcodedWindowsRootDirectoryAstVisitor
}

class HardcodedWindowsRootDirectoryAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (isFirstVisit(call)) {

            if (AstUtil.isConstructorCall(call, 'File') && call.arguments instanceof ArgumentListExpression) {
                for (Expression exp : call.arguments.expressions) {
                    addViolationForWindowsPath(exp)
                }
            }
            super.visitConstructorCallExpression(call)
        }
    }

    private void addViolationForWindowsPath(Expression expression) {
        if (expression instanceof ConstantExpression && expression.value instanceof String) {
            if (expression.value ==~ /[a-zA-Z]:\\.*/) {
                addViolation(expression, "The file location ${expression.value[0..2]} is not portable")
            }
        } else if (expression instanceof GStringExpression) {
            if (expression.strings && expression.strings[0].value ==~ /[a-zA-Z]:\\.*/) {
                addViolation(expression, "The file location ${expression.strings[0].value[0..2]} is not portable")
            }
        }
    }
}
