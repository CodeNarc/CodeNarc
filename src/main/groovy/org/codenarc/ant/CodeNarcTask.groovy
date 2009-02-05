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
package org.codenarc.ant

import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Task
import org.apache.tools.ant.types.FileSet
import org.codenarc.AnalysisContext
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.report.HtmlReportWriter
import org.codenarc.ruleset.CompositeRuleSet
import org.codenarc.ruleset.RuleSet
import org.codenarc.ruleset.XmlFileRuleSet
import org.apache.log4j.Logger

/**
 * Ant Task for running CodeNarc.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class CodeNarcTask extends Task {
    static final LOG = Logger.getLogger(CodeNarcTask)

    /**
     * The path to the XML RuleSet definition files, relative to the classpath. This can be a
     * single file path, or multiple paths separated by commas.
     */
    String ruleSetFiles

    protected List reportWriters = []
    protected FileSet fileSet
    protected ruleSet

    /**
     * Execute this Ant Task
     */
    void execute() throws BuildException {
        assert ruleSetFiles
        assert fileSet

        def sourceAnalyzer = createSourceAnalyzer()
        ruleSet = createRuleSet()
        def results = sourceAnalyzer.analyze(ruleSet)
        LOG.debug("results=$results")
        def analysisContext = new AnalysisContext(ruleSet:ruleSet)
        reportWriters.each { reportWriter -> reportWriter.writeOutReport(analysisContext, results) }
    }

    void addFileset(FileSet fileSet) {
        assert fileSet
        if (this.fileSet) {
            throw new BuildException('The FileSet for this Task has already been set. Only a single FileSet is allowed.')
        }
        this.fileSet = fileSet
    }

    /**
     * Ant-defined method (by convention), called with each instance of a nested <report>
     * element within this task.
     */
    void addConfiguredReport(Report report) {
        if (report.type != 'html') {
            throw new BuildException("Invalid type: [$report.type]")
        }
        reportWriters << new HtmlReportWriter(outputFile:report.toFile, title:report.title)
    }

    /**
     * Create and return the SourceAnalyzer
     * @return a configured SourceAnalyzer instance
     */
    protected SourceAnalyzer createSourceAnalyzer() {
        return new AntFileSetSourceAnalyzer(getProject(), fileSet)
    }

    /**
     * Create and return the RuleSet that provides the source of Rules to be applied.
     * The returned RuleSet may aggregate multiple underlying RuleSets.
     * @return a single RuleSet
     */
    protected RuleSet createRuleSet() {
        def paths = ruleSetFiles.tokenize(',')
        def ruleSet = new CompositeRuleSet()
        paths.each { path -> ruleSet.add(new XmlFileRuleSet(path)) }
        return ruleSet
    }

}