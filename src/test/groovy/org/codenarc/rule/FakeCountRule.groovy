/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.rule

import org.codenarc.source.SourceCode

/**
 * Test-specific Rule implementation that maintains count of how many times it has been applied.
 *
 * @author Chris Mair
 */
class FakeCountRule extends AbstractRule {
    String name = 'FakeCountRule'
    int priority = 2
    int count = 0

    /**
     * Increment count and add no violations
     * @param sourceCode - the sourceCode to which the rule is applied
     */
    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        count ++
    }
}
