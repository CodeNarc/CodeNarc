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

import org.codehaus.groovy.ast.ModuleNode
import org.codenarc.source.SourceCode
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for ImportUtil
 *
 * @author Chris Mair
 */
class ImportUtilTest extends AbstractTestCase {

    @Test
    void testGetAllImports() {
        final SOURCE = '''
            import java.lang.String;
            import java.net.*;

            import static java.lang.Math.*;
            import static org.other.OtherUtil.doStuff;
        '''
        def sourceCode = new SourceString(SOURCE)
        def allImports = ImportUtil.getAllImports(sourceCode)

        allImports.each { node -> println "${node.text} -- ${node.lineNumber}" }

        assert allImports.find { node -> node.text.contains('java.lang.String') && node.lineNumber == 2 }
        assert allImports.find { node -> node.text.contains('import java.net.*') && node.lineNumber == 3 }
        assert allImports.find { node -> node.text.contains('import static java.lang.Math.*') && node.lineNumber == 5 && node.isStatic() }
        assert allImports.find { node -> node.text.contains('org.other.OtherUtil.doStuff') && node.lineNumber == 6 && node.isStatic() }
    }

    @Test
    void testGetAllImports_NoImports() {
        final SOURCE = '''
            println 123
        '''
        def sourceCode = new SourceString(SOURCE)
        def allImports = ImportUtil.getAllImports(sourceCode)
        assert allImports == []
    }

    @Test
    void testGetAllImports_InvalidSource_NoAst() {
        final SOURCE = '''
            @will not compile@ &^%$#
        '''
        def sourceCode = new SourceString(SOURCE)
        def allImports = ImportUtil.getAllImports(sourceCode)
        assert allImports == []
    }

    @Test
    void testSourceLineAndNumberForImport() {
        final SOURCE = '''
            import a.b.MyClass
            import a.b.MyClass as Boo
            // some comment
            import a.pkg1.MyOtherClass as MOC
            import a.b.MyOtherClass;
            import a.b.MyOtherClass as Moo;
        '''
        def sourceCode = new SourceString(SOURCE)
        def ast = sourceCode.ast

        assertImport(sourceCode, ast, [sourceLine:'import a.b.MyClass', lineNumber:2])
        assertImport(sourceCode, ast, [sourceLine:'import a.b.MyClass as Boo', lineNumber:3])
        assertImport(sourceCode, ast, [sourceLine:'import a.pkg1.MyOtherClass as MOC', lineNumber:5])
        assertImport(sourceCode, ast, [sourceLine:'import a.b.MyOtherClass;', lineNumber:6])
        assertImport(sourceCode, ast, [sourceLine:'import a.b.MyOtherClass as Moo;', lineNumber:7])

        // Not found in source code; AST still contains import nodes
        def otherSourceCode = new SourceString('def v = 1')
        assertImport(otherSourceCode, ast, [sourceLine:'import a.b.MyClass as MyClass', lineNumber:2])
    }

    @Test
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

        // Not found in source code; AST still contains import nodes
        def otherSourceCode = new SourceString('def v = 1')
        assert ImportUtil.sourceLineAndNumberForImport(otherSourceCode, 'a.b.MyClass', 'MyClass') == [sourceLine:'import a.b.MyClass as MyClass', lineNumber:null]
    }

    @Test
    void testSourceLineAndNumberForImport_StaticImport() {
        final SOURCE = '''
            import static java.io.DataInputStream.*
            import static java.lang.Integer.MAX_VALUE
        '''
        def sourceCode = new SourceString(SOURCE)
        def ast = sourceCode.ast

        assertStaticImport(sourceCode, ast, [sourceLine:'import static java.io.DataInputStream.*', lineNumber:2])
        assertStaticImport(sourceCode, ast, [sourceLine:'import static java.lang.Integer.MAX_VALUE', lineNumber:3])
    }

    @Test
    void testSourceLineAndNumberForImport_SimilarlyNamedImports() {
        final SOURCE = '''
            import static com.example.FaultMessages.*
            import com.example.FaultCode.*
            import com.example.Fault
        '''
        def sourceCode = new SourceString(SOURCE)
        assert ImportUtil.sourceLineAndNumberForImport(sourceCode, 'com.example.Fault', 'Fault') ==
            [sourceLine:'import com.example.Fault', lineNumber:4]
    }

    private void assertImport(SourceCode sourceCode, ModuleNode ast, Map expectedImportInfo) {
        assert ast.imports.find { imp ->
            def importInfo = ImportUtil.sourceLineAndNumberForImport(sourceCode, imp)
            if (importInfo.lineNumber == expectedImportInfo.lineNumber) {
                log(importInfo)
            }
            importInfo == expectedImportInfo
        }, expectedImportInfo.toString()
    }

    private void assertStaticImport(SourceCode sourceCode, ModuleNode ast, Map importInfo) {
        def allStaticImports = ast.staticImports + ast.staticStarImports
        assert allStaticImports.find { name, imp ->
            ImportUtil.sourceLineAndNumberForImport(sourceCode, imp) == importInfo
        }
    }

}
