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
 * This is the base class for AST Visitors that only need to visit the methods of
 * a class. It will not visit anything except the MethodNode. It is much faster than
 * the alternative of visiting the whole class. <br/>
 * <br/>
 * When you override visitMethod(MethodNode), there is no need to invoke the super method.
 */
public class AbstractMethodVisitor extends ClassCodeVisitorSupport implements AstVisitor {


    private Rule rule;
    private SourceCode sourceCode;
    private final List<Violation> violations = new ArrayList<Violation>();
    private ClassNode currentClassNode = null;

    @Override
    public void visitClass(ClassNode node) {
        currentClassNode = node;
        for (MethodNode method : node.getMethods()) {
            visitMethod(method);
        }
        currentClassNode = null; 
    }

    protected final ClassNode getCurrentClassNode() {
        return currentClassNode;
    }

    protected final String getCurrentClassName() {
        if (currentClassNode == null) {
            return "<unknown>";
        }
        return currentClassNode.getName(); 
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     *
     * @param node    - the Groovy AST Node
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(ASTNode node, String message) {
        if (node.getLineNumber() >= 0) {
            int lineNumber = AstUtil.findFirstNonAnnotationLine(node, sourceCode);
            String sourceLine = sourceCode.line(AstUtil.findFirstNonAnnotationLine(node, sourceCode) - 1);
            Violation violation = new Violation();
            violation.setRule(rule);
            violation.setLineNumber(lineNumber);
            violation.setSourceLine(sourceLine);
            violation.setMessage(message);
            violations.add(violation);
        }
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     *
     * @param node    - the Groovy AST Node
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(MethodNode node, String message) {
        addViolation((ASTNode) node, String.format(
                "Violation in class %s. %s", node.getDeclaringClass().getNameWithoutPackage(), message
        ));
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     *
     * @param node    - the Groovy AST Node
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(ClassNode node, String message) {
        addViolation((ASTNode) node, String.format(
                "Violation in class %s. %s", node.getNameWithoutPackage(), message
        ));
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
     *
     * @return the rule
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
    public final void visitField(FieldNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void visitObjectInitializerStatements(ClassNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final SourceUnit getSourceUnit() {
        throw new RuntimeException("should never be called");
    }

    @Override
    public final void visitPackage(PackageNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitImports(ModuleNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitAnnotations(AnnotatedNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void visitClassCodeContainer(Statement code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitVariableExpression(VariableExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitConstructor(ConstructorNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitProperty(PropertyNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void addError(String msg, ASTNode expr) {
        super.addError(msg, expr);
    }

    @Override
    protected final void visitStatement(Statement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitAssertStatement(AssertStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitBlockStatement(BlockStatement block) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitBreakStatement(BreakStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitCaseStatement(CaseStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitCatchStatement(CatchStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitContinueStatement(ContinueStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitDoWhileLoop(DoWhileStatement loop) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitExpressionStatement(ExpressionStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitForLoop(ForStatement forLoop) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitIfElse(IfStatement ifElse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitReturnStatement(ReturnStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitSwitch(SwitchStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitSynchronizedStatement(SynchronizedStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitThrowStatement(ThrowStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitTryCatchFinally(TryCatchStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitWhileLoop(WhileStatement loop) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void visitEmptyStatement(EmptyStatement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitMethodCallExpression(MethodCallExpression call) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitConstructorCallExpression(ConstructorCallExpression call) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitBinaryExpression(BinaryExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitTernaryExpression(TernaryExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitPostfixExpression(PostfixExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitPrefixExpression(PrefixExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitBooleanExpression(BooleanExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitNotExpression(NotExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitClosureExpression(ClosureExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitTupleExpression(TupleExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitListExpression(ListExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitArrayExpression(ArrayExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitMapExpression(MapExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitMapEntryExpression(MapEntryExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitRangeExpression(RangeExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitSpreadExpression(SpreadExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitSpreadMapExpression(SpreadMapExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitMethodPointerExpression(MethodPointerExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitCastExpression(CastExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitConstantExpression(ConstantExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitClassExpression(ClassExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitDeclarationExpression(DeclarationExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitPropertyExpression(PropertyExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitAttributeExpression(AttributeExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitFieldExpression(FieldExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitGStringExpression(GStringExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void visitListOfExpressions(List<? extends Expression> list) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitArgumentlistExpression(ArgumentListExpression ale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitClosureListExpression(ClosureListExpression cle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void visitBytecodeExpression(BytecodeExpression cle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitMethod(MethodNode node) {
        throw new UnsupportedOperationException();
    }
}
