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
package org.codenarc.rule;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.control.SourceUnit;
import org.codenarc.source.SourceCode;
import org.codenarc.util.AstUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract superclass for Groovy AST Visitors used with Rules
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
public class AbstractAstVisitor extends ClassCodeVisitorSupport implements AstVisitor {
    private final List<Violation> violations = new ArrayList<Violation>();
    private Rule rule;
    private SourceCode sourceCode;
    private Set<Object> visited = new HashSet<Object>();
    private final List<Boolean> isSuppressed = new ArrayList<Boolean>();
    private ClassNode currentClassNode = null;

    /**
     * Return true if the AST expression has not already been visited. If it is
     * the first visit, register the expression so that the next visit will return false.
     *
     * @param expression - the AST expression to check
     * @return true if the AST expression has NOT already been visited
     */
    protected boolean isFirstVisit(Object expression) {
        if (visited.contains(expression)) {
            return false;
        }
        visited.add(expression);
        return true;
    }

    /**
     * Return the source line corresponding to the specified AST node
     *
     * @param node - the Groovy AST node
     */
    protected String sourceLine(ASTNode node) {
        return sourceCode.line(AstUtil.findFirstNonAnnotationLine(node, sourceCode) - 1);
    }

    /**
     * Return the last source line corresponding to the specified AST node
     *
     * @param node - the Groovy AST node
     */
    protected String lastSourceLine(ASTNode node) {
        return sourceCode.line(node.getLastLineNumber() - 1);
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     *
     * @param node - the Groovy AST Node
     * @Deprecated Always define a message. Use the other addViolation method instead of this one.
     */
    @Deprecated
    protected void addViolation(ASTNode node) {
        addViolation(node, null);
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     *
     * @param node    - the Groovy AST Node
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(ASTNode node, String message) {
        if (isSuppressed.isEmpty()) {
            int lineNumber = node.getLineNumber();
            if (lineNumber >= 0) {
                if (node instanceof AnnotatedNode) {
                    lineNumber = AstUtil.findFirstNonAnnotationLine(node, sourceCode);
                }
                String sourceLine = sourceLine(node);
                Violation violation = new Violation();
                violation.setRule(rule);
                violation.setLineNumber(lineNumber);
                violation.setSourceLine(sourceLine);
                violation.setMessage(message);
                violations.add(violation);
            }
        }
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     *
     * @param violation - the violation to add
     */
    protected void addViolation(Violation violation) {
        if (isSuppressed.isEmpty()) {
            violations.add(violation);
        }
    }

    protected SourceUnit getSourceUnit() {
        throw new RuntimeException("should never be called");
    }

    protected boolean suppressionIsPresent(AnnotatedNode node) {
        if (rule == null) {
            return AstUtil.isSuppressionPresent(node, null);
        } else {
            return AstUtil.isSuppressionPresent(node, rule.getName());
        }
    }

    public final void visitClass(final ClassNode node) {
        currentClassNode = node;
        withSuppressionCheck(node, new Runnable() {
            public void run() {
                visitClassEx(node);
                AbstractAstVisitor.super.visitClass(node);
                visitClassComplete(node);
            }
        });
        currentClassNode = null;
    }

    protected void visitClassEx(ClassNode node) {
        // empty on purpose
    }

    protected void visitClassComplete(ClassNode node) {
        // empty on purpose
    }

    final protected void visitConstructorOrMethod(final MethodNode node, final boolean isConstructor) {
        withSuppressionCheck(node, new Runnable() {
            public void run() {
                visitConstructorOrMethodEx(node, isConstructor);
                AbstractAstVisitor.super.visitConstructorOrMethod(node, isConstructor);
            }
        });
    }

    protected void visitConstructorOrMethodEx(MethodNode node, boolean isConstructor) {
        // empty on purpose
    }

    public final void visitMethod(final MethodNode node) {
        withSuppressionCheck(node, new Runnable() {
            public void run() {
                visitMethodEx(node);
                if (node != null && node.getParameters() != null) {
                    for (Parameter parameter : node.getParameters()) {
                        if (parameter.hasInitialExpression()) {
                            parameter.getInitialExpression().visit(AbstractAstVisitor.this);
                        }
                    }
                }
                AbstractAstVisitor.super.visitMethod(node);
                visitMethodComplete(node);
            }
        });
    }

    protected void visitMethodComplete(MethodNode node) {
        // empty on purpose
    }

    protected void visitMethodEx(MethodNode node) {
        // empty on purpose
    }

    public final void visitField(final FieldNode node) {
        withSuppressionCheck(node, new Runnable() {
            public void run() {
                visitFieldEx(node);
                AbstractAstVisitor.super.visitField(node);
            }
        });
    }

    protected void visitFieldEx(FieldNode node) {
        // empty on purpose
    }

    public final void visitProperty(final PropertyNode node) {
        withSuppressionCheck(node.getField(), new Runnable() {
            public void run() {
                visitPropertyEx(node);
                AbstractAstVisitor.super.visitProperty(node);
            }
        });
    }

    protected void visitPropertyEx(PropertyNode node) {
        // empty on purpose
    }

    public final void visitConstructor(final ConstructorNode node) {
        withSuppressionCheck(node, new Runnable() {
            public void run() {
                visitConstructorEx(node);
                AbstractAstVisitor.super.visitConstructor(node);
            }
        });
    }

    protected void visitConstructorEx(ConstructorNode node) {
        // empty on purpose
    }

    private void withSuppressionCheck(AnnotatedNode node, Runnable f) {
        boolean suppress = suppressionIsPresent(node);
        if (suppress) {
            isSuppressed.add(true);
        }
        f.run();
        if (suppress) {
            isSuppressed.remove(0);
        }
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public void setSourceCode(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public Set<Object> getVisited() {
        return visited;
    }

    public Rule getRule() {
        return rule;
    }

    public SourceCode getSourceCode() {
        return sourceCode;
    }

    public void setVisited(Set<Object> visited) {
        this.visited = visited;
    }

    protected String getCurrentClassName() {
        return currentClassNode.getName();
    }

    protected ClassNode getCurrentClassNode() {
        return currentClassNode;
    }
}
