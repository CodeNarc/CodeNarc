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
 * Contains SourceCode-related utility methods.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SourceCodeUtil {

    /**
     * Return true if all of the criteria specified in the provided Map apply to thw SourceCode.
     * @param sourceCode - the SourceCode
     * @param criteria - the Map containing the criteria for the source code (files).
     *    The supported criteria keys include:
     *    <ul>
     *      <li>applyToFilesMatching - only apply to source code (file) pathnames matching this regular expression.
     *          May be null, in which case all SourceCode instances match.</li>
     *      <li>doNotApplyToFilesMatching - only apply to source code (file) pathnames that do NOT match this
     *          regular expression. May be null, in which case all SourceCode instances match.</li>
     *      <li>applyToFilenames - only apply to source code (file) names matching this name.
     *          This value may optionally be a comma-separated list of names.
     *          The name(s) may optionally include wildcard characters ('*' or '?').
     *          May be null, in which case all SourceCode instances match.</li>
     *      <li>doNotApplyToFilenames - only apply to source code (file) names that do NOT match this name.
     *          This value may optionally be a comma-separated list of names.
     *          The name(s) may optionally include wildcard characters ('*' or '?').
     *          May be null, in which case all SourceCode instances match.</li>
     *    </ul>
     * @return true only if all of the criteria match to the SourceCode
     */
    public static boolean shouldApplyTo(SourceCode sourceCode, Map criteria) {
        boolean apply = (criteria.applyToFilesMatching) ? sourceCode.path ==~ criteria.applyToFilesMatching : true

        if (apply && criteria.doNotApplyToFilesMatching) {
            apply = !(sourceCode.path ==~ criteria.doNotApplyToFilesMatching)
        }

        if (apply && criteria.applyToFilenames) {
            def names = criteria.applyToFilenames.tokenize(',')
            apply = names.find { namePattern -> new WildcardPattern(namePattern).matches(sourceCode.name) }
        }

        if (apply && criteria.doNotApplyToFilenames) {
            def names = criteria.doNotApplyToFilenames.tokenize(',')
            apply = !(names.find { namePattern -> new WildcardPattern(namePattern).matches(sourceCode.name) } )
        }

        return apply
    }

    /**
     * Private constructor. All members are static.
     */
    private SourceCodeUtil() { }
}