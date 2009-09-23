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
 * Tests for AbcAggregateMetricResults
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcAggregateMetricResultsTest extends AbstractAbcTest {

    private abcAggregateMetricResults

    void testAverageForEmptyVectorSetIsZeroVector() {
        assertEquals(abcAggregateMetricResults.averageAbcVector, [0, 0, 0])
    }

    void testSumForEmptyVectorSetIsZeroVector() {
        assertEquals(abcAggregateMetricResults.totalAbcVector, [0, 0, 0])
    }

    void testNumberOfAbcVectorsForEmptyVectorSetIsZero() {
        assert abcAggregateMetricResults.numberOfChildren == 0
    }

    void testAverageForSingleVectorIsThatVector() {
        abcAggregateMetricResults.add('x', abcMetricResult(7, 9, 21))
        assertEquals(abcAggregateMetricResults.averageAbcVector, [7, 9, 21])
    }

    void testSumForSingleVectorIsThatVector() {
        abcAggregateMetricResults.add('x', abcMetricResult(7, 9, 21))
        assertEquals(abcAggregateMetricResults.totalAbcVector, [7, 9, 21])
    }

    void testCorrectRoundedAverageForSeveralVectors() {
        abcAggregateMetricResults.add('x', abcMetricResult(7, 9, 21))
        abcAggregateMetricResults.add('y', abcMetricResult(12, 1, 21))
        abcAggregateMetricResults.add('z', abcMetricResult(10, 2, 25))
        assertEquals(abcAggregateMetricResults.averageAbcVector, [9, 4, 22])     // A and C are rounded down
    }

    void testCorrectTotalForSeveralVectors() {
        abcAggregateMetricResults.add('x', abcMetricResult(7, 9, 21))
        abcAggregateMetricResults.add('y', abcMetricResult(11, 1, 21))
        abcAggregateMetricResults.add('z', abcMetricResult(9, 2, 24))
        assertEquals(abcAggregateMetricResults.totalAbcVector, [27, 12, 66])
        assert abcAggregateMetricResults.numberOfChildren == 3
    }

    void setUp() {
        super.setUp()
        abcAggregateMetricResults = new AbcAggregateMetricResults()
    }

    private AbcMetricResult abcMetricResult(int a, int b, int c) {
        def abcVector = new AbcVector(a, b, c)
        return new AbcMetricResult(abcVector:abcVector)
    }
    
}