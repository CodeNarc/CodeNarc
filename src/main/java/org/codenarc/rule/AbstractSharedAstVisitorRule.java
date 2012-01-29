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
import org.codenarc.source.SourceCode;

import java.util.List;

/**
 * Abstract superclass for Rules that use a single, shared AstVisitor across all ClassNodes in a source (file).
 *
 * @author Chris Mair
 */
public abstract class AbstractSharedAstVisitorRule extends AbstractAstVisitorRule {

    protected abstract List<Violation> getViolations(AstVisitor astVisitor, SourceCode sourceCode);

    @Override
    public void applyTo(SourceCode sourceCode, List violations) {
        if (!sourceCode.isValid()) { return; }

        AstVisitor visitor = getAstVisitor(sourceCode);
        applyVisitor(visitor, sourceCode);
        List<Violation> allViolations = getViolations(visitor, sourceCode);
        List<Violation> visitorViolations = removeSuppressedViolations(allViolations, sourceCode);
        violations.addAll(visitorViolations);
    }

    /**
     * Subclasses can override to provide an AstVisitor with SourceCode or AST-specific initialization.
     */
    protected AstVisitor getAstVisitor(SourceCode sourceCode) {
        return super.getAstVisitor();
    }

    protected void applyVisitor(AstVisitor visitor, SourceCode sourceCode) {
        visitor.setRule(this);
        visitor.setSourceCode(sourceCode);

        for (ClassNode classNode : sourceCode.getAst().getClasses()) {
            if (shouldApplyThisRuleTo(classNode)) {
                visitor.visitClass(classNode);
            }
        }
    }

    private List<Violation> removeSuppressedViolations(List<Violation> violations, SourceCode sourceCode) {
        return sourceCode.getSuppressionAnalyzer().filterSuppressedViolations(violations);
    }
}