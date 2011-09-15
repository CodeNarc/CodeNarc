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
package org.codenarc.rule.logging

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * If you are logging an exception then the proper API is to call error(Object, Throwable), which will log the message
 * and the exception stack trace. If you call error(Object) then the stacktrace may not be logged.
 *
 * @author Hamlet D'Arcy
  */
class LoggingSwallowsStacktraceRule extends AbstractAstVisitorRule {
    String name = 'LoggingSwallowsStacktrace'
    int priority = 2
    Class astVisitorClass = LoggingSwallowsStacktraceAstVisitor
}

class LoggingSwallowsStacktraceAstVisitor extends AbstractAstVisitor {
    Map<ClassNode, List<String>> classNodeToLoggerNames = [:]

    @Override
    void visitField(FieldNode fieldNode) {
        if (LogUtil.isMatchingLoggerDefinition(fieldNode.getInitialExpression())) {

            List<String> logFields = classNodeToLoggerNames[fieldNode.declaringClass]
            if (logFields) {
                logFields.add(fieldNode.name)
            } else {
                classNodeToLoggerNames[fieldNode.declaringClass] = [fieldNode.name]
            }
        }
        super.visitField(fieldNode)
    }

    @Override
    void visitCatchStatement(CatchStatement statement) {

        if (currentClassNode && statement.code instanceof BlockStatement) {
            List<String> loggerNames = classNodeToLoggerNames[currentClassNode]

            def expressions = statement.code.statements.findAll {
                it instanceof ExpressionStatement && it.expression instanceof MethodCallExpression
            }
            expressions*.expression.each { MethodCallExpression it ->
                if (AstUtil.isMethodCall(it, loggerNames, ['error'], 1)) {
                    addViolation(it, 'The error logging may hide the stacktrace from the exception named ' + statement.variable.name)
                }
            }
        }
        super.visitCatchStatement(statement)
    }
}
