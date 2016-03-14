/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.rule.formatting

import groovy.transform.CompileStatic
import org.apache.log4j.Logger
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.BytecodeExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Checks if indents are proper
 *
 * @author Rahul Somasunderam
 */
@CompileStatic
class IndentationRule extends AbstractRule {

    private static final Logger LOG = Logger.getLogger(IndentationRule.class)

    String name = 'Indentation'
    int priority = 3

    boolean useTabs = false
    int indentSize = 4
    int continuationIndentSize = 8
    int tabSize = 4

    @Override
    public void applyTo(SourceCode sourceCode, List<Violation> violations) {

        ModuleNode ast = sourceCode.getAst()
        if (!ast) {
            return
        }
        def visitor = new IndentationRuleAstVisitor(this, sourceCode, sourceCode.lines.size())

        ast.classes.each { ClassNode it -> visitor.visitClass(it) }
        ast.methods.each { MethodNode it -> visitor.visitMethod(it) }
        ast.statementBlock.each { BlockStatement it -> visitor.visitBlockStatement(it) }

        def expectedIndents = visitor.expectedIndents

        def lines = sourceCode.lines
        for (int idx = 0; idx < sourceCode.lines.size(); idx ++) {
            String line = lines[idx]
            if (line) {
                LOG.info "${expectedIndents[idx].toString().padLeft(3)}>$line"
                def indent = line.trim() ? ((line =~ /^([ \t]*)\S.*/)[0] as List<String>)[1] : line
                if (useTabs) {
                    if (indent.contains(' ')) {
                        violations << createViolation(idx + 1, line, 'Tabs were expected for indents. Spaces were found')
                        indent = indent.replace(' ' * tabSize, '\t')
                    }
                } else {
                    if (indent.contains('\t')) {
                        violations << createViolation(idx + 1, line, 'Spaces were expected for indents. Tabs were found')
                        indent = indent.replace('\t', ' ' * tabSize)
                    }
                }
                if (indent.length() != expectedIndents[idx]) {
                    violations << createViolation(idx + 1, line, "Expected indent ${expectedIndents[idx]} characters. Actual was ${indent.length()} characters.")
                }
            }
        }
    }
}

@CompileStatic
class IndentationRuleAstVisitor extends AbstractAstVisitor {

    private static final Logger LOG = Logger.getLogger(IndentationRuleAstVisitor.class)

    IndentationRuleAstVisitor(IndentationRule theRule, SourceCode sourceCode, int size) {
        this.rule = theRule
        this.sourceCode = sourceCode
        this.expectedIndents = new int[size]
    }

    @Lazy
    IndentationRule theRule = getRule() as IndentationRule

    int[] expectedIndents;

    void indent(ASTNode node, int level, boolean braces) {
        int start = node.lineNumber + 1
        int stop = braces ? node.lastLineNumber - 1 : node.lastLineNumber
        if (stop >= start && stop > 0 && start > 0) {
            indent(node, level, start, stop)
        }
    }

    void indent(ASTNode node, int level, int start, int stop) {
        LOG.info "Indenting $node ($start..$stop) by $level"
        (start..stop).each { int it ->
            expectedIndents[it - 1] += level
        }
    }

    private void debug(String name, ASTNode obj) {
        if (!obj || obj.lineNumber < 1) {
            return
        }
        def debugIndent = theRule.useTabs ?
                expectedIndents[obj.lineNumber - 1] :
                expectedIndents[obj.lineNumber - 1] / (theRule.indentSize)
        def prefix = "${'  ' * debugIndent}$name".toString().padRight(30)
        def lines = "${obj.lineNumber}c${obj.columnNumber}..${obj.lastLineNumber}c${obj.lastColumnNumber}".
                toString().padRight(15)

        def objRender = obj.toString()
                .replace('org.codehaus.groovy.ast.stmt.', '')
                .replaceAll(/@[0-9a-f]+/, '')
        LOG.info "$prefix|$lines|${objRender}"
    }

