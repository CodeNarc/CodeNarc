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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for GroovyLangImmutableRule
 *
 * @author Hamlet D'Arcy
 */
class GroovyLangImmutableRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GroovyLangImmutable'
    }

    @Test
    void testSuccessScenario1() {
        final SOURCE = '''
              @groovy.transform.Immutable
              class Person { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario2() {
        final SOURCE = '''
              import groovy.transform.Immutable
              @Immutable
              class Person { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario3() {
        final SOURCE = '''
              import groovy.transform.*
              @Immutable
              class Person { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario4() {
        final SOURCE = '''
              import groovy.transform.Immutable as Imtl
              @Imtl
              class Person { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDefaultImport() {
        final SOURCE = '''
              @Immutable
              class Person { }
        '''
        assertSingleViolation(SOURCE, 2, '@Immutable', 'groovy.lang.Immutable is deprecated in favor of groovy.transform.Immutable')
    }

    @Test
    void testFullyQualified() {
        final SOURCE = '''
          @groovy.lang.Immutable
          class Person { }
        '''
        assertSingleViolation(SOURCE, 2, '@groovy.lang.Immutable', 'groovy.lang.Immutable is deprecated in favor of groovy.transform.Immutable')
    }

    @Test
    void testImportAlias() {
        final SOURCE = '''
              import groovy.lang.Immutable as Imtl
              @Imtl
              class Person { }
        '''
        assertSingleViolation(SOURCE, 3, '@Imtl', 'groovy.lang.Immutable is deprecated in favor of groovy.transform.Immutable')
    }

    protected Rule createRule() {
        new GroovyLangImmutableRule()
    }
}
