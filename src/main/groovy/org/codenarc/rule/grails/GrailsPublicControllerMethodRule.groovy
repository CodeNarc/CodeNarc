/*
 * Copyright 2009 the original author or authors.
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
import org.codehaus.groovy.ast.MethodNode

/**
 * Rule that checks for public methods on Grails controller classes. Static methods are ignored.
 * <p/>
 * Grails controller actions and interceptors are defined as properties on the controller class.
 * Public methods on a controller class are unnecessary. They break encapsulation and can
 * be confusing.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match files
 * under the 'grails-app/controllers' folder. You can override this with a different regular
 * expression value if appropriate.
 *
 * @author Chris Mair
 * @version $Revision: 151 $ - $Date: 2009-05-17 22:37:02 -0400 (Sun, 17 May 2009) $
 */
class GrailsPublicControllerMethodRule extends AbstractAstVisitorRule {
    String name = 'GrailsPublicControllerMethod'
    int priority = 2
    Class astVisitorClass = GrailsPublicControllerMethodAstVisitor
    String applyToFilesMatching = GrailsUtil.CONTROLLERS_FILES
}

class GrailsPublicControllerMethodAstVisitor extends AbstractAstVisitor  {
    void visitMethod(MethodNode methodNode) {
        if ( (methodNode.modifiers & MethodNode.ACC_PUBLIC) && !(methodNode.modifiers & MethodNode.ACC_STATIC))  {
            addViolation(methodNode)
        }
        super.visitMethod(methodNode)
    }
}