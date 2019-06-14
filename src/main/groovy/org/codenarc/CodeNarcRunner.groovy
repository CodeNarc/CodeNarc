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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.results.Results
import org.codenarc.ruleregistry.RuleRegistryInitializer
import org.codenarc.ruleset.CompositeRuleSet
import org.codenarc.ruleset.PropertiesFileRuleSetConfigurer
import org.codenarc.ruleset.RuleSet
import org.codenarc.ruleset.RuleSetUtil

/**
 * Helper class to run CodeNarc.
 * <p/>
 * The following properties must be configured before invoking the <code>execute()</code> method:
 * <ul>
 *   <li><code>rulesetfiles</code> - The path to the Groovy or XML RuleSet definition files, relative to the classpath. This can be a
 *          single file path, or multiple paths separated by commas.</li>
 *   <li><code>sourceAnalyzer</code> - An instance of a <code>org.codenarc.analyzer.SourceAnalyzer</code> implementation.</li>
 *   <li><code>reportWriters</code> - The list of <code>ReportWriter</code> instances. A report is generated
 *          for each element in this list. At least one <code>ReportWriter</code> must be configured.</li>
 * </ul>
 *
 * NOTE: This is an internal class. Its API is subject to change.
 *
 * @author Chris Mair
 */
class CodeNarcRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CodeNarcRunner)

    String ruleSetFiles
    SourceAnalyzer sourceAnalyzer
    ResultsProcessor resultsProcessor = new NullResultsProcessor()
    List reportWriters = []

    /**
     * The main entry point for this class. Runs CodeNarc and returns the results. Processing steps include:
     * <ol>
     *   <li>Parse the <code>ruleSetFiles</code> property to create a RuleSet. Each path may be optionally prefixed by
     *     any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute filesystem path),
     *     or "http:". If it is a URL, its path may be optionally URL-encoded. That can be useful if the path contains
     *     any problematic characters, such as comma (',') or hash ('#'). See {@link URLEncoder#encode(java.lang.String, java.lang.String)}.
     *   </li>
     *   <li>Configure the RuleSet from the "codenarc.properties" file, if that file is found on the classpath.</li>
     *   <li>Apply the configured <code>SourceAnalyzer</code>.</li>
     *   <li>Apply the configured <code>ResultsProcessor</code>.</li>
     *   <li>Generate a report for each configured <code>ReportWriter</code>.</li>
     *   <li>Return the <code>Results</code> object representing the analysis results.</li>
     * </ol>
     * @returns the <code>Results</code> object containing the results of the CodeNarc analysis.
     */
    @SuppressWarnings('Println')
    Results execute() {
        assert ruleSetFiles, 'The ruleSetFiles property must be set'
        assert sourceAnalyzer, 'The sourceAnalyzer property must be set to a valid SourceAnalayzer'

        def startTime = System.currentTimeMillis()
        new RuleRegistryInitializer().initializeRuleRegistry()
        def ruleSet = createRuleSet()
        new PropertiesFileRuleSetConfigurer().configure(ruleSet)
        def results = sourceAnalyzer.analyze(ruleSet)
        resultsProcessor.processResults(results)
        def p1 = results.getNumberOfViolationsWithPriority(1, true)
        def p2 = results.getNumberOfViolationsWithPriority(2, true)
        def p3 = results.getNumberOfViolationsWithPriority(3, true)
        def countsText = "(p1=$p1; p2=$p2; p3=$p3)"
        def elapsedTime = System.currentTimeMillis() - startTime
        LOG.debug("results=$results")
        def analysisContext = new AnalysisContext(ruleSet:ruleSet, sourceDirectories:sourceAnalyzer.sourceDirectories)

        reportWriters.each { reportWriter ->
            reportWriter.writeReport(analysisContext, results)
        }

        def resultsMessage = 'CodeNarc completed: ' + countsText + " ${elapsedTime}ms"
        println resultsMessage
        results
    }

    /**
     * Create and return the RuleSet that provides the source of Rules to be applied.
     * The returned RuleSet may aggregate multiple underlying RuleSets.
     * @return a single RuleSet
     */
    protected RuleSet createRuleSet() {
        def paths = ruleSetFiles.tokenize(',')
        def newRuleSet = new CompositeRuleSet()
        paths.each { path ->
            def ruleSet = RuleSetUtil.loadRuleSetFile(path.trim())
            newRuleSet.addRuleSet(ruleSet)
        }
        newRuleSet
    }

}
