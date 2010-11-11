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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.MethodCallExpression

/**
 * This rule detects JUnit calling assertEquals where the first or second parameter is null. These assertion should be made against the assertNull method instead.
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class UseAssertNullInsteadOfAssertEqualsRule extends AbstractAstVisitorRule {
    String name = 'UseAssertNullInsteadOfAssertEquals'
    int priority = 3
    Class astVisitorClass = UseAssertNullInsteadOfAssertEqualsAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class UseAssertNullInsteadOfAssertEqualsAstVisitor extends AbstractAstVisitor {
    def void visitMethodCallExpression(MethodCallExpression call) {

        List args = AstUtil.getMethodArguments(call)
        if (AstUtil.isMethodCall(call, 'this', 'assertEquals')) {

            if (args.size() == 2 && (AstUtil.isNull(args[0]) || AstUtil.isNull(args[1]))) {
                addViolation call
            } else if (args.size() == 3 && (AstUtil.isNull(args[1]) || AstUtil.isNull(args[2]))) {
                addViolation call
            }
        }
        super.visitMethodCallExpression call
    }

}
