package org.codenarc.rule;


import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.BytecodeExpression;

import java.util.List;

/**
 * This is a horrible hack needed because method dispatch is broken in Groovy 1.8.
 * When they fix the defect then we can remove this class. 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public abstract class ClassCodeVisitorSupportHack extends ClassCodeVisitorSupport {

    @Override
    public void visitClass(ClassNode node) {
        super.visitClass(node);
    }

    @Override
    protected void visitObjectInitializerStatements(ClassNode node) {
        super.visitObjectInitializerStatements(node);
    }

    @Override
    public void visitPackage(PackageNode node) {
        super.visitPackage(node);
    }

    @Override
    public void visitImports(ModuleNode node) {
        super.visitImports(node);
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        super.visitAnnotations(node);
    }

    @Override
    protected void visitClassCodeContainer(Statement code) {
        super.visitClassCodeContainer(code);
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        super.visitDeclarationExpression(expression);
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        super.visitConstructorOrMethod(node, isConstructor);
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        super.visitConstructor(node);
    }

    @Override
    public void visitMethod(MethodNode node) {
        super.visitMethod(node);
    }

    @Override
    public void visitField(FieldNode node) {
        super.visitField(node);
    }

    @Override
    public void visitProperty(PropertyNode node) {
        super.visitProperty(node);
    }

    @Override
    protected void addError(String msg, ASTNode expr) {
        super.addError(msg, expr);
    }

    @Override
    protected void visitStatement(Statement statement) {
        super.visitStatement(statement);
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        super.visitAssertStatement(statement);
    }

    @Override
    public void visitBlockStatement(BlockStatement block) {
        super.visitBlockStatement(block);
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
        super.visitBreakStatement(statement);
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        super.visitCaseStatement(statement);
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        super.visitCatchStatement(statement);
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
        super.visitContinueStatement(statement);
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        super.visitDoWhileLoop(loop);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        super.visitExpressionStatement(statement);
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        super.visitForLoop(forLoop);
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        super.visitIfElse(ifElse);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        super.visitReturnStatement(statement);
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        super.visitSwitch(statement);
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        super.visitSynchronizedStatement(statement);
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        super.visitThrowStatement(statement);
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        super.visitTryCatchFinally(statement);
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        super.visitWhileLoop(loop);
    }

    @Override
    protected void visitEmptyStatement(EmptyStatement statement) {
        super.visitEmptyStatement(statement);
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        super.visitMethodCallExpression(call);
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        super.visitStaticMethodCallExpression(call);
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        super.visitConstructorCallExpression(call);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        super.visitBinaryExpression(expression);
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        super.visitTernaryExpression(expression);
    }

    @Override
    public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        super.visitShortTernaryExpression(expression);
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
        super.visitPostfixExpression(expression);
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
        super.visitPrefixExpression(expression);
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
        super.visitBooleanExpression(expression);
    }

    @Override
    public void visitNotExpression(NotExpression expression) {
        super.visitNotExpression(expression);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        super.visitClosureExpression(expression);
    }

    @Override
    public void visitTupleExpression(TupleExpression expression) {
        super.visitTupleExpression(expression);
    }

    @Override
    public void visitListExpression(ListExpression expression) {
        super.visitListExpression(expression);
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        super.visitArrayExpression(expression);
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
        super.visitMapExpression(expression);
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
        super.visitMapEntryExpression(expression);
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        super.visitRangeExpression(expression);
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
        super.visitSpreadExpression(expression);
    }

    @Override
    public void visitSpreadMapExpression(SpreadMapExpression expression) {
        super.visitSpreadMapExpression(expression);
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression expression) {
        super.visitMethodPointerExpression(expression);
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        super.visitUnaryMinusExpression(expression);
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        super.visitUnaryPlusExpression(expression);
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        super.visitBitwiseNegationExpression(expression);
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression);
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
        super.visitConstantExpression(expression);
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
        super.visitClassExpression(expression);
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        super.visitVariableExpression(expression);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        super.visitPropertyExpression(expression);
    }

    @Override
    public void visitAttributeExpression(AttributeExpression expression) {
        super.visitAttributeExpression(expression);
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
        super.visitFieldExpression(expression);
    }

    @Override
    public void visitGStringExpression(GStringExpression expression) {
        super.visitGStringExpression(expression);
    }

    @Override
    protected void visitListOfExpressions(List<? extends Expression> list) {
        super.visitListOfExpressions(list);
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression ale) {
        super.visitArgumentlistExpression(ale);
    }

    @Override
    public void visitClosureListExpression(ClosureListExpression cle) {
        super.visitClosureListExpression(cle);
    }

    @Override
    public void visitBytecodeExpression(BytecodeExpression cle) {
        super.visitBytecodeExpression(cle);
    }
}
