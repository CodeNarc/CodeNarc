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

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil
import org.codenarc.util.WildcardPattern

/**
 * Rule that checks for private fields that are not referenced within the same class.
 * <p/>
 * The <code>ignoreFieldNames</code> property optionally specifies one or more
 * (comma-separated) field names that should be ignored (i.e., that should not cause a
 * rule violation). The name(s) may optionally include wildcard characters ('*' or '?').
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
class UnusedPrivateFieldRule extends AbstractAstVisitorRule {
    String name = 'UnusedPrivateField'
    int priority = 2
    String ignoreFieldNames = 'serialVersionUID'

    @Override
    void applyTo(SourceCode sourceCode, List violations) {
        // If AST is null, skip this source code
        def ast = sourceCode.ast
        if (!ast) { return }

        def allPrivateFields = collectAllPrivateFields(ast)

        def visitor = new UnusedPrivateFieldAstVisitor(unusedPrivateFields: allPrivateFields)
        visitor.rule = this
        ast.classes.each { classNode ->
            visitor.visitClass(classNode)
        }
        visitor.sourceCode = sourceCode

        allPrivateFields.each { key, FieldNode value ->
            visitor.addViolation(value, "The field $key is not used within the class ${value.owner?.name}")
        }
        def filteredViolations = sourceCode.suppressionAnalyzer.filterSuppressedViolations(visitor.violations)
        violations.addAll(filteredViolations)
    }

    @SuppressWarnings('NestedBlockDepth')
    private collectAllPrivateFields(ast) {
        def allPrivateFields = [:]
        ast.classes.each { classNode ->
            if (shouldApplyThisRuleTo(classNode)) {
                classNode.fields.inject(allPrivateFields) { acc, fieldNode ->
                    def wildcardPattern = new WildcardPattern(ignoreFieldNames, false)
                    def isPrivate = fieldNode.modifiers & FieldNode.ACC_PRIVATE
                    def isNotGenerated = fieldNode.lineNumber != -1
                    def isIgnored = wildcardPattern.matches(fieldNode.name)
                    if (isPrivate && isNotGenerated && !isIgnored) {
                        acc.put(fieldNode.name, fieldNode)
                    }
                    acc
                }
            }
        }
        allPrivateFields
    }
}

@SuppressWarnings('DuplicateLiteral')
class UnusedPrivateFieldAstVisitor extends AbstractAstVisitor {
    private Map<String, FieldNode> unusedPrivateFields

    void visitVariableExpression(VariableExpression expression) {
        unusedPrivateFields.remove(expression.name)

        // This causes problems (StackOverflow) in Groovy 1.7.0
        //super.visitVariableExpression(expression)
    }

    void visitProperty(PropertyNode node) {
        unusedPrivateFields.remove(node.name)
        super.visitProperty(node)
    }

    void visitPropertyExpression(PropertyExpression expression) {
        if (expression.objectExpression instanceof VariableExpression &&
            expression.objectExpression.name in ['this', currentClassNode.nameWithoutPackage] &&
            expression.property instanceof ConstantExpression) {

            unusedPrivateFields.remove(expression.property.value)
        } else if (expression.objectExpression instanceof PropertyExpression &&
            expression.objectExpression.objectExpression instanceof VariableExpression &&
            expression.objectExpression.property instanceof ConstantExpression &&
            expression.objectExpression.objectExpression.name  == currentClassNode.outerClass?.name &&
            expression.objectExpression.property.value == 'this' ) {

            unusedPrivateFields.remove(expression.property.value)
        }
        super.visitPropertyExpression(expression)
    }

    void visitMethodEx(MethodNode node) {
        if (node.parameters) {
            node.parameters.each { parameter ->
                def initialExpression = parameter.initialExpression
                if (initialExpression && AstUtil.respondsTo(initialExpression, 'getName')) {
                    unusedPrivateFields.remove(initialExpression.name)
                }
            }
        }
        super.visitMethodEx(node)
    }

    void visitMethodCallExpression(MethodCallExpression call) {
        // If there happens to be a method call on a method with the same name as the field.
        // This handles the case of defining a closure and then executing it, e.g.:
        //      private myClosure = { println 'ok' }
        //      ...
        //      myClosure()
        // But this could potentially "hide" some unused fields (i.e. false negatives).
        if (AstUtil.isMethodCallOnObject(call, 'this') && call.method instanceof ConstantExpression) {
            unusedPrivateFields.remove(call.method.value)
        } else if (call.objectExpression instanceof PropertyExpression &&
            call.objectExpression.objectExpression instanceof VariableExpression &&
            call.objectExpression.property instanceof ConstantExpression &&
            call.method instanceof ConstantExpression &&
            call.objectExpression.objectExpression.name == currentClassNode.outerClass?.name &&
            call.objectExpression.property.value == 'this') {
            unusedPrivateFields.remove(call.method.value)
        }
        super.visitMethodCallExpression(call)
    }
}