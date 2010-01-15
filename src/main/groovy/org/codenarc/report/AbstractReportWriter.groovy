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

import org.codenarc.AnalysisContext
import org.codenarc.results.Results
import org.apache.log4j.Logger
import org.codenarc.util.io.ClassPathResource
import org.codenarc.rule.Rule
import org.codenarc.util.AstUtil

/**
 * Abstract superclass for ReportWriter implementation classes.
 * <p/>
 * Subclasses must implement the <code>writeReport(ResultsNode, MetricSet, Writer)</code> method
 * and define a <code>defaultOutputFile</code> property.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractReportWriter implements ReportWriter {

    protected static final BASE_MESSSAGES_BUNDLE = "codenarc-base-messages"
    protected static final CUSTOM_MESSSAGES_BUNDLE = "codenarc-messages"
    protected static final VERSION_FILE = 'codenarc-version.txt'
    protected static final CODENARC_URL = "http://www.codenarc.org"

    String outputFile
    protected final LOG = Logger.getLogger(getClass())
    protected getTimestamp = { new Date() }
    protected customMessagesBundleName = CUSTOM_MESSSAGES_BUNDLE
    protected resourceBundle

    // Allow tests to override this
    protected initializeResourceBundle = { initializeDefaultResourceBundle() }

    abstract void writeReport(Writer writer, AnalysisContext analysisContext, Results results)


    /**
     * Write out a report for the specified analysis results
     * @param analysisContext - the AnalysisContext containing the analysis configuration information
     * @param results - the analysis results
     */
    void writeReport(AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results
        def outputFilename = outputFile ?: getProperty('defaultOutputFile')
        def file = new File(outputFilename)
        file.withWriter { writer ->
            writeReport(writer, analysisContext, results)
        }
        LOG.info("Report file [$outputFilename] created.")
    }

    protected void initializeDefaultResourceBundle() {
        def baseBundle = ResourceBundle.getBundle(BASE_MESSSAGES_BUNDLE)
        resourceBundle = baseBundle
        try {
            resourceBundle = ResourceBundle.getBundle(customMessagesBundleName)
            LOG.info("Using custom message bundle [$customMessagesBundleName]")
            resourceBundle.setParent(baseBundle)
        }
        catch(MissingResourceException) {
            LOG.info("No custom message bundle found for [$customMessagesBundleName]. Using default messages.")
        }
    }

    protected String getDescriptionForRule(Rule rule) {
        if (AstUtil.respondsTo(rule, 'getDescription') && rule.description != null) {
            return rule.description
        }

        def resourceKey = rule.name + '.description'
        return getResourceBundleString(resourceKey, "No description provided for rule named [$rule.name]")
    }

    protected String getResourceBundleString(String resourceKey, String defaultString='?') {
        def string = defaultString
        try {
            string = resourceBundle.getString(resourceKey)
        } catch (MissingResourceException e) {
            LOG.warn("No string found for resourceKey=[$resourceKey]")
        }
        return string
    }

    protected String getFormattedTimestamp() {
        def dateFormat = java.text.DateFormat.getDateTimeInstance()
        return dateFormat.format(getTimestamp())
    }

    protected String getCodeNarcVersion() {
        return ClassPathResource.getInputStream(VERSION_FILE).text
    }
}