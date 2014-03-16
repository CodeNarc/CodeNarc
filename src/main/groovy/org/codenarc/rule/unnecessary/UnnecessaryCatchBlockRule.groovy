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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Violations are triggered when a catch block does nothing but throw the original exception. In this scenario there is
 * usually no need for a catch block, just let the exception be thrown from the original code. This condition frequently
 * occurs when catching an exception for debugging purposes but then forgetting to take the catch statement out. 
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryCatchBlockRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryCatchBlock'
    int priority = 3
    Class astVisitorClass = UnnecessaryCatchBlockAstVisitor
}

class UnnecessaryCatchBlockAstVisitor extends AbstractAstVisitor {
    @Override
    void visitTryCatchFinally(TryCatchStatement statement) {

        def badNodes = statement.catchStatements?.findAll { CatchStatement it ->
            def paramName = it.variable.name
            if (it.code instanceof BlockStatement && it.code.statements.size() == 1) {
                def throwStatement = it.code.statements[0]
                if (throwStatement instanceof ThrowStatement && AstUtil.isVariable(throwStatement.expression, paramName)) {
                    return true
                }
            }
            false
        }

        if (badNodes.size() == statement.catchStatements?.size()) {
            badNodes.each {
                addViolation it, 'Catch statement can probably be removed.'
            }
        }
        super.visitTryCatchFinally statement
    }

}
