/*
 * Copyright 2010 the original author or authors.
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

import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * A rule that disallows GStrings as map keys as they might change
 * their hashcode over time.
 * The map keys and values are recursively checked.
 *
 * @author @Hackergarten
 */
class GStringAsMapKeyRule extends AbstractAstVisitorRule {
    String name = 'GStringAsMapKey'
    int priority = 2
    Class astVisitorClass = GStringAsMapKeyAstVisitor
}

class GStringAsMapKeyAstVisitor extends AbstractAstVisitor {

    void visitMapEntryExpression(MapEntryExpression expression) {
        if (AstUtil.classNodeImplementsType(expression?.keyExpression?.type, GString)) {
            addViolation expression, 'GString as a key in a map is unsafe'
        }
        super.visitMapEntryExpression expression // needed for GStrings in nested keys and values
    }

}
