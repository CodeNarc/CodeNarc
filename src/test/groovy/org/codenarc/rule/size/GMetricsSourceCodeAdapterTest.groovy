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

import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for GMetricsSourceCodeAdapter
 *
 * @author Chris Mair
  */
class GMetricsSourceCodeAdapterTest extends AbstractTestCase {
    private static final SOURCE = 'abc\ndef'

    @Test
    void testConstructor_Null() {
        shouldFailWithMessageContaining('sourceCode') { new GMetricsSourceCodeAdapter(null) }
    }

    @Test
    void testDelegatesMethods() {
        def sourceCode = new SourceString(SOURCE, 'path', 'name')
        def adapter = new GMetricsSourceCodeAdapter(sourceCode)
        assert adapter.name == sourceCode.name
        assert adapter.path == sourceCode.path
        assert adapter.text == sourceCode.text
        assert adapter.valid == sourceCode.valid
        assert adapter.lines == sourceCode.lines
        assert adapter.line(0) == sourceCode.line(0)
        assert adapter.ast == sourceCode.ast
        assert adapter.getLineNumberForCharacterIndex(4) == sourceCode.getLineNumberForCharacterIndex(4)
    }

}
