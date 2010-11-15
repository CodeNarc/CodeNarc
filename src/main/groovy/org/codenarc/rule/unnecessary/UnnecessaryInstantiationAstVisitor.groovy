/*
 * Copyright 2010 the original author or authors.
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

import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * Base visitor for unnecessary constructor calls.
 * @author Hamlet D'Arcy
 */
class UnnecessaryInstantiationAstVisitor extends AbstractAstVisitor {

    Class targetType
    List<Class> parameterTypes
    String suffix

    UnnecessaryInstantiationAstVisitor(Class targetType, List<Class> parameterTypes, String suffix) {
        this.targetType = targetType
        this.parameterTypes = parameterTypes
        this.suffix = suffix
    }

    @Override
    final void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (AstUtil.classNodeImplementsType(call.type, targetType)
                && call.arguments instanceof TupleExpression
                && call.arguments.expressions.size() == 1) {
            Expression argument = call.arguments.expressions.head()
            parameterTypes.each { Class clazz ->
                if (argument instanceof ConstantExpression && argument.value.getClass() == clazz) {
                    addViolation call, "Can probably be rewritten as ${argument.value}$suffix"
                }
            }
        }
        super.visitConstructorCallExpression call
    }
}
