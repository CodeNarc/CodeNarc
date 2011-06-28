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

import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * To make a reference to a class, it is unnecessary to specify the '.class' identifier. For instance String.class can be shortened to String.
 *
 * @author 'Dean Del Ponte'
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class UnnecessaryDotClassRule extends AbstractAstVisitorRule {
    String name = 'UnecessaryDotClass'
    int priority = 2
    Class astVisitorClass = UnnecessaryDotClassAstVisitor
}

class UnnecessaryDotClassAstVisitor extends AbstractAstVisitor {

    @Override
    void visitPropertyExpression(PropertyExpression expression) {

        if (AstUtil.isConstant(expression.property, 'class')) {
            if (AstUtil.isVariable(expression.objectExpression, '[A-Z].*')) {
                addViolation(expression, "${expression.objectExpression.name}.class can be rewritten as $expression.objectExpression.name")
            }
        }
        super.visitPropertyExpression(expression)
    }


}
