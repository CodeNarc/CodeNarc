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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ModuleNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * The groovy.lang.Immutable annotation has been deprecated and replaced by groovy.transform.Immutable. Do not use the
 * Immutable in groovy.lang.
 *
 * @author Hamlet D'Arcy
 */
class GroovyLangImmutableRule extends AbstractAstVisitorRule {
    String name = 'GroovyLangImmutable'
    int priority = 2
    Class astVisitorClass = GroovyLangImmutableAstVisitor
}

class GroovyLangImmutableAstVisitor extends AbstractAstVisitor {
    boolean groovyTransformIsStarImported = false
    boolean groovyTransformIsImported = false
    List<String> aliases = []
    
    @Override
    void visitImports(ModuleNode node) {

        groovyTransformIsImported  = node.imports.any { it.type.name == 'groovy.transform.Immutable' }
        groovyTransformIsStarImported = node.starImports.any { it.packageName == 'groovy.transform.' }
        aliases = node.imports.findAll { it.type.name == 'groovy.lang.Immutable' && it.alias }*.alias 
        super.visitImports(node)
    }

    @Override protected void visitClassComplete(ClassNode node) {
        node?.annotations?.each { AnnotationNode anno ->
            if (anno?.classNode?.name == 'groovy.lang.Immutable') {
                addViolation(anno, 'groovy.lang.Immutable is deprecated in favor of groovy.transform.Immutable')
            } else if (anno?.classNode?.name == 'Immutable' && !groovyTransformIsStarImported && !groovyTransformIsImported ) {
                addViolation(anno, 'groovy.lang.Immutable is deprecated in favor of groovy.transform.Immutable')
            } else if (aliases.contains(anno?.classNode?.name)) {
                addViolation(anno, 'groovy.lang.Immutable is deprecated in favor of groovy.transform.Immutable')
            }
        }
        super.visitClassComplete(node)
    }

}
