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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * String objects should be created with single quotes, and GString objects created with double quotes. Creating normal String objects with double quotes is confusing to readers. 
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryGStringRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryGString'
    int priority = 3
    Class astVisitorClass = UnnecessaryGStringAstVisitor
}

class UnnecessaryGStringAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstantExpression(ConstantExpression expression) {
        if (isFirstVisit(expression) && expression.value instanceof String && expression.lineNumber > -1) {
            if (!expressionContainsBannedCharacters(expression)) {
                suppressException(StringIndexOutOfBoundsException) {
                    addViolationIfDoubleQuoted(expression)
                }
            }
        }
        super.visitConstantExpression(expression)
    }

    private addViolationIfDoubleQuoted(ConstantExpression expression) {
        def line = getSourceCode().getLines()[expression.lineNumber - 1]

        def col = line[expression.columnNumber - 1]
        if (col == '"') {
            addViolation(expression, "The String '$expression.value' can be wrapped in single quotes instead of double quotes")
        }
    }

    @Override
    void visitGStringExpression(GStringExpression expression) {
        // do not visit the string portions of the GString
        visitListOfExpressions(expression.getValues())
    }

    @SuppressWarnings('CatchThrowable')
    private static suppressException(Class exceptionType, Closure c) {
        try {
            c()
        } catch (Throwable t) {
            if (!exceptionType.getClass().isAssignableFrom(t.getClass())) {
                throw t
            }
        }
    }

    private boolean expressionContainsBannedCharacters(ConstantExpression expression) {
        if (expression.value.contains('$')) { return true }
        if (expression.value.contains("'")) { return true }

        if (expression.lineNumber == expression.lastLineNumber) { return false }
        
        def lines = getSourceCode().getLines()
        ((expression.lineNumber - 1).. (expression.lastLineNumber - 1)).any {
            (lines[it].contains('$')) || (lines[it].contains("'"))
        }
    }
}
