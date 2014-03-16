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
package org.codenarc.analyzer

import org.codenarc.results.Results
import org.codenarc.ruleset.RuleSet

/**
 * The interface for objects that can analyze the source files within one or more directory
 * trees using a specified RuleSet and produce report results.
 *
 * @author Chris Mair
 */
interface SourceAnalyzer {

    /**
     * Analyze all source code using the specified RuleSet and return the report results.
     * @param ruleset - the RuleSet to apply to each source component; must not be null.
     * @return the results from applying the RuleSet to all of the source
     */
    Results analyze(RuleSet ruleSet)

    /**
     * Return the List of source directories to be analyzed. May be empty; may not be null.
     */
    List getSourceDirectories()
}
