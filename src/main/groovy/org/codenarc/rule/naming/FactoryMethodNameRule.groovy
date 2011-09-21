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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

/**
 * A factory method is a method that creates objects, and they are typically named either buildFoo(), makeFoo(), or
 * createFoo(). This rule enforces that only one naming convention is used. It defaults to makeFoo(), but that can
 * be changed using the property 'regex'.
 *
 * @author Hamlet D'Arcy
 */
class FactoryMethodNameRule extends AbstractAstVisitorRule {
    String name = 'FactoryMethodName'
    int priority = 2
    Class astVisitorClass = FactoryMethodNameAstVisitor
    String regex = /(build.*|create.*)/
}

class FactoryMethodNameAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {
        if (!AstUtil.getAnnotation(node, 'Override') && node.name ==~ rule.regex) {
            if (!(currentClassName ==~ /.*Builder/)) {
                addViolation(node, "The method '$node.name' matches the regular expression /$rule.regex/ and does not appear in a class matching /*.Builder/")
            } else if (!(node.name ==~ /build.*/)) {
                addViolation(node, "The method '$node.name' matches the regular expression /$rule.regex/")
            }
        }
    }

}
