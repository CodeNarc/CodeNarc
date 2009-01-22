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
package org.codenarc.ant

import org.apache.tools.ant.types.FileSet
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.ruleset.RuleSet
import org.codenarc.source.SourceFile
import org.apache.log4j.Logger
import org.apache.tools.ant.Project

/**
 * SourceAnalyzer implementation that gets source files from an Ant FileSet.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AntFileSetSourceAnalyzer implements SourceAnalyzer {
    static final LOG = Logger.getLogger(AntFileSetSourceAnalyzer)
    static final SEP = '/'

    private Project project
    private FileSet fileSet
    private Map resultsMap = [:]
    private Map fileCountMap = [:]

    // TODO This class could still use some TLC and redesign/refactoring

    /**
     * Construct a new instance on the specified Ant FileSet.
     * @param project - the Ant Project
     * @param fileSet - the Ant FileSet
     */
    AntFileSetSourceAnalyzer(Project project, FileSet fileSet) {
        assert project
        assert fileSet
        this.project = project
        this.fileSet = fileSet
    }

    /**
     * Analyze all source code using the specified RuleSet and return the report results.
     * @param ruleset - the RuleSet to apply to each source component; must not be null.
     * @return the results from applying the RuleSet to all of the source
     */
    public Results analyze(RuleSet ruleSet) {
        def reportResults = new DirectoryResults()

        def dirScanner = fileSet.getDirectoryScanner(project)
        def includedFiles = dirScanner.includedFiles

        if (!includedFiles) {
            LOG.info("No matching files found for FileSet with basedir [${fileSet.getDir(project)}]")
        }

        includedFiles.each { filePath ->
            processFile(filePath, reportResults, ruleSet)
        }
        addDirectoryResults(reportResults)
        return reportResults
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private String getParentPath(String filePath) {
        def normalizedPath = normalizePath(filePath)
        def partList = normalizedPath ? normalizedPath.tokenize(SEP) : []
        if (partList.size() < 2) {
            return null
        }
        def parentList = partList[0..-2]
        return parentList.join(SEP)
    }

    private void incrementFileCount(String filePath) {
        def normalizedParentPath = getParentPath(filePath)
        def fileCount = fileCountMap[normalizedParentPath]
        fileCountMap[normalizedParentPath] = fileCount ? fileCount + 1 : 1
    }

    private void addToResultsMap(String filePath, results) {
        def normalizedParentPath = getParentPath(filePath)
        def dirResults = resultsMap[normalizedParentPath]
        if (dirResults == null) {
            dirResults = []
            resultsMap[normalizedParentPath] = dirResults
        }
        if (results) {
            dirResults << results
        }
    }

    private void addToParentResults(Results reportResults, Results results) {
        def parentPath = getParentPath(results.path)
        if (parentPath == null) {
            reportResults.addChild(results)
            return
        }
        def parent = reportResults.findResultsForPath(parentPath)
        if (!parent) {
            parent = new DirectoryResults(parentPath)
            addToParentResults(reportResults, parent)
        }
        parent.addChild(results)
    }

    private void addDirectoryResults(Results reportResults) {
        def allPaths = resultsMap.keySet()
        allPaths.each { path ->
            def dirResults = new DirectoryResults(path)
            resultsMap[path].each { child ->
                dirResults.addChild(child)
            }
            dirResults.numberOfFilesInThisDirectory = fileCountMap[path] ?: 0
            addToParentResults(reportResults, dirResults)
        }
    }

    private def processFile(String filePath, reportResults, RuleSet ruleSet) {
        def file = new File(fileSet.getDir(project), filePath)
        def sourceFile = new SourceFile(file)
        def allViolations = []
        ruleSet.rules.each {rule ->
            def violations = rule.applyTo(sourceFile)
            allViolations.addAll(violations)
        }

        def fileResults = null
        if (allViolations) {
            fileResults = new FileResults(normalizePath(filePath), allViolations)
        }
        addToResultsMap(filePath, fileResults)
        incrementFileCount(filePath)
    }

    private String normalizePath(String path) {
        return path ? path.replaceAll('\\\\', SEP) : path
    }

}