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
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.VariableExpression

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
 * Calculate the ABC Metric for a class/method.
 *
 * The ABC Counting Rules for Groovy:
 * <pre>
 *   1. Add one to the assignment count for each occurrence of an assignment operator, excluding constant declarations:
 *      = *= /= %= += <<= >>= &= |= ^= >>>=
 *   2. Add one to the assignment count for each occurrence of an increment or decrement operator (prefix or postfix):
 *      ++ --
 *   3. Add one to the branch count for each function call or class method call.
 *   4. Add one to the branch count for each occurrence of the new operator.
 *   5. Add one to the condition count for each use of a conditional operator:
 *      == != <= >= < > <=> =~ ==~
 *   6. Add one to the condition count for each use of the following keywords:
 *      else case default try catch ?
 *   7. Add one to the condition count for each unary conditional expression.
 * </pre>
 *
 * Additional notes:
 * <ul>
 *   <li>A property access is treated like a method call (and thus increments the branch count)</li>
 * </ul>
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
        return result
    }
}

class AbcComplexityAstVisitor extends AbstractAstVisitor {

    private static final ASSIGNMENT_OPERATIONS =
        ['=', '++', '--', '+=', '-=', '/=', '*=', '%=', '<<=', '>>=', '>>>=', '&=', '|=', '^=']
    private static final COMPARISON_OPERATIONS = ['<', '>', '>=', '<=', '==', '!=', '<=>', '=~', '==~']
    private static final BOOLEAN_LOGIC_OPERATIONS = ['&&', '||']

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

    void visitBooleanExpression(BooleanExpression booleanExpression) {
        numberOfConditions += countUnaryConditionals(booleanExpression.expression)
        super.visitBooleanExpression(booleanExpression)
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

    // Use Groovy dynamic dispatch to achieve pseudo-polymorphism.
    // Call appropriate countUnaryConditionals() logic based on type of expression

    private int countUnaryConditionals(VariableExpression expression) {
        return 1
    }

    private int countUnaryConditionals(BinaryExpression binaryExpression) {
        def operationName = binaryExpression.operation.text
        return operationName in BOOLEAN_LOGIC_OPERATIONS ?
            countUnaryConditionals(binaryExpression.leftExpression) +
                countUnaryConditionals(binaryExpression.rightExpression) : 0
    }

    private int countUnaryConditionals(Expression expression) {
        return 0
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