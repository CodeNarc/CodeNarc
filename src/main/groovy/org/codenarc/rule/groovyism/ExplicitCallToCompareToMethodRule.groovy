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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * This rule detects when the compareTo(Object) method is called directly in code instead of using the <=>, >, >=, <, and <= operators. A groovier way to express this: a.compareTo(b) is this: a <=> b, or using the other operators. 
 *
 * @author Hamlet D'Arcy
 */
class ExplicitCallToCompareToMethodRule extends AbstractAstVisitorRule {
    String name = 'ExplicitCallToCompareToMethod'
    int priority = 2
    Class astVisitorClass = ExplicitCallToCompareToMethodAstVisitor
    boolean ignoreThisReference = false
}

class ExplicitCallToCompareToMethodAstVisitor extends ExplicitCallToMethodAstVisitor {

    ExplicitCallToCompareToMethodAstVisitor() {
        super('compareTo')
    }

    @Override
    protected String getViolationMessage(MethodCallExpression exp) {
        "Explicit call to ${exp.text} method can be rewritten using the compareTo operators such as >, <, <=, >=, and <=>"
    }
}
