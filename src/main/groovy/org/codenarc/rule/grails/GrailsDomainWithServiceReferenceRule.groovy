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
package org.codenarc.rule.grails

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor

/**
 * Checks that Grails Domain classes do not have Service classes injected.
 *
 * @author Artur Gajowy
 */
class GrailsDomainWithServiceReferenceRule extends AbstractAstVisitorRule {
    String name = 'GrailsDomainWithServiceReference'
    int priority = 2
    Class astVisitorClass = GrailsDomainCantReferenceServiceAstVisitor
    String applyToFilesMatching = GrailsUtil.DOMAIN_FILES
}

class GrailsDomainCantReferenceServiceAstVisitor extends AbstractFieldVisitor {

    @Override
    void visitField(FieldNode node) {
        if (node.name.endsWith('Service')) {
            def domainName = node.declaringClass.nameWithoutPackage
            addViolation(node, "Domain class $domainName should not reference services (offending field: ${node.name})")
        }
    }
}
