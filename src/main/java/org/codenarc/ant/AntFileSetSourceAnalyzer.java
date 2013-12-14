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
package org.codenarc.ant;

import org.apache.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.codenarc.analyzer.AbstractSourceAnalyzer;
import org.codenarc.results.DirectoryResults;
import org.codenarc.results.FileResults;
import org.codenarc.results.Results;
import org.codenarc.rule.Violation;
import org.codenarc.ruleset.RuleSet;
import org.codenarc.source.SourceFile;
import org.codenarc.util.PathUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SourceAnalyzer implementation that gets source files from one or more Ant FileSets.
 *
 * @author Chris Mair
 */
public class AntFileSetSourceAnalyzer extends AbstractSourceAnalyzer {

    private static final Logger LOG = Logger.getLogger(AntFileSetSourceAnalyzer.class);
    private static final int POOL_TIMEOUT_SECONDS = 60 * 60;

    private final Project project;
    protected final List<FileSet> fileSets;

    // Concurrent shared state
    private final ConcurrentMap<String, List<FileResults>> resultsMap = new ConcurrentHashMap<String, List<FileResults>>();
    private final ConcurrentMap<String, AtomicInteger> fileCountMap = new ConcurrentHashMap<String, AtomicInteger>();

    /**
     * Construct a new instance on the specified Ant FileSet.
     *
     * @param project - the Ant Project; must not be null
     * @param fileSet - the Ant FileSet; must not be null
     */
    public AntFileSetSourceAnalyzer(Project project, FileSet fileSet) {
        if (fileSet == null) {
            throw new IllegalArgumentException("Null: fileSet");
        }
        if (project == null) {
            throw new IllegalArgumentException("Null: project");
        }
        this.project = project;
        this.fileSets = Arrays.asList(fileSet);
    }

    /**
     * Construct a new instance on the specified List of Ant FileSets.
     *
     * @param project  - the Ant Project
     * @param fileSets - the List of Ant FileSet; my be empty; must not be null
     */
    AntFileSetSourceAnalyzer(Project project, List<FileSet> fileSets) {
        if (fileSets == null) {
            throw new IllegalArgumentException("Null: fileSets");
        }
        if (project == null) {
            throw new IllegalArgumentException("Null: project");
        }

        this.project = project;
        this.fileSets = new ArrayList<FileSet>(fileSets);
    }

    /**
     * Analyze all source code using the specified RuleSet and return the report results.
     *
     * @param ruleSet - the RuleSet to apply to each source component; must not be null.
     * @return the results from applying the RuleSet to all of the source
     */
    public Results analyze(RuleSet ruleSet) {
        long startTime = System.currentTimeMillis();
        DirectoryResults reportResults = new DirectoryResults();

        int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);

        for (FileSet fileSet : fileSets) {
            processFileSet(fileSet, ruleSet, pool);
        }

        pool.shutdown();

        try {
            boolean completed = pool.awaitTermination(POOL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!completed) {
                throw new IllegalStateException("Thread Pool terminated before comp<FileResults>letion");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread Pool interrupted before completion");
        }

        addDirectoryResults(reportResults);
        LOG.info("Analysis time=" + (System.currentTimeMillis() - startTime) + "ms");
        return reportResults;
    }

    public List getSourceDirectories() {
        String baseDir = project.getBaseDir().getAbsolutePath();

        List<String> result = new ArrayList<String>();
        for (FileSet fileSet : fileSets) {
            String path = fileSet.getDir(project).getPath();
            String trimmedPath = PathUtil.removePathPrefix(baseDir, path);
            result.add(trimmedPath);
        }
        return result;
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private void processFileSet(FileSet fileSet, RuleSet ruleSet, ExecutorService pool) {
        DirectoryScanner dirScanner = fileSet.getDirectoryScanner(project);
        File baseDir = fileSet.getDir(project);
        String[] includedFiles = dirScanner.getIncludedFiles();

        if (includedFiles == null || includedFiles.length == 0) {
            LOG.info("No matching files found for FileSet with basedir [" + baseDir + "]");
            return;
        }

        for (String filePath : includedFiles) {
            Runnable task = buildTask(baseDir, filePath, ruleSet);
            pool.submit(task);
        }
    }

    private Runnable buildTask(final File baseDir, final String filePath, final RuleSet ruleSet) {
        return new Runnable() {
            public void run() {
                try {
                    processFile(baseDir, filePath, ruleSet);
                } catch (Throwable t) {
                    LOG.info("Error processing filePath: '" + filePath + "'", t);
                }
            }
        };
    }

    private void processFile(File baseDir, String filePath, RuleSet ruleSet) {
        File file = new File(baseDir, filePath);
        SourceFile sourceFile = new SourceFile(file);
        List<Violation> allViolations = collectViolations(sourceFile, ruleSet);
        FileResults fileResults = null;
        if (allViolations != null && !allViolations.isEmpty()) {
            fileResults = new FileResults(PathUtil.normalizePath(filePath), allViolations);
        }
        String parentPath = PathUtil.getParentPath(filePath);
        String safeParentPath = parentPath != null ? parentPath : "";
        addToResultsMap(safeParentPath, fileResults);
        incrementFileCount(safeParentPath);
    }

    private void incrementFileCount(String parentPath) {
        AtomicInteger initialZeroCount = new AtomicInteger(0);
        fileCountMap.putIfAbsent(parentPath, initialZeroCount);
        AtomicInteger fileCount = fileCountMap.get(parentPath);
        fileCount.incrementAndGet();
    }

    private void addToResultsMap(String parentPath, FileResults results) {
        List<FileResults> initialEmptyResults = Collections.synchronizedList(new ArrayList<FileResults>());
        resultsMap.putIfAbsent(parentPath, initialEmptyResults);

        if (results != null) {
            List<FileResults> dirResults = resultsMap.get(parentPath);
            dirResults.add(results);
        }
    }

    private void addToParentResults(DirectoryResults reportResults, Results results) {
        String parentPath = PathUtil.getParentPath(results.getPath());
        if (parentPath == null) {
            reportResults.addChild(results);
            return;
        }
        DirectoryResults parent = (DirectoryResults) reportResults.findResultsForPath(parentPath);
        if (parent == null) {
            parent = new DirectoryResults(parentPath);
            addToParentResults(reportResults, parent);
        }
        parent.addChild(results);
    }

    private void addDirectoryResults(DirectoryResults reportResults) {
        Set<String> set = resultsMap.keySet();
        ArrayList<String> allPaths = new ArrayList<String>(set);
        Collections.sort(allPaths);

        for (String path : allPaths) {
            DirectoryResults dirResults = new DirectoryResults(path);
            List<FileResults> allResults = resultsMap.get(path);
            Collections.sort(allResults, new Comparator<FileResults>() {
                public int compare(FileResults o1, FileResults o2) {
                    return o1.getPath().compareTo(o2.getPath());
                }
            });

            for (FileResults child : allResults) {
                dirResults.addChild(child);
            }
            AtomicInteger cnt = fileCountMap.get(path);
            dirResults.setNumberOfFilesInThisDirectory(cnt != null ? cnt.get() : 0);
            addToParentResults(reportResults, dirResults);
        }
    }
}