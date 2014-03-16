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
package org.codenarc.analyzer

import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.ruleset.RuleSet
import org.codenarc.source.SourceCodeCriteria
import org.codenarc.source.SourceFile

/**
 * SourceAnalyzer implementation that recursively processes files in the configured source directories.
 *
 * @deprecated This is an internal class that will be removed in the future
 *
 * @author Chris Mair
 */
class DirectorySourceAnalyzer extends AbstractSourceAnalyzer {
    static final SEP = '/'

    /**
     * The base directory; the sourceDirectories are relative to this,
     * if not null. If this value is null, then treat sourceDirectories as full paths.
     */
    String baseDirectory

    /**
     *  The list of source directories, relative to the baseDirectory
     *  if it is not null. If sourceDirectories is null, then analyze files recursively
     *  from baseDirectory.
     */
    List sourceDirectories

    /**
     * Only analyze pathnames matching this regular expression. If null, match all pathnames.
     * This defaults to matching all pathnames that end with '.groovy'.
     */
    String applyToFilesMatching = /.*\.groovy/

    /**
     * Do NOT analyze pathnames matching this regular expression. If null, then do not exclude any pathnames.
     */
    String doNotApplyToFilesMatching

    /**
     * Only analyze filenames matching this value.
     * The value may optionally be a comma-separated list of names.
     * The name(s) may optionally include wildcard characters ('*' or '?').
     */
    String applyToFileNames

    /**
     * Do NOT analyze filenames matching this value.
     * The value may optionally be a comma-separated list of names.
     * The name(s) may optionally include wildcard characters ('*' or '?').
     */
    String doNotApplyToFileNames

    /**
     * Analyze the source with the configured directory tree(s) using the specified RuleSet and return the report results.
     * @param ruleset - the RuleSet to apply to each of the (applicable) files in the source directories
     * @return the results from applying the RuleSet to all of the files in the source directories
     */
    Results analyze(RuleSet ruleSet) {
        assert baseDirectory || sourceDirectories
        assert ruleSet

        def reportResults = new DirectoryResults()
        def srcDirs = sourceDirectories ?: ['']
        srcDirs.each { srcDir ->
            def dirResults = processDirectory(srcDir, ruleSet)
            reportResults.addChild(dirResults)
        }
        reportResults
    }

    private DirectoryResults processDirectory(String dir, RuleSet ruleSet) {
        def dirResults = new DirectoryResults(dir)
        def dirFile = new File((String)baseDirectory, (String)dir)
        dirFile.eachFile { file ->
            def dirPrefix = dir ? dir + SEP : dir
            def filePath = dirPrefix + file.name
            if (file.directory) {
                def subdirResults = processDirectory(filePath, ruleSet)
                // If any of the descendent directories have matching files, then include in final results
                if (subdirResults.getTotalNumberOfFiles(true)) {
                    dirResults.addChild(subdirResults)
                }
            }
            else {
                processFile(filePath, dirResults, ruleSet)
            }
        }
        dirResults
    }

    private processFile(String filePath, DirectoryResults dirResults, RuleSet ruleSet) {
        def file = new File((String)baseDirectory, filePath)
        def sourceFile = new SourceFile(file)

        if (new SourceCodeCriteria(
                applyToFilesMatching:applyToFilesMatching,
                doNotApplyToFilesMatching:doNotApplyToFilesMatching,
                applyToFileNames:applyToFileNames,
                doNotApplyToFileNames:doNotApplyToFileNames).matches(sourceFile)) {

            dirResults.numberOfFilesInThisDirectory ++
            List allViolations = collectViolations(sourceFile, ruleSet)
            if (allViolations) {
                def fileResults = new FileResults(filePath, allViolations)
                dirResults.addChild(fileResults)
            }
        }
    }
}
