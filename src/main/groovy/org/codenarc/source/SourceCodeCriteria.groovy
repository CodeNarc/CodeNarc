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
package org.codenarc.source

import org.codenarc.util.WildcardPattern

/**
 * Represents the set of criteria used to filter source code (files). Provides an API
 * to determine whether a particular source code file matches the criteria.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SourceCodeCriteria {

    /**
     * Apply only to source code (file) pathnames matching this regular expression.
     * If null, then all SourceCode instances match this part of the criteria (i.e., this property is ignored).
     */
    String applyToFilesMatching

    /**
     * Do NOT apply to source code (file) pathnames matching this regular expression.
     * If null, then all SourceCode instances match this part of the criteria (i.e., this property is ignored).
     */
    String doNotApplyToFilesMatching

    /**
     * Only apply to source code (file) names matching this value.
     * The value may optionally be a comma-separated list of names, in which case one of the names must match.
     * The name(s) may optionally include wildcard characters ('*' or '?').
     * If null, then all SourceCode instances match this part of the criteria (i.e., this property is ignored).
     */
    String applyToFileNames

    /**
     * Do NOT apply to source code (file) names matching this value.
     * The value may optionally be a comma-separated list of names, in which case any one of the names can match.
     * The name(s) may optionally include wildcard characters ('*' or '?').
     * If null, then all SourceCode instances match this part of the criteria (i.e., this property is ignored).
     */
    String doNotApplyToFileNames


    /**
     * Return true if all of the criteria specified in this object apply to thw SourceCode.
     * @param sourceCode - the SourceCode
     * @return true only if all of the (specified, i.e. non-null) criteria match the SourceCode
     */
    boolean matches(SourceCode sourceCode) {
        boolean apply = (applyToFilesMatching) ? sourceCode.path ==~ applyToFilesMatching : true

        if (apply && doNotApplyToFilesMatching) {
            apply = !(sourceCode.path ==~ doNotApplyToFilesMatching)
        }

        if (apply && applyToFileNames) {
            apply = new WildcardPattern(applyToFileNames).matches(sourceCode.name)
        }

        if (apply && doNotApplyToFileNames) {
            apply = !new WildcardPattern(doNotApplyToFileNames).matches(sourceCode.name) 
        }

        return apply
    }

}