/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.rule

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.control.SourceUnit
import org.codenarc.source.SourceCode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.ConstructorNode
import java.beans.Expression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.Parameter

/**
 * Abstract superclass for Groovy AST Visitors used with Rules
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
abstract class AbstractAstVisitor extends ClassCodeVisitorSupport implements AstVisitor {
    public static final MAX_SOURCE_LINE_LENGTH = 60
    public static final SOURCE_LINE_LAST_SEGMENT_LENGTH = 12
    protected static final CONSTANT_EXPRESSION_VALUES = ['Boolean.TRUE', 'Boolean.FALSE', 'null']
    protected static final BOOLEAN_CLASS = Boolean.name
    List violations = []
    Rule rule
    SourceCode sourceCode
    Set visited = [] as Set
    List isSuppressed = []

    /**
     * Return true if the AST expression has not already been visited. If it is
     * the first visit, register the expression so that the next visit will return false.
     * @param expression - the AST expression to check
     * @return true if the AST expression has NOT already been visited
     */
    protected isFirstVisit(expression) {
        if (visited.contains(expression)) {
            return false
        }
        else {
            visited << expression
            return true
        }
    }

    /**
     * Return the source line corresponding to the specified AST node
     * @param node - the Groovy AST node
     */
    protected String sourceLine(ASTNode node) {
        // TODO Handle statements that cross multiple lines?
        sourceCode.line(findLastLineNumber(node) - 1)
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     * @param node - the Groovy AST Node
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(ASTNode node, message = null) {
        if (!isSuppressed) {
            def lineNumber = node.lineNumber
            if (lineNumber >= 0) {
                if (node instanceof AnnotatedNode) {
                    lineNumber = findLastLineNumber(node)
                }
                def sourceLine = sourceLine(node)
                violations.add(new Violation(rule: rule, sourceLine: sourceLine, lineNumber: lineNumber, message: message))
            }
        }
    }

    /**
     * gets the last line number of a node, taking into account annotations
     * @param node
     * @return
     */
    private static int findLastLineNumber(ASTNode node) {
        if (node instanceof AnnotatedNode) {
            node.annotations?.inject(node.lineNumber) { int acc, AnnotationNode val ->
                Math.max(acc, val.lastLineNumber)
            }
        } else {
            node.lineNumber
        }
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * @param violation - the violation to add
     */
    protected void addViolation(Violation violation) {
        if (!isSuppressed) {
            violations.add(violation)
        }
    }

    protected SourceUnit getSourceUnit() {
        source
    }

    private boolean suppressionIsPresent(AnnotatedNode node) {
        if (rule?.name) {
            def annos = node.annotations?.findAll { it.classNode?.name == 'SuppressWarnings' }
            for (AnnotationNode annotation: annos) {
                if (suppressionIsPresent(annotation)) {
                    return true
                }
            }
        }
        false
    }

    @SuppressWarnings('NestedBlockDepth')
    private boolean suppressionIsPresent(AnnotationNode node) {
        for (Expression exp: node?.members?.values()) {
            if (exp instanceof ConstantExpression && exp.value == rule.name) {
                return true
            } else if (exp instanceof ListExpression) {
                for (Expression entry: exp.expressions) {
                    if (entry instanceof ConstantExpression && entry.value == rule.name) {
                        return true
                    }
                }
            }
        }
    }

    final void visitClass(ClassNode node) {
        withSuppressionCheck(node) {
            visitClassEx node
            super.visitClass node
            visitClassComplete node
        }
    }

    @SuppressWarnings('EmptyMethodInAbstractClass')
    protected void visitClassEx(ClassNode node) {
        // empty on purpose
    }

    @SuppressWarnings('EmptyMethodInAbstractClass')
    protected void visitClassComplete(ClassNode node) {
        // empty on purpose
    }

    final protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        withSuppressionCheck(node) {
            visitConstructorOrMethodEx node, isConstructor
            super.visitConstructorOrMethod(node, isConstructor)
        }
    }

    @SuppressWarnings('EmptyMethodInAbstractClass')
    protected void visitConstructorOrMethodEx(MethodNode node, boolean isConstructor) {
        // empty on purpose
    }

    final void visitMethod(MethodNode node) {
        withSuppressionCheck(node) {
            visitMethodEx node
            node?.parameters?.each { Parameter parameter ->
                if (parameter?.hasInitialExpression()) {
                    parameter.initialExpression.visit this
                }
            }
            super.visitMethod node
            visitMethodComplete(node)
        }
    }

    @SuppressWarnings('EmptyMethodInAbstractClass')
    protected void visitMethodComplete(MethodNode node) {
        // empty on purpose
    }

    @SuppressWarnings('EmptyMethodInAbstractClass')
    void visitMethodEx(MethodNode node) {
        // empty on purpose
    }

    final void visitField(FieldNode node) {
        withSuppressionCheck(node) {
            visitFieldEx node
            super.visitField node
        }
    }

    @SuppressWarnings('EmptyMethodInAbstractClass')
    void visitFieldEx(FieldNode node) {
        // empty on purpose
    }

    final void visitProperty(PropertyNode node) {
        withSuppressionCheck(node.field) {
            visitPropertyEx node
            super.visitProperty node
        }
    }

    @SuppressWarnings('EmptyMethodInAbstractClass')
    void visitPropertyEx(PropertyNode node) {
        // empty on purpose
    }

    final void visitConstructor(ConstructorNode node) {
        withSuppressionCheck(node) {
            visitConstructorEx node
            super.visitConstructor node
        }
    }

    @SuppressWarnings('EmptyMethodInAbstractClass')
    def void visitConstructorEx(ConstructorNode node) {
        // empty on purpose
    }

    private withSuppressionCheck(AnnotatedNode node, Closure f) {
        boolean suppress = suppressionIsPresent(node)
        if (suppress) {
            isSuppressed.add(true)
        }
        f()
        if (suppress) {
            isSuppressed.remove(0)
        }
    }
}
