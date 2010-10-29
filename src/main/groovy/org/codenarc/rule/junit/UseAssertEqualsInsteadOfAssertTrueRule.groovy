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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * This rule detects JUnit assertions in object equality. These assertions should be made by more specific
 * methods, like assertEquals.
 *
 * @author Per Junel
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class UseAssertEqualsInsteadOfAssertTrueRule extends AbstractAstVisitorRule {
    String name = 'UseAssertEqualsInsteadOfAssertTrue'
    int priority = 2
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
    Class astVisitorClass = UseAssertEqualsInsteadOfAssertTrueAstVisitor
}

class UseAssertEqualsInsteadOfAssertTrueAstVisitor extends AbstractAstVisitor {

    def void visitMethodCallExpression(MethodCallExpression call) {

        List args = AstUtil.getMethodArguments(call)
        if (AstUtil.isMethodCall(call, "this", "assertTrue") || AstUtil.isMethodCall(call, "this", "assertFalse")) {

            if (args.size() < 3 && args.size() > 0) {
                def arg = args[-1]
                if (AstUtil.isBinaryExpressionType(arg, '==') || AstUtil.isBinaryExpressionType(arg, '!=')) {
                    addViolation call
                }
            }
        }

        super.visitMethodCallExpression call
    }

}
