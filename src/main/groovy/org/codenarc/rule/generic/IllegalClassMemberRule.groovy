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
package org.codenarc.rule.generic

import static org.codenarc.util.ModifiersUtil.matchesAnyModifiers
import static org.codenarc.util.ModifiersUtil.parseModifiersList

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.PropertyNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern

/**
 * Checks for classes containing fields/properties/methods matching configured illegal member modifiers.
 *
 * @author Chris Mair
 */
class IllegalClassMemberRule extends AbstractAstVisitorRule {

    String name = 'IllegalClassMember'
    int priority = 2
    Class astVisitorClass = IllegalClassMemberAstVisitor
    String ignoreMethodNames
    String ignoreMethodsWithAnnotationNames

    protected Collection<Integer> illegalFieldModifiersList = []
    protected String illegalFieldModifiersString
    protected Collection<Integer> allowedFieldModifiersList = []
    protected String allowedFieldModifiersString

    protected Collection<Integer> illegalPropertyModifiersList = []
    protected String illegalPropertyModifiersString
    protected Collection<Integer> allowedPropertyModifiersList = []
    protected String allowedPropertyModifiersString

    protected Collection<Integer> illegalMethodModifiersList = []
    protected String illegalMethodModifiersString
    protected Collection<Integer> allowedMethodModifiersList = []
    protected String allowedMethodModifiersString

    void setIllegalFieldModifiers(String illegalFieldModifiers) {
        this.illegalFieldModifiersString = illegalFieldModifiers
        this.illegalFieldModifiersList = parseModifiersList(illegalFieldModifiers)
    }

    void setAllowedFieldModifiers(String allowedFieldModifiers) {
        this.allowedFieldModifiersString = allowedFieldModifiers
        this.allowedFieldModifiersList = parseModifiersList(allowedFieldModifiers)
    }

    void setIllegalPropertyModifiers(String illegalPropertyModifiers) {
        this.illegalPropertyModifiersString = illegalPropertyModifiers
        this.illegalPropertyModifiersList = parseModifiersList(illegalPropertyModifiers)
    }

    void setAllowedPropertyModifiers(String allowedPropertyModifiers) {
        this.allowedPropertyModifiersString = allowedPropertyModifiers
        this.allowedPropertyModifiersList = parseModifiersList(allowedPropertyModifiers)
    }

    void setIllegalMethodModifiers(String illegalMethodModifiers) {
        this.illegalMethodModifiersString = illegalMethodModifiers
        this.illegalMethodModifiersList = parseModifiersList(illegalMethodModifiers)
    }

    void setAllowedMethodModifiers(String allowedMethodModifiers) {
        this.allowedMethodModifiersString = allowedMethodModifiers
        this.allowedMethodModifiersList = parseModifiersList(allowedMethodModifiers)
    }

    @Override
    boolean isReady() {
        (illegalFieldModifiersList || allowedFieldModifiersList ||
            illegalMethodModifiersList || allowedMethodModifiersList ||
            illegalPropertyModifiersList || allowedPropertyModifiersList) &&
            (applyToClassNames || applyToFileNames || applyToFilesMatching)
    }
}

class IllegalClassMemberAstVisitor extends AbstractAstVisitor {

    @Override
    void visitField(FieldNode node) {
        boolean matchesIllegal = matchesAnyModifiers(node.modifiers, rule.illegalFieldModifiersList)
        if (matchesIllegal) {
            addViolation(node, "Field \"${node.name}\" has modifiers matching one of the configured illegalFieldModifiers: \"${rule.illegalFieldModifiersString}\"")
        }

        if (rule.allowedFieldModifiersList) {
            boolean matchesAllowed = matchesAnyModifiers(node.modifiers, rule.allowedFieldModifiersList)
            if (!matchesAllowed) {
                addViolation(node, "Field \"${node.name}\" does not have modifiers matching one of the configured allowedFieldModifiers: \"${rule.allowedFieldModifiersString}\"")
            }
        }
        super.visitField(node)
    }

    @Override
    void visitProperty(PropertyNode node) {
        boolean matchesIllegal = matchesAnyModifiers(node.modifiers, rule.illegalPropertyModifiersList)
        if (matchesIllegal) {
            addViolation(node, "Property \"${node.name}\" has modifiers matching one of the configured illegalPropertyModifiers: \"${rule.illegalPropertyModifiersString}\"")
        }

        if (rule.allowedPropertyModifiersList) {
            boolean matchesAllowed = matchesAnyModifiers(node.modifiers, rule.allowedPropertyModifiersList)
            if (!matchesAllowed) {
                addViolation(node, "Property \"${node.name}\" does not have modifiers matching one of the configured allowedPropertyModifiers: \"${rule.allowedPropertyModifiersString}\"")
            }
        }
        super.visitProperty(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        boolean matchesIllegal = matchesAnyModifiers(node.modifiers, rule.illegalMethodModifiersList)
        if (matchesIllegal && !matchesIgnoreMethodNames(node) && !matchesIgnoreMethodsWithAnnotationNames(node)) {
            addViolation(node, "Method \"${node.name}\" has modifiers matching one of the configured illegalMethodModifiers: \"${rule.illegalMethodModifiersString}\"")
        }

        if (rule.allowedMethodModifiersList) {
            boolean matchesAllowed = matchesAnyModifiers(node.modifiers, rule.allowedMethodModifiersList)
            if (!matchesAllowed && !matchesIgnoreMethodNames(node) && !matchesIgnoreMethodsWithAnnotationNames(node)) {
                addViolation(node, "Method \"${node.name}\" does not have modifiers matching one of the configured allowedMethodModifiers: \"${rule.allowedMethodModifiersString}\"")
            }
        }
    }

    private boolean matchesIgnoreMethodNames(MethodNode methodNode) {
        return new WildcardPattern(rule.ignoreMethodNames, false).matches(methodNode.name)
    }

    private boolean matchesIgnoreMethodsWithAnnotationNames(MethodNode methodNode) {
        def wildcardPattern = new WildcardPattern(rule.ignoreMethodsWithAnnotationNames, false)
        List<AnnotationNode> annotations = methodNode.getAnnotations()
        for (AnnotationNode annotation : annotations) {
            def annotationName = annotation.getClassNode().getName()
            if (wildcardPattern.matches(annotationName)) {
                return true
            }
        }
        return false
    }
}
