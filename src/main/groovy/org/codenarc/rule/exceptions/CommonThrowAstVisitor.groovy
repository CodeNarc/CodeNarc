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
package org.codenarc.rule.exceptions

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codenarc.rule.AbstractAstVisitor

/**
 * AstVisitor implementation that checks for throwing one of the exception type names specified in the constructor
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
  */
class CommonThrowAstVisitor extends AbstractAstVisitor  {

    private final List exceptionTypeNames

    /**
     * Construct a new instance, specifying the exception type names
     * @param exceptionTypeNames - the names of the exception class; typically including class name
     *      with and without package
     */
    CommonThrowAstVisitor(List exceptionTypeNames) {
        this.exceptionTypeNames = exceptionTypeNames
    }

    void visitThrowStatement(ThrowStatement throwStatement) {
        def throwExpression = throwStatement.expression
        if (isFirstVisit(throwStatement) &&
                throwExpression instanceof ConstructorCallExpression &&
                throwExpression.type.name in exceptionTypeNames) {
            addViolation(throwStatement, "The type $throwExpression.type.name should not be thrown")
        }
        super.visitThrowStatement(throwStatement)
    }

}
