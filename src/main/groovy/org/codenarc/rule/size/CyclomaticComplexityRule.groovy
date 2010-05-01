/*
 * Copyright 2009 the original author or authors.
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
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric
import org.codenarc.util.AstUtil
import org.codenarc.util.WildcardPattern
import org.codehaus.groovy.ast.MethodNode

/**
 * Rule that calculates the Cyclomatic Complexity for methods/classes and checks against
 * configured threshold values.
 * <p/>
 * The <code>maxMethodComplexity</code> property holds the threshold value for the cyclomatic complexity
 * value for each method. If this value is non-zero, a method with a cyclomatic complexity value greater than
 * this value is considered a violation. The <code>maxMethodComplexity</code> property defaults to 20.
 * <p/>
 * The <code>maxClassAverageMethodComplexity</code> property holds the threshold value for the average cyclomatic
 * complexity value for each class. If this value is non-zero, a class with an average cyclomatic complexity
 * value greater than this value is considered a violation. The <code>maxMethodComplexity</code> property
 * defaults to 20.
 * <p/>
 * The <code>ignoreMethodNames</code> property optionally specifies one or more (comma-separated) method
 * names that should be ignored (i.e., that should not cause a rule violation). The name(s) may optionally
 * include wildcard characters ('*' or '?'). Note that the ignored methods still contribute to the class
 * complexity value.
 * <p/>
 * This rule treats "closure fields" as methods. If a class field is initialized to a Closure (ClosureExpression),
 * then that Closure is analyzed and checked just like a method.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Cyclomatic_complexity">Cyclomatic Complexity Wikipedia entry</a>.
 * @see <a href="http://www.literateprogramming.com/mccabe.pdf">The original paper describing Cyclomatic Complexity</a>.
 * @see <a href="http://gmetrics.sourceforge.net/gmetrics-CyclomaticComplexityMetric.html">GMetrics Cyclomatic Complexity metric</a>.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class CyclomaticComplexityRule extends AbstractAstVisitorRule {
    String name = 'CyclomaticComplexity'
    int priority = 2
    Class astVisitorClass = CyclomaticComplexityAstVisitor
    int maxMethodComplexity = 20
    int maxClassAverageMethodComplexity = 20
    String ignoreMethodNames
}

class CyclomaticComplexityAstVisitor extends AbstractAstVisitor  {
    private metric = new CyclomaticComplexityMetric()

    void visitClass(ClassNode classNode) {
        if (!AstUtil.isFromGeneratedSourceCode(classNode)) {
            def gmetricsSourceCode = new GMetricsSourceCodeAdapter(this.sourceCode)
            def classMetricResult = metric.calculateForClass(classNode, gmetricsSourceCode)
    
            if (classMetricResult == null) {    // no methods or closure fields
                return
            }

            checkMethods(classMetricResult)
            checkClass(classMetricResult, classNode.name)
        }
        super.visitClass(classNode)
    }


    private void checkMethods(classMetricResult) {
        def methodResults = classMetricResult.methodMetricResults
        methodResults.each { methodName, results ->
            if (results.total > rule.maxMethodComplexity && !isIgnoredMethodName(methodName)) {
                def message = "The cyclomatic complexity for method [$methodName] is [${results.total}]"
                // TODO include line number and source line
                violations.add(new Violation(rule:rule, message:message))
            }
        }
    }

    private void checkClass(classMetricResult, String className) {
        def methodResults = classMetricResult.classMetricResult
        if (methodResults.average > rule.maxClassAverageMethodComplexity) {
            def message = "The cyclomatic complexity for class [$className] is [${methodResults.average}]"
            // TODO include line number and source line
            violations.add(new Violation(rule:rule, message:message))
        }
    }

    private boolean isIgnoredMethodName(String methodName) {
        return new WildcardPattern(rule.ignoreMethodNames, false).matches(methodName)
    }
}