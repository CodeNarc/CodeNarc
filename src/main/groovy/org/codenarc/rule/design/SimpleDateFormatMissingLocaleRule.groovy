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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Be sure to specify a Locale when creating a new instance of SimpleDateFormat; the class is locale-sensitive. If you
 * instantiate SimpleDateFormat without a Locale parameter, it will format the date and time according to the default
 * Locale. Both the pattern and the Locale determine the format. For the same pattern, SimpleDateFormat may format a date
 * and time differently if the Locale varies.
 *
 * @author Hamlet D'Arcy
 */
class SimpleDateFormatMissingLocaleRule extends AbstractAstVisitorRule {
    String name = 'SimpleDateFormatMissingLocale'
    int priority = 2
    Class astVisitorClass = SimpleDateFormatMissingLocaleAstVisitor
}

class SimpleDateFormatMissingLocaleAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {

        if (isFirstVisit(call) && call.type.name in ['SimpleDateFormat', 'java.text.SimpleDateFormat']) {
            if (!hasMinimumParameterCount(call, 2)) {
                addViolation(call, 'Created an instance of SimpleDateFormat without specifying a Locale')
            }
        }

        super.visitConstructorCallExpression(call)
    }

    private static boolean hasMinimumParameterCount(ConstructorCallExpression call, int minimum) {
        def argumentsExpression = call.arguments
        if (AstUtil.respondsTo(argumentsExpression, 'getExpressions')) {
            return argumentsExpression.expressions.size() >= minimum
        }
        false
    }

}
