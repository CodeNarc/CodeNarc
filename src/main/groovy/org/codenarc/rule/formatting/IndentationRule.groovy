/*
 * Copyright 2017 the original author or authors.
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

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil
import org.codenarc.util.GroovyVersion

import java.util.concurrent.ConcurrentHashMap

/**
 * Check indentation for class and method declarations
 *
 * @author Chris Mair
 */
class IndentationRule extends AbstractAstVisitorRule {

    String name = 'Indentation'
    int priority = 3
    Class astVisitorClass = IndentationAstVisitor
    int spacesPerIndentLevel = 4

    // Global Map of indent levels by class; enables coordination across different Class Nodes.
    protected final Map<SourceCode, Map<ClassNode, Integer>> classNodeIndentLevels = new ConcurrentHashMap<>()

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        super.applyTo(sourceCode, violations)

        // Clear out the classNodeIndentLevels for the source file so the global Map does not keep growing
        classNodeIndentLevels.remove(sourceCode)
    }
}

class IndentationAstVisitor extends AbstractAstVisitor {

    // Limitations:
    //  - Checks spaces only (not tabs)
    //  - Does not check comments
    //  - Does not check  line-continuations
    //  - Does not check Map entry expressions
    //  - Does not check List expressions

    private static final List<String> SPOCK_BLOCKS = [
            'given',
            'setup',
            'cleanup',
            'when',
            'then',
            'expect',
            'and',
            'where'
    ]

    private int indentLevel = 0
    private final Set<Integer> ignoreLineNumbers = []
    private final Set<BlockStatement> nestedBlocks = []
    private final Set<BlockStatement> flexibleIndentBlocks = []
    private final Set<ConstructorCallExpression> constructorCallInStatements = []
    private final Map<BlockStatement, Integer> blockIndentLevel = [:].withDefault { 0 }
    private final Map<BlockStatement, Tuple2<Integer, String>> methodColumnAndSourceLineForClosureBlock = [:].withDefault { new Tuple2<>(0, '') }

    @Override
    protected void visitClassEx(ClassNode node) {
        indentLevel = nestingLevelForClass(node)

        // Ignore line containing class declaration as well as lines for class annotations.
        // The line range for the last annotation typically includes the first line of the class declaration.
        ignoreLineNumbers << node.lineNumber
        node.annotations.each { annotationNode -> ignoreLineNumbers << annotationNode.lastLineNumber }

        // Groovy 3.x ClassNode lineNumber is line number of first annotation
        if (GroovyVersion.isNotGroovyVersion2() && node.annotations) {
            int classDeclarationLine = AstUtil.findClassDeclarationLineNumber(node, sourceCode)
            ignoreLineNumbers << classDeclarationLine
        }

        boolean isInnerClass = node instanceof InnerClassNode
        boolean isAnonymous = isInnerClass && node.anonymous
        if (!isAnonymous) {
            String description = "class ${node.getNameWithoutPackage()}"

            if (GroovyVersion.isGroovyVersion2() || !isInnerClass) {
                checkForCorrectColumn(node, description)
            } else {
                int column = firstNonWhitespaceColumn(sourceLine(node))
                checkForCorrectColumn(node, description, column)
            }
        }
        if (!node.script) {
            indentLevel++
        }
        super.visitClassEx(node)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (call.isUsingAnonymousInnerClass()) {
            // If the anonymous inner class is the first/only statement on the line, then its contents should be indented
            int firstColumn = firstNonWhitespaceColumn(sourceLine(call))
            boolean beginsTheLine = firstColumn == call.columnNumber

            int indentLevelForClass = indentLevel

            if (beginsTheLine && !constructorCallInStatements.contains(call)) {
                int column = call.columnNumber
                if (!isValidColumn(column)) {
                    def description = "inner class ${call.type.name} on line ${call.type.lineNumber}"
                    addViolation(call, "The $description starting column $column is not a valid column for the indentation level")
                }
                else if (column < columnForIndentLevel(indentLevel)) {
                    def description = "inner class ${call.type.name} on line ${call.type.lineNumber}"
                    addViolation(call, "The $description should be indented beyond the enclosing indent level")
                }

                indentLevelForClass = indentLevelFromColumn(column)
            }

            getIndentLevelsMap()[call.type] = indentLevelForClass
        }
        super.visitConstructorCallExpression(call)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        checkForCorrectColumn(node, "method ${node.name} in class ${currentClassName}")
        ignoreLineNumbers << node.lineNumber

        // If annotated and a single-line method, then ignore its last line (annotations may make that a different line number)
        if (node.annotations && node.lastLineNumber == node.code?.lastLineNumber) {
            ignoreLineNumbers << node.lastLineNumber
        }

        // If this is a static initializer, then expect its block to be indented (even though its lineNumber == -1)
        if (node.staticConstructor) {
            blockIndentLevel[node.code] = blockIndentLevel[node.code] + 1
        }

        super.visitMethodEx(node)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        checkForCorrectColumn(node, "constructor in class ${currentClassName}")
        ignoreLineNumbers << node.lineNumber
        super.visitConstructor(node)
    }

