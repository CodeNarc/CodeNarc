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
package org.codenarc.rule.security

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * This rule reports violations of the Enterprise JavaBeans specification by using the java.io package to access files or the file system. 
 *
 * @author 'Hamlet D'Arcy'
  */
class JavaIoPackageAccessRule extends AbstractAstVisitorRule {
    String name = 'JavaIoPackageAccess'
    int priority = 2
    Class astVisitorClass = JavaIoPackageAccessAstVisitor
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
}

class JavaIoPackageAccessAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {

        if (AstUtil.classNodeImplementsType(call.type, File)) {
            def argCount = AstUtil.getMethodArguments(call).size()
            if (argCount == 1 || argCount == 2) {
                addViolation(call, 'The use of java.io.File violates the Enterprise Java Bean specification')
            }
        } else if (AstUtil.classNodeImplementsType(call.type, FileOutputStream)) {
            def argCount = AstUtil.getMethodArguments(call).size()
            if (argCount == 1 || argCount == 2) {
                addViolation(call, 'The use of java.io.FileOutputStream violates the Enterprise Java Bean specification')
            }
        } else if (AstUtil.classNodeImplementsType(call.type, FileReader)) {
            def argCount = AstUtil.getMethodArguments(call).size()
            if (argCount == 1) {
                addViolation(call, 'The use of java.io.FileReader violates the Enterprise Java Bean specification')
            }
        } else if (AstUtil.classNodeImplementsType(call.type, RandomAccessFile)) {
            def argCount = AstUtil.getMethodArguments(call).size()
            if (argCount == 2) {
                addViolation(call, 'The use of java.io.RandomAccessFile violates the Enterprise Java Bean specification')
            }
        }

        super.visitConstructorCallExpression(call)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodCallOnObject(call, 'FileSystem')) {
            addViolation(call, 'The use of java.io.FileSystem violates the Enterprise Java Bean specification')
        }

        super.visitMethodCallExpression(call)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        if (expression.objectExpression instanceof VariableExpression) {
            if (expression.objectExpression.variable == 'FileSystem') {
                addViolation(expression, 'The use of java.io.FileSystem violates the Enterprise Java Bean specification')
            }
        }

        super.visitPropertyExpression(expression)
    }
    
}
