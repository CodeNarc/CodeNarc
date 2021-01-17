/*
 * Copyright 2012 the original author or authors.
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

import org.codenarc.analyzer.FilesystemSourceAnalyzer
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.report.JsonReportWriter
import org.codenarc.report.HtmlReportWriter
import org.codenarc.report.ReportWriterFactory
import org.codenarc.results.Results
import org.codenarc.util.CodeNarcVersion

/**
 * Command-line runner for CodeNarc.
 * <p/>
 * The supported command-line parameters are all of the form: "-OPTION=VALUE", where OPTION is one
 * of the options in the following list.
 * <ul>
 *   <li>help - Display the command-line help; If present, this must be the only command-line parameter.</li>
 *   <li>basedir - The base directory for the source code to be analyzed. This is the root of the
 *          directory tree. Defaults to the current directory ('.').</li>
 *   <li>rulesetfiles - The path to the Groovy or XML RuleSet definition files, relative to the classpath. This can be a
 *          single file path, or multiple paths separated by commas. Each path may be optionally prefixed by
 *          any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute filesystem path),
 *          or "http:". If it is a URL, its path may be optionally URL-encoded. That can be useful if the path contains
 *          any problematic characters, such as comma (',') or hash ('#'). See URLEncoder#encode(java.lang.String, java.lang.String).
 *          Defaults to "rulesets/basic.xml".</li>
 *   <li>ruleset - JSON string (URL-encoded in UTF-8) containing a ruleSet in JSON format (if set, rulesetfiles will be ignored).
 *   <li>includes - The comma-separated list of Ant file patterns specifying files that must be included;
 *          all files are included when omitted.</li>
 *   <li>excludes - The comma-separated list of Ant file patterns specifying files that must be excluded;
 *          no files are excluded when omitted.</li>
 *   <li>maxPriority1Violations - The maximum number of priority 1 violations allowed. Optional.</li>
 *   <li>maxPriority2Violations - The maximum number of priority 2 violations allowed. Optional.</li>
 *   <li>maxPriority3Violations - The maximum number of priority 3 violations allowed. Optional.</li>
 *   <li>title - The title description for this analysis; used in the output report(s), if supported. Optional.</li>
 *   <li>report - The definition of the report to produce. The option value is of the form TYPE[:FILENAME|:stdout].
 *          where TYPE is 'html' and FILENAME is the filename (with optional path) of the output report filename.
 *          If the TYPE is followed by :stdout (e.g. "html:stdout", "json:stdout"), then the report is written to standard out.
 *          If the report filename is omitted, the default filename is used ("CodeNarcReport.html").
 *          If no report option is specified, defaults to a single 'html' report with the default filename.
 *          </li>
 *   <li>plugins - The optional list of CodeNarcPlugin class names to register, separated by commas.</li>
 * </ul>
 *
 * @author Chris Mair
 * @author Nicolas Vuillamy
 */
@SuppressWarnings(['Println', 'PrintStackTrace'])
class CodeNarc {

    protected static final String HELP = """CodeNarc - static analysis for Groovy,
Usage: java org.codenarc.CodeNarc [OPTIONS]
  where OPTIONS are zero or more command-line options of the form "-NAME[=VALUE]":
    -basedir=<DIR>
        The base (root) directory for the source code to be analyzed.
        Defaults to the current directory (".").
    -includes=<PATTERNS>
        The comma-separated list of Ant-style file patterns specifying files that must
        be included. Defaults to "**/*.groovy".
    -excludes=<PATTERNS>
        The comma-separated list of Ant-style file patterns specifying files that must
        be excluded. No files are excluded when omitted.
    -rulesetfiles=<FILENAMES>
        The path to the Groovy or XML RuleSet definition files, relative to the classpath.
        This can be a single file path, or multiple paths separated by commas. Each path may be optionally prefixed by
        any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute filesystem path),
        or "http:". If it is a URL, its path may be optionally URL-encoded. That can be useful if the path contains
        any problematic characters, such as comma (',') or hash ('#'). For instance:
            file:src/test/resources/RuleSet-,#.txt
        can be encoded as:
            file:src%2Ftest%2Fresources%2FRuleSet-%2C%23.txt
        See URLEncoder#encode(java.lang.String, java.lang.String). Defaults to "rulesets/basic.xml"
    -ruleset=JSON_STRING
        String containing a ruleSet in JSON format (if set, rulesetfiles argument will be ignored)
        The JSON string must be URL-encoded in UTF-8 before being sent as argument to CodeNarc
    -maxPriority1Violations=<MAX>
        The maximum number of priority 1 violations allowed (int).
    -maxPriority2Violations=<MAX>
        The maximum number of priority 2 violations allowed (int).
    -maxPriority3Violations=<MAX>
        The maximum number of priority 3 violations allowed (int).
    -title=<REPORT TITLE>
        The title for this analysis; used in the output report(s), if supported by the report type. Optional.
    -report=<REPORT-TYPE[:FILENAME|:stdout]>
        The definition of the report to produce. The option value is of the form
        TYPE[:FILENAME], where TYPE is "html", "text", "xml", or "console" and FILENAME is the filename (with
        optional path) of the output report filename. If the TYPE is followed by :stdout (e.g. "html:stdout", "json:stdout"),
        then the report is written to standard out. If the report filename is  omitted, the default filename
        is used for the specified report type ("CodeNarcReport.html" for "html", "CodeNarcXmlReport.xml" for
        "xml" and "CodeNarcJsonReport.json" for "json"). If no report option is specified, default to a
        single "html" report with the default filename.
    -plugins=<PLUGIN CLASS NAMES>
        The optional list of CodeNarcPlugin class names to register, separated by commas.
    -help
        Display the command-line help. If present, this must be the only command-line parameter.
  Example command-line invocations:
    java org.codenarc.CodeNarc
    java org.codenarc.CodeNarc -rulesetfiles="rulesets/basic.xml" title="My Project"
    java org.codenarc.CodeNarc -report=xml:MyXmlReport.xml -report=html
    java org.codenarc.CodeNarc -report=json:stdout
    java org.codenarc.CodeNarc -help'"""

