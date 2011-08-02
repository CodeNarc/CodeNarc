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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractAstVisitorRule

/**
 * It is unnecessary to instantiate BigInteger objects. Instead just use the literal with the 'G' identifier to force the type, such as 8G or 42G.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryBigIntegerInstantiationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryBigIntegerInstantiation'
    int priority = 3
    Class astVisitorClass = UnnecessaryBigIntegerInstantiationAstVisitor
}

class UnnecessaryBigIntegerInstantiationAstVisitor extends UnnecessaryInstantiationAstVisitor {

    UnnecessaryBigIntegerInstantiationAstVisitor() {
        super(BigInteger, [String], 'G')
    }
}

