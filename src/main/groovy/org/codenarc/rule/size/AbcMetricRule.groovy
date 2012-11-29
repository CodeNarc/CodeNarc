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

import org.codenarc.rule.AbstractAstVisitorRule
import org.gmetrics.metric.abc.AbcMetric

/**
 * Rule that calculates the ABC metric score for methods/classes and checks against
 * configured threshold values.
 * <p/>
 * The <code>maxMethodAbcScore</code> property holds the threshold value for the ABC score
 * (magnitude) for each method. If this value is non-zero, a method with an ABC score greater than
 * this value is considered a violation. The value does not have to be an integer (i.e., 1.7 is allowed). The
 * <code>maxMethodAbcScore</code> property defaults to 60.
 * <p/>
 * The <code>maxClassAverageMethodAbcScore</code> property holds the threshold value for the average ABC
 * score for each class. If this value is non-zero, a class with an average ABC score
 * greater than this value is considered a violation. The value does not have to be an integer
 * (i.e., 1.7 is allowed). The <code>maxClassAverageMethodAbcScore</code> property defaults to 60.
 * <p/>
 * The <code>maxClassAbcScore</code> property holds the threshold value for the total ABC
 * score value for each class. If this value is non-zero, a class with a total ABC score
 * greater than this value is considered a violation. The value does not have to be an integer
 * (i.e., 1.7 is allowed). The <code>maxClassAbcScore</code> property defaults to 0.
 * <p/>
 * The <code>ignoreMethodNames</code> property optionally specifies one or more (comma-separated) method
 * names that should be ignored (i.e., that should not cause a rule violation). The name(s) may optionally
 * include wildcard characters ('*' or '?'). Note that the ignored methods still contribute to the class
 * complexity value.
 * <p/>
 * This rule treats "closure fields" as methods. If a class field is initialized to a Closure (ClosureExpression),
 * then that Closure is analyzed and checked just like a method.
 *
 * @see <a href="http://www.softwarerenovation.com/ABCMetric.pdf">ABC Metric specification</a>.
 * @see <a href="http://jakescruggs.blogspot.com/2008/08/whats-good-flog-score.html">Blog post</a> describing guidelines for interpreting an ABC score.
 * @see <a href="http://gmetrics.sourceforge.net/gmetrics-AbcMetric.html">GMetrics ABC metric</a>.
 *
 * @author Chris Mair
  */
class AbcMetricRule extends AbstractAstVisitorRule {

    String name = 'AbcMetric'
    int priority = 2
    Class astVisitorClass = AbcMetricAstVisitor
    int maxMethodAbcScore = 60
    int maxClassAverageMethodAbcScore = 60
    int maxClassAbcScore = 0
    String ignoreMethodNames
}

class AbcMetricAstVisitor extends AbstractMethodMetricAstVisitor  {

    final String metricShortDescription = 'ABC score'

    protected Object createMetric() {
        new AbcMetric()
    }

    protected Object getMaxMethodMetricValue() {
        rule.maxMethodAbcScore
    }

    protected Object getMaxClassAverageMethodMetricValue() {
        rule.maxClassAverageMethodAbcScore
    }

    protected Object getMaxClassMetricValue() {
        rule.maxClassAbcScore
    }
}
