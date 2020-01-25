/*
 * Copyright 2018 the original author or authors.
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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.ImportNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Do not use java.util.Date. Prefer the classes in the java.time.* packages. This rule checks for
 * construction of new java.util.Date objects.
 *
 * @author Eric Helgeson
 * @author Chris Mair
 */
class NoJavaUtilDateRule extends AbstractAstVisitorRule {

    String name = 'NoJavaUtilDate'
    int priority = 2
    Class astVisitorClass = NoJavaUtilDateAstVisitor
}

class NoJavaUtilDateAstVisitor extends AbstractAstVisitor {

    protected static final String VIOLATION_MESSAGE = 'Do not use java.util.Date. Prefer the classes in the java.time.* packages.'

    private boolean importsDateClass = false

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (isFirstVisit(call)) {
            if (call.type.name == 'java.util.Date' || (!importsDateClass && call.type.name == 'Date')) {
                addViolation(call, VIOLATION_MESSAGE)
            }
        }
        super.visitConstructorCallExpression(call)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (isFirstVisit(call) && !importsDateClass) {
            if (AstUtil.isMethodCall(call, 'Date', 'parse', 1) || AstUtil.isMethodCall(call, 'Date', 'UTC', 6)) {
                addViolation(call, VIOLATION_MESSAGE)
            }
        }
    }

    @Override
    void visitImports(ModuleNode node) {
        def allImports = node.imports
        this.importsDateClass = allImports?.find { ImportNode importNode -> importNode.alias == 'Date' }
        super.visitImports(node)
    }
}
