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

import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that checks for references to the servletContext object from within Grails controller and
 * taglib classes.
 * <p/>
 * This rule is intended as a "governance" rule to enable monitoring and controlling access to the
 * servletContext from within application source code. Storing objects in the servletContext may
 * inhibit scalability and/or performance and should be carefully considered. Furthermore, access
 * to the servletContext is not synchronized, so reading/writing objects from the servletConext must
 * be manually synchronized, as described in <b>The Definitive Guide to Grails</b> (2nd edition).
 * <p/>
 * Enabling this rule may make most sense in a team environment where team members exhibit a broad
 * range of skill and experience levels. Appropriate servletContext access can be configured as
 * exceptions to this rule by configuring either the <code>doNotApplyToFilenames</code> or
 * <code>doNotApplyToFilesMatching</code> property of the rule.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match files
 * under the 'grails-app/controllers' or 'grails-app/taglib' folders. You can override this
 * with a different regular expression value if appropriate.
 *
 * @author Chris Mair
  */
class GrailsServletContextReferenceRule extends AbstractAstVisitorRule {
    String name = 'GrailsServletContextReference'
    int priority = 2
    Class astVisitorClass = GrailsServletContextReferenceAstVisitor
    String applyToFilesMatching = GrailsUtil.CONTROLLERS_AND_TAGLIB_FILES
}

class GrailsServletContextReferenceAstVisitor extends AbstractAstVisitor  {
    void visitVariableExpression(VariableExpression expression) {
        if (isFirstVisit(expression) && expression.variable == 'servletContext') {
            addViolation(expression, 'Storing objects in the servletContext can limit scalability')
        }
        super.visitVariableExpression(expression)
    }
}