    @Override
    void visitClosureExpression(ClosureExpression closureExpression) {
        // Ignore line containing closure declaration, as well as any of the closure's parameters (since they may be on separate lines)
        ignoreLineNumbers << closureExpression.lineNumber
        closureExpression.parameters.each { param -> ignoreLineNumbers << param.lineNumber }

        super.visitClosureExpression(closureExpression)
    }

    @Override
    void visitField(FieldNode node) {
        if (!ignoreLineNumbers.contains(node.lineNumber)) {
            ignoreLineNumbers << node.lineNumber
            checkForCorrectColumn(node, "field ${node.name} in class ${currentClassName}")
        }
        super.visitField(node)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        def args = call.arguments
        if (args instanceof ArgumentListExpression) {
            // If the method name starts on a different line, then assume it is a chained method call,
            // and any blocks that are arguments should be indented.
            if (isChainedMethodCallOnDifferentLine(call)) {
                increaseIndentForClosureBlocks(args)
            }

            setupFlexibleIndentForAnyClosureParameterBlocks(args)
            recordMethodColumnAndSourceLineForClosureBlocks(call)
        }

        super.visitMethodCallExpression(call)
    }

    private List<Expression> increaseIndentForClosureBlocks(ArgumentListExpression args) {
        return args.expressions.each { expr ->
            if (isClosureWithBlock(expr)) {
                blockIndentLevel[expr.code] = blockIndentLevel[expr.code] + 1
            }
        }
    }

    private List<Expression> setupFlexibleIndentForAnyClosureParameterBlocks(ArgumentListExpression args) {
        args.expressions.each { expr ->
            if (isClosureWithBlock(expr)) {
                flexibleIndentBlocks << expr.code
            }
        }
    }

    private void recordMethodColumnAndSourceLineForClosureBlocks(MethodCallExpression methodCallExpression) {
        def method = methodCallExpression.method
        if (isGeneratedCode(method)) {
            return
        }
        methodCallExpression.arguments.expressions.each { expr ->
            if (isClosureWithBlock(expr)) {
                BlockStatement blockStatementCode = (expr as ClosureExpression).code as BlockStatement
                String rawMethodSourceLine = sourceLine(method)
                methodColumnAndSourceLineForClosureBlock[blockStatementCode] = new Tuple2<Integer, String>(method.columnNumber, rawMethodSourceLine)
            }
        }
    }

    private boolean isClosureWithBlock(Expression expr) {
        return expr instanceof ClosureExpression && expr.code instanceof BlockStatement
    }

    private boolean isChainedMethodCallOnDifferentLine(MethodCallExpression call) {
        return call.method instanceof ConstantExpression &&
                call.lineNumber != -1 &&
                call.lineNumber != call.method.lineNumber &&
                sourceLineTrimmed(call.method)?.startsWith('.')
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        // finally blocks have extra level of nested BlockStatement
        boolean isNestedBlock = nestedBlocks.contains(block)
        boolean isGenerated = block.lineNumber == -1
        int addToIndentLevel = (isNestedBlock || isGenerated) ? 0 : 1
        addToIndentLevel += blockIndentLevel[block]
        indentLevel += addToIndentLevel

        block.statements.each { statement ->
            // Skip statements on the same line as another statement or a field declaration
            // Skip statements that are spock block labels
            if (!ignoreLineNumbers.contains(statement.lineNumber) && !isSpockBlockLabel(statement)) {
                ignoreLineNumbers << statement.lineNumber

                // Ignore nested BlockStatement (e.g. finally blocks)
                boolean isNestedBlockStatement = statement instanceof BlockStatement
                if (isNestedBlockStatement) {
                    nestedBlocks << statement
                }

                // If the statement is a ConstructorCall, then register it so we don't check its indent again when visiting those
                registerConstructorCalls(statement)

                // Ignore super/this constructor calls -- they have messed up column numbers
                boolean isConstructorCall = (statement instanceof ExpressionStatement) && (statement.expression instanceof ConstructorCallExpression)
                boolean isSuperConstructorCall = isConstructorCall && statement.expression.superCall
                boolean isThisConstructorCall = isConstructorCall && statement.expression.thisCall

                boolean ignoreStatement = isNestedBlockStatement || isSuperConstructorCall || isThisConstructorCall
                if (!ignoreStatement) {
                    checkStatementIndent(statement, block)
                }
            }
        }
        super.visitBlockStatement(block)
        indentLevel -= addToIndentLevel
    }

    private void registerConstructorCalls(Statement statement) {
        if (statement instanceof ExpressionStatement) {
            Expression expression = statement.expression
            if (expression instanceof ConstructorCallExpression) {
                constructorCallInStatements << expression
            }
            if (expression instanceof MethodCallExpression && expression.objectExpression instanceof ConstructorCallExpression) {
                constructorCallInStatements << expression.objectExpression
            }
        }
    }

    private void checkStatementIndent(Statement statement, BlockStatement block) {
        String description = "statement on line ${statement.lineNumber} in class ${currentClassName}"
        if (flexibleIndentBlocks.contains(block)) {
            flexibleCheckForCorrectColumn(statement, description, block)
        } else {
            checkForCorrectColumn(statement, description)
        }
    }

