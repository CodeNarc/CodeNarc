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
package org.codenarc

import org.codenarc.ruleset.RuleSet

/**
 * Holds information related to the configuration and context for the source code analysis.
 *
 * @author Chris Mair
 */
class AnalysisContext {

    /**
     * The List of source directories being analyzed. May be null or empty.
     */
    List sourceDirectories

    /**
     * The RuleSet containing the rules being applied.
     */
    RuleSet ruleSet

}
