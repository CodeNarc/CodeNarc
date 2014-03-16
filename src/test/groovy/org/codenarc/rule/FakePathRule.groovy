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
 * Test-specific Rule implementation that always returns one or more violation whose messages are
 * equal to the source file path. Set <code>numberOfViolations</code> to generate more than one violation.
 *
 * @author Chris Mair
 */
class FakePathRule extends AbstractRule {
    String name = 'TestPath'
    int priority = 1
    int numberOfViolations = 1

    /**
     * Always add a single violation whose message is equal to the source file path
     * @param sourceCode - the sourceCode to which the rule is applied
     */
    void applyTo(SourceCode sourceCode, List violations) {
        def message = sourceCode.path?.replaceAll('\\\\', '/')
        numberOfViolations.times { violations << new Violation(rule:this, message:message) }
    }

}
