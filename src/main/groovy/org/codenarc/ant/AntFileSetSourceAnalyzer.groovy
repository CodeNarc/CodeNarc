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
 * SourceAnalyzer implementation that gets source files from one or more Ant FileSets.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AntFileSetSourceAnalyzer implements SourceAnalyzer {
    private static final LOG = Logger.getLogger(AntFileSetSourceAnalyzer)
    private static final SEP = '/'

    private Project project
    protected List fileSets = []
    private Map resultsMap = [:]
    private Map fileCountMap = [:]

    // TODO This class could still use some TLC and redesign/refactoring

    /**
     * Construct a new instance on the specified Ant FileSet.
     * @param project - the Ant Project; must not be null
     * @param fileSet - the Ant FileSet; must not be null
     */
    AntFileSetSourceAnalyzer(Project project, FileSet fileSet) {
        assert fileSet
        initialize(project, [fileSet])
    }

    /**
     * Construct a new instance on the specified List of Ant FileSets.
     * @param project - the Ant Project
     * @param fileSets - the List of Ant FileSet; my be empty; must not be null
     */
    AntFileSetSourceAnalyzer(Project project, List fileSets) {
        initialize(project, fileSets)
    }

    /**
     * Analyze all source code using the specified RuleSet and return the report results.
     * @param ruleset - the RuleSet to apply to each source component; must not be null.
     * @return the results from applying the RuleSet to all of the source
     */
    Results analyze(RuleSet ruleSet) {
        def reportResults = new DirectoryResults()

        fileSets.each { fileSet ->
            processFileSet(fileSet, ruleSet, reportResults)
        }

        addDirectoryResults(reportResults)
        return reportResults
    }

    List getSourceDirectories() {
        def baseDir = project.baseDir.absolutePath
        return fileSets.collect { fileSet ->
            def path = fileSet.getDir(project).path
            removeBaseDirectoryPrefix(baseDir, path)
        }
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private initialize(Project project, List fileSets) {
        assert project
        assert fileSets != null
        this.project = project
        this.fileSets = fileSets
    }

    private void processFileSet(fileSet, ruleSet, reportResults) {
        def dirScanner = fileSet.getDirectoryScanner(project)
        def baseDir = fileSet.getDir(project)
        def includedFiles = dirScanner.includedFiles

        if (!includedFiles) {
            LOG.info("No matching files found for FileSet with basedir [$baseDir]")
        }

        includedFiles.each {filePath ->
            processFile(baseDir, filePath, reportResults, ruleSet)
        }
    }

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

    private void processFile(File baseDir, String filePath, reportResults, RuleSet ruleSet) {
        def file = new File(baseDir, filePath)
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

    private String removeBaseDirectoryPrefix(String baseDir, String path) {
        if (path.startsWith(baseDir)) {
            path = path - baseDir
            return removeLeadingSlash(path)
        }
        return path
    }

    private String removeLeadingSlash(path) {
        return (path.startsWith('\\') || path.startsWith('/')) ? path.substring(1) : path
    }
}