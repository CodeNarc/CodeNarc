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
 * Tests for DuplicateMapLiteralRule
 *
 * @author Chris Mair
 */
class DuplicateMapLiteralRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'DuplicateMapLiteral'
        assert 'MyTest.groovy' =~ rule.doNotApplyToFilesMatching
    }

    @Test
    void testIgnoresDifferentKeys_NoViolations() {
        final SOURCE = '''
            class MyClass {
        	  def var1 = [a:1, b:1, c:1]
              def var2 = [a:1, 2:1]
              def var3 = [a:1, c:1]
              def var4 = [b:1, c:1]
        	  def var1 = [a:1, b:1, c:1, d:1]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresDifferentValues_NoViolations() {
        final SOURCE = '''
            class MyClass {
        	  def var1 = [a:1, b:1, c:1]
        	  def var2 = [a:2, b:1, c:1]
        	  def var3 = [a:1, b:1, c:2]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresMapsWithVariableKeys_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def init(String name) {
        	        def var1 = [(name):1, b:1, c:1]
                }
                def cleanUp(String name) {
        	        return [(name):1, b:1, c:1]
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresNestedMapsWithNonConstantValues_NoViolations() {
        final SOURCE = '''
            class MyClass {
              def name
        	  def var1 = [a:1, b:[a:name]]
        	  def var2 = [a:1, b:[a:name]]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresNestedListsWithNonConstantValues_NoViolations() {
        final SOURCE = '''
            class MyClass {
              def name
        	  def var1 = [a:1, b:['x', name]]
        	  def var2 = [a:1, b:['x', name]]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresMapsWithVariableValues_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private count = 7
                def init(String name) {
        	        def var1 = [a:name, b:1, c:1]

                    def var2 = [a:count+1, b:1, c:1]
        	        def var3 = [a:count+1, b:1, c:1]
                }
                def cleanUp(String name) {
        	        return [a:name, b:1, c:1]
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresValuesContainingExpressions_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def var1 = [a:7+5]
                def var2 = [a:7+5]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresEmptyMap_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def var1 = [:]
                def var2 = [:]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDuplicateMapLiteral_MapValuesAsConstants() {
        final SOURCE = '''
            class MyClass {
        	  def var1 = [a:1, b:null, c:Boolean.FALSE, d:'x', e:true]
        	  def var2 = [a:1, b:null, c:Boolean.FALSE, d:'x', e:true]
        	  def var3 = ["a":99]
              Map getMap() {
        	        return ["a":99]
              }
            }
        '''
        assertTwoViolations(SOURCE,
            4, "def var2 = [a:1, b:null, c:Boolean.FALSE, d:'x', e:true]", '[a:1, b:null, c:Boolean.FALSE, d:x, e:true]',
            7, 'return ["a":99]', '[a:99]')
    }

    @Test
    void testDuplicateMapLiteral_HandlesNestedMapLiterals() {
        final SOURCE = '''
        	  def var1 = [a:1, b:[x:3,y:4]]
        	  def var2 = [a:1, b:[x:3,y:4]]
        '''
        assertSingleViolation(SOURCE, 3, 'def var2 = [a:1, b:[x:3,y:4]]', '[a:1, b:[x:3, y:4]]')
    }

    @Test
    void testDuplicateMapLiteral_HandlesNestedMapLiterals_OuterMapsAreNotTheSame() {
        final SOURCE = '''
        	  def var1 = [a:123, b:[x:3,y:4]]
        	  def var2 = [a:99, b:[x:3,y:4]]
        '''
        assertSingleViolation(SOURCE, 3, 'def var2 = [a:99, b:[x:3,y:4]]', '[x:3, y:4]')
    }

    @Test
    void testDuplicateMapLiteral_HandlesNestedListLiterals() {
        final SOURCE = '''
        	  def var1 = [a:1, b:[3,4]]
        	  def var2 = [a:1, b:[3,4]]
        '''
        assertSingleViolation(SOURCE, 3, 'def var2 = [a:1, b:[3,4]]', '[a:1, b:[3, 4]]')
    }

    @Test
    void testDuplicateMapLiteral_HandlesMapsNestedWithinListLiterals() {
        final SOURCE = '''
        	  def var1 = [a:123, b:[3, 4, [x:99], 5]]
        	  def var2 = [a:99, b:[3, 4, [other:[x:99]], 5]]
        '''
        assertSingleViolation(SOURCE, 3, 'def var2 = [a:99, b:[3, 4, [other:[x:99]], 5]]', '[x:99]')
    }

    @Test
    void testDuplicateMapLiteral_MapKeysAsConstants() {
        final SOURCE = '''
        	  def var1 = [null:1, 'b':2, (Boolean.FALSE):3, (4):4, (true):5]
        	  def var2 = [null:1, 'b':2, (Boolean.FALSE):3, (4):4, (true):5]
        '''
        assertSingleViolation(SOURCE,
            3, "def var2 = [null:1, 'b':2, (Boolean.FALSE):3, (4):4, (true):5]", '[null:1, b:2, Boolean.FALSE:3, 4:4, true:5]')
    }

    @Test
    void testDuplicateMapLiteral_HandlesQuotedKeys() {
        final SOURCE = '''
            class MyClass {
        	  def var1 = ["a":99]
        	  def var2 = [a:99]
            }
        '''
        assertSingleViolation(SOURCE, 4, 'def var2 = [a:99]', '[a:99]')
    }

    @Test
    void testDuplicateMapLiteral_NestedEmptyMap_Violations() {
        final SOURCE = '''
            class Maps {
                def map1 = [messages:[:]]
                def map2 = [messages:[:]]
            }
        '''
        assertSingleViolation(SOURCE, 4, 'def map2 = [messages:[:]]', '[messages:[:]]')
    }

    protected Rule createRule() {
        new DuplicateMapLiteralRule()
    }
}
