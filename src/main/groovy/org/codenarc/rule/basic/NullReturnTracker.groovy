package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ClosureExpression

/**
 * Created by IntelliJ IDEA.
 * User: hdarcy
 * Date: Oct 24, 2010
 * Time: 5:38:47 PM
 * To change this template use File | Settings | File Templates.
 */
class NullReturnTracker extends AbstractAstVisitor { /**
 * Created by IntelliJ IDEA.
 * User: hdarcy
 * Date: Oct 24, 2010
 * Time: 5:38:47 PM
 * To change this template use File | Settings | File Templates.
 */

    def parent

    def void visitReturnStatement(ReturnStatement statement) {
        if (statement.expression == ConstantExpression.NULL) {
            parent.addViolation(statement, 'Returning null from a method that should return an array.')
        } else if (statement.expression instanceof ConstantExpression && statement.expression.value == null) {
            parent.addViolation(statement, 'Returning null from a method that should return an array.')
        }
        super.visitReturnStatement(statement)
    }

    def void visitClosureExpression(ClosureExpression expression) {
        parent.handleClosure(expression)
        // do not keep walking, let the parent start a new walk for this new scope
    }
}
