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
import org.codenarc.rule.AbstractAstVisitor

/**
 * Abstract superclass for AstVisitor classes dealing with class/type names, e.g. classes,
 * interfaces and abstract classes.
 *
 * @author Chris Mair
 * @version $Revision: 37 $ - $Date: 2009-02-06 21:31:05 -0500 (Fri, 06 Feb 2009) $
 */
abstract class AbstractTypeNameAstVisitor extends AbstractAstVisitor {
    void visitClass(ClassNode classNode) {
        assert rule.regex
        if (shouldVisit(classNode) && classNode.lineNumber >= 0 && !(classNode.getNameWithoutPackage() ==~ rule.regex)) {
            addViolation(classNode)
        }
        super.visitClass(classNode)
    }

    /**
     * @return true only if this visitor should be applied to (visit) the specified ClassNode.
     */
    protected abstract boolean shouldVisit(ClassNode classNode)
}