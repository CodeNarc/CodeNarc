/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Do not declare a method return type of Optional<List> (or Collection, ArrayList, Set, Map, HashMap, etc.). Return an empty collection instead.
 *
 * @author Chris Mair
 */
class OptionalCollectionReturnTypeRule extends AbstractAstVisitorRule {

    String name = 'OptionalCollectionReturnType'
    int priority = 2
    Class astVisitorClass = OptionalCollectionReturnTypeAstVisitor
}

class OptionalCollectionReturnTypeAstVisitor extends AbstractAstVisitor {

    private static final List<String> COLLECTION_TYPE_NAMES = [
            'Collection',
            'List', 'ArrayList', 'LinkedList',
            'Set', 'HashSet', 'LinkedHashSet', 'EnumSet', 'SortedSet', 'TreeSet',
            'Map', 'HashMap', 'LinkedHashMap', 'EnumMap', 'SortedMap', 'TreeMap']

    @Override
    void visitMethodEx(MethodNode methodNode) {
        if (methodNode.returnType.name == 'Optional') {
            def genericsTypes = methodNode.returnType.genericsTypes
            if (genericsTypes && genericsTypes[0].name in COLLECTION_TYPE_NAMES) {
                String message = "The method ${methodNode.name} in class $currentClassName returns an Optional collection"
                addViolation(methodNode, message)
            }
        }

        super.visitMethodEx(methodNode)
    }

}
