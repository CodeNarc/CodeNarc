/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.expr.AnnotationConstantExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import java.util.regex.Matcher

/**
 * Check for regular (single quote) strings containing a GString-type expression (${..}).
 *
 * @author Chris Mair
 */
class GStringExpressionWithinStringRule extends AbstractAstVisitorRule {
    String name = 'GStringExpressionWithinString'
    int priority = 2
    Class astVisitorClass = GStringExpressionWithinStringAstVisitor
}

class GStringExpressionWithinStringAstVisitor extends AbstractAstVisitor {

    private static final GSTRING_EXPRESSION_REGEX = /\$\{.*\}/
    private AnnotatedNode currentAnnotatedNode

    @Override
    void visitConstantExpression(ConstantExpression expression) {
        if (isFirstVisit(expression) && expression.value instanceof String && expression.lineNumber > -1) {
            Matcher matcher = expression.value =~ GSTRING_EXPRESSION_REGEX
            boolean matchesGStringExpressionPattern = matcher as boolean
            if (matchesGStringExpressionPattern && isNotElementOfAnnotation(expression)) {
                addViolation(expression, "The String '$expression.value' contains a GString-type expression: '${matcher[0]}'")
            }
        }
        super.visitConstantExpression(expression)
    }

    @Override
    void visitAnnotations(AnnotatedNode node) {
        saveCurrentAnnotatedNode(node)
        super.visitAnnotations(node)
        resetCurrentAnnotatedNode()
    }

    private boolean isNotElementOfAnnotation(ConstantExpression expression) {
        return !isProcessingAnnotatedNode() || !isUsedInAnyAnnotationOnCurrentAnnotatedNode(expression)
    }

    private boolean isUsedInAnyAnnotationOnCurrentAnnotatedNode(ConstantExpression constantExpression) {
        return currentAnnotatedNode.annotations.any { AnnotationNode annotationNode ->
            isExpressionUsedInAnnotation(constantExpression, annotationNode)
        }
    }

    private static boolean isExpressionUsedInAnnotation(ConstantExpression expression, AnnotationNode annotationNode) {
        List<ConstantExpression> constantExpressions = new ConstantExpressionExtractor().extractFrom(annotationNode)
        return constantExpressions.any { it.is(expression) }
    }

    private boolean isProcessingAnnotatedNode() {
        return this.currentAnnotatedNode != null
    }

    private void resetCurrentAnnotatedNode() {
        this.currentAnnotatedNode = null
    }

    private void saveCurrentAnnotatedNode(AnnotatedNode annotatedNode) {
        this.currentAnnotatedNode = annotatedNode
    }
}

class ConstantExpressionExtractor {

    @SuppressWarnings('UseCollectMany') //collectMany is not available in Groovy 1.7.5
    List<ConstantExpression> extractFrom(AnnotationNode annotationNode) {
        return annotationNode.members.values().collect { 
            Expression expression -> extractFromExpression(expression) 
        }.flatten()
    }

    private List<ConstantExpression> extractFromExpression(ListExpression listExpression) {
        return listExpression.expressions.collect { 
            Expression expression -> extractFromExpression(expression) 
        }
    }

    private List<ConstantExpression> extractFromExpression(ConstantExpression expression) {
        return [expression]
    }

    private List<ConstantExpression> extractFromExpression(AnnotationConstantExpression expression) {
        AnnotationNode annotationNode = expression.value as AnnotationNode
        return extractFrom(annotationNode)
    }
}
