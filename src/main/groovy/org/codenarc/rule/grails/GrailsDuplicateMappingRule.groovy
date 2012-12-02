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
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.FieldNode

/**
 * Check for duplicate entry in a domain class mapping
 *
 * @author Chris Mair
 */
class GrailsDuplicateMappingRule extends AbstractAstVisitorRule {

    String name = 'GrailsDuplicateMapping'
    int priority = 2
    Class astVisitorClass = GrailsDuplicateMappingAstVisitor
    String applyToFilesMatching = GrailsUtil.DOMAIN_FILES
}

class GrailsDuplicateMappingAstVisitor extends AbstractAstVisitor {

    private final Set<String> mappingNames = []
    private boolean withinMapping

    @Override
    void visitField(FieldNode node) {
        if (node.name == 'mapping') {
            withinMapping = true
            super.visitField(node)
            withinMapping = false
        }
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (withinMapping && isFirstVisit(call)) {
            def name = call.methodAsString
            if (name in mappingNames) {
                addViolation(call, "The mapping for $name in domain class $currentClassName has already been specified")
            }
            else {
                mappingNames << name
            }

            // Process optional nested columns closure
            if (name == 'columns') {
                super.visitMethodCallExpression(call)
            }
        }
    }
}
