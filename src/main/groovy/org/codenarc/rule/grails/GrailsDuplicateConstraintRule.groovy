/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.grails

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.MethodCallExpression

/**
 * Check for duplicate constraints entry
 *
 * @author Chris Mair
 */
class GrailsDuplicateConstraintRule extends AbstractAstVisitorRule {
    String name = 'GrailsDuplicateConstraint'
    int priority = 2
    Class astVisitorClass = GrailsDuplicateConstraintAstVisitor
    String applyToFilesMatching = GrailsUtil.DOMAIN_FILES
}

class GrailsDuplicateConstraintAstVisitor extends AbstractAstVisitor {

    private final Set<String> constraintNames = []
    private boolean withinConstraint

    @Override
    void visitField(FieldNode node) {
        if (node.name == 'constraints') {
            withinConstraint = true
        }
        super.visitField(node)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (withinConstraint && isFirstVisit(call)) {
            def name = call.methodAsString
            if (name in constraintNames) {
                addViolation(call, "The constraint for $name in domain class $currentClassName has already been specified")
            }
            else {
                constraintNames << name
            }
        }
        super.visitMethodCallExpression(call)
    }
}
