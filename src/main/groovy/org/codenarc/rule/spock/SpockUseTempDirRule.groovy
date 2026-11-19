/*
 * Copyright 2026 the original author or authors.
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
package org.codenarc.rule.spock

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.util.AstUtil

/**
 * Spock's own <code>@TempDir</code> is the lifecycle-correct temporary directory: one per iteration by
 * default, one per specification when combined with <code>@Shared</code>, and always deleted afterwards.
 * The alternatives leak. <code>@Rule TemporaryFolder</code> is a JUnit 4 carry-over that only works
 * through Spock's rule interop, and <code>File.createTempDir()</code> /
 * <code>Files.createTempDirectory(..)</code> are never cleaned up at all unless somebody hand-writes a
 * <code>cleanup:</code> block for them.
 *
 * The rule reports two distinct shapes, with distinct messages, because the fixes differ in effort:
 * a <code>@Rule</code>/<code>@ClassRule</code> field of type <code>TemporaryFolder</code>, and a call
 * that creates a file or directory in the JVM-wide default temporary directory. The second shape can be
 * switched off with <code>checkAdHocCreation</code>.
 *
 * Because CodeNarc runs without a classpath, the receiver of an ad-hoc creation call is matched
 * textually against <code>File</code> and <code>Files</code>, and only calls that positively select the
 * default temporary directory are reported: a call that is handed an explicit target directory - most
 * importantly a <code>@TempDir</code> field or parameter - is left alone.
 *
 * @author Leonard Bruenings
 */
class SpockUseTempDirRule extends AbstractSpockRule {

    String name = 'SpockUseTempDir'
    int priority = 3

    /**
     * If false, only <code>@Rule</code>/<code>@ClassRule</code> <code>TemporaryFolder</code> fields are
     * reported, and ad-hoc temporary file or directory creation is ignored.
     */
    boolean checkAdHocCreation = true

    Class astVisitorClass = SpockUseTempDirAstVisitor
}

class SpockUseTempDirAstVisitor extends AbstractSpockAstVisitor<SpockUseTempDirRule> {

    private static final String TEMPORARY_FOLDER = 'TemporaryFolder'
    private static final String TMP_DIR_PROPERTY = 'java.io.tmpdir'
    private static final List<String> JUNIT_RULE_ANNOTATIONS = ['Rule', 'ClassRule']

    /**
     * Receiver name -&gt; (method name -&gt; the highest argument count that still means "in the JVM-wide
     * default temporary directory"). Every one of these methods has an overload that takes an explicit
     * parent directory as an extra leading or trailing argument; those overloads are not reported,
     * because they are exactly how a <code>@TempDir</code> is meant to be used.
     */
    private static final Map<String, Map<String, Integer>> DEFAULT_TEMP_DIR_METHODS = [
        File: [createTempDir: 2, createTempFile: 2],
        Files: [createTempDirectory: 1, createTempFile: 2],
    ].asImmutable()

    /**
     * Receivers whose default-directory overloads end in a <code>FileAttribute...</code> varargs parameter,
     * so that the argument count alone cannot tell them from the overloads that take a leading directory.
     */
    private static final Set<String> VARARGS_RECEIVERS = ['Files'].toSet().asImmutable()

    private boolean specification = false

    @Override
    protected void visitClassEx(ClassNode node) {
        super.visitClassEx(node)
        specification = SpockUtil.isSpockSpecification(node, rule.specificationSuperclassNames, rule.specificationClassNames)
        if (specification) {
            checkTemporaryFolderRuleFields(node)
        }
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        super.visitClassComplete(node)
        specification = false
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        checkAdHocCreationCall(call)
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        checkFileUnderTmpDirProperty(call)
        super.visitConstructorCallExpression(call)
    }

    //--------------------------------------------------------------------------
    // Shape 1 - @Rule / @ClassRule TemporaryFolder fields
    //--------------------------------------------------------------------------

    private void checkTemporaryFolderRuleFields(ClassNode node) {
        node.fields.each { FieldNode field ->
            String annotationName = junitRuleAnnotationName(field, node)
            if (annotationName != null && isTemporaryFolder(field)) {
                addViolation(field, "'@${annotationName} TemporaryFolder' is a JUnit 4 carry-over - use Spock's '@TempDir'")
            }
        }
    }

    private static String junitRuleAnnotationName(FieldNode field, ClassNode node) {
        return JUNIT_RULE_ANNOTATIONS.find { String annotationName -> hasFieldAnnotation(field, node, annotationName) }
    }

