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
 * Represents a single ABC Metric result: a vector of the three A, B, C values.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcVector {
    final int assignments
    final int branches
    final int conditions

    AbcVector(int assignments, int branches, int conditions) {
        assert assignments >= 0
        assert branches >= 0
        assert conditions >= 0
        this.assignments = assignments
        this.branches = branches
        this.conditions = conditions
    }

    /**
     * Return the magnitude of this ABC vector, specifically:
     *         |ABC| = sqrt((A*A)+(B*B)+(C*C))
     * @return the magnitude of the ABC vector as a BigDecimal with scale of 1
     */
    BigDecimal getMagnitude() {
        def sumOfSquares = squared(assignments) + squared(branches) + squared(conditions)
        def result = Math.sqrt(sumOfSquares)
        return new BigDecimal(result).setScale(1, BigDecimal.ROUND_HALF_DOWN)
    }

    String toString() {
        "<$assignments, $branches, $conditions>"
    }

    private int squared(int val) {
        return val * val
    }

}