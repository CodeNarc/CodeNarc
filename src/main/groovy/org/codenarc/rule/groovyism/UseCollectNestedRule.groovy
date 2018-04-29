/*
 * Copyright 2012 the original author or authors.
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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Instead of nested collect{}-calls use collectNested{}
 *
 * @author Joachim Baumann
 * @author Chris Mair
 */
class UseCollectNestedRule extends AbstractAstVisitorRule {

    protected static final String MESSAGE = 'Instead of nested collect{}-calls use collectNested{}'

    String name = 'UseCollectNested'
    int priority = 2
    Class astVisitorClass = UseCollectNestedAstVisitor

}

class UseCollectNestedAstVisitor extends AbstractAstVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(UseCollectNestedAstVisitor)

    private final Stack<Parameter> parameterStack = []

    @Override
    protected void visitClassComplete(ClassNode cn) {
        if (parameterStack.size() != 0) {
            LOG.warn("Internal Error for ${cn.name}: Visits are unbalanced")
        }
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        boolean isCollectCall = false
        Parameter parameter
        Parameter it = new Parameter(ClassHelper.OBJECT_TYPE, 'it')

        /*
         The idea for this rule is to add the parameter of the
         closure used in the collect call to the stack of parameters,
         and check for each collect expression whether it is called on
         the parameter on the top of the stack
         */
        if(AstUtil.isMethodCall(call, 'collect', 1..2)) {
            Expression expression = getMethodCallParameterThatIsAClosure(call)
            isCollectCall = expression instanceof ClosureExpression

            if (isCollectCall) {
                parameter = getClosureParameter(expression, it)
                checkForCallToClosureParameter(call)
            }
        }

        addArgumentList(isCollectCall, parameter)

        super.visitMethodCallExpression(call)

        removeArgumentList(isCollectCall)
    }

    private Parameter getClosureParameter(ClosureExpression expression, Parameter it) {
        Parameter param
        if (expression.parameters.size() != 0) {
            // we assume correct syntax and thus only one parameter
            param = expression.parameters[0]
        }
        else {
            // implicit parameter, we use our own parameter object as placeholder
            param = it
        }
        return param
    }

    private Expression getMethodCallParameterThatIsAClosure(MethodCallExpression call) {
        int arity = AstUtil.getMethodArguments(call).size()
        Expression expression
        if (arity == 1) {
            // closure is the first parameter
            expression = call.arguments.expressions[0]
        } else {
            // closure is second parameter
            expression = call.arguments.expressions[1]
        }
        return expression
    }

    private void checkForCallToClosureParameter(MethodCallExpression call) {
        // Now if the call is to the parameter of the closure then the node on
        // which collect is called has to be a VariableExpression
        if (call.objectExpression instanceof VariableExpression
            && !parameterStack.empty()
            && parameterStack.peek().name == call.objectExpression.name) {
            addViolation(call, UseCollectNestedRule.MESSAGE)
        }
    }

    private void addArgumentList(boolean isCollectCall, Parameter param) {
        if (isCollectCall) {
            parameterStack.push(param)
        }
    }

    private void removeArgumentList(boolean isCollectCall) {
        if (isCollectCall) {
            parameterStack.pop()
        }
    }

}
