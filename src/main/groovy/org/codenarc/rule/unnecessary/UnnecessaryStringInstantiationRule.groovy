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

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractConstructorCallAstVisitor

/**
 * Rule that checks for direct call to the String constructor that accepts a String literal.
 * In almost all cases, this is unnecessary - use a String literal instead.
 *
 * @author Chris Mair
  */
class UnnecessaryStringInstantiationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryStringInstantiation'
    int priority = 3
    Class astVisitorClass = UnnecessaryStringInstantiationAstVisitor
}

class UnnecessaryStringInstantiationAstVisitor extends AbstractConstructorCallAstVisitor {
    static final NEW_STRING = /new +(java\.lang\.)?String\(/

    @SuppressWarnings('ExplicitCallToGetAtMethod')
    protected isConstructorCallAViolation(ConstructorCallExpression constructorCall) {
        def firstArgExpression = constructorCall.arguments?.expressions?.getAt(0)
        constructorCall.text =~ NEW_STRING && (firstArgExpression instanceof ConstantExpression)
    }

    @Override protected String getViolationMessage(ConstructorCallExpression call) {
        'There is typically no need to call the String constructor'
    }
}

