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
            super.visitField(node)
            withinConstraint = false
        }
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (withinConstraint && isFirstVisit(call)) {
            String methodName = call.methodAsString
            if (methodName == 'importFrom') {
                Collection importedConstraintNames = extractImportedConstraints(call.text)
                constraintNames.intersect(importedConstraintNames).each {
                    addViolation(call, "The constraint for $it in domain class $currentClassName has already been specified")
                }
                constraintNames.addAll(importedConstraintNames)
            }
            else if (methodName in constraintNames) {
                addViolation(call, "The constraint for $methodName in domain class $currentClassName has already been specified")
            }
            else {
                constraintNames << methodName
            }
        }
    }

    /**
     * Extract the properties included in an importFrom constraint definition.  So far this just handles included properties.
     * It doesn't currently support extracting all the properties from the given class, so no exclude: support either.
     * @param text - e.g. "this.importFrom([include:[firstName]], Entity)"
     * @return the collection of properties specifically included
     */
    private static Collection extractImportedConstraints(String text) {
        Collection importedConstraintNames = []
        if (text.indexOf('include:') > 0) {
            String collectionString =  text[(text.lastIndexOf('[') + 1)..(text.indexOf(']') - 1)]
            importedConstraintNames = collectionString.split(',')
        }
        importedConstraintNames
    }
}
