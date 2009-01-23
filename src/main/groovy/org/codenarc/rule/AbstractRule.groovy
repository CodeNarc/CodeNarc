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

import org.codehaus.groovy.ast.ImportNode
import org.codenarc.source.SourceCode
import org.codenarc.source.SourceCodeUtil

/**
 * Abstract superclass for Rules.
 * <p/>
 * Each subclass must define an <code>id</code> property (String) and a <code>priority</code> property
 * (integer 1..3).
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractRule implements Rule {

    /**
     * Flag indicating whether this rule should be enabled (applied). Defaults to true.
     * If set to false, this rule will not produce any violations.
     */
    boolean enabled = true

    /**
     * This rule is only applied to source code (file) pathnames matching this regular expression.
     */
    String applyToFilesMatching

    /**
     * This rule is NOT applied to source code (file) pathnames matching this regular expression.
     */
    String doNotApplyToFilesMatching

    /**
     * @return the unique id for this rule
     */
    abstract String getId()

    /**
     * @return the priority of this rule, between 1 (highest priority) and 3 (lowest priority), inclusive.
     */
    abstract int getPriority()

    /**
     * Apply this rule to the specified source and return a list of violations (or an empty List)
     * @param source - the source to apply this rule to
     * @param violations - the List of violations to which new violations from this rule are to be added
     */
    abstract void applyTo(SourceCode sourceCode, List violations)

    /**
     * Apply this rule to the specified source and return a list of violations (or an empty List).
     * This implementation delegates to the abstract applyCode(SourceCode,List), provided by
     * concrete subclasses. This simplifies subclass implementations and also enables common
     * handling of enablement logic.
     * @param source - the source to apply this rule to
     * @return the List of violations; may be empty
     */
    List applyTo(SourceCode sourceCode) {
        def violations = []
        if (shouldApplyThisRuleTo(sourceCode)) {
            applyTo(sourceCode, violations)
        }
        return violations
    }

    String toString() {
        "${getClassNameNoPackage()}[id=${getId()}, priority=${getPriority()}]"
    }

    /**
     * Create and return a new Violation for this rule and the specified import
     * @param importNode - the ImportNode for the import triggering the violation
     * @return a new Violation object
     */
    protected Violation createViolationForImport(ImportNode importNode) {
        return new Violation(rule:this, sourceLine:importNode.text)
    }

    /**
     * Return the package name for the specified import statement
     * @param importNode - the ImportNode for the import
     * @return the name package being imported (i.e., the import minus the class name/spec)
     */
    protected String packageNameForImport(ImportNode importNode) {
        def importClassName = importNode.className
        def index = importClassName.lastIndexOf('.')
        return importClassName.substring(0, index)
    }

    private boolean shouldApplyThisRuleTo(SourceCode sourceCode) {
        return enabled && SourceCodeUtil.shouldApplyTo(sourceCode, applyToFilesMatching, doNotApplyToFilesMatching) 
    }

    private String getClassNameNoPackage() {
        def className = getClass().name
        def indexOfLastPeriod = className.lastIndexOf('.')
        return (indexOfLastPeriod == -1) ? className : className.substring(indexOfLastPeriod+1)
    }

}