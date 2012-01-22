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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for UnnecessaryDotClassRule
 *
 * @author Dean Del Ponte
  */
class UnnecessaryDotClassRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryDotClass'
    }

    void testNoViolations() {
        final SOURCE = '''
          def x = String
          def y = x.class
          def z = x.getClass()
          def a = String.class()
        '''
        assertNoViolations(SOURCE)
    }

    void testSingleViolation() {
        final SOURCE = '''
            def x = String.class
        '''
        assertSingleViolation(SOURCE, 2, 'def x = String.class', 'String.class can be rewritten as String')
    }

    void testNoDuplicateViolation() {
        final SOURCE = '''
            class MyClass {
                static final Logger LOG = Logger.getLogger(MyClass.class);
            }
        '''
        assertSingleViolation(SOURCE, 3, 'static final Logger LOG = Logger.getLogger(MyClass.class);', 'MyClass.class can be rewritten as MyClass')
    }

    protected Rule createRule() {
        new UnnecessaryDotClassRule()
    }
}