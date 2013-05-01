/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.results

import org.codenarc.rule.Rule
import org.codenarc.rule.Violation
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Test for virtual results
 * @author Hamlet D'Arcy
 */
class VirtualResultsTest extends AbstractTestCase {

    @Test
    void testViolations() {
        def violations = [
                new Violation(rule: [getPriority : { 1 } ] as Rule),
                new Violation(rule: [getPriority : { 2 } ] as Rule),
                new Violation(rule: [getPriority : { 2 } ] as Rule),
                new Violation(rule: [getPriority : { 3 } ] as Rule),
                new Violation(rule: [getPriority : { 3 } ] as Rule),
                new Violation(rule: [getPriority : { 3 } ] as Rule),
                new Violation(rule: [getPriority : { 7 } ] as Rule),
        ]
        def results = new VirtualResults(violations)

        assert results.getViolations() == violations

        assert 1 == results.violations.findAll { v -> v.rule.priority == 1 }.size()
        assert 2 == results.violations.findAll { v -> v.rule.priority == 2 }.size()
        assert 3 == results.violations.findAll { v -> v.rule.priority == 3 }.size()
        assert 1 == results.violations.findAll { v -> v.rule.priority == 7 }.size()
    }

    @Test
    void testGetViolations_ReturnsDefensiveCopy() {
        def results = new VirtualResults([new Violation(rule: [getPriority : { 1 } ] as Rule)])
        results.getViolations() << 123
        assert results.getViolations().size() == 1
    }

}
