/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.size

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.stmt.*

/**
 * Rule that checks for blocks or closures nested more than a configured maximum number.
 * Blocks include if, for, while, switch, try, catch, finally and synchronized
 * blocks/statements, as well as closures. 
 * <p/>
 * The <code>maxNestedBlockDepth</code> property holds the threshold value for the maximum number of
 * nesting levels. A block or closures nested deeper than that number of levels is considered a
 * violation. The <code>maxNestedBlockDepth</code> property defaults to 3.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class NestedBlockDepthRule extends AbstractAstVisitorRule {
    String name = 'NestedBlockDepth'
    int priority = 2
    int maxNestedBlockDepth = 5
    String ignoreRegex = '.*(b|B)uilder'
    Class astVisitorClass = NestedBlockDepthAstVisitor
}

class NestedBlockDepthAstVisitor extends AbstractAstVisitor {

    private final Set blocksToProcess = []
    private Set closureFieldExpressions
    private nestedBlockDepth = 0

    void visitClassEx(ClassNode classNode) {
        addClosureFields(classNode)
        super.visitClassEx(classNode)
    }

    private void addClosureFields(ClassNode classNode) {
        closureFieldExpressions = []
        classNode.fields.each { fieldNode ->
            if (!AstUtil.isFromGeneratedSourceCode(fieldNode) &&
                    fieldNode.initialExpression instanceof ClosureExpression) {
                closureFieldExpressions << fieldNode.initialExpression
            }
        }
    }

    void visitBlockStatement(BlockStatement block) {
        if (isFirstVisit(block) && block in blocksToProcess) {
            handleNestedNode(block) { super.visitBlockStatement(block) }
        }
        else {
            super.visitBlockStatement(block)
        }
    }

    void visitTryCatchFinally(TryCatchStatement tryCatchStatement) {
        addBlockIfNotEmpty(tryCatchStatement.tryStatement)
        addBlockIfNotEmpty(tryCatchStatement.finallyStatement)
        super.visitTryCatchFinally(tryCatchStatement)
    }

    void visitCatchStatement(CatchStatement statement) {
        handleNestedNode(statement) { super.visitCatchStatement(statement) }
    }

    private void addBlockIfNotEmpty(block) {
        if (!(block instanceof EmptyStatement)) {
            blocksToProcess << block
        }
    }

    void visitIfElse(IfStatement ifStatement) {
        if (isFirstVisit(ifStatement)) {
            addBlockIfNotEmpty(ifStatement.ifBlock)
            addBlockIfNotEmpty(ifStatement.elseBlock)
        }
        super.visitIfElse(ifStatement)
    }

    void visitWhileLoop(WhileStatement whileStatement) {
        handleNestedNode(whileStatement) { super.visitWhileLoop(whileStatement) }
    }

    void visitForLoop(ForStatement forStatement) {
        handleNestedNode(forStatement) { super.visitForLoop(forStatement) }
    }

    void visitCaseStatement(CaseStatement statement) {
        handleNestedNode(statement) { super.visitCaseStatement(statement) }
    }

    void visitSynchronizedStatement(SynchronizedStatement statement) {
        handleNestedNode(statement) { super.visitSynchronizedStatement(statement) }
    }

    void visitClosureExpression(ClosureExpression expression) {
        if (closureFieldExpressions.contains(expression)) {
            super.visitClosureExpression(expression)
        }
        else {
            handleNestedNode(expression) { super.visitClosureExpression(expression) }
        }
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (!AstUtil.isMethodCallOnObject(call, rule.ignoreRegex)
                && !AstUtil.isConstructorCall(call.objectExpression, rule.ignoreRegex)) {
            super.visitMethodCallExpression(call)
        }
    }

    private void handleNestedNode(node, Closure callVisitorMethod) {
        nestedBlockDepth++
        if (nestedBlockDepth > rule.maxNestedBlockDepth) {
            addViolation(node, "The nested block depth is $nestedBlockDepth")
        }
        callVisitorMethod()
        nestedBlockDepth--
    }
}
