/*
 * Copyright 2008 the original author or authors.
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

/**
 * Tests for ImportFromSamePackageRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ImportFromSamePackageRuleTest extends AbstractImportRuleTest {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ImportFromSamePackage'
    }

    void testApplyTo_Violations() {
        final SOURCE = '''
            package org.xyz
            import org.xyz.MyController
            import java.text.SimpleDateFormat
            import org.xyz.MyService
        '''
        assertImportViolations(SOURCE, ['org.xyz.MyController', 'org.xyz.MyService'])
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''
            package org.mypackage
            import java.text.SimpleDateFormat
            import com.xxx.MyClass
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new ImportFromSamePackageRule()
    }

}