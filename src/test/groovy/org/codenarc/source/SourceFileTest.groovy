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
package org.codenarc.source

import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFail

/**
 * Tests for SourceFile 
 *
 * @author Chris Mair
  */
class SourceFileTest extends AbstractTestCase {

    private static final FILE = 'src/test/resources/SampleFile.groovy'
    private static final INVALID_FILE = 'src/test/resources/SampleInvalidFile.txt'
    private sourceFile
    private file

    @Test
    void testImplementsSourceCode() {
        assert sourceFile instanceof SourceCode
    }

    @Test
    void testConstructor_NullPath() {
        shouldFail { new SourceFile(null) }
    }

    @Test
    void testConstructor_EmptyPath() {
        shouldFail { new SourceFile('') }
    }

    @Test
    void testGetName() {
        assert sourceFile.getName() == 'SampleFile.groovy'
    }

    @Test
    void testGetPath() {
        log("path=${sourceFile.path}") 
        assert sourceFile.getPath() == FILE
    }

    @Test
    void testGetText() {
        def text = sourceFile.text
        assert text == new File(FILE).text

        // Make sure instance is cached
        assert sourceFile.text.is(text)
    }

    @Test
    void testGetLines() {
        def lines = sourceFile.lines
        assert lines == ['class SampleFile {', '', '}']

        // Make sure instance is cached
        assert sourceFile.lines.is(lines)
    }

    @Test
    void testLine() {
        assert sourceFile.line(0) ==  'class SampleFile {'
        assert sourceFile.line(-1) ==  null
        assert sourceFile.line(99) ==  null
    }

    @Test
    void testGetAst() {
        def ast = sourceFile.ast
        log("classes=${ast.classes}")
        assert ast.classes[0].name == 'SampleFile'

        // Make sure instance is cached
        assert sourceFile.ast.is(ast)
    }

    @Test
    void testGetLineNumberForCharacterIndex() {
        assert sourceFile.getLineNumberForCharacterIndex(0) == 1
        assert sourceFile.getLineNumberForCharacterIndex(1) == 1
        assert sourceFile.getLineNumberForCharacterIndex(21) == 2
        assert sourceFile.getLineNumberForCharacterIndex(999) == -1
        assert sourceFile.getLineNumberForCharacterIndex(-1) == -1
    }
    
    @Test
    void testIsValid() {
        assert sourceFile.valid
        assert !new SourceFile(new File(INVALID_FILE)).valid
    }

    @Before
    void setUpSourceFileTest() {
        file = new File(FILE)
        sourceFile = new SourceFile(file)
    }
}
