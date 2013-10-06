/*
 * Copyright 2013 the original author or authors.
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
 * Mock implementation of the Rule interface for testing
 */
class MockRule implements Rule {

    String name
    int priority
    int compilerPhase
    private final Closure applyToClosure

    MockRule(Map parameters) {
        this.name = parameters.name
        this.priority = parameters.priority ?: 0
        this.compilerPhase = parameters.compilerPhase ?: SourceCode.DEFAULT_COMPILER_PHASE
        this.applyToClosure = parameters.applyTo
    }

    @Override
    List applyTo(SourceCode sourceCode) {
        if (applyToClosure) {
            applyToClosure(sourceCode)
        } else {
            throw new UnsupportedOperationException()
        }
    }

}
