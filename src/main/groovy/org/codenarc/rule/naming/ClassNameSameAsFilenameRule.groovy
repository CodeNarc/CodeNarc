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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Reports files containing only one top level class / enum / interface which is named differently than the file.
 *
 * @author Artur Gajowy
 * @author Chris Mair
 */
class ClassNameSameAsFilenameRule extends AbstractRule {
    
    String name = 'ClassNameSameAsFilename'
    int priority = 2

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        if (!sourceCode.name) {
            return
        }

        List<ClassNode> classes = sourceCode?.ast?.classes
        List<ClassNode> topLevelClasses = classes?.findAll { !it.outerClass }
        ClassNode onlyTopLevelClass = topLevelClasses?.size() == 1 ? topLevelClasses.first() : null
        if (onlyTopLevelClass && onlyTopLevelClass.superClass != ClassHelper.make(Script)) {
            String className = onlyTopLevelClass.nameWithoutPackage
            if (className != sourceCode.name - '.groovy') {
                violations << createViolation(sourceCode, onlyTopLevelClass, 
                    "${classNodeType(onlyTopLevelClass)} `$className` is the only class in `${sourceCode.name}`. " +
                    'In such a case the file and the class should have the same name.')
            }
        }
    }

    String classNodeType(ClassNode classNode) {
        return classNode.isInterface() ? 'Interface' :
               classNode.isEnum() ? 'Enum' :
               'Class' 
    }
}
