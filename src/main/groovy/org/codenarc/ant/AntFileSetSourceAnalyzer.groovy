/*
 * Copyright 2011 the original author or authors.
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

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.apache.log4j.Logger
import org.apache.tools.ant.Project
import org.apache.tools.ant.types.FileSet
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.ruleset.RuleSet
import org.codenarc.source.SourceFile
import org.codenarc.util.PathUtil
import org.codenarc.analyzer.SuppressionAnalyzer

/**
 * SourceAnalyzer implementation that gets source files from one or more Ant FileSets.
 *
 * @author Chris Mair
 */
class AntFileSetSourceAnalyzer implements SourceAnalyzer {

    private static final LOG = Logger.getLogger(AntFileSetSourceAnalyzer)
    private static final POOL_TIMEOUT_SECONDS = 60 * 60

    private Project project
    protected List fileSets = []

    // Concurrent shared state
    private ConcurrentMap resultsMap = new ConcurrentHashMap()
    private ConcurrentMap fileCountMap = new ConcurrentHashMap()

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
        def startTime = System.currentTimeMillis()
        def reportResults = new DirectoryResults()

        fileSets.each { fileSet ->
            processFileSet(fileSet, ruleSet)
        }

        addDirectoryResults(reportResults)
        LOG.info("Analysis time=${System.currentTimeMillis() - startTime}ms")
        reportResults
    }

    List getSourceDirectories() {
        def baseDir = project.baseDir.absolutePath
        fileSets.collect { fileSet ->
            def path = fileSet.getDir(project).path
            PathUtil.removePathPrefix(baseDir, path)
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

    private void processFileSet(FileSet fileSet, RuleSet ruleSet) {
        def dirScanner = fileSet.getDirectoryScanner(project)
        def baseDir = fileSet.getDir(project)
        def includedFiles = dirScanner.includedFiles

        if (!includedFiles) {
            LOG.info("No matching files found for FileSet with basedir [$baseDir]")
            return
        }

        executeWithThreadPool { pool ->
            includedFiles.each { filePath ->
                def task = buildTask(baseDir, filePath, ruleSet)
                pool.submit(task)
            }
        }
    }

    private void executeWithThreadPool(Closure closure) {
        def numThreads = Runtime.getRuntime().availableProcessors() + 1
        def pool = Executors.newFixedThreadPool(numThreads)

        closure(pool)

        pool.shutdown()
        def completed = pool.awaitTermination(POOL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        assert completed, 'Thread Pool terminated before completion.'
    }

    private Runnable buildTask(File baseDir, String filePath, RuleSet ruleSet) {
        return {
            try {
                processFile(baseDir, filePath, ruleSet)
            } catch (Throwable t) {
                LOG.info("Error processing filePath: $filePath''", t)
            }
        } as Runnable
    }

    private void processFile(File baseDir, String filePath, RuleSet ruleSet) {
        def file = new File(baseDir, filePath)
        def sourceFile = new SourceFile(file)
        def suppressionService = new SuppressionAnalyzer(sourceFile)
        def allViolations = []
        ruleSet.rules.each {rule ->
            if (!suppressionService.isRuleSuppressed(rule)) {
                def violations = rule.applyTo(sourceFile)
                violations.removeAll { suppressionService.isViolationSuppressed(it) }
                allViolations.addAll(violations)
            }
        }

        def fileResults = null
        if (allViolations) {
            fileResults = new FileResults(PathUtil.normalizePath(filePath), allViolations)
        }
        def parentPath = PathUtil.getParentPath(filePath)
        def safeParentPath = parentPath ?: ''
        addToResultsMap(safeParentPath, fileResults)
        incrementFileCount(safeParentPath)
    }

    private void incrementFileCount(String parentPath) {
        def initialZeroCount = new AtomicInteger(0)
        fileCountMap.putIfAbsent(parentPath, initialZeroCount)
        def fileCount = fileCountMap[parentPath]
        fileCount.incrementAndGet()
    }

    private void addToResultsMap(String parentPath, results) {
        def initialEmptyResults = Collections.synchronizedList([])
        resultsMap.putIfAbsent(parentPath, initialEmptyResults)
        def dirResults = resultsMap[parentPath]
        if (results) {
            dirResults << results
        }
    }

    private void addToParentResults(Results reportResults, Results results) {
        def parentPath = PathUtil.getParentPath(results.path)
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
        def allPaths = resultsMap.keySet().sort()
        allPaths.each { path ->
            def dirResults = new DirectoryResults(path)
            def children = resultsMap[path].sort { child -> child.path }
            children.each { child ->
                dirResults.addChild(child)
            }
            dirResults.numberOfFilesInThisDirectory = fileCountMap[path] ?: 0
            addToParentResults(reportResults, dirResults)
        }
    }
}