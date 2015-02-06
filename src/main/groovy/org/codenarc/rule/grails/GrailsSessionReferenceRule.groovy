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
 * Rule that checks for references to the session object from within Grails controller and
 * taglib classes.
 * <p/>
 * This rule is intended as a "governance" rule to enable monitoring and controlling access to the
 * session from within application source code. Storing objects in the session may inhibit scalability
 * and/or performance and should be carefully considered.
 * <p/>
 * Enabling this rule may make most sense in a team environment where team members exhibit a broad
 * range of skill and experience levels. Appropriate session access can be configured as exceptions
 * to this rule by configuring either the <code>doNotApplyToFilenames</code> or
 * <code>doNotApplyToFilesMatching</code> property of the rule.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match files
 * under the 'grails-app/controllers' or 'grails-app/taglib' folders. You can override this
 * with a different regular expression value if appropriate.
 *
 * @deprecated This rule is deprecated and disabled (enabled=false) by default
 *
 * @author Chris Mair
  */
class GrailsSessionReferenceRule extends AbstractAstVisitorRule {
    String name = 'GrailsSessionReference'
    int priority = 2
    Class astVisitorClass = GrailsSessionReferenceAstVisitor
    String applyToFilesMatching = GrailsUtil.CONTROLLERS_AND_TAGLIB_FILES

    GrailsSessionReferenceRule() {
        this.enabled = false        // deprecated; disabled by default
    }
}

class GrailsSessionReferenceAstVisitor extends AbstractAstVisitor  {
    void visitVariableExpression(VariableExpression expression) {
        if (isFirstVisit(expression) && expression.variable == 'session') {
            addViolation(expression, 'Interacting with the Grails session object can limit scalability')
        }
        super.visitVariableExpression(expression)
    }
}
