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

    void testAverageAbcVectorForNoVectorsIsZeroVector() {
        assertEquals(abcAggregateMetricResults.averageAbcVector, [0, 0, 0])
    }

    void testTotalAbcVectorForNoVectorsIsZeroVector() {
        assertEquals(abcAggregateMetricResults.totalAbcVector, [0, 0, 0])
    }

    void testAverageValueForNoVectorsIsZero() {
        assert abcAggregateMetricResults.totalValue == 0
    }

    void testTotalValueForNoVectorsIsZero() {
        assert abcAggregateMetricResults.totalValue == 0
    }

    void testNumberOfChildrenForNoVectorsIsZero() {
        assert abcAggregateMetricResults.numberOfChildren == 0
    }

    void testAverageAbcVectorForSingleVectorIsThatVector() {
        abcAggregateMetricResults.add('x', abcMetricResult(7, 9, 21))
        assertEquals(abcAggregateMetricResults.averageAbcVector, [7, 9, 21])
    }

    void testTotalAbcVectorForSingleVectorIsThatVector() {
        abcAggregateMetricResults.add('x', abcMetricResult(7, 9, 21))
        assertEquals(abcAggregateMetricResults.totalAbcVector, [7, 9, 21])
    }

    void testCorrectRoundedAverageForSeveralVectors() {
        addThreeAbcMetricResults()
        assertEquals(abcAggregateMetricResults.averageAbcVector, [9, 4, 22])     // A and C are rounded down
    }

    void testCorrectTotalAbcVectorForSeveralVectors() {
        addThreeAbcMetricResults()
        assertEquals(abcAggregateMetricResults.totalAbcVector, [27, 12, 66])
    }

    void testTotalValueForSeveralVectorsIsTheMagnitudeOfTheSumOfTheVectors() {
        addThreeAbcMetricResults()
        assert abcAggregateMetricResults.totalValue == new AbcVector(27, 12, 66).magnitude
    }

    void testAverageValueForSeveralVectorsIsTheMagnitudeOfTheAverageOfTheVectors() {
        addThreeAbcMetricResults()
        assert abcAggregateMetricResults.averageValue == new AbcVector(9, 4, 22).magnitude
    }

    void testCorrectNumberOfChildrenForSeveralVectors() {
        addThreeAbcMetricResults()
        assert abcAggregateMetricResults.numberOfChildren == 3
    }

    void setUp() {
        super.setUp()
        abcAggregateMetricResults = new AbcAggregateMetricResults()
    }

    private void addThreeAbcMetricResults() {
        abcAggregateMetricResults.add('x', abcMetricResult(7, 9, 21))
        abcAggregateMetricResults.add('y', abcMetricResult(11, 1, 21))
        abcAggregateMetricResults.add('z', abcMetricResult(9, 2, 24))
    }
}