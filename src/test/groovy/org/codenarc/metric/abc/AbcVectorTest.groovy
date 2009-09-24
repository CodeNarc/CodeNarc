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
 * Tests for AbcVector
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcVectorTest extends AbstractAbcTest {

    void testPassingNegativeAssignmentsIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('assignments') { new AbcVector(-1, 0, 0) } 
    }

    void testPassingNegativeBranchesIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('branches') { new AbcVector(0, -1, 0) } 
    }

    void testPassingNegativeConditionsIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('conditions') { new AbcVector(0, 0, -1) }
    }

    void testValueForEmptyVectorSetIsZero() {
        assert abcVectorMagnitude(0, 0, 0) == 0
    }

    void testVectorWithIntegerResultValue() {
        assert abcVectorMagnitude(1, 2, 2) == 3
    }

    void testVectorWithNonIntegerResultValue() {
        assert abcVectorMagnitude(7, 1, 2) == 7.3
    }

    void testVectorWithOnlyAssignmentValueIsThatValue() {
        assert abcVectorMagnitude(7, 0, 0) == 7
    }

    void testVectorWithOnlyBranchValueIsThatValue() {
        assert abcVectorMagnitude(0, 7, 0) == 7
    }

    void testVectorWithOnlyConditionalValueIsThatValue() {
        assert abcVectorMagnitude(0, 0, 7) == 7
    }

    private abcVectorMagnitude(int a, int b, int c) {
        def abcVector = new AbcVector(a, b, c)
        log(abcVector)
        return abcVector.getMagnitude()
    }
    
}