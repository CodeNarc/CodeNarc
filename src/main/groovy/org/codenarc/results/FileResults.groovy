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
 */
@SuppressWarnings('UnusedMethodParameter')
class FileResults implements Results {

    private final String path
    private final List violations

    FileResults(String path, List violations) {
        this.path = path
        this.violations = violations
    }

    /**
     * @return the path to the file or directory associated with these results
     */
    String getPath() {
        path
    }

    /**
     * @return true (this object represents the results for a single file)
     */
    boolean isFile() {
        true
    }

    /**
     * Return an empty List
     * @return the List of child Results objects; may be empty
     */
    List getChildren() {
        Collections.EMPTY_LIST
    }

    /**
     * @return the List of all violations
     */
    List getViolations() {
        new ArrayList(violations)
    }

    /**
     * @param recursive - ignored
     * @return the number of violations with the specified priority
     */
    //@Override - causes compile error in Groovy 2.4.0
    int getNumberOfViolationsWithPriority(int priority, boolean recursive=true) {
        violations.sum(0) { violation -> violation.rule.priority == priority ? 1 : 0 }
    }

    /**
     * Return the total number of (Groovy) files analyzed
     * @param recursive - ignored
     * @return the total number of files (with or without violations)
     */
    //@Override - causes compile error in Groovy 2.4.0
    int getTotalNumberOfFiles(boolean recursive=true) {
        1
    }

    /**
     * Return 1 if these results include at least one violation
     * @param maxPriority - the maximum priority level; ignore violations with priority greater than this
     * @param recursive - ignored
     * @return the number of files containing violations
     */
    //@Override - causes compile error in Groovy 2.4.0
    int getNumberOfFilesWithViolations(int maxPriority, boolean recursive=true) {
        violations.find { v -> v.rule.priority <= maxPriority } ? 1 : 0
    }

    /**
     * Return the Results object with the specified path.
     * @param path - the path to search for
     * @return this Results object if the path matches, otherwise null
     */
    Results findResultsForPath(String path) {
        this.path == path ? this : null
    }

    String toString() {
        "FileResults($path) $violations"
    }
}
