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

import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.plugin.CodeNarcPlugin
import org.codenarc.results.Results
import org.codenarc.rule.Rule
import org.codenarc.ruleregistry.RuleRegistryInitializer
import org.codenarc.ruleset.CompositeRuleSet
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.ruleset.PropertiesFileRuleSetConfigurer
import org.codenarc.ruleset.RuleSet
import org.codenarc.ruleset.RuleSetUtil

/**
 * Helper class to run CodeNarc.
 * <p/>
 * The following properties must be configured before invoking the <code>execute()</code> method:
 * <ul>
 *   <li><code>ruleSetFiles</code> - The path to the Groovy or XML RuleSet definition files, relative to the classpath. This can be a
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

    String ruleSetFiles
    SourceAnalyzer sourceAnalyzer
    ResultsProcessor resultsProcessor = new NullResultsProcessor()
    List reportWriters = []
    private final List<CodeNarcPlugin> plugins = []

    /**
     * The main entry point for this class. Runs CodeNarc and returns the results. Processing steps include:
     * <ol>
     *   <li>Call initialize() for each registered CodeNarcPlugin</li>
     *   <li>Parse the <code>ruleSetFiles</code> property to create a RuleSet. Each path may be optionally prefixed by
     *     any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute filesystem path),
     *     or "http:". If it is a URL, its path may be optionally URL-encoded. That can be useful if the path contains
     *     any problematic characters, such as comma (',') or hash ('#'). See {@link URLEncoder#encode(java.lang.String, java.lang.String)}.
     *   </li>
     *   <li>Call processRules(List<Rule>) for each registered CodeNarcPlugin</li>
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
        assert sourceAnalyzer, 'The sourceAnalyzer property must be set to a valid SourceAnalyzer'

        def startTime = System.currentTimeMillis()
        initializeRuleRegistry()
        initializePlugins()

        def ruleSet = buildRuleSet()

        def results = sourceAnalyzer.analyze(ruleSet)
        resultsProcessor.processResults(results)
        String countsText = buildCountsText(results)
        def elapsedTime = System.currentTimeMillis() - startTime
        def analysisContext = new AnalysisContext(ruleSet:ruleSet, sourceDirectories:sourceAnalyzer.sourceDirectories)

        writeReports(analysisContext, results)

        def resultsMessage = 'CodeNarc completed: ' + countsText + " ${elapsedTime}ms"
        println resultsMessage
        results
    }

    void registerPlugin(CodeNarcPlugin plugin) {
        assert plugin
        this.plugins << plugin
    }

    List<CodeNarcPlugin> getPlugins() {
        return plugins
    }

    private void initializeRuleRegistry() {
        new RuleRegistryInitializer().initializeRuleRegistry()
    }

    private void initializePlugins() {
        this.plugins.each { plugin -> plugin.initialize() }
    }

    private RuleSet buildRuleSet() {
        RuleSet initialRuleSet = createInitialRuleSet()
        List<Rule> rules = applyPluginsProcessRules(initialRuleSet)
        RuleSet ruleSet = new ListRuleSet(rules)
        new PropertiesFileRuleSetConfigurer().configure(ruleSet)
        return ruleSet
    }

    private List<Rule> applyPluginsProcessRules(RuleSet ruleSet) {
        List<Rule> rules = new ArrayList(ruleSet.getRules())    // need it mutable
        this.plugins.each { plugin -> plugin.processRules(rules) }
        return rules
    }

    /**
     * Create and return the RuleSet that provides the source of Rules to be applied.
     * The returned RuleSet may aggregate multiple underlying RuleSets.
     * @return a single RuleSet
     */
    protected RuleSet createInitialRuleSet() {
        def paths = ruleSetFiles.tokenize(',')
        def newRuleSet = new CompositeRuleSet()
        paths.each { path ->
            def ruleSet = RuleSetUtil.loadRuleSetFile(path.trim())
            newRuleSet.addRuleSet(ruleSet)
        }
        newRuleSet
    }

    private List<Object> writeReports(AnalysisContext analysisContext, Results results) {
        return reportWriters.each { reportWriter ->
            reportWriter.writeReport(analysisContext, results)
        }
    }

    private String buildCountsText(Results results) {
        def p1 = results.getNumberOfViolationsWithPriority(1, true)
        def p2 = results.getNumberOfViolationsWithPriority(2, true)
        def p3 = results.getNumberOfViolationsWithPriority(3, true)
        return "(p1=$p1; p2=$p2; p3=$p3)"
    }

}
