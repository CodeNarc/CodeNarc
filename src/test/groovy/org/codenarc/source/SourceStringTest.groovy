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
 * Tests for SourceString
 *
 * @author Chris Mair
  */
class SourceStringTest extends AbstractTestCase {

    private static final SOURCE = '''class SampleFile {
            int count
        }'''
    private sourceString

    @Test
    void testImplementsSourceCode() {
        assert sourceString instanceof SourceCode
    }

    @Test
    void testConstructor_NullSource() {
        shouldFail { new SourceString(null) }
    }

    @Test
    void testConstructor_EmptySource() {
        shouldFail { new SourceString('') }
    }

    @Test
    void testConstructor_DefaultPathAndName() {
        assert sourceString.getPath() == null
        assert sourceString.getName() == null
    }

    @Test
    void testConstructor_PathAndName() {
        sourceString = new SourceString(SOURCE, 'Path', 'Name')
        assert sourceString.getPath() == 'Path'
        assert sourceString.getName() == 'Name'
    }

    @Test
    void testNormalizedPath() {
        def originalFileSeparator = System.getProperty('file.separator')
        try {
            System.setProperty('file.separator', '\\')
            sourceString.path = 'abc\\def\\ghi'
            assert sourceString.path == 'abc/def/ghi'

            assert new SourceString('src', '\\abc').path == '/abc'

            System.setProperty('file.separator', '~')
            assert new SourceString('src', '~abc~def').path == '/abc/def'

            System.setProperty('file.separator', '/')
            assert new SourceString('src', '/abc/def').path == '/abc/def'
        }
        finally {
            System.setProperty('file.separator', originalFileSeparator)
        }
    }

    @Test
    void testGetText() {
        def text = sourceString.text
        assert text == SOURCE

        // Make sure instance is cached
        assert sourceString.text.is(text)
    }

    @Test
    void testGetLines() {
        def lines = sourceString.lines
        assert lines == ['class SampleFile {', '            int count', '        }']

        // Make sure instance is cached
        assert sourceString.lines.is(lines)
    }

    @Test
    void testLine() {
        assert sourceString.line(0) ==  'class SampleFile {'
        assert sourceString.line(1) ==  'int count'
        assert sourceString.line(-1) ==  null
        assert sourceString.line(3) ==  null
    }

    @Test
    void testGetAst() {
        def ast = sourceString.ast
        log("classes=${ast.classes}")
        assert ast.classes[0].name == 'SampleFile'

        // Make sure instance is cached
        assert sourceString.ast.is(ast)
    }

    @Test
    void testGetAst_ReferencesClassNotInClasspath() {
        final NEW_SOURCE = '''
            import some.other.pkg.Processor
            class MyClass extends Processor {
                String name
                Processor processor        
            }
        '''
        sourceString = new SourceString(NEW_SOURCE)
        def ast = sourceString.ast
        assert ast.classes[0].name == 'MyClass'
    }

    @Test
    void testGetAst_CompilerErrorInSource() {
        final NEW_SOURCE = '''
            class MyClass {
                try {
                } catch(MyException e) {
                    // TODO Should do something here
                }
            }
        '''
        sourceString = new SourceString(NEW_SOURCE)
        assert sourceString.ast == null
    }

    @Test
    void testGetLineNumberForCharacterIndex() {
        final NEW_SOURCE = '\nclass MyClass { \r\n  try {\n} catch(MyException e) {\n// TODO \n }\n }\n'
        NEW_SOURCE.eachWithIndex { ch, i -> print "$i=${(int)(ch as char)} " }
        sourceString = new SourceString(NEW_SOURCE)
        assert sourceString.getLineNumberForCharacterIndex(0) == 1
        assert sourceString.getLineNumberForCharacterIndex(1) == 2
        assert sourceString.getLineNumberForCharacterIndex(18) == 2
        assert sourceString.getLineNumberForCharacterIndex(19) == 3
        assert sourceString.getLineNumberForCharacterIndex(26) == 3
        assert sourceString.getLineNumberForCharacterIndex(27) == 4
        assert sourceString.getLineNumberForCharacterIndex(51) == 4
        assert sourceString.getLineNumberForCharacterIndex(52) == 5
        assert sourceString.getLineNumberForCharacterIndex(60) == 5
        assert sourceString.getLineNumberForCharacterIndex(61) == 6
        assert sourceString.getLineNumberForCharacterIndex(63) == 6
        assert sourceString.getLineNumberForCharacterIndex(64) == 7
        assert sourceString.getLineNumberForCharacterIndex(66) == 7
        assert sourceString.getLineNumberForCharacterIndex(67) == -1
        assert sourceString.getLineNumberForCharacterIndex(999) == -1
        assert sourceString.getLineNumberForCharacterIndex(-1) == -1
    }

    @Test
    void testIsValid() {
        assert sourceString.valid
        assert !new SourceString('%^#@$').valid
    }

    @Before
    void setUpSourceStringTest() {
        sourceString = new SourceString(SOURCE)
    }

}
