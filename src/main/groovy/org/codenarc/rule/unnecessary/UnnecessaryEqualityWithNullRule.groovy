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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.util.AstUtil

/**
 * The expression object.equals(null) is always false and is unnecessary.
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class UnnecessaryEqualityWithNullRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryEqualityWithNull'
    int priority = 2
    Class astVisitorClass = UnnecessaryEqualityWithNullAstVisitor
}

class UnnecessaryEqualityWithNullAstVisitor extends AbstractAstVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodNamed(call, 'equals', 1)) {
            List args = AstUtil.getMethodArguments(call)
            if (args?.size() == 1 && AstUtil.isNull(args[0])) {
                addViolation(call, 'The result of equals(null) is always false')
            }
        }

        super.visitMethodCallExpression(call)
    }


}
