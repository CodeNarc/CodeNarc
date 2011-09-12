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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * This rule catches usages of java.lang.Boolean.getBoolean(String) which reads a boolean from the System properties.
 * It is often mistakenly used to attempt to read user input or parse a String into a boolean. It is a poor piece of API
 * to use; replace it with System.properties['prop̈́'].
 *
 * @author Hamlet D'Arcy
 */
class BooleanGetBooleanRule extends AbstractAstVisitorRule {
    String name = 'BooleanGetBoolean'
    int priority = 2
    Class astVisitorClass = BooleanGetBooleanAstVisitor
}

class BooleanGetBooleanAstVisitor extends AbstractMethodCallExpressionVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (AstUtil.isMethodCall(call, 'Boolean', 'getBoolean', 1)) {
            addViolation(call, 'Boolean.getBoolean(String) is a confusing API for reading System properties. Prefer the System.getProperty(String) API.')
        }
    }
}
