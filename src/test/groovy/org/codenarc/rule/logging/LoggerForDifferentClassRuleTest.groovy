/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.logging

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for LoggerForDifferentClassRule
 *
 * @author Chris Mair
  */
class LoggerForDifferentClassRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'LoggerForDifferentClass'
    }

    @Test
    void testApplyTo_NoLoggers_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final int count = 67
            }
        '''
        assertNoViolations(SOURCE)
    }

    // Logger (Log4J and Java Logging API) Tests

    @Test
    void testApplyTo_Logger_Violations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = Logger.getLogger(SomeOtherClass)
                def log1 = Logger.getLogger(SomeOtherClass.class)
                def log2 = Logger.getLogger(SomeOtherClass.class.name)
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'private static final LOG = Logger.getLogger(SomeOtherClass)'],
            [lineNumber:4, sourceLineText:'def log1 = Logger.getLogger(SomeOtherClass.class)'],
            [lineNumber:5, sourceLineText:'def log2 = Logger.getLogger(SomeOtherClass.class.name)'])
    }

    @Test
    void testApplyTo_Logger_SameClass_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = Logger.getLogger(MyClass)
                def log2 = Logger.getLogger(MyClass.class)
                private static log3 = Logger.getLogger(MyClass.getClass().getName())
                private static log4 = Logger.getLogger(MyClass.getClass().name)
                private static log5 = Logger.getLogger(MyClass.class.getName())
                private static log6 = Logger.getLogger(MyClass.class.name)
                private static log7 = Logger.getLogger(MyClass.name)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Logger_SameClass_NoViolations_DerivedAllowed() {
        rule.allowDerivedClasses = true
        final SOURCE = '''
            class MyClass {
                private final LOG1 = Logger.getLogger(this.class)
                private final LOG2 = Logger.getLogger(this.getClass())
                private final LOG3 = Logger.getLogger(getClass())
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Logger_SameClass_Violations_DerivedAllowed() {
        rule.allowDerivedClasses = true
        final SOURCE = '''
            class MyClass {
                private final LOG1 = Logger.getLogger(unknown)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private final LOG1 = Logger.getLogger(unknown)', 'Logger is defined in MyClass but initialized with unknown')
    }

    @Test
    void testApplyTo_Logger_This_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = Logger.getLogger(this)
                def log2 = Logger.getLogger(this.class)
                private static log3 = Logger.getLogger(this.getName())
                private static log4 = Logger.getLogger(this.name)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Logger_NotAClassOrClassName_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static log1 = Logger.getLogger(getLogName())
                private static log2 = Logger.getLogger("some.OtherName")
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Logger_ConstantForLoggerName_Violation() {
        final SOURCE = '''
            class MyClass {
                private static final log = Logger.getLogger(CONSTANT)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private static final log = Logger.getLogger(CONSTANT)', 'Logger is defined in MyClass but initialized with CONSTANT')
    }

    @Test
    void testApplyTo_Logger_FullPackageNameOfLogger_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final log = org.apache.log4.Logger.getLogger(SomeOtherClass)
            }
        '''
        assertNoViolations(SOURCE)
    }

    // LogFactory (Commons Logging) Tests

    @Test
    void testApplyTo_LogFactory_Violations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = LogFactory.getLog(SomeOtherClass)
                Log log = LogFactory.getLog(SomeOtherClass.class)
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'private static final LOG = LogFactory.getLog(SomeOtherClass)'],
            [lineNumber:4, sourceLineText:'Log log = LogFactory.getLog(SomeOtherClass.class)'])
    }

    @Test
    void testApplyTo_LogFactory_SameClass_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = LogFactory.getLog(MyClass)
                def log2 = LogFactory.getLog(MyClass.class)
                private static log3 = LogFactory.getLog(MyClass.getClass().getName())
                private static log4 = LogFactory.getLog(MyClass.getClass().name)
                private static log5 = LogFactory.getLog(MyClass.class.getName())
                private static log6 = LogFactory.getLog(MyClass.class.name)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_LogFactory_This_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = LogFactory.getLog(this)
                def log2 = LogFactory.getLog(this.class)
                private static log3 = LogFactory.getLog(this.getName())
                private static log4 = LogFactory.getLog(this.name)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_LogFactory_NotAClassOrClassName_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final log1 = LogFactory.getLog(getLogName())
                private static final log2 = LogFactory.getLog("some.OtherName")
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_LogFactory_ConstantForLoggerName_Violation() {
        final SOURCE = '''
            class MyClass {
                private static final log = LogFactory.getLog(CONSTANT)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private static final log = LogFactory.getLog(CONSTANT)')
    }

    @Test
    void testApplyTo_LogFactory_ConstantForLoggerName_InnerClassViolation() {
        final SOURCE = '''
            class MyClass {
                class MyOtherClass {
                }
                private static final log = LogFactory.getLog(MyOtherClass)
            }
        '''
        assertSingleViolation(SOURCE, 5, 'private static final log = LogFactory.getLog(MyOtherClass)')
    }

    @Test
    void testApplyTo_LogFactory_FullPackageNameOfLogFactory_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final log = org.apache.commons.logging.LogFactory.getLog(SomeOtherClass)
            }
        '''
        assertNoViolations(SOURCE)
    }

    // LoggerFactory (SLF4J and Logback) Tests

    @Test
    void testApplyTo_LoggerFactory_Violations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = LoggerFactory.getLogger(SomeOtherClass)
                Log log = LoggerFactory.getLogger(SomeOtherClass.class)
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'private static final LOG = LoggerFactory.getLogger(SomeOtherClass)'],
            [lineNumber:4, sourceLineText:'Log log = LoggerFactory.getLogger(SomeOtherClass.class)'])
    }

    @Test
    void testApplyTo_LoggerFactory_SameClass_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = LoggerFactory.getLogger(MyClass)
                def log2 = LoggerFactory.getLogger(MyClass.class)
                private static log3 = LoggerFactory.getLogger(MyClass.getClass().getName())
                private static log4 = LoggerFactory.getLogger(MyClass.getClass().name)
                private static log5 = LoggerFactory.getLogger(MyClass.class.getName())
                private static log6 = LoggerFactory.getLogger(MyClass.class.name)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_LoggerFactory_This_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = LoggerFactory.getLogger(this)
                def log2 = LoggerFactory.getLogger(this.class)
                private static log3 = LoggerFactory.getLogger(this.getName())
                private static log4 = LoggerFactory.getLogger(this.name)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_LoggerFactory_NotAClassOrClassName_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final log1 = LoggerFactory.getLogger(getLogName())
                private static final log2 = LoggerFactory.getLogger("some.OtherName")
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_LoggerFactory_ConstantForLoggerName_Violation() {
        final SOURCE = '''
            class MyClass {
                private static final log = LoggerFactory.getLogger(CONSTANT)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private static final log = LoggerFactory.getLogger(CONSTANT)')
    }

    @Test
    void testApplyTo_LoggerFactory_FullPackageNameOfLoggerFactory_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final log = org.apache.commons.logging.LoggerFactory.getLogger(SomeOtherClass)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInnerClasses() {
        final SOURCE = '''
        class Outer {
            private class InnerRunnable implements Runnable {
                final Logger LOGGER = LoggerFactory.getLogger(InnerRunnable.class)
            }
        }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInnerClassViolation() {
        final SOURCE = '''
        class Outer {
            private class InnerRunnable implements Runnable {
                final Logger LOGGER = LoggerFactory.getLogger(Outer)
            }
        }
        '''
        assertSingleViolation(SOURCE, 4, 'LoggerFactory.getLogger(Outer)', 'Logger is defined in InnerRunnable but initialized with Outer')
    }

    protected Rule createRule() {
        new LoggerForDifferentClassRule()
    }
}
