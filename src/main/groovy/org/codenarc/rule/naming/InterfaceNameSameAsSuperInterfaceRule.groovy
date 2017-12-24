/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks for any interface that has an identical name to its super-interface, other than the package. This can be very confusing.
 *
 * @author Chris Mair
 */
class InterfaceNameSameAsSuperInterfaceRule extends AbstractAstVisitorRule {

    String name = 'InterfaceNameSameAsSuperInterface'
    int priority = 2
    Class astVisitorClass = InterfaceNameSameAsSuperInterfaceAstVisitor
}

class InterfaceNameSameAsSuperInterfaceAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassEx(ClassNode node) {
        if (node.isInterface()) {
            node.interfaces.each { superInterface ->
                if (node.nameWithoutPackage == superInterface.nameWithoutPackage) {
                    addViolation(node, "Interface ${node.name} has the same simple name as its super-interface ${superInterface.name}")
                }
            }
        }
        super.visitClassEx(node)
    }

}
