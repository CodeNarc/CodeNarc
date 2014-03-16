/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.grails

import org.codenarc.rule.Rule
import org.junit.Before
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for GrailsDomainWithServiceReferenceRule
 *
 * @author Artur Gajowy
 */
class GrailsDomainWithServiceReferenceRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsDomainWithServiceReference'
    }

    @Before
    void setup() {
        sourceCodePath = 'project/MyProject/grails-app/domain/com/xxx/Book.groovy'
    }

    @Test
    void testDomain_NoService_NoViolations() {
        final SOURCE = '''
        	class Book {
                String title
                Author author
        	}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDomain_SingleService_SingleViolation() {
        final SOURCE = '''
            package foo.bar.baz;

            class Book {
                FooService fooService
            }
        '''
        assertSingleViolation(SOURCE, 5, 'FooService fooService', errorMessage('fooService'))
    }

    @Test
    void testDomain_TwoServiceFields_TwoViolations() {
        final SOURCE = '''
            class Book {
                FooService fooService
                def barService
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'FooService fooService', errorMessage('fooService'),
                4, 'def barService', errorMessage('barService'))
    }

    @Test
    void testNonDomain_WithService_NoViolations() {
        final SOURCE = '''
            class BookService {
                FooService fooService
            }
        '''
        sourceCodePath = 'project/MyProject/grails-app/service/BookService.groovy'
        assertNoViolations(SOURCE)
    }

    private static String errorMessage(String fieldName) {
        "Domain class Book should not reference services (offending field: $fieldName)"
    }

    protected Rule createRule() {
        new GrailsDomainWithServiceReferenceRule()
    }
}
