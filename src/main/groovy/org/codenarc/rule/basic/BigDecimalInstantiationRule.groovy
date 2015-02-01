/*
 * Copyright 2010 the original author or authors.
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

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractConstructorCallAstVisitor

/**
 * Rule that checks for calls to the BigDecimal constructor with a double or float literal.
 * The String literal should be used instead.
 *
 * @author Chris Mair
 */
class BigDecimalInstantiationRule extends AbstractAstVisitorRule {
    String name = 'BigDecimalInstantiation'
    int priority = 2
    Class astVisitorClass = BigDecimalInstantiationAstVisitor
}

class BigDecimalInstantiationAstVisitor extends AbstractConstructorCallAstVisitor {
    static final NEW_BIG_DECIMAL = /new +(java\.math\.)?BigDecimal\(/

    @SuppressWarnings('ExplicitCallToGetAtMethod')
    protected isConstructorCallAViolation(ConstructorCallExpression constructorCall) {
        def firstArgExpression = constructorCall.arguments?.expressions?.getAt(0)
        constructorCall.text =~ NEW_BIG_DECIMAL &&
            (firstArgExpression instanceof ConstantExpression) &&
            (firstArgExpression.type.name in ['java.math.BigDecimal', 'java.lang.Double', 'double'])
    }

    @Override protected String getViolationMessage(ConstructorCallExpression call) {
        """Call to $call.text uses the double constructor and should probably be replaced with new ${call.type.name}("${call.arguments?.expressions?.first().text}")"""
    }
}
