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

import org.codenarc.test.AbstractTest

/**
 * Tests for SourceFile 
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SourceFileTest extends AbstractTest {

    static final FILE = 'src/test/resources/SampleFile.groovy'
    private sourceFile
    private file

    void testConstructor_NullPath() {
        shouldFail { new SourceFile(null) }
    }

    void testConstructor_EmptyPath() {
        shouldFail { new SourceFile('') }
    }

    void testGetName() {
        assert sourceFile.getName() == 'SampleFile.groovy'
    }

    void testGetPath() {
        assert sourceFile.getPath() == file.path
    }

    void testGetText() {
        def text = sourceFile.text
        assert text == new File(FILE).text

        // Make sure instance is cached
        assert sourceFile.text.is(text)
    }

    void testGetLines() {
        def lines = sourceFile.lines
        assert lines == ['class SampleFile {', '', '}']

        // Make sure instance is cached
        assert sourceFile.lines.is(lines)
    }

    void testLine() {
        assert sourceFile.line(0) ==  'class SampleFile {'
        assert sourceFile.line(-1) ==  null
        assert sourceFile.line(99) ==  null
    }

    void testGetAst() {
        def ast = sourceFile.ast
        log("classes=${ast.classes}")
        assert ast.classes[0].name == 'SampleFile'

        // Make sure instance is cached
        assert sourceFile.ast.is(ast)
    }

    void setUp() {
        super.setUp()
        file = new File(FILE)
        sourceFile = new SourceFile(file)
    }
}