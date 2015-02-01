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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * In Java and Groovy, you can specify long literals with the L or l character, for instance 55L or
 * 24l. It is best practice to always use an uppercase L and never a lowercase l. This is
 * because 11l rendered in some fonts may look like 111 instead of 11L.
 *
 * @author Hamlet D'Arcy
 */
class LongLiteralWithLowerCaseLRule extends AbstractAstVisitorRule {
    String name = 'LongLiteralWithLowerCaseL'
    int priority = 2
    Class astVisitorClass = LongLiteralWithLowerCaseLAstVisitor
}

class LongLiteralWithLowerCaseLAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstantExpression(ConstantExpression expression) {

        if (!isFirstVisit(expression)) {
            return
        }

        if (expression.type.name in ['long', 'java.lang.Long']) {
            def line = getSourceCode().lines[expression.lineNumber - 1]
            if (line?.length() <= expression.lastColumnNumber) {
                def definition = line[expression.lastColumnNumber - 2]
                if (definition == 'l') {
                    addViolation(expression, "The literal ${expression.value}l should be rewritten ${expression.value}L")
                }
            }
        }
        super.visitConstantExpression(expression)
    }
}
