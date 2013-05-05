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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitor

/**
 * AstVisitor that checks for no-arg constructor calls for the type specified in the constructor.
 * Used by the ExplicitXxxInstantiation rules.
 *
 * @author Chris Mair
 */
abstract class ExplicitTypeInstantiationAstVisitor extends AbstractAstVisitor {

    private final String typeName

    protected ExplicitTypeInstantiationAstVisitor(String typeName) {
        this.typeName = typeName
    }

    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (isFirstVisit(call) && call?.type?.name == typeName && call.arguments.expressions.empty) {
            addViolation call, createErrorMessage()
        }
        super.visitConstructorCallExpression call
    }

    abstract protected String createErrorMessage()
}
