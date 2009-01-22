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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that checks for direct call to Boolean constructor. Use Boolean.valueOf() instead.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class BooleanInstantiationRule extends AbstractAstVisitorRule {
    String id = 'BooleanInstantiation'
    int priority = 2
    Class astVisitorClass = BooleanInstantiationAstVisitor
}

class BooleanInstantiationAstVisitor extends AbstractConstructorCallAstVisitor {
    static final NEW_BOOLEAN = /new +(java\.lang\.)?Boolean\(/

    protected isConstructorCallAViolation(ConstructorCallExpression constructorCall) {
        return constructorCall.text =~ NEW_BOOLEAN
    }
}