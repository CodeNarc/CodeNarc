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
 * Using Class.forName(...) is a common way to add dynamic behavior to a system. However, using this method can cause resource leaks because the classes can be pinned in memory for long periods of time. 
 *
 * @author 'Hamlet D'Arcy'
 */
class ClassForNameRule extends AbstractAstVisitorRule {
    String name = 'ClassForName'
    int priority = 2
    Class astVisitorClass = ClassForNameAstVisitor
}

class ClassForNameAstVisitor extends AbstractMethodCallExpressionVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression node) {

            if (AstUtil.isMethodCall(node, 'Class', 'forName', 1) || AstUtil.isMethodCall(node, 'Class', 'forName', 3)) {
                addViolation(node, 'Methods calls to Class.forName(...) can create resource leaks and should almost always be replaced with calls to ClassLoader.loadClass(...)')
            }
    }
}
