/*
 * Copyright 2011 the original author or authors.
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
 package org.codenarc.util

import org.codenarc.test.AbstractTestCase
import org.codenarc.source.SourceString

/**
 * Tests for ImportUtil
 *
 * @author Chris Mair
 */
class ImportUtilTest extends AbstractTestCase {

    void testSourceLineAndNumberForImport() {
        final SOURCE = '''
            import a.b.MyClass
            import a.b.MyClass as Boo
            // some comment
            import a.pkg1.MyOtherClass as MOC
        '''
        def sourceCode = new SourceString(SOURCE)
        def ast = sourceCode.ast

        assertImport(sourceCode, ast, [sourceLine:'import a.b.MyClass', lineNumber:2])
        assertImport(sourceCode, ast, [sourceLine:'import a.b.MyClass as Boo', lineNumber:3])
        assertImport(sourceCode, ast, [sourceLine:'import a.pkg1.MyOtherClass as MOC', lineNumber:5])

        // Not found
        def otherSourceCode = new SourceString('def v = 1')
        assertImport(otherSourceCode, ast, [sourceLine:'import a.b.MyClass as MyClass', lineNumber:null])
    }

    void testSourceLineAndNumberForImport_ClassNameAndAlias() {
        final SOURCE = '''
            import a.b.MyClass
            import a.b.MyClass as Boo
            // some comment
            import a.pkg1.MyOtherClass as MOC
        '''
        def sourceCode = new SourceString(SOURCE)
        assert ImportUtil.sourceLineAndNumberForImport(sourceCode, 'a.b.MyClass', 'MyClass') == [sourceLine:'import a.b.MyClass', lineNumber:2]
        assert ImportUtil.sourceLineAndNumberForImport(sourceCode, 'a.b.MyClass', 'Boo') == [sourceLine:'import a.b.MyClass as Boo', lineNumber:3]
        assert ImportUtil.sourceLineAndNumberForImport(sourceCode, 'a.pkg1.MyOtherClass', 'MOC') == [sourceLine:'import a.pkg1.MyOtherClass as MOC', lineNumber:5]

        // Not found
        def otherSourceCode = new SourceString('def v = 1')
        assert ImportUtil.sourceLineAndNumberForImport(otherSourceCode, 'a.b.MyClass', 'MyClass') == [sourceLine:'import a.b.MyClass as MyClass', lineNumber:null]
    }

    private void assertImport(sourceCode, ast, Map importInfo) {
        assert ast.imports.find { imp ->
            ImportUtil.sourceLineAndNumberForImport(sourceCode, imp) == importInfo
        }
    }


}
