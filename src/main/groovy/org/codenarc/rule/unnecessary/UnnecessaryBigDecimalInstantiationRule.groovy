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

import org.codenarc.rule.AbstractAstVisitorRule

/**
 * It is unnecessary to instantiate BigDecimal objects. Instead just use the decimal literal or the 'G' identifier to force the type, such as 123.45 or 123.45G.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryBigDecimalInstantiationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryBigDecimalInstantiation'
    int priority = 3
    Class astVisitorClass = UnnecessaryBigDecimalInstantiationAstVisitor
}

class UnnecessaryBigDecimalInstantiationAstVisitor extends UnnecessaryInstantiationAstVisitor {

    UnnecessaryBigDecimalInstantiationAstVisitor() {
        super(BigDecimal, [String, Double], 'G')
    }

    @Override
    protected boolean shouldSkipViolation(Object value) {
        (value instanceof String) && !value.contains('.')
    }

    @Override
    protected boolean isTypeSuffixNecessary(argument) {
        if (argument.getClass() == String) {
            return !argument.contains('.')
        }
        return argument.getClass() in [Integer, Long]
    }

}
