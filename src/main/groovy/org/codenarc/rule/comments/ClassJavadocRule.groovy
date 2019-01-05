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
package org.codenarc.rule.comments

import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Makes sure each class and interface definition is preceded by javadoc. Enum definitions are not checked,
 * due to strange behavior in the Groovy AST.
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
  */
class ClassJavadocRule extends AbstractRule {

    String name = 'ClassJavadoc'
    int priority = 2
    boolean applyToNonMainClasses = false

    /**
     * Apply the rule to the given source, writing violations to the given list.
     * @param sourceCode The source to check
     * @param violations A list of Violations that may be added to. It can be an empty list
     */
    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        def lines = sourceCode.getLines()
        sourceCode.ast?.classes?.each { classNode ->
            if (!applyToNonMainClasses && sourceCodeNameWithoutExtension(sourceCode) != classNode.nameWithoutPackage) {
                return // only apply to classes that have same name as the source unit.
            }

            if (classNode.isPrimaryClassNode() && classNode.superClass.name != 'java.lang.Enum') {
                def index = classNode.lineNumber - 1
                boolean isValidLineBeforeStartfOfJavadoc = true

                while (index > 0 && isValidLineBeforeStartfOfJavadoc) {
                    String currentLineTrimmed = lines[--index].trim()

                    // Valid lines before the start of the javadoc are:
                    // - a blank line
                    isValidLineBeforeStartfOfJavadoc = currentLineTrimmed.isEmpty()
                    // - a regular comment
                    isValidLineBeforeStartfOfJavadoc |= currentLineTrimmed.startsWith('//')
                    // - a class annotation
                    isValidLineBeforeStartfOfJavadoc |= currentLineTrimmed.startsWith('@')
                    // - a line of javadoc
                    isValidLineBeforeStartfOfJavadoc |= currentLineTrimmed.startsWith('*')
                }

                if (!lines[index].trim().startsWith('/**')) {
                    violations.add(createViolation(sourceCode, classNode, "Class $classNode.name missing Javadoc"))
                }
            }
        }
    }

    protected String sourceCodeNameWithoutExtension(SourceCode sourceCode) {
        def indexOfPeriod = sourceCode.name?.lastIndexOf('.')
        if (indexOfPeriod && indexOfPeriod != -1) {
            return sourceCode.name[0..indexOfPeriod - 1]
        }
        return sourceCode.getName()
    }
}
