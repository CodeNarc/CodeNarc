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

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

import static org.codenarc.test.TestUtil.captureLog4JMessages

/**
 * Tests for CrapMetricRule
 *
 * @author Chris Mair
  */
class CrapMetricRuleTest extends AbstractRuleTestCase {

    private static final String COBERTURA_FILE = 'coverage/Cobertura-example.xml'
    private static final BigDecimal CRAP_SCORE = 6.0
    private static final String CLASS_NAME = 'com.example.service.Email'
    private static final String METHOD_NAME = 'toString'
    private static final String METRIC_DESCRIPTION = 'CRAP score'

    private static final SOURCE = '''
        package com.example.service
        class Email {
            String toString() {     // complexity=4, coverage=0.5
                if (ready || paused || started) return null
            }
        }
        '''

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CrapMetric'
        assert rule.maxMethodCrapScore == 30.0
        assert rule.maxClassAverageMethodCrapScore == 30.0
        assert rule.maxClassCrapScore == 0
        assert rule.crapMetricClassName == 'org.gmetrics.metric.crap.CrapMetric'
    }

    @Test
    void testCoberturaXmlFileNullOrEmpty_IsReadyReturnsFalse() {
        def logEvents = captureLog4JMessages {
            rule.coberturaXmlFile = null
            assert !rule.ready

            rule.coberturaXmlFile = ''
            assert !rule.ready
        }
        assertNumberOfLogMessages(logEvents, 'Cobertura XML file', 1)
    }

    @Test
    void testApplyTo_CoberturaXmlFileDoesNotExist_IsReadyReturnsFalse_OnlyLogsWarningOnce() {
        rule.coberturaXmlFile = 'DoesNotExist.xml'
        assert !rule.ready
    }

    @Test
    void testApplyTo_CoberturaXmlFileDoesNotExist_NoViolations_OnlyLogsWarningOnce() {
        rule.coberturaXmlFile = 'DoesNotExist.xml'
        def logEvents = captureLog4JMessages {
            assertNoViolations(SOURCE)
            assertNoViolations(SOURCE)
        }
        assertNumberOfLogMessages(logEvents, 'Cobertura XML file', 1)
    }

    @Test
    void testCrapMetricClassNotOnClassPath_IsReadyReturnsFalse() {
        rule.crapMetricClassName = 'some.NonExistentClass'
        assert !rule.ready
    }

    @Test
    void testCrapMetricClassNotOnClassPath_NoViolations() {
        rule.crapMetricClassName = 'some.NonExistentClass'
        rule.maxMethodCrapScore = 1.0

        def logEvents = captureLog4JMessages {
            assertNoViolations(SOURCE)
            assertNoViolations(SOURCE)
        }
        assertNumberOfLogMessages(logEvents, 'GMetrics CrapMetric class', 1)
    }

    @Test
    void testApplyTo_ClassWithNoMethods() {
        final SOURCE = '''
            class MyClass {
                def myValue = 23
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SingleMethod_EqualToMaxMethodCrapScore() {
        rule.maxMethodCrapScore = CRAP_SCORE
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SingleMethod_ExceedsMaxMethodCrapScore() {
        rule.maxMethodCrapScore = 5.0
        assertSingleViolation(SOURCE, 4, 'String toString() {', [CLASS_NAME, METRIC_DESCRIPTION, METHOD_NAME, CRAP_SCORE])
    }

    @Test
    void testSuppressWarningsOnClass() {
        final SOURCE = '''
            package com.example.service
            @SuppressWarnings('CrapMetric')
            class Email {
                String toString() {
                    if (ready || paused || started) return null
                }
            }
        '''
        rule.maxMethodCrapScore = 1.0
        assert manuallyApplyRule(SOURCE).size() == 0
    }

    @Test
    void testSuppressWarningsOnMethod() {
        final SOURCE = '''
            package com.example.service
            class Email {
            @SuppressWarnings('CrapMetric')
            String toString() {
                    if (ready || paused || started) return null
                }
            }
        '''
        rule.maxMethodCrapScore = 1.0
        assert manuallyApplyRule(SOURCE).size() == 0
    }

    @Test
    void testApplyTo_IgnoresClosureFields() {
        final SOURCE = '''
            class MyClass {
                def myClosure = { a && b && c && d && e }
            }
        '''
        rule.maxMethodCrapScore = 1.0
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoresMethodThatHasNoCoverageInformation() {
        final SOURCE = '''
            package com.example.service
            class Email {
                String unknown() {
                    if (ready || paused || started) return null
                }
            }
        '''
        rule.maxMethodCrapScore = 1.0
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoresAbstractMethods() {
        final SOURCE = '''
            package com.example.service
            abstract class Email {
                abstract String toString()
            }
        '''
        rule.maxMethodCrapScore = 1.0
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Class_ExceedsMaxAverageClassComplexity() {
        rule.maxClassAverageMethodCrapScore = 1.0
        assertSingleViolation(SOURCE, 3, 'class Email', [CLASS_NAME, METRIC_DESCRIPTION, CRAP_SCORE])
    }

    @Test
    void testApplyTo_Class_ExceedsMaxClassComplexity() {
        rule.maxClassCrapScore = 1.0
        assertSingleViolation(SOURCE, 3, 'class Email', [CLASS_NAME, METRIC_DESCRIPTION, 'total', CRAP_SCORE])
    }

    @Test
    void testApplyTo_Class_ZeroMaxClassAverageMethodCrapScore_NoViolations() {
        rule.maxClassAverageMethodCrapScore = 0.0
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Class_NullMaxClassAverageMethodCrapScore_NoViolations() {
        rule.maxClassAverageMethodCrapScore = null
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClassAndMethod_ExceedThreshold() {
        rule.maxMethodCrapScore = 1.0
        rule.maxClassAverageMethodCrapScore = 1.0
        rule.maxClassCrapScore = 5.9
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'class Email', messageText:[CLASS_NAME, METRIC_DESCRIPTION, 'average', CRAP_SCORE]],
                [lineNumber:3, sourceLineText:'class Email', messageText:[CLASS_NAME, METRIC_DESCRIPTION, 'total', CRAP_SCORE]],
                [lineNumber:4, sourceLineText:'String toString() {', messageText:[CLASS_NAME, METRIC_DESCRIPTION, METHOD_NAME, CRAP_SCORE]])
    }

    @Test
    void testApplyTo_ClassAndMethods_AtThreshold() {
        rule.maxMethodCrapScore = CRAP_SCORE
        rule.maxClassAverageMethodCrapScore = CRAP_SCORE
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesSingleName() {
        rule.maxMethodCrapScore = 1.0
        rule.ignoreMethodNames = METHOD_NAME
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesNoNames() {
        rule.maxMethodCrapScore = 1.0
        rule.ignoreMethodNames = 'other,x*'
        assertSingleViolation(SOURCE, 4, 'String toString() {', [CLASS_NAME, METRIC_DESCRIPTION, METHOD_NAME, CRAP_SCORE])
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MultipleNamesWithWildcards() {
        rule.ignoreMethodNames = 'myM*d*,t?Str*ng'
        assertNoViolations(SOURCE)
    }

    @Override
    protected Rule createRule() {
        new CrapMetricRule(coberturaXmlFile:COBERTURA_FILE)
    }

    private void assertNumberOfLogMessages(logEvents, String expectedText, int expectedCount) {
        def matchingLogEvents = logEvents.findAll { it.message.contains(expectedText) }
        assert matchingLogEvents.size() == expectedCount
    }

}
