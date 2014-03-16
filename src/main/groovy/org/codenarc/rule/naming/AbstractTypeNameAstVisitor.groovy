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
import org.codehaus.groovy.ast.InnerClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.util.GroovyVersion

/**
 * Abstract superclass for AstVisitor classes dealing with class/type names, e.g. classes,
 * interfaces and abstract classes.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
abstract class AbstractTypeNameAstVisitor extends AbstractAstVisitor {
    void visitClassEx(ClassNode classNode) {
        assert rule.regex

        if (GroovyVersion.isGroovy1_8_OrGreater() && classNode instanceof InnerClassNode && classNode.anonymous) {
            // do nothing for anonymous inner classes
            super.visitClassEx(classNode)
        } else if (GroovyVersion.isGroovy1_8_OrGreater() && classNode.isScript()) {
            // do nothing for script classes 
            super.visitClassEx(classNode)
        } else {
            if (shouldVisit(classNode) && !(classNode.getNameWithoutPackage() ==~ rule.regex)) {
                addViolation(classNode, "The name ${classNode.getNameWithoutPackage()} failed to match the pattern ${rule.regex.toString()}")
            }
            super.visitClassEx(classNode)
        }
    }

    /**
     * @return true only if this visitor should be applied to (visit) the specified ClassNode.
     */
    protected abstract boolean shouldVisit(ClassNode classNode)
}
