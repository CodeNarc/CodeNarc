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
 * It is unnecessary to instantiate Float objects. Instead just use the float literal with the 'F' identifier to force the type, such as 123.45F or 0.42f.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryFloatInstantiationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryFloatInstantiation'
    int priority = 3
    Class astVisitorClass = UnnecessaryFloatInstantiationAstVisitor
}

class UnnecessaryFloatInstantiationAstVisitor extends UnnecessaryInstantiationAstVisitor {

    UnnecessaryFloatInstantiationAstVisitor() {
        super(Float, [String, Double, Float], 'f')
    }
}
