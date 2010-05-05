package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation

/**
 * Rule to detect nested synchronization blocks.
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class NestedSynchronizationRule extends AbstractAstVisitorRule {

    String name = 'NestedSynchronization'
    int priority = 2
    Class astVisitorClass = NestedSynchronizationAstVisitor
}

class NestedSynchronizationAstVisitor extends AbstractAstVisitor  {

    private int visitCount = 0;

    def void visitSynchronizedStatement(SynchronizedStatement statement) {
        if (!isFirstVisit(statement)) {
            super.visitSynchronizedStatement(statement);
        } else {
            if (visitCount > 0) {
                addViolation statement
            }
            visitCount++
            super.visitSynchronizedStatement(statement);
            visitCount--
        }
    }

    def void visitClosureExpression(ClosureExpression expression) {

        if (!isFirstVisit(expression)) {
            super.visitClosureExpression(expression);
        } else {
            // dispatch to a new instance b/c we have a new scope
            AbstractAstVisitor newVisitor = new NestedSynchronizationAstVisitor(sourceCode: this.sourceCode, rule: this.rule, visited: this.visited)
            expression.getCode().visit(newVisitor);
            newVisitor.getViolations().each { Violation it ->
                addViolation(it)
            }
        } 
    }


}
