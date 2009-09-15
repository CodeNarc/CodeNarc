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
 * Represents the aggregate of zero or more ABC Metric results.
 *
 * @author Chris Mair
 * @version $Revision: 120 $ - $Date: 2009-04-06 12:58:09 -0400 (Mon, 06 Apr 2009) $
 */
class AbcVectorAggregate {
    final numberOfAbcVectors = 0
    private assignmentSum = 0
    private branchSum = 0
    private conditionSum = 0

    void add(AbcVector abcVector) {
        assignmentSum += abcVector.assignments
        branchSum += abcVector.branches
        conditionSum += abcVector.conditions
        numberOfAbcVectors++
    }

    /**
     * Return the average of this set of ABC vectors. Each component (A,B,C) of the result
     * is calculated and averaged separately. The formula for each component is:
     *      (A1 + A2 + .. AN) / N
     * and likewise for B and C values. Each component of the result vector is rounded down to an integer.
     */
    AbcVector getAverageAbcVector() {
        def a = average(assignmentSum, numberOfAbcVectors)
        def b = average(branchSum, numberOfAbcVectors)
        def c = average(conditionSum, numberOfAbcVectors)
        return new AbcVector(a, b, c)
    }

    /**
     * Return the sum of this set of ABC vectors. Each component (A,B,C) of the result
     * is summed separately. The formula for each component is:
     *      A1 + A2 + .. AN
     * and likewise for B and C values.
     */
    AbcVector getSumAbcVector() {
        return new AbcVector(assignmentSum, branchSum, conditionSum)
    }

    String toString() {
        "AbcVectorAggregate[numVectors=$numberOfAbcVectors, A=$assignmentSum, B=$branchSum, C=$conditionSum]"
    }

    private average(int sum, int count) {
        return sum && count ? sum / count as Integer : 0
    }

}