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
package org.codenarc.source

import org.codehaus.groovy.control.Phases
import org.codenarc.analyzer.SuppressionAnalyzer

/**
 * A {@link SourceCode} decorator overriding the decorated source's returned AST
 * so that it meets the user's compilerPhase requirements.
 *
 * Ensures that the compiler phase is before classes are output to disk.
 *
 * @author Artur Gajowy
 */
class CustomCompilerPhaseSourceDecorator extends AbstractSourceCode {

    private final SourceCode delegate
    private final int compilerPhase

    CustomCompilerPhaseSourceDecorator(SourceCode delegate, int compilerPhase) {
        assert delegate
        this.delegate = delegate
        assert compilerPhase < Phases.OUTPUT
        this.compilerPhase = compilerPhase
    }

    @Override
    int getAstCompilerPhase() {
        compilerPhase
    }

    @Override
    String getName() {
        delegate.name
    }

    @Override
    String getPath() {
        delegate.path
    }

    @Override
    String getText() {
        delegate.text
    }

    @Override
    SuppressionAnalyzer getSuppressionAnalyzer() {
        delegate.suppressionAnalyzer
    }

    @Override
    String toString() {
        "CustomCompilerPhaseSourceDecorator[${delegate}]; phase=${compilerPhase} (${Phases.getDescription(compilerPhase)})"
    }
}
