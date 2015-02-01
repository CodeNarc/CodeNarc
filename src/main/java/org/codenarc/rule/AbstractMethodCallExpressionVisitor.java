package org.codenarc.rule;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codenarc.source.SourceCode;
import org.codenarc.util.AstUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for AST Visitors that only need to visit the MethodCallExpressions of
 * a class. It will not visit anything except MethodCallExpression-s. It is much faster than
 * the alternative of visiting the whole class. <br/>
 * <br/>
 * When you override visitField(FieldNode), there is no need to invoke the super method.
 */
public class AbstractMethodCallExpressionVisitor extends ClassCodeVisitorSupport implements AstVisitor {


    private Rule rule;
    private SourceCode sourceCode;
    private final List<Violation> violations = new ArrayList<Violation>();
    private ClassNode currentClassNode = null; 

    @Override
    public void visitClass(ClassNode node) {

        currentClassNode = node;
        if (sourceCode == null) {
            throw new IllegalStateException("CodeNarc developer error. SourceCode may not be null");
        }

        List<MethodCallExpression> expressions = sourceCode.getMethodCallExpressions().get(node);
        if (expressions != null) {
            for (MethodCallExpression expression : expressions) {
                visitMethodCallExpression(expression);
            }
        }

        currentClassNode = null;
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     *
     * @param node    - the Groovy AST Node
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(MethodCallExpression node, String message) {
        if (node.getLineNumber() >= 0) {
            int lineNumber = AstUtil.findFirstNonAnnotationLine(node, sourceCode);
            String sourceLine = sourceCode.line(AstUtil.findFirstNonAnnotationLine(node, sourceCode) - 1);
            Violation violation = new Violation();
            violation.setRule(rule);
            violation.setLineNumber(lineNumber);
            violation.setSourceLine(sourceLine);
            if (currentClassNode != null) {
                violation.setMessage(String.format(
                        "Violation in class %s. %s", currentClassNode.getName(), message
                ));
            } else {
                violation.setMessage(message);
            }
            violations.add(violation);
        }
    }

    protected SourceCode getSourceCode() {
        return sourceCode;
    }

    /**
     * Set the Rule associated with this visitor
     *
     * @param rule - the Rule
     */
    public void setRule(Rule rule) {
        this.rule = rule;
    }

    /**
     * Gets the rule for this visitor.
     * @return
     *      the rule
     */
    public Rule getRule() {
        return rule;
    }
    /**
     * Set the SourceCode associated with this visitor
     *
     * @param sourceCode - the SourceCode
     */
    public void setSourceCode(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * Retrieve the List of Violations resulting from applying this visitor
     *
     * @return the List of Violations; may be empty
     */
    public List<Violation> getViolations() {
        return violations;
    }

    @Override
    protected final void visitObjectInitializerStatements(ClassNode node) {
        super.visitObjectInitializerStatements(node);
    }

    @Override
    protected final SourceUnit getSourceUnit() {
        throw new RuntimeException("should never be called");
    }

    @Override
    public final void visitPackage(PackageNode node) {
        super.visitPackage(node);
    }

    @Override
    public final void visitImports(ModuleNode node) {
        super.visitImports(node);
    }

    @Override
    public final void visitAnnotations(AnnotatedNode node) {
        super.visitAnnotations(node);
    }

    @Override
    protected final void visitClassCodeContainer(Statement code) {
        super.visitClassCodeContainer(code);
    }

    @Override
    public final void visitVariableExpression(VariableExpression expression) {
        super.visitVariableExpression(expression);
    }

    @Override
    protected final void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        super.visitConstructorOrMethod(node, isConstructor);
    }

    @Override
    public final void visitConstructor(ConstructorNode node) {
        super.visitConstructor(node);
    }

    @Override
    public final void visitProperty(PropertyNode node) {
        super.visitProperty(node);
    }

    @Override
    protected final void addError(String msg, ASTNode expr) {
        super.addError(msg, expr);
    }

    @Override
    protected final void visitStatement(Statement statement) {
        super.visitStatement(statement);
    }

    @Override
    public final void visitAssertStatement(AssertStatement statement) {
        super.visitAssertStatement(statement);
    }

    @Override
    public final void visitBlockStatement(BlockStatement block) {
        super.visitBlockStatement(block);
    }

    @Override
    public final void visitBreakStatement(BreakStatement statement) {
        super.visitBreakStatement(statement);
    }

    @Override
    public final void visitCaseStatement(CaseStatement statement) {
        super.visitCaseStatement(statement);
    }

    @Override
    public final void visitCatchStatement(CatchStatement statement) {
        super.visitCatchStatement(statement);
    }

    @Override
    public final void visitContinueStatement(ContinueStatement statement) {
        super.visitContinueStatement(statement);
    }

    @Override
    public final void visitDoWhileLoop(DoWhileStatement loop) {
        super.visitDoWhileLoop(loop);
    }

    @Override
    public final void visitExpressionStatement(ExpressionStatement statement) {
        super.visitExpressionStatement(statement);
    }

    @Override
    public final void visitForLoop(ForStatement forLoop) {
        super.visitForLoop(forLoop);
    }

    @Override
    public final void visitIfElse(IfStatement ifElse) {
        super.visitIfElse(ifElse);
    }

    @Override
    public final void visitReturnStatement(ReturnStatement statement) {
        super.visitReturnStatement(statement);
    }

    @Override
    public final void visitSwitch(SwitchStatement statement) {
        super.visitSwitch(statement);
    }

    @Override
    public final void visitSynchronizedStatement(SynchronizedStatement statement) {
        super.visitSynchronizedStatement(statement);
    }

    @Override
    public final void visitThrowStatement(ThrowStatement statement) {
        super.visitThrowStatement(statement);
    }

    @Override
    public final void visitTryCatchFinally(TryCatchStatement statement) {
        super.visitTryCatchFinally(statement);
    }

    @Override
    public final void visitWhileLoop(WhileStatement loop) {
        super.visitWhileLoop(loop);
    }

    @Override
    protected final void visitEmptyStatement(EmptyStatement statement) {
        super.visitEmptyStatement(statement);
    }

    @Override
    public final void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        super.visitStaticMethodCallExpression(call);
    }

