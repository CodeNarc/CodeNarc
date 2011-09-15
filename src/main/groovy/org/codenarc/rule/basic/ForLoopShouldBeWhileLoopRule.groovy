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

import org.codehaus.groovy.ast.expr.EmptyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * For loops where init and update statements are empty can be simplified to while loops.
 * Ignores For-Each Loops (for (Plan p : plans) { .. }) and Groovy For Loops (for (p in plans) { .. }).
 *
 * @author Victor Savkin
 */
class ForLoopShouldBeWhileLoopRule extends AbstractAstVisitorRule {
    String name = 'ForLoopShouldBeWhileLoop'
    int priority = 3
    Class astVisitorClass = ForLoopShouldBeWhileLoopAstVisitor
}

class ForLoopShouldBeWhileLoopAstVisitor extends AbstractAstVisitor {
    
    @Override
    void visitForLoop(ForStatement node) {
        if(!isForEachLoop(node) && hasOnlyConditionExpr(node)) {
            addViolation node, 'The for loop can be simplified to a while loop'
        }
        super.visitForLoop node
    }

    private hasOnlyConditionExpr(ForStatement forStatement) {
        if (AstUtil.respondsTo(forStatement.collectionExpression, 'expressions')) {
            def (init, condition, update) = forStatement.collectionExpression.expressions
            return isEmptyExpression(init) && isEmptyExpression(update) && !isEmptyExpression(condition)
        }
        false
    }

    private static isEmptyExpression(expr) {
        expr instanceof EmptyExpression
    }

    private boolean isForEachLoop(ForStatement forStatement) {
        return forStatement.collectionExpression instanceof VariableExpression
    }
}
