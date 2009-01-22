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
package org.codenarc.rule

import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTest

/**
 * Tests for AbstractAstVisitor
 *
 * @author Chris Mair
 * @version $Revision: 196 $ - $Date: 2009-01-15 19:47:56 -0500 (Thu, 15 Jan 2009) $
 */
class AbstractAstVisitorClassTest extends AbstractTest {

    static final SOURCE = '''class ABC {
            def justReturn() {
                println "about to return"; return "ABC"
            }
            def printVeryLongLine() {
                println "prefix"; if (true) println "1234567890123456789012345678901234567890123456789012345678901234567890"
            }
        }
    '''
    def astVisitor
    def sourceCode
    def rule

    void testSourceLine() {
        def sourceLine = astVisitor.sourceLine(astVisitor.returnStatement)
        log("sourceLine=[$sourceLine]")
        assert sourceLine == 'println "about to return"; return "ABC"'
    }

    void testSourceLine_LineTooLong() {
        def sourceLine = astVisitor.sourceLine(astVisitor.ifStatement)
        log("sourceLine=[$sourceLine]")
        assert sourceLine == 'if (true) println "12345678901234567890..234567890"'
    }

    void testAddViolation() {
        assert astVisitor.violations == []
        astVisitor.addViolation(astVisitor.returnStatement)
        assert astVisitor.violations.size() == 1
        def v = astVisitor.violations[0]
        assert v.sourceLine == 'println "about to return"; return "ABC"'
        assert v.rule == rule
        assert v.lineNumber == 3
    }

    void setUp() {
        super.setUp()
        sourceCode = new SourceString(SOURCE)
        rule = [:] as Rule
        astVisitor = new TestAstVisitor1(rule:rule, sourceCode:sourceCode)
        def ast = sourceCode.ast
        astVisitor.visitClass(ast.classes[0])
    }
}

// Test AstVisitor implementation class
class TestAstVisitor1 extends AbstractAstVisitor {
    def returnStatement
    def ifStatement

    void visitReturnStatement(ReturnStatement returnStatement) {
        this.returnStatement = returnStatement
        super.visitReturnStatement(returnStatement)
    }

    void visitIfElse(IfStatement ifStatement) {
        this.ifStatement = ifStatement
        super.visitIfElse(ifStatement)
    }

}