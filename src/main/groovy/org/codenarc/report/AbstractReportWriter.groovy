/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.report

import groovy.text.SimpleTemplateEngine
import groovy.text.TemplateEngine
import org.codenarc.AnalysisContext
import org.codenarc.results.Results
import org.codenarc.rule.Rule
import org.codenarc.util.AstUtil
import org.codenarc.util.CodeNarcVersion
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Abstract superclass for ReportWriter implementation classes.
 * <p/>
 * Subclasses must implement the <code>writeReport(ResultsNode, MetricSet, Writer)</code> method
 * and define a <code>defaultOutputFile</code> property.`
 *
 * @author Chris Mair
 */
abstract class AbstractReportWriter implements ReportWriter {

    protected static final String BASE_MESSAGES_BUNDLE = 'codenarc-base-messages'
    protected static final String CUSTOM_MESSAGES_BUNDLE = 'codenarc-messages'
    protected static final String CODENARC_URL = 'https://www.codenarc.org'

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReportWriter)

    String outputFile
    Object writeToStandardOut

    protected Closure getTimestamp = { new Date() }
    protected String customMessagesBundleName = CUSTOM_MESSAGES_BUNDLE
    protected ResourceBundle resourceBundle
    private final TemplateEngine templateEngine = new SimpleTemplateEngine()

    // Allow tests to override this
    protected Closure initializeResourceBundle = { initializeDefaultResourceBundle() }

    abstract void writeReport(Writer writer, AnalysisContext analysisContext, Results results)

    /**
     * Write out a report for the specified analysis results
     * @param analysisContext - the AnalysisContext containing the analysis configuration information
     * @param results - the analysis results
     */
    @Override
    void writeReport(AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results

        if (isWriteToStandardOut()) {
            writeReportToStandardOut(analysisContext, results)
        }
        else {
            writeReportToFile(analysisContext, results)
        }
    }

    boolean isWriteToStandardOut() {
        writeToStandardOut == true || writeToStandardOut == 'true'
    }

    private void writeReportToStandardOut(AnalysisContext analysisContext, Results results) {
        def writer = new OutputStreamWriter(System.out)
        writeReport(writer, analysisContext, results)
    }

    private void writeReportToFile(AnalysisContext analysisContext, Results results) {
        def outputFilename = outputFile ?: getProperty('defaultOutputFile')
        def outputFile = new File(outputFilename)
        outputFile.getParentFile()?.mkdirs()
        outputFile.withWriter { writer ->
            writeReport(writer, analysisContext, results)
        }
        LOG.info("Report file [$outputFilename] created.")
    }

    protected void initializeDefaultResourceBundle() {
        def baseBundle = ResourceBundle.getBundle(BASE_MESSAGES_BUNDLE)
        resourceBundle = baseBundle
        try {
            resourceBundle = ResourceBundle.getBundle(customMessagesBundleName)
            LOG.info("Using custom message bundle [$customMessagesBundleName]")
            resourceBundle.parent = baseBundle
        }
        catch (MissingResourceException) {
            LOG.debug("No custom message bundle found for [$customMessagesBundleName]. Using default messages.")
        }
    }

    protected String getHtmlDescriptionForRule(Rule rule) {
        def rawMessageText = getDescriptionProperty(rule) ?: getHtmlRuleDescription(rule) ?: getRuleDescriptionOrDefaultMessage(rule)
        return substituteMessageParametersIfPresent(rule, rawMessageText)
    }

    protected String getDescriptionForRule(Rule rule) {
        def rawMessageText = getDescriptionProperty(rule) ?: getRuleDescriptionOrDefaultMessage(rule)
        return substituteMessageParametersIfPresent(rule, rawMessageText)
    }

    private String substituteMessageParametersIfPresent(Rule rule, String rawMessageText) {
        if (rawMessageText.contains('${')) {
            def template = templateEngine.createTemplate(rawMessageText)
            def binding = [rule:rule]
            return template.make(binding)
    }
        rawMessageText
}

    private String getHtmlRuleDescription(Rule rule) {
        def resourceKey = rule.name + '.description.html'
        getResourceBundleString(resourceKey, null, false)
    }

    private String getRuleDescriptionOrDefaultMessage(Rule rule) {
        def resourceKey = rule.name + '.description'
        getResourceBundleString(resourceKey, "No description provided for rule named [$rule.name]")
    }

    private String getDescriptionProperty(Rule rule) {
        AstUtil.respondsTo(rule, 'getDescription') ? rule.description : null
    }

    protected String getResourceBundleString(String resourceKey, String defaultString='?', boolean logWarning=true) {
        def string = defaultString
        try {
            string = resourceBundle.getString(resourceKey)
        } catch (MissingResourceException e) {
            if (logWarning) {
                LOG.warn("No string found for resourceKey=[$resourceKey]")
            }
        }
        string
    }

    protected String getFormattedTimestamp() {
        def dateFormat = java.text.DateFormat.getDateTimeInstance()
        dateFormat.format(getTimestamp())
    }

    protected List getSortedRules(AnalysisContext analysisContext) {
        def rules = analysisContext.ruleSet.rules.findAll { rule -> isEnabled(rule) }
        rules.toList().sort { rule -> rule.name }
    }

    protected boolean isEnabled(Rule rule) {
        (!AstUtil.respondsTo(rule, 'isEnabled') || rule.enabled)
    }

    protected String getCodeNarcVersion() {
        return CodeNarcVersion.getVersion()
    }

}
