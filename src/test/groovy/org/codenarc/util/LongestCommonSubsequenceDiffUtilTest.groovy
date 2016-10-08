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

import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Test for LongestCommonSubsequenceDiffUtil
 *
 * @author Rahul Somasunderam
 */
class LongestCommonSubsequenceDiffUtilTest extends AbstractTestCase {

    @Test
    void testEmptyArrays() {
        assert LongestCommonSubsequenceDiffUtil.computeDiff([] as String[], [] as String[]) == []
    }

    @Test
    void testSingleLineSame() {
        def expected = 'alpha'.split('\n')

        def actual = 'alpha'.split('\n')
        assert LongestCommonSubsequenceDiffUtil.computeDiff(expected, actual) == []
    }

    @Test
    void testSingleLineDifferent() {
        def expected = 'alpha'.split('\n')
        def actual = 'bravo'.split('\n')
        assert LongestCommonSubsequenceDiffUtil.computeDiff(expected, actual).toSet() ==
                [
                        new LongestCommonSubsequenceDiffUtil.Line(0, 'alpha', LongestCommonSubsequenceDiffUtil.Line.Mode.REMOVE),
                        new LongestCommonSubsequenceDiffUtil.Line(0, 'bravo', LongestCommonSubsequenceDiffUtil.Line.Mode.ADD),
                ].toSet()
    }

    @Test
    void testSingleLineInsert() {
        def expected = 'alpha,charlie,delta'.split(',')
        def actual = 'alpha,bravo,charlie,delta'.split(',')

        assert LongestCommonSubsequenceDiffUtil.computeDiff(expected, actual).toSet() ==
                [
                        new LongestCommonSubsequenceDiffUtil.Line(1, 'bravo', LongestCommonSubsequenceDiffUtil.Line.Mode.ADD),
                ].toSet()
    }

    @Test
    void testSingleLineDelete() {
        def expected = 'alpha,charlie,delta'.split(',')
        def actual = 'alpha,delta'.split(',')

        assert LongestCommonSubsequenceDiffUtil.computeDiff(expected, actual).toSet() ==
                [
                        new LongestCommonSubsequenceDiffUtil.Line(1, 'charlie', LongestCommonSubsequenceDiffUtil.Line.Mode.REMOVE),
                ].toSet()
    }

    @Test
    void testSingleLineReplace() {
        def expected = 'alpha,charlie,delta'.split(',')
        def actual = 'alpha,bravo,delta'.split(',')

        assert LongestCommonSubsequenceDiffUtil.computeDiff(expected, actual).toSet() ==
                [
                        new LongestCommonSubsequenceDiffUtil.Line(1, 'charlie', LongestCommonSubsequenceDiffUtil.Line.Mode.REMOVE),
                        new LongestCommonSubsequenceDiffUtil.Line(1, 'bravo', LongestCommonSubsequenceDiffUtil.Line.Mode.ADD),
                ].toSet()
    }

    @Test
    void testSingleLineMisorder() {
        def expected = 'alpha,bravo,charlie'.split(',')
        def actual = 'alpha,charlie,bravo'.split(',')

        assert LongestCommonSubsequenceDiffUtil.computeDiff(expected, actual).toSet() ==
                [
                        new LongestCommonSubsequenceDiffUtil.Line(1, 'charlie', LongestCommonSubsequenceDiffUtil.Line.Mode.ADD),
                        new LongestCommonSubsequenceDiffUtil.Line(2, 'charlie', LongestCommonSubsequenceDiffUtil.Line.Mode.REMOVE),
                ].toSet()
    }

    @Test
    void testReverseList() {
        def expected = 'alpha,bravo,charlie'.split(',')
        def actual = 'charlie,bravo,alpha'.split(',')

        assert LongestCommonSubsequenceDiffUtil.computeDiff(expected, actual).toSet() ==
                [
                        new LongestCommonSubsequenceDiffUtil.Line(0, 'charlie', LongestCommonSubsequenceDiffUtil.Line.Mode.ADD),
                        new LongestCommonSubsequenceDiffUtil.Line(2, 'charlie', LongestCommonSubsequenceDiffUtil.Line.Mode.REMOVE),
                        /*
                            This next part may be hard to comprehend, but that's how patches work.
                            By itself it doesn't makes much sense to remove bravo from position 1 and
                                add bravo at position 1, but if you look at it as a linked list, it
                                makes more sense.
                         */
                        new LongestCommonSubsequenceDiffUtil.Line(1, 'bravo', LongestCommonSubsequenceDiffUtil.Line.Mode.ADD),
                        new LongestCommonSubsequenceDiffUtil.Line(1, 'bravo', LongestCommonSubsequenceDiffUtil.Line.Mode.REMOVE),
                ].toSet()
    }

    @Test
    void testRemoveFromTopAndAddToBottom() {
        def expected = 'alpha,bravo,charlie'.split(',')
        def actual = 'bravo,charlie,alpha'.split(',')

        assert LongestCommonSubsequenceDiffUtil.computeDiff(expected, actual).toSet() ==
                [
                        new LongestCommonSubsequenceDiffUtil.Line(2, 'alpha', LongestCommonSubsequenceDiffUtil.Line.Mode.ADD),
                        new LongestCommonSubsequenceDiffUtil.Line(0, 'alpha', LongestCommonSubsequenceDiffUtil.Line.Mode.REMOVE),
                ].toSet()
    }

}
