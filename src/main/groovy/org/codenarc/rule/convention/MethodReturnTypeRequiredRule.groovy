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
import org.codenarc.util.WildcardPattern

/**
 * Checks that method return types are not dynamic, that they are explicitly stated and different than def.
 * The <code>ignoreMethods</code> property optionally specifices which methode names to ignore. Format is a comma separated list.
 *
 * @author Marcin Erdmann
 */
class MethodReturnTypeRequiredRule extends AbstractAstVisitorRule {

    String name = 'MethodReturnTypeRequired'
    int priority = 3
    Class astVisitorClass = MethodReturnTypeRequiredAstVisitor
    String ignoreMethodNames = ''
}

class MethodReturnTypeRequiredAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitMethodEx(MethodNode node) {
        if (node.dynamicReturnType && isNotIgnoredMethodName(node)) {
            addViolation(node, $/Method "$node.name" has a dynamic return type/$)
        }
    }

    private boolean isNotIgnoredMethodName(MethodNode node) {
        !(new WildcardPattern(rule.ignoreMethodNames, false).matches(node.name))
    }
}
