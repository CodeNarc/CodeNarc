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
package org.codenarc.rule.unused

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractSharedAstVisitorRule
import org.codenarc.rule.AstVisitor
import org.codenarc.rule.FieldReferenceAstVisitor
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.WildcardPattern

/**
 * Rule that checks for private fields that are not referenced within the same class.
 * <p/>
 * The <code>ignoreFieldNames</code> property optionally specifies one or more
 * (comma-separated) field names that should be ignored (i.e., that should not cause a
 * rule violation). The name(s) may optionally include wildcard characters ('*' or '?').
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
class UnusedPrivateFieldRule extends AbstractSharedAstVisitorRule {
    String name = 'UnusedPrivateField'
    int priority = 2
    String ignoreFieldNames = 'serialVersionUID'

    @Override
    protected AstVisitor getAstVisitor(SourceCode sourceCode) {
        def allPrivateFields = collectAllPrivateFields(sourceCode.ast)
        return new FieldReferenceAstVisitor(allPrivateFields)
    }

    @Override
    protected List<Violation> getViolations(AstVisitor visitor, SourceCode sourceCode) {
        visitor.unreferencedFields.each { FieldNode fieldNode ->
            visitor.addViolation(fieldNode, "The field ${fieldNode.name} is not used within the class ${fieldNode.owner?.name}")
        }
        return visitor.violations
    }

    @SuppressWarnings('NestedBlockDepth')
    private collectAllPrivateFields(ast) {
        def allPrivateFields = []
        ast.classes.each { classNode ->
            if (shouldApplyThisRuleTo(classNode)) {
                classNode.fields.inject(allPrivateFields) { acc, fieldNode ->
                    def wildcardPattern = new WildcardPattern(ignoreFieldNames, false)
                    def isPrivate = fieldNode.modifiers & FieldNode.ACC_PRIVATE
                    def isNotGenerated = fieldNode.lineNumber != -1
                    def isIgnored = wildcardPattern.matches(fieldNode.name)
                    if (isPrivate && isNotGenerated && !isIgnored) {
                        acc << fieldNode
                    }
                    acc
                }
            }
        }
        allPrivateFields
    }
}

