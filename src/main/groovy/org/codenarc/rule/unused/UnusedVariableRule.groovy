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
package org.codenarc.rule.unused

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil
import org.codenarc.util.WildcardPattern
import org.codehaus.groovy.ast.stmt.ForStatement

/**
 * Rule that checks for variables that are not referenced.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class UnusedVariableRule extends AbstractAstVisitorRule {
    String name = 'UnusedVariable'
    int priority = 2
    String ignoreVariableNames

    @Override
    void applyTo(SourceCode sourceCode, List violations) {
        // If AST is null, skip this source code
        def ast = sourceCode.ast
        if (ast && ast.classes) {

            def anonymousClasses = getAnonymousClasses(ast.classes)
            def collector = new ReferenceCollector()
            anonymousClasses.each {
                collector.visitClass(it)
            }
            def anonymousReferences = collector.references
            
            ast.classes.each { classNode ->

                if (shouldApplyThisRuleTo(classNode)) {
                    def visitor = new UnusedVariableAstVisitor(anonymousReferences: anonymousReferences)
                    visitor.rule = this
                    visitor.sourceCode = sourceCode
                    visitor.visitClass(classNode)
                    violations.addAll(visitor.violations)
                }
            }
        }
    }

    private static List<ClassNode> getAnonymousClasses(List<ClassNode> classes) {
        classes.findAll {
            it instanceof InnerClassNode && it.anonymous
        }
    }

}

class UnusedVariableAstVisitor extends AbstractAstVisitor  {

    private final variablesByBlockScope = [] as Stack
    private variablesInCurrentBlockScope
    private anonymousReferences

    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        if (isFirstVisit(declarationExpression)) {
            def varExpressions = AstUtil.getVariableExpressions(declarationExpression)
            varExpressions.each { varExpression ->
                variablesInCurrentBlockScope[varExpression] = false
            }
        }
        super.visitDeclarationExpression(declarationExpression)
    }

    void visitBlockStatement(BlockStatement block) {
        beforeBlock()
        super.visitBlockStatement(block)
        afterBlock()
    }

    @Override
    void visitForLoop(ForStatement forLoop) {
        beforeBlock()
        super.visitForLoop(forLoop)
        afterBlock()
    }

    private beforeBlock() {
        variablesInCurrentBlockScope = [:]
        variablesByBlockScope.push(variablesInCurrentBlockScope)
    }

    private afterBlock() {
        variablesInCurrentBlockScope.each { varExpression, isUsed ->
            if (!isIgnoredVariable(varExpression) && !isUsed && !anonymousReferences.contains(varExpression.name)) {
                addViolation(varExpression, "The variable [${varExpression.name}] in class $currentClassName is not used")
            }
        }
        variablesByBlockScope.pop()
        variablesInCurrentBlockScope = variablesByBlockScope.empty() ? null : variablesByBlockScope.peek()
    }

    void visitVariableExpression(VariableExpression expression) {
        markVariableAsReferenced(expression.name, expression)
        super.visitVariableExpression(expression)
    }

    void visitMethodCallExpression(MethodCallExpression call) {
        // If there happens to be a method call on a method with the same name as the variable.
        // This handles the case of defining a closure and then executing it, e.g.:
        //      def myClosure = { println 'ok' }
        //      myClosure()
        // But this could potentially "hide" some unused variables (i.e. false negatives).
        if (call.isImplicitThis() &&
            call.method instanceof ConstantExpression) {
            markVariableAsReferenced(call.method.value, null)
        }
        super.visitMethodCallExpression(call)
    }

    @SuppressWarnings('NestedForLoop')
    private void markVariableAsReferenced(String varName, VariableExpression varExpression) {
        for(blockVariables in variablesByBlockScope) {
            for(var in blockVariables.keySet()) {
                if (var.name == varName && var != varExpression) {
                    blockVariables[var] = true
                    return
                }
            }
        }
    }

    private boolean isIgnoredVariable(VariableExpression expression) {
        new WildcardPattern(rule.ignoreVariableNames, false).matches(expression.name)
    }
}
