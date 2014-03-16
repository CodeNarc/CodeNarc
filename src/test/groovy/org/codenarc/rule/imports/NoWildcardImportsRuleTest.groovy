/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.imports

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for NoWildcardImportsRule
 *
 * @author Kyle Boon
 */
class NoWildcardImportsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'NoWildcardImports'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            import com.google

            public class Foo {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            import com.google.*

            public class Foo {}
        '''
        assertSingleViolation(SOURCE, 2, 'import com.google.*')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            import com.google.*
            import org.codenarc.rule.*

            public class Foo {}
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'import com.google.*'],	// todo: replace line number, source line and message
            [lineNumber:3, sourceLineText:'import org.codenarc.rule.*'])	// todo: replace line number, source line and message
    }

    protected Rule createRule() {
        new NoWildcardImportsRule()
    }
}