    @Override
    protected void visitClassEx(ClassNode node) {
        debug('ClassEx', node)

        def start = node.lineNumber
        def end = node.lastLineNumber
        if (!node.annotations.isEmpty()) {
            start = node.annotations.last().lastLineNumber + 1
        }
        if (!node.isScript()) {
            if (start < end - 1) {
                indent(node, theRule.indentSize, start + 1, end - 1)
            }
        }
        super.visitClassEx(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        debug('MethodEx', node)
        if (node.getFirstStatement()) {
            def lastLine = node.getLastLineNumber()
            def firstLine = node.getFirstStatement().lineNumber
            if (firstLine < lastLine) {
                indent(node, theRule.indentSize, firstLine, lastLine - 1)
            }
        }
        super.visitMethodEx(node)
    }

    @Override
    protected void visitObjectInitializerStatements(ClassNode node) {
        debug('ObjectInitializerStatements', node)
        super.visitObjectInitializerStatements(node)
    }

    @Override
    void visitPackage(PackageNode node) {
        debug('Package', node)
        super.visitPackage(node)
    }

    @Override
    void visitImports(ModuleNode node) {
        debug('Imports', node)
        super.visitImports(node)
    }

    @Override
    void visitAnnotations(AnnotatedNode node) {
        debug('Annotations', node)
        super.visitAnnotations(node)
    }

    @Override
    protected void visitClassCodeContainer(Statement code) {
        debug('ClassCodeContainer', code)
        super.visitClassCodeContainer(code)
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        debug('DeclarationExpression', expression)
        super.visitDeclarationExpression(expression)
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        debug('ConstructorOrMethod', node)
        super.visitConstructorOrMethod(node, isConstructor)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        debug('Constructor', node)
        super.visitConstructor(node)
    }

    @Override
    void visitField(FieldNode node) {
        debug('Field', node)
        super.visitField(node)
    }

    @Override
    void visitProperty(PropertyNode node) {
        debug('Property', node)
        super.visitProperty(node)
    }

    @Override
    protected void visitStatement(Statement statement) {
        debug('Statement', statement)
        super.visitStatement(statement)
    }

    @Override
    void visitAssertStatement(AssertStatement statement) {
        debug('AssertStatement', statement)
        super.visitAssertStatement(statement)
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        debug('BlockStatement', block)
        super.visitBlockStatement(block)
    }

    @Override
    void visitBreakStatement(BreakStatement statement) {
        debug('BreakStatement', statement)
        super.visitBreakStatement(statement)
    }

    @Override
    void visitCaseStatement(CaseStatement statement) {
        debug('CaseStatement', statement)
        super.visitCaseStatement(statement)
    }

    @Override
    void visitCatchStatement(CatchStatement statement) {
        debug('CatchStatement', statement)
        super.visitCatchStatement(statement)
    }

    @Override
    void visitContinueStatement(ContinueStatement statement) {
        debug('ContinueStatement', statement)
        super.visitContinueStatement(statement)
    }

    @Override
    void visitDoWhileLoop(DoWhileStatement loop) {
        debug('DoWhileLoop', loop)
        super.visitDoWhileLoop(loop)
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        debug('ExpressionStatement', statement)
        super.visitExpressionStatement(statement)
    }

    @Override
    void visitForLoop(ForStatement forLoop) {
        debug('ForLoop', forLoop)
        super.visitForLoop(forLoop)
    }

    @Override
    void visitIfElse(IfStatement ifElse) {
        debug('IfElse', ifElse)
        super.visitIfElse(ifElse)
    }

    @Override
    void visitReturnStatement(ReturnStatement statement) {
        debug('ReturnStatement', statement)
        super.visitReturnStatement(statement)
    }

    @Override
    void visitSwitch(SwitchStatement statement) {
        debug('Switch', statement)
        super.visitSwitch(statement)
    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement) {
        debug('SynchronizedStatement', statement)
        super.visitSynchronizedStatement(statement)
    }

    @Override
    void visitThrowStatement(ThrowStatement statement) {
        debug('ThrowStatement', statement)
        super.visitThrowStatement(statement)
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement statement) {
        debug('TryCatchFinally', statement)
        super.visitTryCatchFinally(statement)
    }

    @Override
    void visitWhileLoop(WhileStatement loop) {
        debug('WhileLoop', loop)
        super.visitWhileLoop(loop)
    }

    @Override
    protected void visitEmptyStatement(EmptyStatement statement) {
        debug('EmptyStatement', statement)
        super.visitEmptyStatement(statement)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        debug('MethodCallExpression', call)
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        debug('StaticMethodCallExpression', call)
        super.visitStaticMethodCallExpression(call)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        debug('ConstructorCallExpression', call)
        super.visitConstructorCallExpression(call)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        debug('BinaryExpression', expression)

        def right = expression.rightExpression
        def left = expression.leftExpression
        if (right.lineNumber > left.lastLineNumber && isFirstVisit(expression)) {
            indent(right, theRule.continuationIndentSize, right.lineNumber, right.lastLineNumber)
        }
        super.visitBinaryExpression(expression)
    }

    @Override
    void visitTernaryExpression(TernaryExpression expression) {
        debug('TernaryExpression', expression)
        super.visitTernaryExpression(expression)
    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        debug('ShortTernaryExpression', expression)
        super.visitShortTernaryExpression(expression)
    }

    @Override
    void visitPostfixExpression(PostfixExpression expression) {
        debug('PostfixExpression', expression)
        super.visitPostfixExpression(expression)
    }

    @Override
    void visitPrefixExpression(PrefixExpression expression) {
        debug('PrefixExpression', expression)
        super.visitPrefixExpression(expression)
    }

    @Override
    void visitBooleanExpression(BooleanExpression expression) {
        debug('BooleanExpression', expression)
        super.visitBooleanExpression(expression)
    }

    @Override
    void visitNotExpression(NotExpression expression) {
        debug('NotExpression', expression)
        super.visitNotExpression(expression)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        debug('ClosureExpression', expression)
        if (isFirstVisit(expression)) {
            indent(expression, theRule.indentSize, true)
        }
        super.visitClosureExpression(expression)
    }

    @Override
    void visitTupleExpression(TupleExpression expression) {
        debug('TupleExpression', expression)
        super.visitTupleExpression(expression)
    }

    @Override
    void visitListExpression(ListExpression expression) {
        debug('ListExpression', expression)
        def lastValue = expression.expressions.last()
        def offset = lastValue.lastColumnNumber == 1 ? -1 : 0
        if (expression.lastLineNumber > expression.lineNumber && isFirstVisit(expression)) {
            indent(expression, theRule.continuationIndentSize, expression.lineNumber + 1, expression.lastLineNumber + offset)
        }
        super.visitListExpression(expression)
    }

    @Override
    void visitArrayExpression(ArrayExpression expression) {
        debug('ArrayExpression', expression)
        super.visitArrayExpression(expression)
    }

    @Override
    void visitMapExpression(MapExpression expression) {
        debug('MapExpression', expression)
        super.visitMapExpression(expression)
    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression) {
        debug('MapEntryExpression', expression)
        super.visitMapEntryExpression(expression)
    }

    @Override
    void visitRangeExpression(RangeExpression expression) {
        debug('RangeExpression', expression)
        super.visitRangeExpression(expression)
    }

    @Override
    void visitSpreadExpression(SpreadExpression expression) {
        debug('SpreadExpression', expression)
        super.visitSpreadExpression(expression)
    }

    @Override
    void visitSpreadMapExpression(SpreadMapExpression expression) {
        debug('SpreadMapExpression', expression)
        super.visitSpreadMapExpression(expression)
    }

    @Override
    void visitMethodPointerExpression(MethodPointerExpression expression) {
        debug('MethodPointerExpression', expression)
        super.visitMethodPointerExpression(expression)
    }

    @Override
    void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        debug('UnaryMinusExpression', expression)
        super.visitUnaryMinusExpression(expression)
    }

