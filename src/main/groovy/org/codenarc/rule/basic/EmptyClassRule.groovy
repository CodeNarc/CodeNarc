/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Reports classes without methods, fields or properties.
 *
 * Ignores interfaces, Enums, anonymous inner classes, subclasses (extends), and classes with annotations.
 *
 * @author Artur Gajowy
 */
class EmptyClassRule extends AbstractRule {
    String name = 'EmptyClass'
    int priority = 2

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        sourceCode.ast?.classes?.each { classNode ->
            if (
                    !classNode.isInterface() &&
                    !classNode.isEnum() &&
                    !isAnonymousInnerClass(classNode) &&
                    !isSubclass(classNode) &&
                    !hasAnnotation(classNode) &&
                    isEmpty(classNode)) {
                violations << createViolation(sourceCode, classNode, violationMessage(classNode))
            }
        }
    }

    private boolean isSubclass(ClassNode classNode) {
        return classNode.superClass.name != 'java.lang.Object'
    }

    private boolean hasAnnotation(ClassNode classNode) {
        return classNode.getAnnotations()
    }

    private boolean isAnonymousInnerClass(ClassNode classNode) {
        return classNode instanceof InnerClassNode && classNode.isAnonymous()
    }

    private boolean isEmpty(ClassNode classNode) {
        classNode.with {
            [methods, declaredConstructors, fields].every { it.isEmpty() }
        }
    }

    private String violationMessage(ClassNode classNode) {
        def name = classNode.nameWithoutPackage
        "Class '$name' is empty (has no methods, fields or properties). Why would you need a class like this?"
    }
}
