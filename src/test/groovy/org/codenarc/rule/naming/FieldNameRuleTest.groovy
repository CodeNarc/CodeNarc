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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for FieldNameRule
 *
 * @author Chris Mair
  */
class FieldNameRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'FieldName'
    }

    @Test
    void testRegex_DefaultValue() {
        assert 'abc' ==~ rule.regex
        assert 'aXaX123' ==~ rule.regex
        assert !('abc_def' ==~ rule.regex)
        assert !('ABC123abc' ==~ rule.regex)
    }

    @Test
    void testFinalRegex_DefaultValue() {
        assert rule.finalRegex == null
    }

    @Test
    void testStaticRegex_DefaultValue() {
        assert rule.staticRegex == null
    }

    @Test
    void testStaticFinalRegex_DefaultValue() {
        assert 'ABC' ==~ rule.staticFinalRegex
        assert 'ABC_123_DEF' ==~ rule.staticFinalRegex
        assert !('abcdef' ==~ rule.staticFinalRegex)
        assert !('ABC123abc' ==~ rule.staticFinalRegex)
    }

    @Test
    void testRegexIsNull() {
        rule.regex = null
        shouldFailWithMessageContaining('regex') { applyRuleTo('class MyClass { int count }') }
    }

    @Test
    void testApplyTo_MatchesNewRegex() {
        final SOURCE = '''
          class MyClass {
            private File bug4013
          }
        '''
        rule.regex = /^[a-z]([a-zA-Z0-9$])*\b/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_DoesNotMatchNewRegex() {
        final SOURCE = '''
          class MyClass {
            private BigDecimal deposit_amount
          }
        '''
        assertSingleViolation(SOURCE, 3, 'BigDecimal deposit_amount', 'The fieldname deposit_amount in class MyClass does not match [a-z][a-zA-Z0-9]*')
    }

    @Test
    void testApplyTo_MatchesDefaultRegex() {
        final SOURCE = '''
            class MyClass {
                protected int count
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Static_DoesNotMatchDefaultRegex() {
        final SOURCE = '''
          class MyClass {
            protected static int Count
          }
        '''
        assertSingleViolation(SOURCE, 3, 'static int Count')
    }

    @Test
    void testApplyTo_DoesNotMatchCustomRegex() {
        final SOURCE = '''
            class MyClass {
                public int count
            }
        '''
        rule.regex = /z.*/
        assertSingleViolation(SOURCE, 3, 'public int count')
    }

    @Test
    void testApplyTo_MatchesCustomRegex() {
        final SOURCE = '''
            class MyClass {
              public zMethod() { println 'bad' }
            }
        '''
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_DoesNotMatchDefaultRegex_NoClassDefined() {
        final SOURCE = ' int Count '
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MatchesCustomRegex_NoClassDefined() {
        final SOURCE = ' int zCount '
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Final_DefaultFinalRegex() {
        final SOURCE = '''
          class MyClass {
            public final int COUNT
          }
        '''
        assertSingleViolation(SOURCE, 3, 'final int COUNT')
    }

    @Test
    void testApplyTo_Final_FinalRegexSet() {
        final SOURCE = '''
            class MyClass {
                private final int zCount
            }
        '''
        rule.finalRegex = /z.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Static_StaticRegexNotSet() {
        final SOURCE = '''
          class MyClass {
            protected static int Count
          }
        '''
        assertSingleViolation(SOURCE, 3, 'static int Count')
    }

    @Test
    void testApplyTo_Static_StaticRegexSet() {
        final SOURCE = '''
            class MyClass {
                public static int Count
            }
        '''
        rule.staticRegex = /C.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticFinal_DefaultStaticFinalRegex() {
        final SOURCE = '''
          class MyClass {
            public static final int count
          }
        '''
        assertSingleViolation(SOURCE, 3, 'static final int count')
    }

    @Test
    void testApplyTo_StaticFinal_CustomStaticFinalRegex() {
        final SOURCE = '''
            class MyClass {
                private static final int Count
            }
        '''
        rule.staticRegex = /Z.*/    // ignored
        rule.finalRegex = /X.*/     // ignored
        rule.staticFinalRegex = /C.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PrivateStaticFinal_CustomStaticFinalRegex() {
        final SOURCE = '''
            class MyClass {
                private static final int Count
            }
        '''
        rule.staticRegex = /Z.*/    // ignored
        rule.finalRegex = /X.*/     // ignored
        rule.staticFinalRegex = /C.*/ //ignored
        rule.privateStaticFinalRegex = /[a-z][a-zA-Z0-9]*/
        assertSingleViolation(SOURCE, 3, 'private static final int Count')
    }

    @Test
    void testApplyTo_StaticFinal_StaticFinalRegexIsNull_DefaultsToFinalRegex() {
        final SOURCE = '''
          class MyClass {
            protected static final int COUNT
          }
        '''
        rule.staticRegex = /C.*/       // ignored
        rule.finalRegex = /X.*/
        rule.staticFinalRegex = null
        assertSingleViolation(SOURCE, 3, 'static final int COUNT')
    }

    @Test
    void testApplyTo_StaticFinal_DefaultsToStaticRegex() {
        final SOURCE = '''
          class MyClass {
            private static final int Count
          }
        '''
        rule.finalRegex = null
        rule.staticRegex = /C.*/
        rule.staticFinalRegex = null
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticFinal_DefaultsToRegex() {
        final SOURCE = '''
          class MyClass {
            public static final int Count
          }
        '''
        rule.staticFinalRegex = null
        rule.finalRegex = null
        rule.staticRegex = null
        rule.regex = /C.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoresSerialVersionUID () {
        final SOURCE = '''
          class MyClass {
            private static final long serialVersionUID = 1234567890L
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreFieldNames_MatchesSingleName() {
        final SOURCE = '''
          class MyClass {
            protected static int Count
          }
        '''
        rule.ignoreFieldNames = 'Count'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreFieldNames_MatchesNoNames() {
        final SOURCE = '''
          class MyClass {
            private int Count
          }
        '''
        rule.ignoreFieldNames = 'Other'
        assertSingleViolation(SOURCE, 3, 'int Count')
    }

    @Test
    void testApplyTo_IgnoreFieldNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
          class MyClass {
            private String GOOD_NAME = 'good'
            protected static int Count
            private def _amount = 100.25
            private def OTHER_name
          }
        '''
        rule.ignoreFieldNames = 'OTHER?name,_*,GOOD_NAME' 
        assertSingleViolation(SOURCE, 4, 'static int Count')
    }

    @Test
    void testApplyTo_Script() {
        final SOURCE = '''
            private BigDecimal deposit_amount       // not considered a field
            private int COUNT                       // not considered a field
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PropertyDefinitions() {
        final SOURCE = '''
          class MyClass {
            BigDecimal deposit_amount
            int COUNT = 99
            def _NAME = 'abc'
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoFieldDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new FieldNameRule()
    }

}
