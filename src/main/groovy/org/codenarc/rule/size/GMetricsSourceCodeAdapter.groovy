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
package org.codenarc.rule.size

import org.codehaus.groovy.ast.ModuleNode
import org.gmetrics.source.SourceCode

/**
 * Adapter that adapts from a GMetrics SourceCode object to a CodeNarc SourceCode object.
 *
 * @author Chris Mair
  */
class GMetricsSourceCodeAdapter implements SourceCode {

    private final codeNarcSourceCode

    GMetricsSourceCodeAdapter(org.codenarc.source.SourceCode sourceCode) {
        assert sourceCode
        codeNarcSourceCode = sourceCode
    }

    String getName() {
        codeNarcSourceCode.name
    }

    String getPath() {
        codeNarcSourceCode.path
    }

    String getText() {
        codeNarcSourceCode.text
    }

    List getLines() {
        codeNarcSourceCode.lines
    }

    String line(int lineNumber) {
        codeNarcSourceCode.line(lineNumber)
    }

    ModuleNode getAst() {
        codeNarcSourceCode.ast
    }

    int getLineNumberForCharacterIndex(int charIndex) {
        codeNarcSourceCode.getLineNumberForCharacterIndex(charIndex)
    }

    boolean isValid() {
        return codeNarcSourceCode.isValid()
    }
}
