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
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.MethodCallExpression
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
 * @version $Revision$ - $Date$
 */
class UnusedPrivateFieldRule extends AbstractAstVisitorRule {
    String name = 'UnusedPrivateField'
    int priority = 2
    Class astVisitorClass = UnusedPrivateFieldAstVisitor
    String ignoreFieldNames = 'serialVersionUID'
}

@SuppressWarnings('DuplicateLiteral')
class UnusedPrivateFieldAstVisitor extends AbstractAstVisitor  {
    private List<FieldNode> unusedPrivateFields

    void visitClassEx(ClassNode classNode) {
        def wildcardPattern = new WildcardPattern(rule.ignoreFieldNames, false)
        this.unusedPrivateFields = classNode.fields.findAll { fieldNode ->
            def isPrivate = fieldNode.modifiers & FieldNode.ACC_PRIVATE
            def isNotGenerated = fieldNode.lineNumber != -1
            def isIgnored = wildcardPattern.matches(fieldNode.name)
            isPrivate && isNotGenerated && !isIgnored
        }
        super.visitClassEx(classNode)
    }

    void visitClassComplete(ClassNode classNode) {
        unusedPrivateFields.each { unusedPrivateField ->
            addViolation(unusedPrivateField, "The field $unusedPrivateField.name is not used in the class $classNode.name")
        }
    }

    void visitVariableExpression(VariableExpression expression) {
        removeUnusedPrivateField(expression.name)

        // This causes problems (StackOverflow) in Groovy 1.7.0
        //super.visitVariableExpression(expression)
    }

    void visitPropertyEx(PropertyNode node) {
        removeUnusedPrivateField(node.name)
        super.visitPropertyEx(node)
    }

    void visitPropertyExpression(PropertyExpression expression) {
        if (    expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'this' &&
                expression.property instanceof ConstantExpression) {

            removeUnusedPrivateField(expression.property.value)
        }
        super.visitPropertyExpression(expression)
    }

    void visitMethodEx(MethodNode node) {
        if (node.parameters) {
            node.parameters.each { parameter ->
                def initialExpression = parameter.initialExpression
                if (initialExpression && AstUtil.respondsTo(initialExpression, 'getName')) {
                    removeUnusedPrivateField(initialExpression.name)
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
            removeUnusedPrivateField(call.method.value)
        }
        super.visitMethodCallExpression(call)
    }

    private void removeUnusedPrivateField(String name) {
        def referencedField = unusedPrivateFields.find { it.name == name }
        if (referencedField) {
            unusedPrivateFields.remove(referencedField)
        }
    }
}