    @Override
    void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        debug('UnaryPlusExpression', expression)
        super.visitUnaryPlusExpression(expression)
    }

    @Override
    void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        debug('BitwiseNegationExpression', expression)
        super.visitBitwiseNegationExpression(expression)
    }

    @Override
    void visitCastExpression(CastExpression expression) {
        debug('CastExpression', expression)
        super.visitCastExpression(expression)
    }

    @Override
    void visitConstantExpression(ConstantExpression expression) {
        debug('ConstantExpression', expression)
        super.visitConstantExpression(expression)
    }

    @Override
    void visitClassExpression(ClassExpression expression) {
        debug('ClassExpression', expression)
        super.visitClassExpression(expression)
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        debug('VariableExpression', expression)
        super.visitVariableExpression(expression)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        debug('PropertyExpression', expression)
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitAttributeExpression(AttributeExpression expression) {
        debug('AttributeExpression', expression)
        super.visitAttributeExpression(expression)
    }

    @Override
    void visitFieldExpression(FieldExpression expression) {
        debug('FieldExpression', expression)
        super.visitFieldExpression(expression)
    }

    @Override
    void visitGStringExpression(GStringExpression expression) {
        debug('GStringExpression', expression)
        super.visitGStringExpression(expression)
    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression ale) {
        debug('ArgumentlistExpression', ale)
        super.visitArgumentlistExpression(ale)
    }

    @Override
    void visitClosureListExpression(ClosureListExpression cle) {
        debug('ClosureListExpression', cle)
        super.visitClosureListExpression(cle)
    }

    @Override
    void visitBytecodeExpression(BytecodeExpression cle) {
        debug('BytecodeExpression', cle)
        super.visitBytecodeExpression(cle)
    }

}
