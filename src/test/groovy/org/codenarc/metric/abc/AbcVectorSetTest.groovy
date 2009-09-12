/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.metric.abc

/**
 * Tests for AbcVectorSet
 *
 * @author Chris Mair
 * @version $Revision: 120 $ - $Date: 2009-04-06 12:58:09 -0400 (Mon, 06 Apr 2009) $
 */
class AbcVectorSetTest extends AbstractAbcTest {
    private abcVectorSet

    void testAverageForEmptyVectorSetIsZeroVector() {
        assertEquals(abcVectorSet.averageAbcVector, [0, 0, 0])
    }

    void testSumForEmptyVectorSetIsZeroVector() {
        assertEquals(abcVectorSet.sumAbcVector, [0, 0, 0]) 
    }

    void testNumberOfAbcVectorsForEmptyVectorSetIsZero() {
        assert abcVectorSet.numberOfAbcVectors == 0
    }

    void testAverageForSingleVectorIsThatVector() {
        abcVectorSet.add(new AbcVector(7, 9, 21))
        assertEquals(abcVectorSet.averageAbcVector, [7, 9, 21])
    }

    void testSumForSingleVectorIsThatVector() {
        abcVectorSet.add(new AbcVector(7, 9, 21))
        assertEquals(abcVectorSet.sumAbcVector, [7, 9, 21])
    }

    void testCorrectRoundedAverageForSeveralVectors() {
        abcVectorSet.add(new AbcVector(7, 9, 21))
        abcVectorSet.add(new AbcVector(12, 1, 21))
        abcVectorSet.add(new AbcVector(10, 2, 25))
        assertEquals(abcVectorSet.averageAbcVector, [9, 4, 22])     // A and C are rounded down
    }

    void testCorrectSumForSeveralVectors() {
        abcVectorSet.add(new AbcVector(7, 9, 21))
        abcVectorSet.add(new AbcVector(11, 1, 21))
        abcVectorSet.add(new AbcVector(9, 2, 24))
        assertEquals(abcVectorSet.sumAbcVector, [27, 12, 66])
        assert abcVectorSet.numberOfAbcVectors == 3
    }

    void setUp() {
        super.setUp()
        abcVectorSet = new AbcVectorSet()
    }
}