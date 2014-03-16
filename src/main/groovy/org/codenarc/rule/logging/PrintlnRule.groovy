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
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks for calls to <code>this.print()</code>, <code>this.println()</code>
 * or <code>this.printf()</code>.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class PrintlnRule extends AbstractAstVisitorRule {
    String name = 'Println'
    int priority = 2
    Class astVisitorClass = PrintlnAstVisitor
}

class PrintlnAstVisitor extends AbstractAstVisitor  {

    boolean printlnMethodDefined = false
    boolean printlnClosureDefined = false

    @Override
    protected void visitClassEx(ClassNode node) {
        printlnMethodDefined = node?.methods?.any { MethodNode it ->
            it.name == 'println' || it.name == 'print'
        }
        printlnClosureDefined = node?.fields?.any { FieldNode it ->
            AstUtil.isClosureDeclaration(it) && (it.name == 'println' || it.name == 'print')
        }
    }

    @Override protected void visitClassComplete(ClassNode node) {
        printlnMethodDefined = false
        printlnClosureDefined = false
    }

    @SuppressWarnings('DuplicateLiteral')
    void visitMethodCallExpression(MethodCallExpression methodCall) {
        if (printlnMethodDefined || printlnClosureDefined) {
            return
        }
        
        if (isFirstVisit(methodCall)) {
            def isMatch =
                AstUtil.isMethodCall(methodCall, 'this', 'println', 0) ||
                AstUtil.isMethodCall(methodCall, 'this', 'println', 1) ||
                AstUtil.isMethodCall(methodCall, 'this', 'print', 1) ||
                (AstUtil.isMethodCall(methodCall, 'this', 'printf') && AstUtil.getMethodArguments(methodCall).size() > 1)

            if (isMatch) {
                addViolation(methodCall, 'println should be replaced with something more robust')
            }
        }
        super.visitMethodCallExpression(methodCall)
    }
}
