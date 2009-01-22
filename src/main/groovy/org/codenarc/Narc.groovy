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
package org.codenarc

import org.codenarc.analyzer.DirectorySourceAnalyzer
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.report.ReportWriter
import org.codenarc.ruleset.RuleSet
import org.apache.log4j.Logger

/**
 * The main driver for running a code analysis.
 * <p>
 * Either or both of the <code>baseDirectory</code> or <code>sourceDirectories</code>
 * properties must be set (not null or empty). The <code>ruleSet</code> and
 * <code>reportWriter</code> properties must also be set.
 *
 * @author Chris Mair
 * @version $Revision: 196 $ - $Date: 2009-01-15 19:47:56 -0500 (Thu, 15 Jan 2009) $
 */
class Narc {
    static final LOG = Logger.getLogger(Narc)

    /**
     * This is the base directory; the sourceDirectories are relative to this. If this is
     * null or empty, then <code>sourceDirectories</code> must be specified.
     */
    String baseDirectory

    /**
     * The list of sourcedirectories to be analyzed recursively. These paths are relative to
     * the <code>baseDirectory</code> if it is not null. If this value is null or empty, then
     * <code>baseDirectory</code> must be specified.
     */
    List sourceDirectories
    
    RuleSet ruleSet

    /**
     * The ReportWriter used to produce the output report from the analysis results
     */
    ReportWriter reportWriter

    /**
     * Perform the source code analysis and write out the report of the results.
     */
    void run() {
        assert reportWriter
        
        def sourceAnalyzer = createSourceAnalyzer()
        def results = sourceAnalyzer.analyze(ruleSet)
        LOG.info "results=$results"
        def analysisContext = new AnalysisContext(sourceDirectories:sourceDirectories)
        reportWriter.writeOutReport(analysisContext, results)
    }

    /**
     * Create and return the SourceAnalyzer
     * @return a configured SourceAnalyzer instance
     */
    protected SourceAnalyzer createSourceAnalyzer() {
        return new DirectorySourceAnalyzer(baseDirectory:baseDirectory, sourceDirectories:sourceDirectories)
    }
}