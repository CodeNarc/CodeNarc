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

import org.apache.log4j.Logger
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AstVisitor
import org.codenarc.util.io.DefaultResourceFactory
import org.gmetrics.metric.coverage.CoberturaLineCoverageMetric
import org.gmetrics.metric.crap.CrapMetric

/**
 * Rule that calculates the CRAP Metric for methods/classes and checks against
 * configured threshold values.
 * <p/>
 * Note that this rule requires the GMetrics 0.5 (or later) jar on the classpath, as well as
 * a Cobertura XML coverage file. If either of these prerequisites is not available, this rule
 * logs a warning messages and exits (i.e., does nothing).
 * <p/>
 * The <code>coberturaXmlFile</code> property must be set to the path to the Cobertura XML coverage file
 * for the Groovy code being analyzed. By default, the path is relative to the classpath. But the path
 * may be optionally prefixed by any of the valid java.net.URL prefixes, such as "file:" (to load from
 * a relative or absolute path on the filesystem), or "http:". This property is REQUIRED.
 * <p/>
 * The <code>maxMethodCrapScore</code> property holds the threshold value for the CRAP crapMetric
 * value for each method. If this value is non-zero, a method with a CRAP score value greater than
 * this value is considered a violation. The <code>maxMethodCrapScore</code> property defaults to 30.
 * <p/>
 * The <code>maxClassAverageCrapScore</code> property holds the threshold value for the average CRAP
 * crapMetric value for each class. If this value is non-zero, a class with an average CRAP score
 * value greater than this value is considered a violation. The <code>maxMethodAverageCrapScore</code> property
 * defaults to 30.
 * <p/>
 * The <code>maxClassCrapScore</code> property holds the threshold value for the total CRAP
 * crapMetric value for each class. If this value is non-zero, a class with a total CRAP score
 * value greater than this value is considered a violation. The <code>maxClassCrapScore</code> property
 * defaults to 0.
 * <p/>
 * The <code>ignoreMethodNames</code> property optionally specifies one or more (comma-separated) method
 * names that should be ignored (i.e., that should not cause a rule violation). The name(s) may optionally
 * include wildcard characters ('*' or '?'). Note that the ignored methods still contribute to the class
 * complexity value.
 * <p/>
 * This rule does NOT treat "closure fields" as methods (unlike some of the other size/complexity rules).
 *
 * @see <a href="http://www.artima.com/weblogs/viewpost.jsp?thread=210575">The original 2007 blog post that defined the CRAP crapMetric</a>.
 * @see <a href="http://googletesting.blogspot.com/2011/02/this-code-is-crap.html">A 2011 blog post from Alberto Savoia, describing the formula, the motivation, and the CRAP4J tool</a>.
 * @see <a href="http://gmetrics.sourceforge.net/gmetrics-CrapMetric.html">GMetrics CRAP crapMetric</a>.
 *
 * @author Chris Mair
  */
class CrapMetricRule extends AbstractAstVisitorRule {

    private static final LOG = Logger.getLogger(CrapMetricRule)

    String name = 'CrapMetric'
    int priority = 2
    BigDecimal maxMethodCrapScore = 30
    BigDecimal maxClassAverageMethodCrapScore = 30
    BigDecimal maxClassCrapScore = 0
    String coberturaXmlFile
    String ignoreMethodNames

    protected String crapMetricClassName = 'org.gmetrics.metric.crap.CrapMetric'
    private Boolean ready
    private crapMetric      // omit CrapMetric type; it may not be on the classpath
    private final readyLock = new Object()
    private final createMetricLock = new Object()
    private final resourceFactory = new DefaultResourceFactory()

    @Override
    AstVisitor getAstVisitor() {
        return new CrapMetricAstVisitor(createCrapMetric())
    }

    @Override
    boolean isReady() {
        synchronized(readyLock) {
            if (ready == null) {
                ready = true
                if (!doesCoberturaXmlFileExist()) {
                    LOG.warn("The Cobertura XML file [$coberturaXmlFile] is not accessible; skipping this rule")
                    ready = false
                }
                if (!isCrapMetricClassOnClasspath()) {
                    LOG.warn('The GMetrics CrapMetric class is not on the classpath; skipping this rule')
                    ready = false
                }
            }
        }
        return ready
    }

    private boolean doesCoberturaXmlFileExist() {
        if (!coberturaXmlFile) {
            return false
        }
        def resource = resourceFactory.getResource(coberturaXmlFile)
        return resource.exists()
    }

    private createCrapMetric() {        // omit CrapMetric type; it may not be on the classpath
        synchronized(createMetricLock) {
            if (!crapMetric) {
                def coverageMetric = new CoberturaLineCoverageMetric(coberturaFile:coberturaXmlFile)
                crapMetric = new CrapMetric(coverageMetric:coverageMetric)
            }
        }
        return crapMetric
    }

    private boolean isCrapMetricClassOnClasspath() {
        try {
            getClass().classLoader.loadClass(crapMetricClassName)
            return true
        }
        catch (ClassNotFoundException e) {
            return false
        }
    }
}

class CrapMetricAstVisitor extends AbstractMethodMetricAstVisitor  {

    final String metricShortDescription = 'CRAP score'

    private final CrapMetric crapMetric

    protected CrapMetricAstVisitor(CrapMetric crapMetric) {
        this.crapMetric = crapMetric
    }

    @Override
    protected Object createMetric() {
        return crapMetric
    }

    @Override
    protected Object getMaxMethodMetricValue() {
        rule.maxMethodCrapScore
    }

    @Override
    protected Object getMaxClassAverageMethodMetricValue() {
        rule.maxClassAverageMethodCrapScore
    }

    protected Object getMaxClassMetricValue() {
        rule.maxClassCrapScore
    }
}
