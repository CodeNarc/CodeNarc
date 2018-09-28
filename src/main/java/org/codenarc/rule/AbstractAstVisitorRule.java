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

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codenarc.source.SourceCode;
import org.codenarc.util.WildcardPattern;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract superclass for Rules that use a Groovy AST Visitor.
 * <p/>
 * Each subclass must set the <code>astVisitorClass</code> property or else define a new
 * property with the same name, specifying the Class of the <code>AstVisitor</code>
 * to applied to the specified source code.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
public abstract class AbstractAstVisitorRule extends AbstractRule {

    protected static final String DEFAULT_CONST_NAME = "[A-Z][A-Z0-9_]*";
    protected static final String DEFAULT_FIELD_NAME = "[a-z][a-zA-Z0-9]*";
    protected static final String DEFAULT_VAR_NAME   = "[a-z][a-zA-Z0-9]*";
    protected static final String DEFAULT_TEST_FILES = ".*(Spec|Test|Tests|TestCase)\\.groovy";
    protected static final String DEFAULT_TEST_CLASS_NAMES = "*Spec,*Test,*Tests,*TestCase";

    public static final String CLOSURE_TEXT = "{ -> ... }";

    /** Each concrete subclass must either set this property or define its own property with the same name */
    protected Class getAstVisitorClass() { return null; }

    /**
     * This rule is only applied to classes with names matching this value.
     *
     * The value may optionally be a comma-separated list of names, in which case one of the names must match.
     *
     * If a name includes a period ('.'), then it is assumed to specify a full package name, so the name
     * (pattern) is matched against each fully-qualified class name. Otherwise it is matched only against
     * the class name without a package.
     *
     * The name(s) may optionally include wildcard characters ('*' or '?').
     */
    private String applyToClassNames;

    /**
     * This rule is NOT applied to classes with names matching this value.
     *
     * The value may optionally be a comma-separated list of names, in which case any one of the names can match.
     *
     * If a name includes a period ('.'), then it is assumed to specify a full package name, so the name
     * (pattern) is matched against each fully-qualified class name. Otherwise it is matched only against
     * the class name without a package.
     *
     * The name(s) may optionally include wildcard characters ('*' or '?').
     */
    private String doNotApplyToClassNames;

    public AstVisitor getAstVisitor() {
        Class visitorClass = getAstVisitorClass();
        if (visitorClass == null) throw new IllegalArgumentException("The astVisitorClass property must not be null");
        if (!AstVisitor.class.isAssignableFrom(visitorClass)) throw new IllegalArgumentException("The astVisitorClass property must specify a class that implements AstVisitor");
        try {
            return (AstVisitor) visitorClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void applyTo(SourceCode sourceCode, List<Violation> violations) {
        // If AST is null, skip this source code
        ModuleNode ast = sourceCode.getAst();
        if (ast != null && ast.getClasses() != null) {
            for (ClassNode classNode : ast.getClasses()) {
                if (shouldApplyThisRuleTo(classNode)) {
                    AstVisitor visitor = getAstVisitor();
                    visitor.setRule(this);
                    visitor.setSourceCode(sourceCode);
                    visitor.visitClass(classNode);
                    violations.addAll(visitor.getViolations());
                }
            }
        }
        Collections.sort(violations, new Comparator<Violation>() {
            public int compare(Violation o1, Violation o2) {
                if (o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;
                if (o1.getLineNumber() == null && o2.getLineNumber() == null) return 0;
                if (o1.getLineNumber() == null) return -1;
                if (o2.getLineNumber() == null) return 1;
                return o1.getLineNumber().compareTo(o2.getLineNumber());
            }
        });
    }

    /**
     * Return true if this rule should be applied for the specified ClassNode, based on the
     * configuration of this rule.
     * @param classNode - the ClassNode
     * @return true if this rule should be applied for the specified ClassNode
     */
    protected boolean shouldApplyThisRuleTo(ClassNode classNode) {
        // TODO Consider caching applyTo, doNotApplyTo and associated WildcardPatterns
        boolean shouldApply = true;

        String applyTo = getApplyToClassNames();
        String doNotApplyTo = getDoNotApplyToClassNames();

        if (applyTo != null  && applyTo.length() > 0) {
            WildcardPattern pattern = new WildcardPattern(applyTo, true);
            shouldApply = pattern.matches(classNode.getNameWithoutPackage()) || pattern.matches(classNode.getName());
        }

        if (shouldApply && doNotApplyTo != null && doNotApplyTo.length() > 0) {
            WildcardPattern pattern = new WildcardPattern(doNotApplyTo, true);
            shouldApply = !pattern.matches(classNode.getNameWithoutPackage()) && !pattern.matches(classNode.getName());
        }

        return shouldApply;
    }

    public String getApplyToClassNames() {
        return applyToClassNames;
    }

    public void setApplyToClassNames(String applyToClassNames) {
        this.applyToClassNames = applyToClassNames;
    }

    public String getDoNotApplyToClassNames() {
        return doNotApplyToClassNames;
    }

    public void setDoNotApplyToClassNames(String doNotApplyToClassNames) {
        this.doNotApplyToClassNames = doNotApplyToClassNames;
    }
}