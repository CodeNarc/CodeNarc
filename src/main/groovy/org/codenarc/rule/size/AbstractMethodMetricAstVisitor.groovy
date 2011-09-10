/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.size

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.Violation
import org.codenarc.util.AstUtil
import org.codenarc.util.WildcardPattern

/**
 * Abstract superclass for AstVisitor classes that use method-level GMetrics Metrics.
 *
 * Subclasses must:
 * <ul>
 *   <li>Implement the abstract <code>createMetric()</code> method</li>
 *   <li>Implement the abstract <code>getMetricShortDescription()</code> method</li>
 *   <li>Implement the abstract <code>getMaxMethodMetricValue()</code> method</li>
 *   <li>Implement the abstract <code>getMaxClassMetricValue()</code> method</li>
 *   <li>The owning Rule class must have the <code>ignoreMethodNames</code> property</li>
 * </ul>
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
@SuppressWarnings('DuplicateLiteral')
abstract class AbstractMethodMetricAstVisitor extends AbstractAstVisitor  {

    protected metric

    protected abstract createMetric()
    protected abstract String getMetricShortDescription()
    protected abstract Object getMaxMethodMetricValue()
    protected abstract Object getMaxClassMetricValue()

    protected AbstractMethodMetricAstVisitor() {
        metric = createMetric()
    }
    
    void visitClassEx(ClassNode classNode) {
        def gmetricsSourceCode = new GMetricsSourceCodeAdapter(this.sourceCode)
        def classMetricResult = metric.applyToClass(classNode, gmetricsSourceCode)

        if (classMetricResult == null) {    // no methods or closure fields
            return
        }

        checkMethods(classMetricResult)

        if (!AstUtil.isFromGeneratedSourceCode(classNode)) {
            if (!(classNode.isScript() && classNode.name == 'None')) {
            checkClass(classMetricResult, classNode)
            }
        }
        super.visitClassEx(classNode)
    }


    private void checkMethods(classMetricResult) {
        def methodResults = classMetricResult.methodMetricResults
        methodResults.each { methodName, results ->
            if (results['total'] > getMaxMethodMetricValue() &&
                    !isIgnoredMethodName(methodName)) {
                def message = "Violation in class $currentClassName. The ${getMetricShortDescription()} for method [$methodName] is [${results['total']}]"
                def lineNumber = getLineNumber(results)
                def sourceLine = getSourceLine(lineNumber)
                violations.add(new Violation(rule:rule, lineNumber:lineNumber, sourceLine:sourceLine, message:message))
            }
        }
    }

    protected getMethodNode(ClassNode classNode, String methodName, results) {
        def matchingMethods = classNode.getDeclaredMethods(methodName)
        if (matchingMethods.size() == 1) {
            return matchingMethods[0]
        }
        def lineNumber = getLineNumber(results)
        matchingMethods.find { method -> method.lineNumber == lineNumber }
    }

    private void checkClass(classMetricResult, classNode) {
        def className = classNode.name
        def methodResults = classMetricResult.classMetricResult
        if (methodResults['average'] > getMaxClassMetricValue()) {
            def message = "The ${getMetricShortDescription()} for class [$className] is [${methodResults['average']}]"
            def lineNumber = getLineNumber(methodResults)
            def sourceLine = getSourceLine(lineNumber)
            violations.add(new Violation(rule:rule, lineNumber:lineNumber, sourceLine:sourceLine, message:message))
        }
    }

    protected getLineNumber(methodResults) {
        def lineNumber = AstUtil.respondsTo(methodResults, 'getLineNumber')? methodResults.getLineNumber() : null
        (lineNumber == -1) ? null : lineNumber
    }

    protected String getSourceLine(lineNumber) {
        lineNumber == null ?: this.sourceCode.line(lineNumber-1)
    }

    protected boolean isIgnoredMethodName(String methodName) {
        new WildcardPattern(rule.ignoreMethodNames, false).matches(methodName)
    }
}