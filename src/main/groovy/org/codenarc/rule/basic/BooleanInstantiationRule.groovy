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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * Rule that checks for direct call to Boolean constructor - use Boolean.valueOf() instead.
 * Also checks for Boolean.valueOf(true) or Boolean.valueOf(false) - use Boolean.TRUE or
 * Boolean.FALSE instead.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class BooleanInstantiationRule extends AbstractAstVisitorRule {
    String name = 'BooleanInstantiation'
    int priority = 2
    Class astVisitorClass = BooleanInstantiationAstVisitor
}

class BooleanInstantiationAstVisitor extends AbstractConstructorCallAstVisitor {
    static final NEW_BOOLEAN = /new +(java\.lang\.)?Boolean\(/

    protected isConstructorCallAViolation(ConstructorCallExpression constructorCall) {
        constructorCall.text =~ NEW_BOOLEAN
    }

    void visitMethodCallExpression(MethodCallExpression methodCall) {
        if (isFirstVisit(methodCall)) {
            def args = AstUtil.getMethodArguments(methodCall)
            def isMatch = AstUtil.isMethodCall(methodCall, 'Boolean', 'valueOf', 1) &&
                args[0] instanceof ConstantExpression && args[0].value in [true, false]
            if (isMatch) {
                addViolation(methodCall)
            }
        }
        super.visitMethodCallExpression(methodCall)
    }
}