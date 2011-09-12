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
package org.codenarc.rule.jdbc

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * The J2EE standard requires that applications use the container's resource management facilities to obtain
 * connections to resources. Every major web application container provides pooled database connection management as
 * part of its resource management framework. Duplicating this functionality in an application is difficult and
 * error prone, which is part of the reason it is forbidden under the J2EE standard.
 *
 * @author 'Hamlet D'Arcy'
  */
class DirectConnectionManagementRule extends AbstractAstVisitorRule {
    String name = 'DirectConnectionManagement'
    int priority = 2
    Class astVisitorClass = DirectConnectionManagementAstVisitor
}

class DirectConnectionManagementAstVisitor extends AbstractMethodCallExpressionVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodCall(call, 'DriverManager', 'getConnection')) {
            addViolation(call, 'Using DriverManager.getConnection() violates the J2EE standards. Use the connection from the context instead')
        } else if (AstUtil.isMethodNamed(call, 'getConnection') && isJavaSQLDriverManagerCall(call)) {
            addViolation(call, 'Using DriverManager.getConnection() violates the J2EE standards. Use the connection from the context instead')
        }
    }

    private static boolean isJavaSQLDriverManagerCall(MethodCallExpression call) {
        if (AstUtil.isPropertyNamed(call.objectExpression, 'DriverManager')) {
            if (AstUtil.isPropertyNamed(call.objectExpression.objectExpression, 'sql')) {
                if (AstUtil.isVariable(call.objectExpression.objectExpression.objectExpression, 'java')) {
                    return true
                }
            }
        }
        false
    }
}
