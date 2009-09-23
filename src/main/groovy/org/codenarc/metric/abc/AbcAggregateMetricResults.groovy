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

import org.codenarc.metric.AggregateMetricResults
import org.codenarc.metric.MetricResult

/**
 * A AggregateMetricResults implementation specifically for the ABC Metric.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcAggregateMetricResults implements AggregateMetricResults {
    final Map children = [:]
    private assignmentSum = 0
    private branchSum = 0
    private conditionSum = 0
    
    void add(String name, MetricResult metricResult) {
        children[name] = metricResult
        def abcVector = metricResult.abcVector
        assignmentSum += abcVector.assignments
        branchSum += abcVector.branches
        conditionSum += abcVector.conditions
    }

    int getNumberOfChildren() {
        return children.size()
    }

    /**
     * Return the sum of this set of ABC vectors. Each component (A,B,C) of the result
     * is summed separately. The formula for each component is:
     *      A1 + A2 + .. AN
     * and likewise for B and C values.
     */
    Object getTotalAbcVector() {
        return new AbcVector(assignmentSum, branchSum, conditionSum)
    }

    /**
     * Return the average of this set of ABC vectors. Each component (A,B,C) of the result
     * is calculated and averaged separately. The formula for each component is:
     *      (A1 + A2 + .. AN) / N
     * and likewise for B and C values. Each component of the result vector is rounded down to an integer.
     */
    Object getAverageAbcVector() {
        def numberOfAbcVectors = getNumberOfChildren()
        def a = average(assignmentSum, numberOfAbcVectors)
        def b = average(branchSum, numberOfAbcVectors)
        def c = average(conditionSum, numberOfAbcVectors)
        return new AbcVector(a, b, c)
    }

    Object getTotalValue() {
        return null
    }

    Object getAverageValue() {
        return null
    }

    String toString() {
        "AbcAggregateMetricResults[numVectors=${getNumberOfChildren()}, " +
            "A=$assignmentSum, B=$branchSum, C=$conditionSum, children=$children]"
    }

    private average(int sum, int count) {
        return sum && count ? sum / count as Integer : 0
    }

}