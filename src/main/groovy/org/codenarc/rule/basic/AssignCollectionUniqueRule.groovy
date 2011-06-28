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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * the unique method mutates the original list. If a user is using the result of this method then they probably don't understand thiss
 *
 * @author Nick Larson
 * @author Juan Vazquez
 * @author Jon DeJong
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class AssignCollectionUniqueRule extends AbstractAstVisitorRule {
    String name = 'AssignCollectionUnique'
    int priority = 2
    Class astVisitorClass = AssignCollectionUniqueAstVisitor
}

class AssignCollectionUniqueAstVisitor extends AbstractAstVisitor {
    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {

        Expression right = expression.rightExpression

        if (right instanceof MethodCallExpression) {
            if (isSimpleUniqueCall(right) || isChainedUnique(right)) {
                addViolation(expression, 'unique() mutates the original list.')
            }
        }
        super.visitDeclarationExpression expression
    }

    private static boolean isChainedUnique(MethodCallExpression right) {
        if (AstUtil.isMethodCall(right.objectExpression, 'unique', 0) || AstUtil.isMethodCall(right.objectExpression, 'unique', 1)) {
            MethodCallExpression m = right.objectExpression
            if (m.objectExpression instanceof VariableExpression) {
                return true
            } 
        }
        false
    }

    private static boolean isSimpleUniqueCall(MethodCallExpression right) {
        AstUtil.isMethodCall(right, 'unique', 0) || AstUtil.isMethodCall(right, 'unique', 1)
    }
}