    // Abstract calling System.exit() to allow substitution of test spy for unit tests
    protected static Closure systemExit = { exitCode -> System.exit(exitCode) }

    protected String ruleSetFiles
    protected String ruleset
    protected String baseDir
    protected String includes
    protected String excludes
    protected String title
    protected String plugins
    protected String propertiesFilename
    protected List reports = []

    // Abstract creation of the CodeNarcRunner instance to allow substitution of test spy for unit tests
    protected Closure createCodeNarcRunner = { new CodeNarcRunner() }

    protected int maxPriority1Violations = Integer.MAX_VALUE
    protected int maxPriority2Violations = Integer.MAX_VALUE
    protected int maxPriority3Violations = Integer.MAX_VALUE

    /**
     * Main command-line entry-point. Run the CodeNarc application.
     * @param args - the String[] of command-line arguments
     */
    static void main(String[] args) {
        def codeNarc = new CodeNarc()

        // Show help
        if (args == ['-help'] as String[]) {
            println HELP
            return
        }
        // Show version
        else if (args == ['-version']) {
            def version = CodeNarcVersion.getVersion()
            println "CodeNarc version $version"
            return
        }

        try {
            codeNarc.execute(args)
        }
        catch (Throwable t) {
            println "ERROR: $t"
            t.printStackTrace()
            println HELP
            systemExit(1)
        }
    }

    protected void execute(String[] args) {
        parseArgs(args)
        setDefaultsIfNecessary()
        def sourceAnalyzer = createSourceAnalyzer()
        reports.each { reportWriter ->
            if (reportWriter.hasProperty('title')) {
                reportWriter.title = title
            }
        }

        def codeNarcRunner = createCodeNarcRunner()
        codeNarcRunner.ruleSetFiles = ruleSetFiles
        codeNarcRunner.ruleSetString = ruleset
        codeNarcRunner.reportWriters = reports
        codeNarcRunner.sourceAnalyzer = sourceAnalyzer
        codeNarcRunner.propertiesFilename = propertiesFilename

        if (plugins) {
            codeNarcRunner.registerPluginsForClassNames(plugins)
        }

        Results results = codeNarcRunner.execute()
        checkMaxViolations(results, 1, maxPriority1Violations)
        checkMaxViolations(results, 2, maxPriority2Violations)
        checkMaxViolations(results, 3, maxPriority3Violations)
    }

    protected void setDefaultsIfNecessary() {
        baseDir = baseDir ?: '.'
        includes = includes  ?: '**/*.groovy'
        ruleSetFiles = ruleSetFiles ?: 'rulesets/basic.xml'

        if (reports.empty) {
            reports << new HtmlReportWriter(title:title)
        }
    }

    protected void checkMaxViolations(Results results, int priority, int max) {
        def numViolations = results.getNumberOfViolationsWithPriority(priority, true)
        if (numViolations > max) {
            println "ERROR: Number of p${priority} violations greater than maximum of $max"
            systemExit(1)
        }
    }

    /**
     * Create and return the SourceAnalyzer
     * @return a configured SourceAnalyzer instance
     */
    protected SourceAnalyzer createSourceAnalyzer() {
        new FilesystemSourceAnalyzer(baseDirectory: baseDir, includes: includes, excludes: excludes)
    }

    protected void parseArgs(String[] args) {
        args.each { arg ->
            final PATTERN = /\-(.*)\=(.*)/      // -name=value
            def matcher = arg =~ PATTERN
            assert matcher, "Invalid argument format: [$arg]"
            def name = matcher[0][1]
            def value = matcher[0][2]
            switch (name) {
                case 'rulesetfiles': ruleSetFiles = value; break
                case 'ruleset': ruleset = URLDecoder.decode(value, 'UTF-8') ; break
                case 'basedir': baseDir = value; break
                case 'includes': includes = value; break
                case 'excludes': excludes = value; break
                case 'title': title = value; break
                case 'report': parseReport(value); break
                case 'maxPriority1Violations': maxPriority1Violations = value as int; break
                case 'maxPriority2Violations': maxPriority2Violations = value as int; break
                case 'maxPriority3Violations': maxPriority3Violations = value as int; break
                case 'plugins': plugins = value; break
                case 'properties': propertiesFilename = value; break
                default: throw new IllegalArgumentException("Invalid option: [$arg]")
            }
        }
    }

    private void parseReport(String argValue) {
        def parts = argValue.split(':', 2)
        def type = parts[0]
        def reportWriter = new ReportWriterFactory().getReportWriter(type)

        if (parts.size() > 1 && parts[1]) {
            // Output in stdout (default)
            if (parts[1] == 'stdout') {
                reportWriter.writeToStandardOut = true
                // JSON called via command line must be returned as single line for easier parsing
                if (reportWriter instanceof JsonReportWriter) {
                    reportWriter.writeAsSingleLine = true
                }
            }
            else {
                // Output file
                reportWriter.outputFile = parts[1]
            }
        }
        reports << reportWriter
    }

}
