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
package org.codenarc.rule.formatting

import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Check for configured formatting of whitespace around colons for literal Map entries
 *
 * @author Chris Mair
 */
class SpaceAroundMapEntryColonRule extends AbstractAstVisitorRule {

    String name = 'SpaceAroundMapEntryColon'
    int priority = 3
    String characterBeforeColonRegex = /\S/
    String characterAfterColonRegex = /\S/
    Class astVisitorClass = SpaceAroundMapEntryColonAstVisitor
}

class SpaceAroundMapEntryColonAstVisitor extends AbstractAstVisitor {

    @Override
    void visitMapEntryExpression(MapEntryExpression expression) {
        if (expression.lineNumber != -1) {
            handleMapExpression(expression)
        }
        super.visitMapEntryExpression(expression)
    }

    private void handleMapExpression(MapEntryExpression expression) {
        def line = sourceLine(expression)
        def colonIndex = expression.lastColumnNumber - 1
        def charBeforeColon = line[colonIndex - 2]

        // Handle special case of colon as the last char of the line
        def charAfterColon = colonIndex >= line.size() ? '\n' : line[colonIndex]

        if (!(charBeforeColon ==~ rule.characterBeforeColonRegex)) {
            String keyName = expression.keyExpression.text
            addViolation(expression, violationMessage(keyName, 'preceded', rule.characterBeforeColonRegex))
        }
        if (!(charAfterColon ==~ rule.characterAfterColonRegex)) {
            String keyName = expression.keyExpression.text
            addViolation(expression, violationMessage(keyName, 'followed', rule.characterAfterColonRegex))
        }
    }

    private String violationMessage(String keyName, String precededOrFollowed, String regex) {
        return "The colon for the literal Map entry for key [$keyName] within class $currentClassName" +
            " is not $precededOrFollowed by a match for regular expression [$regex]"
    }

}
