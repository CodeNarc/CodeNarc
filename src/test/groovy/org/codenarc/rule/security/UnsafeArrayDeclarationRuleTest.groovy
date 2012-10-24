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
package org.codenarc.rule.security

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnsafeArrayDeclarationRule
 *
 * @author Hamlet D'Arcy
  */
class UnsafeArrayDeclarationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnsafeArrayDeclaration'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
          class MyClass {
            public static final String myArray = init()
            public static final def myArray = []
            static final String[] myArray = init()
            public static String[] myArray = init()
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testArrayDeclaration() {
        final SOURCE = '''
              class MyClass {
                public static final String[] myArray = init()
              }
        '''
        assertSingleViolation(SOURCE, 3, 'public static final String[] myArray = init()', 'The Array field myArray is public, static, and final but still mutable')
    }

    @Test
    void testArrayInitialization() {
        final SOURCE = '''
              class MyClass {
                public static final def myArray2 = [] as String[]
              }
        '''
        assertSingleViolation(SOURCE, 3, 'public static final def myArray2 = [] as String[]', 'The Array field myArray2 is public, static, and final but still mutable')
    }

    protected Rule createRule() {
        new UnsafeArrayDeclarationRule()
    }
}
