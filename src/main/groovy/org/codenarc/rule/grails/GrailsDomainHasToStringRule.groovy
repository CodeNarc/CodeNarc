/*
 * Copyright 2011 the original author or authors.
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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.ClassNode

/**
 * Checks that Grails domain classes redefine toString()
 *
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class GrailsDomainHasToStringRule extends AbstractAstVisitorRule {
    String name = 'GrailsDomainHasToString'
    int priority = 2
    Class astVisitorClass = GrailsDomainHasToStringAstVisitor
    String applyToFilesMatching = GrailsUtil.DOMAIN_FILES
}

class GrailsDomainHasToStringAstVisitor extends AbstractAstVisitor {

    void visitClassComplete(ClassNode classNode) {
        if (isFirstVisit(classNode)) {
            if (!classNode.methods.any { AstUtil.isMethodNode(it, 'toString', 0) }) {
                addViolation(classNode, "The domain class $classNode.name should define a toString() method")
            }
        }
    }
}
