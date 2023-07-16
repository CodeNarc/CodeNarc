/*
 * Copyright 2022 the original author or authors.
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
import org.codenarc.source.SourceFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * SourceAnalyzer implementation that processes a list of source files
 *
 * @author Nicolas Vuillamy
 */
class FilesSourceAnalyzer extends AbstractSourceAnalyzer {

    private static final String SEP = '/'
    private static final Logger LOG = LoggerFactory.getLogger(FilesSourceAnalyzer)

    /**
     * The base (root) directory. If not set, the current directory ('.') will be used
     */
    String baseDirectory = '.'

    /**
     * List of groovy files that will be analyzed.
     * Paths can be absolute, or relative to base directory
     */
    String[] sourceFiles

    /**
     * Whether to throw an exception if errors occur parsing source files (true), or just log the errors (false)
     */
    boolean failOnError = false

    /**
     * Analyze the source with the input list of files using the specified RuleSet and return the report results.
     * @param ruleset - the RuleSet to apply to each of the (applicable) files in the source directories
     * @return the results from applying the RuleSet to all of the files in the source directories
     */
    @SuppressWarnings(['CatchThrowable'])
    @Override
    Results analyze(RuleSet ruleSet) {
        assert baseDirectory
        assert ruleSet
        DirectoryResults baseDirResults = new DirectoryResults('')

        // Get all results from unique named files
        for (String sourceFilePath in sourceFiles) {
            File file = new File(sourceFilePath)
            String relativePath = sourceFilePath
            if (!file.exists()) {
                file = new File(baseDirectory + SEP + sourceFilePath)
                if (!file.exists()) {
                    LOG.error("Unable to find input file: $sourceFilePath")
                    throw new AnalyzerException("Unable to find input file: $sourceFilePath")
                }
            }
            try  {
                FileResults fileResults = processFile(file, relativePath, ruleSet)
                baseDirResults.addFileResultRecursive(fileResults)
            }
            catch (Throwable t) {
                LOG.warn("Error processing file: '" + sourceFilePath + "'; " + t)
                if (failOnError) {
                    throw new AnalyzerException("Error analyzing source file: $sourceFilePath; $t")
                }
            }
        }

        // Convert file results into directory results
        def reportResults = new DirectoryResults()
        reportResults.addChild(baseDirResults)
        reportResults
    }

    @Override
    List getSourceDirectories() {
        [baseDirectory]
    }

    // Get violations for a single file
    private FileResults processFile(File file, String relativePath, RuleSet ruleSet) {
        def sourceFile = new SourceFile(file)
        List allViolations = collectViolations(sourceFile, ruleSet)
        def fileResults = new FileResults(relativePath, allViolations, sourceFile)
        fileResults
    }
}
