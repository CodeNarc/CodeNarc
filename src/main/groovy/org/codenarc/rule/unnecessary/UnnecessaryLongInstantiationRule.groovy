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
 * It is unnecessary to instantiate Long objects. Instead just use the literal with the 'L' identifier to force the type, such as 8L or 42L.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryLongInstantiationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryLongInstantiation'
    int priority = 3
    Class astVisitorClass = UnnecessaryLongInstantiationAstVisitor
}

class UnnecessaryLongInstantiationAstVisitor extends UnnecessaryInstantiationAstVisitor {

    UnnecessaryLongInstantiationAstVisitor() {
        super(Long, [String, Long], 'L')
    }
}