    @Override
    public final void visitConstructorCallExpression(ConstructorCallExpression call) {
        super.visitConstructorCallExpression(call);
    }

    @Override
    public final void visitBinaryExpression(BinaryExpression expression) {
        super.visitBinaryExpression(expression);
    }

    @Override
    public final void visitTernaryExpression(TernaryExpression expression) {
        super.visitTernaryExpression(expression);
    }

    @Override
    public final void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        super.visitShortTernaryExpression(expression);
    }

    @Override
    public final void visitPostfixExpression(PostfixExpression expression) {
        super.visitPostfixExpression(expression);
    }

    @Override
    public final void visitPrefixExpression(PrefixExpression expression) {
        super.visitPrefixExpression(expression);
    }

    @Override
    public final void visitBooleanExpression(BooleanExpression expression) {
        super.visitBooleanExpression(expression);
    }

    @Override
    public final void visitNotExpression(NotExpression expression) {
        super.visitNotExpression(expression);
    }

    @Override
    public final void visitClosureExpression(ClosureExpression expression) {
        super.visitClosureExpression(expression);
    }

    @Override
    public final void visitTupleExpression(TupleExpression expression) {
        super.visitTupleExpression(expression);
    }

    @Override
    public final void visitListExpression(ListExpression expression) {
        super.visitListExpression(expression);
    }

    @Override
    public final void visitArrayExpression(ArrayExpression expression) {
        super.visitArrayExpression(expression);
    }

    @Override
    public final void visitMapExpression(MapExpression expression) {
        super.visitMapExpression(expression);
    }

    @Override
    public final void visitMapEntryExpression(MapEntryExpression expression) {
        super.visitMapEntryExpression(expression);
    }

    @Override
    public final void visitRangeExpression(RangeExpression expression) {
        super.visitRangeExpression(expression);
    }

    @Override
    public final void visitSpreadExpression(SpreadExpression expression) {
        super.visitSpreadExpression(expression);
    }

    @Override
    public final void visitSpreadMapExpression(SpreadMapExpression expression) {
        super.visitSpreadMapExpression(expression);
    }

    @Override
    public final void visitMethodPointerExpression(MethodPointerExpression expression) {
        super.visitMethodPointerExpression(expression);
    }

    @Override
    public final void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        super.visitUnaryMinusExpression(expression);
    }

    @Override
    public final void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        super.visitUnaryPlusExpression(expression);
    }

    @Override
    public final void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        super.visitBitwiseNegationExpression(expression);
    }

    @Override
    public final void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression);
    }

    @Override
    public final void visitConstantExpression(ConstantExpression expression) {
        super.visitConstantExpression(expression);
    }

    @Override
    public final void visitClassExpression(ClassExpression expression) {
        super.visitClassExpression(expression);
    }

    @Override
    public final void visitDeclarationExpression(DeclarationExpression expression) {
        super.visitDeclarationExpression(expression);
    }

    @Override
    public final void visitPropertyExpression(PropertyExpression expression) {
        super.visitPropertyExpression(expression);
    }

    @Override
    public final void visitAttributeExpression(AttributeExpression expression) {
        super.visitAttributeExpression(expression);
    }

    @Override
    public final void visitFieldExpression(FieldExpression expression) {
        super.visitFieldExpression(expression);
    }

    @Override
    public final void visitGStringExpression(GStringExpression expression) {
        super.visitGStringExpression(expression);
    }

    @Override
    protected final void visitListOfExpressions(List<? extends Expression> list) {
        super.visitListOfExpressions(list);
    }

    @Override
    public final void visitArgumentlistExpression(ArgumentListExpression ale) {
        super.visitArgumentlistExpression(ale);
    }

    @Override
    public final void visitClosureListExpression(ClosureListExpression cle) {
        super.visitClosureListExpression(cle);
    }

    @Override
    public final void visitBytecodeExpression(BytecodeExpression cle) {
        super.visitBytecodeExpression(cle);
    }
    @Override
    public final void visitMethod(MethodNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitField(FieldNode node) {
        throw new UnsupportedOperationException();
    }
}
