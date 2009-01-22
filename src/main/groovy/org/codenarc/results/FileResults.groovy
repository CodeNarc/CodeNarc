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
package org.codenarc.results

/**
 * Represents the results of applying a set of rules against a single sourcefile
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class FileResults implements Results {

    private String path
    private List violations

    FileResults(String path, List violations) {
        this.path = path
        this.violations = violations
    }

    /**
     * @return the path to the file or directory associated with these results
     */
    String getPath() {
        return path
    }

    /**
     * @return true (this object represents the results for a single file)
     */
    boolean isFile() {
        return true
    }

    /**
     * Return an empty List
     * @return the List of child Results objects; may be empty
     */
    List getChildren() {
        return Collections.EMPTY_LIST
    }

    /**
     * @return the List of violations with the specified priority; may be empty
     */
    List getViolationsWithPriority(int priority) {
        return violations.findAll { violation -> violation.rule.priority == priority}
    }

    /**
     * @param recursive - true if the returned count should include subdirectories as well; defaults to true
     * @return the number of violations with the specified priority
     */
    int getNumberOfViolationsWithPriority(int priority, boolean recursive=true) {
        return violations.sum(0) { violation -> violation.rule.priority == priority ? 1 : 0}
    }

    /**
     * Return the total number of (Groovy) files analyzed
     * @param recursive - true if the returned count should include subdirectories as well
     * @return the total number of files (with or without violations)
     */
    int getTotalNumberOfFiles(boolean recursive=true) {
        return 1
    }

    /**
     * Return 1 if these results include at least one violation
     * @param recursive - ignored; defaults to true
     * @return the number of files containing violations
     */
    int getNumberOfFilesWithViolations(boolean recursive=true) {
        return violations.empty ? 0 : 1
    }

    /**
     * Return the Results object with the specified path.
     * @param path - the path to search for
     * @return this Results object if the path matches, otherwise null
     */
    Results findResultsForPath(String path) {
        return this.path == path ? this : null 
    }

    String toString() {
        return "FileResults($path) $violations"
    }
}