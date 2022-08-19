/*
 * Copyright 2012 the original author or authors.
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
import org.gmetrics.metric.Metric
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.MetricResult

/**
 * Abstract superclass for AstVisitor classes that use method-level GMetrics Metrics.
 *
 * Subclasses must:
 * <ul>
 *   <li>Implement the abstract <code>createMetric()</code> method</li>
 *   <li>Implement the abstract <code>getMetricShortDescription()</code> method</li>
 *   <li>Implement the abstract <code>getMaxMethodMetricValue()</code> method</li>
 *   <li>Implement the abstract <code>getMaxClassAverageMethodMetricValue()</code> method</li>
 *   <li>The owning Rule class must have the <code>ignoreMethodNames</code> property</li>
 * </ul>
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
abstract class AbstractMethodMetricAstVisitor extends AbstractAstVisitor  {

    protected Metric metric
    private final Object metricLock = new Object()

    protected abstract Metric createMetric()
    protected abstract String getMetricShortDescription()
    protected abstract Object getMaxMethodMetricValue()
    protected abstract Object getMaxClassMetricValue()
    protected abstract Object getMaxClassAverageMethodMetricValue()

    private Metric getMetric() {
        synchronized(metricLock) {
            if (metric == null) {
                metric = createMetric()
            }
            return metric
        }
    }

    @Override
    void visitClassEx(ClassNode classNode) {
        def gmetricsSourceCode = new GMetricsSourceCodeAdapter(this.sourceCode)
        def classMetricResult = getMetric().applyToClass(classNode, gmetricsSourceCode)

        if (classMetricResult == null) {    // no methods or closure fields
            return
        }

        checkMethods(classMetricResult)

        if (isNotGeneratedCode(classNode)) {
            if (!(classNode.isScript() && classNode.name == 'None')) {
                checkClass(classMetricResult, classNode)
            }
        }
        super.visitClassEx(classNode)
    }

    private void checkMethods(ClassMetricResult classMetricResult) {
        def methodResults = classMetricResult.methodMetricResults
        methodResults.each { method, results ->
            String methodName = extractMethodName(method)
            if (results['total'] > getMaxMethodMetricValue() && !isIgnoredMethodName(methodName)) {
                def message = "Violation in class $currentClassName. The ${getMetricShortDescription()} for method [$methodName] is [${results['total']}]"
                addViolation(results, message)
            }
        }
    }

    @SuppressWarnings('MethodParameterTypeRequired')
    protected String extractMethodName(method) {
        // For GMetrics 0.4, it is a String; For GMetrics 0.5 it is a MethodKey
        method instanceof String ? method : method.methodName
    }

    private void checkClass(ClassMetricResult classMetricResult, ClassNode classNode) {
        def className = classNode.name
        def classResults = classMetricResult.classMetricResult
        if (getMaxClassAverageMethodMetricValue() && classResults['average'] > getMaxClassAverageMethodMetricValue()) {
            def message = "The average method ${getMetricShortDescription()} for class [$className] is [${classResults['average']}]"
            addViolation(classResults, message)
        }

        if (getMaxClassMetricValue() && classResults['total'] > getMaxClassMetricValue()) {
            def message = "The total class ${getMetricShortDescription()} for class [$className] is [${classResults['total']}]"
            addViolation(classResults, message)
        }
    }

    protected void addViolation(MetricResult classResults, String message) {
        def lineNumber = getLineNumber(classResults)
        def sourceLine = getSourceLine(lineNumber)
        violations.add(new Violation(rule:rule, lineNumber:lineNumber, sourceLine:sourceLine, message:message))
    }

    protected Integer getLineNumber(MetricResult methodResults) {
        def lineNumber = AstUtil.respondsTo(methodResults, 'getLineNumber') ? methodResults.getLineNumber() : null
        (lineNumber == -1) ? null : lineNumber
    }

    protected String getSourceLine(Integer lineNumber) {
        lineNumber == null ?: this.sourceCode.line(lineNumber - 1)
    }

    protected boolean isIgnoredMethodName(String methodName) {
        new WildcardPattern(rule.ignoreMethodNames, false).matches(methodName)
    }
}
