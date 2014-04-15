/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.PropertyNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern

/**
 * Checks for public properties defined on JUnit test classes. There should typically be no need to
 * expose a public property on a test class.
 *
 * @author Chris Mair
 */
class JUnitPublicPropertyRule extends AbstractAstVisitorRule {

    String name = 'JUnitPublicProperty'
    int priority = 2
    Class astVisitorClass = JUnitPublicPropertyAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
    String ignorePropertyNames
}

class JUnitPublicPropertyAstVisitor extends AbstractAstVisitor {

    @Override
    void visitProperty(PropertyNode node) {
        if (!isIgnoredPropertyName(node)) {
            addViolation(node, "The test class $currentClassName contains a public property ${node.name}. There is usually no reason to have a public property (even a constant) on a test class.")
        }
        super.visitProperty(node)
    }

    private boolean isIgnoredPropertyName(PropertyNode node) {
        new WildcardPattern(rule.ignorePropertyNames, false).matches(node.name)
    }

}
