package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ClosureExpression

/**
 * @author Hamlet D'Arcy
 */
class NullReturnTracker extends AbstractAstVisitor {

    def parent
    static final ERROR_MSG = 'Returning null from a method.'
    def void visitReturnStatement(ReturnStatement statement) {
        if (statement.expression == ConstantExpression.NULL) {
            parent.addViolation(statement, ERROR_MSG)
        } else if (statement.expression instanceof ConstantExpression && statement.expression.value == null) {
            parent.addViolation(statement, ERROR_MSG)
        }
        super.visitReturnStatement(statement)
    }

    def void visitClosureExpression(ClosureExpression expression) {
        parent.handleClosure(expression)
        // do not keep walking, let the parent start a new walk for this new scope
    }
}
