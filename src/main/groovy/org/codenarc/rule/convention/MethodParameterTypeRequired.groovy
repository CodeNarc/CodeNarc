/*
 * Copyright 2017 the original author or authors.
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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AstVisitor

/**
 * Checks that method parameters are not dynamically typed, that is they are explicitly stated and different than def.
 * The <code>ignoredParameters</code> property optionally specifies which parameter names to ignore. Format is a comma separated list.
 *
 * @author Marcin Erdmann
 */
class MethodParameterTypeRequired extends AbstractAstVisitorRule {

    String name = 'MethodParameterTypeRequired'
    int priority = 3
    String ignoredParameters = ''

    @Override
    AstVisitor getAstVisitor() {
        Set<String> parsedIgnoredMethods = ignoredParameters.tokenize(',').toSet()
        return new MethodParameterTypeRequiredAstVisitor(parsedIgnoredMethods)
    }
}

class MethodParameterTypeRequiredAstVisitor extends AbstractAstVisitor {

    Set<String> ignoredParameters

    MethodParameterTypeRequiredAstVisitor(Set<String> ignoredParameters) {
        this.ignoredParameters = ignoredParameters
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        def dynamicallyTypedParameters = node.parameters.findAll { it.dynamicTyped && !ignoredParameters.contains(it.name) }
        dynamicallyTypedParameters.each { parameter ->
            addViolation(node, $/"$parameter.name" parameter of "$node.name" method is dynamically typed/$)
        }
    }

}
