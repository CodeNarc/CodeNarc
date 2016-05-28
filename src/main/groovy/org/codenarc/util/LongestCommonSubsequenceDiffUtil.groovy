/*
 * Copyright 2016 the original author or authors.
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

import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * Computes diffs between arrays of strings based on Longest Common Subsequence
 *
 * @author Rahul Somasunderam
 */
class LongestCommonSubsequenceDiffUtil {

    /**
     * Represents a line in the diff
     */
    @Immutable
    @ToString(includePackage = false)
    static class Line {
        /**
         * Line number on which the diff exists.
         */
        int lineNumber
        /**
         * The text that was different
         */
        String text

        static enum Mode {
            ADD, REMOVE
        }

        Mode mode
    }

    /**
     * Uses Longest Common Subsequence to determine the diff between two arrays of strings.
     * This helps us know when things are where they don't belong.
     *
     * @param expected The ordered strings as expected by using the pattern
     * @param actual The ordered strings in the original code
     * @return List of misplaced strings and line numbers in a Line
     */
    @SuppressWarnings('NestedForLoop')
    static List<Line> computeDiff(String[] expected, String[] actual) {
        def retval = []
        // number of lines of each file
        int actualLength = actual.length
        int expectedLength = expected.length

        // opt[i][j] = length of LCS of x[i..actualLength] and y[j..expectedLength]
        int[][] opt = new int[actualLength + 1][expectedLength + 1]

        // compute length of LCS
        for (int i = actualLength - 1; i >= 0; i--) {
            for (int j = expectedLength - 1; j >= 0; j--) {
                if (actual[i] == expected[j]) {
                    opt[i][j] = opt[i + 1][j + 1] + 1
                } else {
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1])
                }
            }
        }

        // recover LCS itself and turns non matching lines from actual side into Lines
        int i = 0, j = 0
        while (i < actualLength && j < expectedLength) {
            if (actual[i] == expected[j]) {
                i++
                j++
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                retval << new Line(i, actual[i++], Line.Mode.ADD)
            } else {
                retval << new Line(j, expected[j++], Line.Mode.REMOVE)
            }
        }

        // turn out remainder of actuals to Lines
        while (i < actualLength || j < expectedLength) {
            if (i == actualLength) {
                retval << new Line(j, expected[j++], Line.Mode.REMOVE)
            } else if (j == expectedLength) {
                retval << new Line(i, actual[i++], Line.Mode.ADD)
            }
        }

        retval
    }
}
