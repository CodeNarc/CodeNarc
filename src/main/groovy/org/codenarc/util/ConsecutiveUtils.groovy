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
package org.codenarc.util

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression

/**
 *
 * Utility class for Consecutive* rules.
 * 
 * @author Hamlet D'Arcy
 */
class ConsecutiveUtils {
    static boolean areJoinableConstants(Expression left, Expression right) {
        if (left.lastLineNumber != right.lineNumber) {
            return false
        }
        if (isJoinableType(left) && isJoinableType(right)) {
            return true
        }
        false
    }

    static boolean isJoinableType(Expression expression) {
        if (expression instanceof ConstantExpression && !AstUtil.isNull(expression)) {
            return true
        }
        return expression instanceof GStringExpression
    }
}
