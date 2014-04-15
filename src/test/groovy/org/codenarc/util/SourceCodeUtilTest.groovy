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
package org.codenarc.util

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.SourceUnit
import org.codenarc.source.SourceCode
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFail

/**
 * @author Marcin Erdmann
 */
class SourceCodeUtilTest extends AbstractTestCase {

    private static final String SOURCE = '''
        [1, 2, 3].each { println it }
        [1, 2, 3].any {
            it > 3
        }
    '''

    private SourceCode sourceCode
    private SourceCodeUtilTestVisitor visitor

    @Before
    void setUpSourceCodeUtilTest() {
        sourceCode = new SourceString(SOURCE)
    }

    private applyVisitor() {
        visitor = new SourceCodeUtilTestVisitor()
        visitor.visitClass(sourceCode.ast.scriptClassDummy)
    }

    @Test
    void testSourceLinesBetweenForSingleLine() {
        assert SourceCodeUtil.sourceLinesBetween(sourceCode, 2, 19, 2, 23) == ['each']
        assert SourceCodeUtil.sourceLinesBetween(sourceCode, 2, 9, 2, 18) == ['[1, 2, 3]']
    }

    @Test
    void testSourceLinesBetweenForMultiLine() {
        def lines = SourceCodeUtil.sourceLinesBetween(sourceCode, 3, 23, 5, 10)
        assert lines.size() == 3
        assert lines[0] == '{'
        assert lines[1] == '            it > 3'
        assert lines[2] == '        }'
    }

    @Test
    void testIfExceptionIsThrownWhenParamsAreWrong() {
        assert shouldFail(IllegalArgumentException) {
            SourceCodeUtil.sourceLinesBetween(sourceCode, 0, 1, 1, 1)
        } == 'Start and end indexes are one based and have to be greater than zero'

        assert shouldFail(IllegalArgumentException) {
            SourceCodeUtil.sourceLinesBetween(sourceCode, 1, 4, 1, 2)
        } == 'End line/column has to be after start line/column'

        assert shouldFail(IllegalArgumentException) {
            SourceCodeUtil.sourceLinesBetween(sourceCode, 3, 1, 2, 5)
        } == 'End line/column has to be after start line/column'
    }

    @Test
    void testNodeSourceLines() {
        applyVisitor()
        def lines = SourceCodeUtil.nodeSourceLines(sourceCode, visitor.methodCalls['each'])
        assert lines == ['[1, 2, 3].each { println it }']

        lines = SourceCodeUtil.nodeSourceLines(sourceCode, visitor.methodCalls['any'])
        assert lines.size() == 3
        assert lines[0] == '[1, 2, 3].any {'
        assert lines[1] == '            it > 3'
        assert lines[2] == '        }'
    }

    @Test
    void testSourceLinesBetweenNodes() {
        applyVisitor()
        def lines = SourceCodeUtil.sourceLinesBetweenNodes(sourceCode, visitor.methodCalls['each'].method, visitor.methodCalls['any'])
        assert lines.size() == 2
        assert lines[0] == ' { println it }'
        assert lines[1] == '        '

        lines = SourceCodeUtil.sourceLinesBetweenNodes(sourceCode, visitor.methodCalls['each'], visitor.methodCalls['any'])
        assert lines.size() == 2
        assert lines[0] == ''
        assert lines[1] == '        '
    }
}

class SourceCodeUtilTestVisitor extends ClassCodeVisitorSupport {

    final SourceUnit sourceUnit = null

    def methodCalls = [:]

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        super.visitMethodCallExpression(call)
        methodCalls[call.methodAsString] = call
    }
}