    @Override
    void visitSwitch(SwitchStatement statement) {
        statement.caseStatements.each { caseStatement ->
            ignoreLineNumbers << caseStatement.lineNumber
        }
        ignoreLineNumbers << statement.defaultStatement.lineNumber
        indentLevel++
        super.visitSwitch(statement)
        indentLevel--
    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression) {
        // Skip Map entry expressions
    }

    @Override
    void visitListExpression(ListExpression expression) {
        // Skip List expressions
    }

    //------------------------------------------------------------------------------------
    // Helper methods
    //------------------------------------------------------------------------------------

    private void checkForCorrectColumn(ASTNode node, String description) {
        checkForCorrectColumn(node, description, node.columnNumber)
    }

    private void checkForCorrectColumn(ASTNode node, String description, int actualColumnNumber) {
        int expectedColumn = columnForIndentLevel(indentLevel)
        if (actualColumnNumber != expectedColumn) {
            addViolation(node, "The $description is at the incorrect indent level: Expected column $expectedColumn but was ${actualColumnNumber}")
        }
    }

    private void flexibleCheckForCorrectColumn(ASTNode node, String description, BlockStatement block) {
        Integer methodColumn = methodColumnAndSourceLineForClosureBlock[block].getFirst()
        String rawMethodSourceLine = methodColumnAndSourceLineForClosureBlock[block].getSecond()

        Boolean doesMethodWithClosureBlockExists = methodColumn > 0
        Boolean isMethodWithClosureBlockStandaloneAndChained = doesMethodWithClosureBlockExists && rawMethodSourceLine.trim().startsWith('.')

        Integer chainedMethodDotColumn = methodColumn - 1
        Boolean isMethodWithClosureBlockInLineAndChained = doesMethodWithClosureBlockExists && !rawMethodSourceLine.trim().startsWith('.') && (rawMethodSourceLine[chainedMethodDotColumn - 1] == '.')

        if (isMethodWithClosureBlockStandaloneAndChained) {
            List<Integer> allowedColumns = (1..3).collect { level -> chainedMethodDotColumn + (level * (rule as IndentationRule).spacesPerIndentLevel) }
            if (!allowedColumns.contains(node.columnNumber)) {
                addViolation(node, "The $description is at the incorrect indent level: Expected one of columns $allowedColumns but was ${node.columnNumber}")
            }
        } else if (isMethodWithClosureBlockInLineAndChained) {
            List<Integer> allowedColumnsByGlobalIndentLevel = (0..2).collect { level -> columnForIndentLevel(indentLevel + level) }
            List<Integer> allowedColumnsByMethodIndentLevel = (1..3).collect { level -> chainedMethodDotColumn + (level * (rule as IndentationRule).spacesPerIndentLevel) }

            if (!allowedColumnsByGlobalIndentLevel.contains(node.columnNumber) && !allowedColumnsByMethodIndentLevel.contains(node.columnNumber)) {
                String violationMessage = "The $description is at the incorrect indent level: Depending on your chaining style, expected one of $allowedColumnsByGlobalIndentLevel " +
                                          "or one of $allowedColumnsByMethodIndentLevel columns, but was ${node.columnNumber}"
                addViolation(node, violationMessage)
            }
        } else {
            List<Integer> allowedColumns = (0..2).collect { level -> columnForIndentLevel(indentLevel + level) }
            if (!allowedColumns.contains(node.columnNumber)) {
                addViolation(node, "The $description is at the incorrect indent level: Expected one of columns $allowedColumns but was ${node.columnNumber}")
            }
        }
    }

    private Map<ClassNode, Integer> getIndentLevelsMap() {
        if (!rule.classNodeIndentLevels.containsKey(getSourceCode())) {
            rule.classNodeIndentLevels[getSourceCode()] = [:]
        }
        return rule.classNodeIndentLevels[getSourceCode()]
    }

    private int nestingLevelForClass(ClassNode node) {
        def indentLevelsMap = getIndentLevelsMap()
        if (indentLevelsMap.containsKey(node)) {
            return indentLevelsMap[node]
        }

        // If this is a nested class, then add one to the outer class level
        int level = node.outerClass ? nestingLevelForClass(node.outerClass) + 1 : 0

        // If this class is defined within a method, add one to the level
        level += node.enclosingMethod ? 1 : 0

        return level
    }

    private int columnForIndentLevel(int indentLevel) {
        return indentLevel * rule.spacesPerIndentLevel + 1
    }

    private boolean isSpockBlockLabel(Statement statement) {
        return (statement.statementLabel in SPOCK_BLOCKS &&
                statement instanceof ExpressionStatement &&
                statement.expression.class == ConstantExpression &&
                statement.expression.type.clazz == String
        )
    }

    protected static int firstNonWhitespaceColumn(String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i)
            if (!c.isWhitespace()) {
                return i + 1
            }
        }
        return -1
    }

    protected boolean isValidColumn(int column) {
        return column % rule.spacesPerIndentLevel == 1
    }

    private int indentLevelFromColumn(int column) {
        return (column - 1) / rule.spacesPerIndentLevel
    }
}
