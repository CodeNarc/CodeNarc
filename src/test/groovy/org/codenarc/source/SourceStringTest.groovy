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
 * Tests for SourceString
 *
 * @author Chris Mair
 * @version $Revision: 201 $ - $Date: 2009-01-16 18:42:48 -0500 (Fri, 16 Jan 2009) $
 */
class SourceStringTest extends AbstractTest {

    static final SOURCE = 'class SampleFile {\n\n }'
    private sourceString

    void testConstructor_NullSource() {
        shouldFail { new SourceString(null) }
    }

    void testConstructor_EmptySource() {
        shouldFail { new SourceString('') }
    }

    void testConstructor_DefaultPathAndName() {
        assert sourceString.getPath() == null
        assert sourceString.getName() == null
    }

    void testConstructor_PathAndName() {
        sourceString = new SourceString(SOURCE, 'Path', 'Name')
        assert sourceString.getPath() == 'Path'
        assert sourceString.getName() == 'Name'
    }

    void testGetText() {
        def text = sourceString.text
        assert text == SOURCE

        // Make sure instance is cached
        assert sourceString.text.is(text)
    }

    void testGetLines() {
        def lines = sourceString.lines
        assert lines == ['class SampleFile {', '', ' }']

        // Make sure instance is cached
        assert sourceString.lines.is(lines)
    }

    void testGetAst() {
        def ast = sourceString.ast
        log("classes=${ast.classes}")
        assert ast.classes[0].name == 'SampleFile'

        // Make sure instance is cached
        assert sourceString.ast.is(ast)
    }

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

    void setUp() {
        super.setUp()
        sourceString = new SourceString(SOURCE)
    }
}