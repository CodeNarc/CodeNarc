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
package org.codenarc.rule.imports

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ImportFromSamePackageRule
 *
 * @author Chris Mair
  */
class ImportFromSamePackageRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ImportFromSamePackage'
    }

    @Test
    void testApplyTo_Violations() {
        final SOURCE = '''
            package org.xyz
            import org.xyz.MyController
            import java.text.SimpleDateFormat
            import org.xyz.MyService
        '''
        assertTwoViolations(SOURCE, 3, 'org.xyz.MyController', 5, 'org.xyz.MyService')
    }

    @Test
    void testApplyTo_ImportStar_Violations() {
        final SOURCE = '''
            package org.xyz
            import org.xyz.*
        '''
        assertSingleViolation(SOURCE, 3, 'import org.xyz.*')
    }

    @Test
    void testApplyTo_IgnoreStaticImports() {
        final SOURCE = '''
            package org.xyz
            import static org.xyz.MyUtil.calculate
            import static Math.*
            import static org.xyz.Helper.*
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreImportsWithExplicitAliasDeclarations() {
        final SOURCE = '''
            package org.xyz
            import org.xyz.MyBigClass as MBC
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
            package org.mypackage
            import java.text.SimpleDateFormat
            import com.xxx.MyClass
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ImportFromSamePackageRule()
    }

}
