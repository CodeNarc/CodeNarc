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
 * Represents the results of applying rules to one or more source files/directories
 *
 * @author Chris Mair
 */
interface Results {

    /**
     * @return the path to the file or directory associated with these results
     */
    String getPath()

    /**
     * @return the List of child Results objects; may be empty
     */
    List getChildren()

    /**
     * @return the List of all violations; may be empty
     */
    List getViolations()

    /**
     * Return the number of violations with the specified priority
     * @param priority - the priority
     * @param recursive - true if the returned count should include subdirectories as well
     * @return the number of violations with the specified priority
     */
    int getNumberOfViolationsWithPriority(int priority, boolean recursive)

    /**
     * Return the total number of (Groovy) files analyzed 
     * @param recursive - true if the returned count should include subdirectories as well
     * @return the total number of files (with or without violations)
     */
    int getTotalNumberOfFiles(boolean recursive)

    /**
     * Return the number of files with violations
     * @param maxPriority - the maximum priority level; ignore violations with priority greater than this
     * @param recursive - true if the returned count should include subdirectories as well
     * @return the number of files containing violations
     */
    int getNumberOfFilesWithViolations(int maxPriority, boolean recursive)

    /**
     * @return true only if this object represents the results for a single file
     */
    boolean isFile()

    /**
     * Return the Results object with the specified path within this results object or its descendents.
     * @param path - the path to search for
     * @return this Results object if a match is found, otherwise null
     */
    Results findResultsForPath(String path)
}
