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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that verifies that the name of an interface matches a regular expression specified in
 * the <code>regex</code> property. If that property is null or empty, then this rule is not applied
 * (i.e., it does nothing). It defaults to null, so this rule must be explicitly configured to be active.
 *
 * @see ClassNameRule
 *
 * @author Chris Mair
  */
class InterfaceNameRule extends AbstractAstVisitorRule {
    String name = 'InterfaceName'
    int priority = 2
    Class astVisitorClass = InterfaceNameAstVisitor
    String regex

    boolean isReady() {
        regex
    }
}

class InterfaceNameAstVisitor extends AbstractTypeNameAstVisitor  {
    protected boolean shouldVisit(ClassNode classNode) {
        classNode.modifiers & classNode.ACC_INTERFACE
    }
}
