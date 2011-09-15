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

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.util.AstUtil

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

    @SuppressWarnings('UnusedMethodParameter')
    protected boolean isTypeSuffixNecessary(argument) {
        return true
    }

    @Override
    final void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (!isFirstVisit(call)) {
            return
        }
        if (AstUtil.classNodeImplementsType(call.type, targetType)
                && call.arguments instanceof TupleExpression
                && call.arguments.expressions.size() == 1) {
            Expression argument = call.arguments.expressions.head()
            parameterTypes.each { Class clazz ->
                if (argument instanceof ConstantExpression
                        && argument.value.getClass() == clazz
                        && !shouldSkipViolation(argument.value)) {
                    boolean isSuffixNecessary = isTypeSuffixNecessary(argument.value)
                    def replacementOptions = isSuffixNecessary ?
                        "${argument.value}$suffix" :
                        "${argument.value} or ${argument.value}$suffix"
                    addViolation call, "Can be rewritten as $replacementOptions"
                }
            }
        }
        super.visitConstructorCallExpression call
    }

    @SuppressWarnings('UnusedMethodParameter')
    protected boolean shouldSkipViolation(Object value) {
        false
    }
}
