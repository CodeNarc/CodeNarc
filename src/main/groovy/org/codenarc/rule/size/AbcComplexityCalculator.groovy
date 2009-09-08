package org.codenarc.rule.size

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PostfixExpression
import org.codehaus.groovy.ast.expr.PrefixExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil

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

/**
 * ???????????
 *
 * See http://www.softwarerenovation.com/ABCMetric.pdf
 *
 * @author Chris Mair
 * @version $Revision: 120 $ - $Date: 2009-04-06 12:58:09 -0400 (Mon, 06 Apr 2009) $
 */
class AbcComplexityCalculator {
    SourceCode sourceCode

    def calculate(MethodNode methodNode) {
        def visitor = new AbcComplexityAstVisitor(sourceCode:sourceCode)
        visitor.visitMethod(methodNode)
        def result = [visitor.numberOfAssignments, visitor.numberOfBranches, visitor.numberOfConditions]
        println "result=$result"
        return result
    }
}

class AbcComplexityAstVisitor extends AbstractAstVisitor {
    private static final ASSIGNMENT_OPERATIONS =
        ['=', '++', '--', '+=', '-=', '/=', '*=', '%=', '<<=', '>>=', '>>>=', '&=', '|=', '^=']
    private static final COMPARISON_OPERATIONS = ['<', '>', '>=', '<=', '==', '!=']

    int numberOfAssignments = 0
    int numberOfBranches = 0
    int numberOfConditions = 0

    void visitBinaryExpression(BinaryExpression expression) {
        handleExpressionContainingOperation(expression) 
        super.visitBinaryExpression(expression)
    }

    void visitPrefixExpression(PrefixExpression expression) {
        handleExpressionContainingOperation(expression)
        super.visitPrefixExpression(expression)
    }

    void visitPostfixExpression(PostfixExpression expression) {
        handleExpressionContainingOperation(expression)
        super.visitPostfixExpression(expression)
    }

    void visitMethodCallExpression(MethodCallExpression call)  {
        numberOfBranches ++
        super.visitMethodCallExpression(call)
    }

    void visitPropertyExpression(PropertyExpression expression) {
        // Treat a property access as a method call
        numberOfBranches ++
        super.visitPropertyExpression(expression)
    }

    void visitConstructorCallExpression(ConstructorCallExpression call) {
        numberOfBranches ++
        super.visitConstructorCallExpression(call)
    }

    void visitIfElse(IfStatement ifElse) {
        if (isNotEmptyStatement(ifElse.elseBlock)) {
            numberOfConditions ++
        }
        super.visitIfElse(ifElse)
    }

    void visitSwitch(SwitchStatement statement) {
        numberOfConditions += statement.caseStatements.size()
        if (isNotEmptyStatement(statement.defaultStatement)) {
            numberOfConditions ++
        }
        super.visitSwitch(statement)
    }

    void visitTryCatchFinally(TryCatchStatement statement) {
        numberOfConditions ++                                   // for the 'try'
        numberOfConditions += statement.catchStatements.size()  // for each 'catch'
        super.visitTryCatchFinally(statement)
    }

    void visitTernaryExpression(TernaryExpression expression) {
        numberOfConditions ++
        super.visitTernaryExpression(expression)
    }

    //--------------------------------------------------------------------------
    // Internal helper methods
    //--------------------------------------------------------------------------

    private void handleExpressionContainingOperation(Expression expression) {
        def operationName = expression.operation.text
        if (operationName in ASSIGNMENT_OPERATIONS && !isFinalVariableDeclaration(expression)) {
            numberOfAssignments ++
        }
        if (operationName in COMPARISON_OPERATIONS) {
            numberOfConditions ++
        }
    }

    private boolean isFinalVariableDeclaration(expression) {
        return expression instanceof DeclarationExpression &&
            AstUtil.isFinalVariable(expression, sourceCode)
    }

    private boolean respondsTo(Object object, String methodName) {
        return object.metaClass.respondsTo(object, methodName)
    }

    private boolean isNotEmptyStatement(Statement statement) {
        statement.class != EmptyStatement
    }

}