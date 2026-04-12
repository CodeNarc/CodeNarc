/*
 * Copyright 2026 the original author or authors.
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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Detects usages of .every, .each, .eachWithIndex, and .forEach with assertions
 * in Spock specifications, suggesting Spock 2.4's verifyEach instead.
 *
 * @author Leonard Brünings
 */
class SpockUseVerifyEachRule extends AbstractAstVisitorRule {

    String name = 'SpockUseVerifyEach'
    int priority = 3
    String specificationSuperclassNames = '*Specification'
    String specificationClassNames = null
    boolean checkAllBlocks = true
    Class astVisitorClass = SpockUseVerifyEachAstVisitor
}

class SpockUseVerifyEachAstVisitor extends AbstractAstVisitor {

    private static final List<String> TARGET_METHODS = ['every', 'each', 'eachWithIndex', 'forEach']

    private String currentLabel = null

    @Override
    SpockUseVerifyEachRule getRule() {
        super.rule as SpockUseVerifyEachRule
    }

    @Override
    void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        visitIfInSpockClass {
            currentLabel = null
            super.visitConstructorOrMethod(node, isConstructor)
        }
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        visitIfInSpockClass {
            updateCurrentLabel(statement)
            checkForIterationWithAssertions(statement)
        }
    }

    private void checkForIterationWithAssertions(ExpressionStatement statement) {
        if (!(statement.expression instanceof MethodCallExpression)) {
            return
        }
        MethodCallExpression methodCall = statement.expression as MethodCallExpression
        String methodName = SpockUtil.getMethodName(methodCall)
        if (!(methodName in TARGET_METHODS)) {
            return
        }
        def closureArg = SpockUtil.getClosureArgument(methodCall)
        if (closureArg == null) {
            return
        }
        boolean inImplicitAssertBlock = SpockUtil.isImplicitAssertBlock(currentLabel) && currentLabel != 'filter'

        boolean shouldReport = inImplicitAssertBlock
            ? isImplicitAssertionOverIteration(methodName, closureArg)
            : isExplicitAssertionOverIteration(methodName, closureArg)

        if (shouldReport) {
            addViolation(statement, "Replace '${methodName}' with Spock's 'verifyEach' for better per-item failure diagnostics")
        }
    }

    private static boolean isImplicitAssertionOverIteration(String methodName, ClosureExpression closureArg) {
        methodName == 'every' || SpockUtil.closureContainsAssertions(closureArg, true)
    }

    private boolean isExplicitAssertionOverIteration(String methodName, ClosureExpression closureArg) {
        rule.checkAllBlocks
            && methodName != 'every'
            && SpockUtil.closureContainsAssertions(closureArg, false)
    }

    private void updateCurrentLabel(Statement statement) {
        List<String> labels = statement.statementLabels
        if (labels != null) {
            Collection<String> spockLabels = labels.intersect(SpockUtil.SPOCK_LABELS)
            if (spockLabels.size() > 0) {
                currentLabel = spockLabels.last()
            }
        }
    }

    private void visitIfInSpockClass(Closure callVisitorMethod) {
        if (SpockUtil.isSpockSpecification(currentClassNode, rule.specificationSuperclassNames, rule.specificationClassNames)) {
            callVisitorMethod()
        }
    }
}
