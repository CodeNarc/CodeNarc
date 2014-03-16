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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AstVisitor

/**
 * This rule checks for the explicit instantiation of a LinkedList using the no-arg constructor.
 * In Groovy, it is best to write "new LinkedList()" as "[] as Queue", which creates the same object.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class ExplicitLinkedListInstantiationRule extends AbstractAstVisitorRule {
    String name = 'ExplicitLinkedListInstantiation'
    int priority = 2

    @Override
    AstVisitor getAstVisitor() {
        new ExplicitTypeInstantiationAstVisitor('LinkedList') {
            @Override
            protected String createErrorMessage() {
                'LinkedList objects are better instantiated using the form "[] as Queue"'
            }
        }
    }
}
