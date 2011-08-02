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
 * It is unnecessary to instantiate Integer objects. Instead just use the literal with the 'I' identifier to force the type, such as 8I or 42i. 
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryIntegerInstantiationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryIntegerInstantiation'
    int priority = 3
    Class astVisitorClass = UnnecessaryIntegerInstantiationAstVisitor
}

class UnnecessaryIntegerInstantiationAstVisitor extends UnnecessaryInstantiationAstVisitor {

    UnnecessaryIntegerInstantiationAstVisitor() {
        super(Integer, [String, Integer], 'i')
    }

    @Override
    protected boolean isTypeSuffixNecessary(argument) {
        return false
    }

}
