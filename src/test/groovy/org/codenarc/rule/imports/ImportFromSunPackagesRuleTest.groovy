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
package org.codenarc.rule.imports

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ImportFromSunPackagesRule
 *
 * @author Hamlet D'Arcy
  */
class ImportFromSunPackagesRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ImportFromSunPackages'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	import foo.bar
            "hellO"
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            import sun.misc.foo
            import sun.misc.foo as Foo

            public class Foo {}
        '''
        assertViolations(SOURCE,
                [lineNumber: 2, sourceLineText: 'import sun.misc.foo', messageText: 'The file imports sun.misc.foo, which is not portable and likely to change'],
                [lineNumber: 3, sourceLineText: 'import sun.misc.foo as Foo', messageText: 'The file imports sun.misc.foo, which is not portable and likely to change'])
    }

    @Test
    void testStarImport() {
        final SOURCE = '''
            import sun.*

            public class Foo {}
        '''
        assertSingleViolation(SOURCE, 2, 'import sun.*', 'The file imports sun.*, which is not portable and likely to change')
    }

    protected Rule createRule() {
        new ImportFromSunPackagesRule()
    }
}
