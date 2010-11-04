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
 * @version $Revision$ - $Date$
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
            checkClass(classMetricResult, classNode.name)
        }
        super.visitClassEx(classNode)
    }


    private void checkMethods(classMetricResult) {
        def methodResults = classMetricResult.methodMetricResults
        methodResults.each { methodName, results ->
            if (results['total'] > getMaxMethodMetricValue() && !isIgnoredMethodName(methodName)) {
                def message = "The ${getMetricShortDescription()} for method [$methodName] is [${results['total']}]"
                def lineNumber = getLineNumber(results)
                def sourceLine = getSourceLine(lineNumber)
                violations.add(new Violation(rule:rule, lineNumber:lineNumber, sourceLine:sourceLine, message:message))
            }
        }
    }

    private void checkClass(classMetricResult, String className) {
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
        return (lineNumber != -1) ? lineNumber : null
    }

    protected String getSourceLine(lineNumber) {
        return lineNumber != null ? this.sourceCode.line(lineNumber-1) : null
    }

    protected boolean isIgnoredMethodName(String methodName) {
        return new WildcardPattern(rule.ignoreMethodNames, false).matches(methodName)
    }
}