    private static boolean isTemporaryFolder(FieldNode field) {
        if (field.type?.nameWithoutPackage == TEMPORARY_FOLDER) {
            return true
        }
        // 'def folder = new TemporaryFolder()' has no declared type, so fall back to the initializer
        Expression initialExpression = field.initialExpression
        return initialExpression instanceof ConstructorCallExpression &&
            (initialExpression as ConstructorCallExpression).type?.nameWithoutPackage == TEMPORARY_FOLDER
    }

    //--------------------------------------------------------------------------
    // Shape 2 - ad-hoc temporary file / directory creation
    //--------------------------------------------------------------------------

    private void checkAdHocCreationCall(MethodCallExpression call) {
        if (!specification || !rule.checkAdHocCreation || !isFirstVisit(call)) {
            return
        }
        // get() rather than the subscript operator: both keys are identifiers written in the analysed
        // source, and for a String key that happens to name a JavaBean property of Map - 'metaClass',
        // 'properties' - the subscript operator returns that bean property instead of the entry
        String receiver = receiverName(call.objectExpression)
        Map<String, Integer> methods = DEFAULT_TEMP_DIR_METHODS.get(receiver)
        if (methods == null) {
            return
        }
        String methodName = SpockUtil.getMethodName(call)
        Integer maxArgumentCount = methods.get(methodName)
        if (maxArgumentCount == null) {
            return
        }
        List<? extends Expression> arguments = AstUtil.getMethodArguments(call)
        // More arguments than the default-directory overload takes means an explicit target directory -
        // unless the overload has trailing FileAttribute varargs and its leading prefix is a String literal
        if (arguments.size() > maxArgumentCount && !(receiver in VARARGS_RECEIVERS && isStringLiteral(arguments[0]))) {
            return
        }
        addViolation(call, "'${receiver}.${methodName}()' is never cleaned up - use a '@TempDir' field")
    }

    private void checkFileUnderTmpDirProperty(ConstructorCallExpression call) {
        if (!specification || !rule.checkAdHocCreation || !isFirstVisit(call)) {
            return
        }
        if (call.type?.nameWithoutPackage != 'File') {
            return
        }
        if (!AstUtil.getMethodArguments(call).any { referencesTmpDirProperty(it) }) {
            return
        }
        addViolation(call, "Creating a file under 'java.io.tmpdir' is never cleaned up - use a '@TempDir' field")
    }

    private static boolean referencesTmpDirProperty(Expression expression) {
        if (isTmpDirProperty(expression)) {
            return true
        }
        if (expression instanceof BinaryExpression) {
            BinaryExpression binary = expression as BinaryExpression
            return referencesTmpDirProperty(binary.leftExpression) || referencesTmpDirProperty(binary.rightExpression)
        }
        if (expression instanceof GStringExpression) {
            return (expression as GStringExpression).values.any { referencesTmpDirProperty(it) }
        }
        return false
    }

    private static boolean isTmpDirProperty(Expression expression) {
        if (!(expression instanceof MethodCallExpression)) {
            return false
        }
        MethodCallExpression call = expression as MethodCallExpression
        if (receiverName(call.objectExpression) != 'System' || SpockUtil.getMethodName(call) != 'getProperty') {
            return false
        }
        List<? extends Expression> arguments = AstUtil.getMethodArguments(call)
        return !arguments.isEmpty() && AstUtil.isConstant(arguments[0], TMP_DIR_PROPERTY)
    }

    //--------------------------------------------------------------------------
    // Expression helpers
    //--------------------------------------------------------------------------

    private static boolean hasFieldAnnotation(FieldNode field, ClassNode node, String annotationName) {
        if (SpockUtil.hasAnnotation(field, annotationName)) {
            return true
        }
        PropertyNode property = node.properties.find { PropertyNode candidate -> candidate.name == field.name }
        return SpockUtil.hasAnnotation(property, annotationName)
    }

    /**
     * @param expression - the expression to inspect
     * @return true if the expression is a String constant or a GString, i.e. positively a file name prefix
     *         rather than a directory; a variable could hold either and so does not count
     */
    private static boolean isStringLiteral(Expression expression) {
        return expression instanceof GStringExpression ||
            (expression instanceof ConstantExpression && (expression as ConstantExpression).value instanceof String)
    }

    /**
     * @param expression - the receiver expression of a call
     * @return the textual name of the receiver, so that both <code>Files.x()</code> and
     *         <code>java.nio.file.Files.x()</code> report <code>Files</code>; null if the receiver is
     *         not a plain name
     */
    private static String receiverName(Expression expression) {
        switch (expression) {
            case ClassExpression:
                return (expression as ClassExpression).type?.nameWithoutPackage
            case VariableExpression:
                return (expression as VariableExpression).name
            case PropertyExpression:
                return (expression as PropertyExpression).propertyAsString
            default:
                return null
        }
    }
}
