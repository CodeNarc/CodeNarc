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

import org.codenarc.ant.CodeNarcTask
import org.apache.tools.ant.Task
import org.codenarc.ant.Report
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.Project

/**
 * Command-line runner for CodeNarc. This is basically a wrapper around the {@link CodeNarcTask} Ant Task.
 * <p/>
 * The supported command-line parameters are all of the form: "-OPTION=VALUE", where OPTION is one
 * of the options in the following list.
 * <ul>
 *   <li>help - Display the command-line help; If present, this must be the only command-line parameter.</li>
 *   <li>basedir - The base directory for the source code to be analyzed. This is the root of the
 *          directory tree. Defaults to the current directory ('.').</li>
 *   <li>rulesetfiles - The path to the XML RuleSet definition files, relative to the classpath. This can be a
 *          single file path, or multiple paths separated by commas. Defaults to "rulesets/basic.xml".</li>
 *   <li>includes - The comma-separated list of Ant file patterns specifying files that must be included;
 *          all files are included when omitted.</li>
 *   <li>excludes - The comma-separated list of Ant file patterns specifying files that must be excluded;
 *          no files are excluded when omitted.</li>
 *   <li>title - The title description for this analysis; used in the output report(s). Optional.</li>
 *   <li>report - The definition of the report to produce. The option value is of the form TYPE[:FILENAME].
 *          where TYPE is 'html' and FILENAME is the filename (with optional path) of the output report filename.
 *          If the report filename is omitted, the default filename is used ("CodeNarcReport.html").
 *          If no report option is specified, defaults to a single 'html' report with the default filename.
 *          </li>
 * </ul>
 *
 * @see CodeNarcTask
 *
 * @author Chris Mair
 * @version $Revision: 7 $ - $Date: 2009-01-21 21:52:00 -0500 (Wed, 21 Jan 2009) $
 */
class CodeNarc {
    protected static final HELP = [
        'CodeNarc - static analysis for Groovy',
        'Usage: java org.codenarc.CodeNarc [OPTIONS]',
        '  where OPTIONS are zero or more command-line options of the form "-NAME[=VALUE]":',
        '    -basedir=<DIR>',
        '        The base (root) directory for the source code to be analyzed.',
        '        Defaults to the current directory (".").',
        '    -includes=<PATTERNS>',
        '        The comma-separated list of Ant file patterns specifying files that must',
        '        be included. Defaults to "**/*.groovy".',
        '    -excludes=<PATTERNS>',
        '        The comma-separated list of Ant file patterns specifying files that must',
        '        be excluded. No files are excluded when omitted.',
        '    -rulesetfiles=<FILENAMES>',
        '        The path to the XML RuleSet definition files, relative to the classpath.',
        '        This can be a single file path, or multiple paths separated by commas.',
        '        Defaults to "rulesets/basic.xml"',
        '    -title=<REPORT TITLE>',
        '        The title descriptive for this analysis; used in the output report(s). Optional.',
        '    -report=<REPORT-TYPE[:FILENAME]>',
        '        The definition of the report to produce. The option value is of the form',
        '        TYPE[:FILENAME], where TYPE is "html" and FILENAME is the filename (with ',
        '        optional path) of the output report filename. If the report filename is ',
        '        omitted, the default filename is used ("CodeNarcReport.html"). If no',
        '        report option is specified, default to a single "html" report with the',
        '        default filename.',
        '    -help',
        '        Display the command-line help. If present, this must be the only command-line parameter.',
        '    Example command-line invocations:',
        '      java org.codenarc.CodeNarc',
        '      java org.codenarc.CodeNarc -rulesetfiles="rulesets/basic.xml" title="My Project"',
        '      java org.codenarc.CodeNarc -help'
    ].join('\n')

    protected String ruleSetFiles
    protected String baseDir
    protected String includes
    protected String excludes
    protected String title
    protected List reports = []

    // Abstract Ant Task execution to allow substitution for unit tests
    protected antTaskExecutor = { antTask -> antTask.execute() }

    public static void main(String[] args) {
        def codeNarc = new CodeNarc()

        if (args == ['-help'] as String[]) {
            println HELP
            return
        }

        try {
            codeNarc.execute(args)
        }
        catch(Throwable t) {
            println "ERROR: ${t.message}"
            t.printStackTrace()
            println HELP            
        }
    }

    protected void execute(String[] args) {
        parseArgs(args)
        setDefaultsIfNecessary()
        def antTask = buildAntTask()
        antTaskExecutor(antTask)
    }

    protected void setDefaultsIfNecessary() {
        if (!baseDir) {
            baseDir = '.'
        }
        if (!includes) {
            includes = '**/*.groovy'
        }
        if (!ruleSetFiles) {
            ruleSetFiles = 'rulesets/basic.xml'
        }
        if (reports.empty) {
            reports << new Report(type:'html',title:title)
        }
    }

    private Task buildAntTask() {
        def project = new Project(basedir:'.')
        def antTask = new CodeNarcTask(project:project)
        antTask.addFileset(createFileSet(project))
        antTask.ruleSetFiles = ruleSetFiles
        reports.each { report ->
            report.title = title
            antTask.addConfiguredReport(report) 
        }
        return antTask
    }

    private FileSet createFileSet(Project project) {
        def fileSet = new FileSet(dir:new File(baseDir), project:project)
        fileSet.setIncludes(includes)
        fileSet.setExcludes(excludes)
        return fileSet
    }

    protected void parseArgs(String[] args) {
        args.each { arg ->
            final PATTERN = /\-(.*)\=(.*)/      // -name=value
            def matcher = arg =~ PATTERN
            assert matcher, "Invalid argument format: [$arg]"
            def name = matcher[0][1]
            def value = matcher[0][2]
            switch(name) {
                case 'rulesetfiles': parseRuleSetFiles(value); break
                case 'basedir': parseBaseDir(value); break
                case 'includes': parseIncludes(value); break
                case 'excludes': parseExcludes(value); break
                case 'title': parseTitle(value); break
                case 'report': parseReport(value); break
                default: assert false, "Invalid option: [$arg]"
            }
        }
    }

    private parseRuleSetFiles(String argValue) {
        ruleSetFiles = argValue
    }

    private parseBaseDir(String argValue) {
        baseDir = argValue
    }

    private parseIncludes(String argValue) {
        includes = argValue
    }

    private parseExcludes(String argValue) {
        excludes = argValue
    }

    private parseTitle(String argValue) {
        title = argValue
    }

    private parseReport(String argValue) {
        def report = new Report()
        if (argValue.contains(':')) {
            def parts = argValue.tokenize(':')
            report.type = parts[0]
            report.toFile = parts[1]
        }
        else {
            report.type = argValue
        }
        reports << report
    }

}