/*
 * Copyright 2017 the original author or authors.
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
package org.codenarc.util

/**
 * A class that is used to hold the state of a the presence of multiline comments.
 *
 * When processing files for violations, not all violations apply to comments. This class provides a simple way to
 * process each line of code and determine if the current line exists inside of a multiline comment.
 *
 * @author Russell Sanborn
 */
class MultilineCommentChecker {
    private static final String START_MULTILINE_COMMENT = '/*'
    private static final String END_MULTILINE_COMMENT = '*/'
    private static final String START_MULTILINE_COMMENT_PATTERN = '.*/\\*.*'
    private static final String END_MULTILINE_COMMENT_PATTERN = '.*\\*/.*'

    protected boolean inMultilineComment

    MultilineCommentChecker() {
        inMultilineComment = false
    }

    /**
     * Processes a line of code sets the inMultilineComment state based on the status of the current line.
     *
     * @param line the current line to be checked
     */
    void processLine(String line) {
        if (line.matches(START_MULTILINE_COMMENT_PATTERN) && line.matches(END_MULTILINE_COMMENT_PATTERN)) {
            int startIndex = line.indexOf(START_MULTILINE_COMMENT)
            int endIndex = line.indexOf(END_MULTILINE_COMMENT)

            inMultilineComment = (endIndex < startIndex)
        } else if (line.matches(START_MULTILINE_COMMENT_PATTERN)) {
            inMultilineComment = true
        } else if (line.matches(END_MULTILINE_COMMENT_PATTERN)) {
            inMultilineComment = false
        }
    }
}
