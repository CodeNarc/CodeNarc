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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Using public fields is considered to be a bad design. Use properties instead.
 *
 * @author Victor Savkin
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class PublicInstanceFieldRule extends AbstractAstVisitorRule {
    String name = 'PublicInstanceField'
    int priority = 5
    Class astVisitorClass = PublicInstanceFieldAstVisitor
}

class PublicInstanceFieldAstVisitor extends AbstractAstVisitor {
    
    @Override
    void visitFieldEx(FieldNode node) {
        if(node.public && !node.static){
            addViolation node, createErrorMessage(node)
        }
        super.visitFieldEx node
    }

    private createErrorMessage(node) {
        "Using public fields is considered bad design. Create property '${node.name}' instead."
    }
}
