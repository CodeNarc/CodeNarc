/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.rule

import org.codenarc.source.SourceCode
import org.codenarc.util.WildcardPattern

/**
 * Abstract superclass for Rules that use a Groovy AST Visitor.
 * <p/>
 * Each subclass must set the <code>astVisitorClass</code> property or else define a new
 * property with the same name, specifying the Class of the <code>AstVisitor</code>
 * to applied to the specified source code.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractAstVisitorRule extends AbstractRule {

    protected static final DEFAULT_CONST_NAME = /[A-Z][A-Z0-9_]*/
    protected static final DEFAULT_VAR_NAME = /[a-z][a-zA-Z0-9]*/
    protected static final DEFAULT_TEST_FILES = /.*Tests?\.groovy/

    /** Each concrete subclass must either set this property or define its own property with the same name */
    Class astVisitorClass

    /**
     * This rule is only applied to classes with names matching this value.
     *
     * The value may optionally be a comma-separated list of names, in which case one of the names must match.
     *
     * If a name includes a period ('.'), then it is assumed to specify a full package name, so the name
     * (pattern) is matched against each fully-qualified class name. Otherwise it is matched only against
     * the class name without a package.
     *
     * The name(s) may optionally include wildcard characters ('*' or '?').
     */
    String applyToClassNames

    /**
     * This rule is NOT applied to classes with names matching this value.
     *
     * The value may optionally be a comma-separated list of names, in which case any one of the names can match.
     *
     * If a name includes a period ('.'), then it is assumed to specify a full package name, so the name
     * (pattern) is matched against each fully-qualified class name. Otherwise it is matched only against
     * the class name without a package.
     *
     * The name(s) may optionally include wildcard characters ('*' or '?').
     */
    String doNotApplyToClassNames

    AstVisitor getAstVisitor() {
        def visitorClass = getAstVisitorClass()
        assert visitorClass, "The astVisitorClass property must not be null"
        assert AstVisitor.isAssignableFrom(visitorClass), "The astVisitorClass property must specify a class that implements AstVisitor"
        return visitorClass.newInstance()
    }

    void applyTo(SourceCode sourceCode, List violations) {
        // If AST is null, skip this source code
        def ast = sourceCode.ast
        if (ast) {
            ast.classes.each { classNode ->

                if (shouldApplyThisRuleTo(classNode)) {
                    def visitor = getAstVisitor()
                    visitor.rule = this
                    visitor.sourceCode = sourceCode
                    visitor.visitClass(classNode)
                    violations.addAll(visitor.violations)
                }
            }
        }
    }

    /**
     * Return true if this rule should be applied for the specified ClassNode, based on the
     * configuration of this rule.
     * @param classNode - the ClassNode
     * @return true if this rule should be applied for the specified ClassNode
     */
    protected boolean shouldApplyThisRuleTo(classNode) {
        boolean apply = true

        if (applyToClassNames) {
            def target = applyToClassNames.contains('.') ? classNode.name : classNode.nameWithoutPackage
            apply = new WildcardPattern(applyToClassNames).matches(target)
        }

        if (apply && doNotApplyToClassNames) {
            def target = doNotApplyToClassNames.contains('.') ? classNode.name : classNode.nameWithoutPackage
            apply = !new WildcardPattern(doNotApplyToClassNames).matches(target)
        }

        return apply
    }

}