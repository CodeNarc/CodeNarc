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
package org.codenarc.rule.dry

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for DuplicateListLiteralRule
 *
 * @author Chris Mair
 */
class DuplicateListLiteralRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'DuplicateListLiteral'
        assert 'MyTest.groovy' =~ rule.doNotApplyToFilesMatching
    }

    @Test
    void testIgnoresDifferentValues_NoViolations() {
        final SOURCE = '''
            class MyClass {
        	  def var1 = [1, 1, 1]
        	  def var2 = [2, 1, 1]
        	  def var3 = [1, 1, 2]

              def var4 = ['a', 'b']
              def var5 = ['b', 'a']
              def var6 = ['a', 'b', 'c']
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresVariableValues_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def init(String name) {
        	        def var1 = [name, 'b', 'c']
                }
                def cleanUp(String name) {
        	        return [name, 'b', 'c']
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresNestedListsWithNonConstantValues_NoViolations() {
        final SOURCE = '''
            class MyClass {
              def name
        	  def var1 = [1, ['x', name]]
        	  def var2 = [1, ['x', name]]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresValuesContainingExpressions_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def var1 = [7+5]
                def var2 = [7+5]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresEmptyList_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def var1 = []
                def var2 = []
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDuplicateListLiteral_ListValuesAsConstants() {
        final SOURCE = '''
            class MyClass {
        	  def var1 = [1, null, Boolean.FALSE, 'x', true]
        	  def var2 = [1, null, Boolean.FALSE, 'x', true]
        	  def var3 = ["a", 99]
              Map getMap() {
        	        return ["a", 99]
              }
            }
        '''
        assertTwoViolations(SOURCE,
            4, "def var2 = [1, null, Boolean.FALSE, 'x', true]", '[1, null, Boolean.FALSE, x, true]',
            7, 'return ["a", 99]', '[a, 99]')
    }

    @Test
    void testDuplicateListLiteral_HandlesNestedListLiterals() {
        final SOURCE = '''
        	  def var1 = [1, [3, 4]]
        	  def var2 = [1, [3,4]]
        '''
        assertSingleViolation(SOURCE, 3, 'def var2 = [1, [3,4]]', '[1, [3, 4]]')
    }

    @Test
    void testDuplicateListLiteral_HandlesNestedListLiterals_OuterListsAreNotTheSame() {
        final SOURCE = '''
        	  def var1 = [123, [3,4]]
        	  def var2 = [99, [3,4]]
        '''
        assertSingleViolation(SOURCE, 3, 'def var2 = [99, [3,4]]', '[3, 4]')
    }

    @Test
    void testDuplicateListLiteral_HandlesMapsNestedWithinListLiterals() {
        final SOURCE = '''
        	  def var1 = [123, [3, 4, [x:99], 5]]
        	  def var2 = [99, [3, 4, [x:99], 5]]
        '''
        assertSingleViolation(SOURCE, 3, 'def var2 = [99, [3, 4, [x:99], 5]]', '[3, 4, [x:99], 5]')
    }

    @Test
    void testDuplicateListLiteral_NestedEmptyList_Violations() {
        final SOURCE = '''
            class Lists {
                def list1 = [1, []]
                def list2 = [1, []]
            }
        '''
        assertSingleViolation(SOURCE, 4, 'def list2 = [1, []]', '[1, []]')
    }

    protected Rule createRule() {
        new DuplicateListLiteralRule()
    }
}
