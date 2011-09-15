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

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * Avoid instantiating an object just to call getClass() on it; use the .class public member instead.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryInstantiationToGetClassRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryInstantiationToGetClass'
    int priority = 3
    Class astVisitorClass = UnnecessaryInstantiationToGetClassAstVisitor
}

class UnnecessaryInstantiationToGetClassAstVisitor extends AbstractMethodCallExpressionVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodNamed(call, 'getClass', 0)) {
            if (call.objectExpression instanceof ConstructorCallExpression) {
                def typeName = call.objectExpression.type.name
                addViolation(call, "$typeName instantiation with getClass() should be simplified to ${typeName}.class")
            }
        }
    }
}
