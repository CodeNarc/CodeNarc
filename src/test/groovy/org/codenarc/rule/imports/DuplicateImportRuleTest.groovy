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
import org.junit.Test

/**
 * Tests for DuplicateImportRule
 *
 * @author Chris Mair
  */
class DuplicateImportRuleTest extends AbstractRuleTestCase<DuplicateImportRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'DuplicateImport'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            import java.io.InputStream
            import java.io.OutputStream
            import java.io.InputStream
            class MyClass {
                def str = "import java.io.InputStream"    // ignored
            }
            interface MyInterface {
                static final NAME = "import java.io.OutputStream"    // ignored
            }
        '''
        assertSingleViolation(SOURCE, 4, 'import java.io.InputStream')
    }

    @Test
    void testApplyTo_SameImportButDifferentLineContents_Violations() {
        final SOURCE = '''
            import java.io.InputStream
            import java.io.OutputStream
            import java.io.OutputStream     // Some comment
            import java.io.InputStream;
        '''
        assertViolations(SOURCE,
                [line:4, source:'import java.io.OutputStream     // Some comment'],
                [line:5, source:'import java.io.InputStream'])
    }

    @Test
    void testApplyTo_DuplicateImportWithWildcards_Violation() {
        final SOURCE = '''
            import java.io.*
            import org.sample.MyClass
            import java.io.*
        '''
        assertSingleViolation(SOURCE, 4, 'import java.io.*')
    }

    @Test
    void testApplyTo_ImportsWithWildcards_NoViolations() {
        final SOURCE = '''
            import java.io.*
            import org.sample.*
            import java.text.*
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MultipleDuplicateImports() {
        final SOURCE = '''
            import abc.def.MyClass
            import java.io.OutputStream
            import abc.def.MyClass
            import xyz.OtherClass
            import abc.def.MyClass
            interface MyInterface { }
        '''
        assertTwoViolations(SOURCE, 4, 'import abc.def.MyClass', 6, 'import abc.def.MyClass')
    }

    @Test
    void testApplyTo_Comments_Violations() {
        final SOURCE = '''
            import payroll.util.DataMaintenanceUtil.ONE//comment
            import payroll.util.DataMaintenanceUtil.ONE         // comment
            import payroll.util.DataMaintenanceUtil.TWO /* comment */
            import payroll.util.DataMaintenanceUtil.TWO//comment
            import payroll.util.Constants.*//comment
            import payroll.util.Constants.*    /* comment */
        '''
        assertViolations(SOURCE,
                [line:3, source:'import payroll.util.DataMaintenanceUtil.ONE         // comment'],
                [line:5, source:'import payroll.util.DataMaintenanceUtil.TWO//comment'],
                [line:7, source:'import payroll.util.Constants.*    /* comment */'])
    }

    @Test
    void testApplyTo_PolishLettersUsedInNames_NoViolations() {
        final SOURCE = '''
            import other.PolishEnum.PÓŁ
            import other.PolishEnum.DWA_I_PÓŁ
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PolishLettersUsedInNames_Violations() {
        final SOURCE = '''
            import other.PolishEnum.PÓŁ
            import other.PolishEnum.DWA_I_PÓŁ
            import other.PolishEnum.PÓŁ
        '''
        assertSingleViolation(SOURCE, 4, 'import other.PolishEnum.PÓŁ')
    }

    // Static Imports

    @Test
    void testApplyTo_DuplicateStaticImportWithWildcards_Violation() {
        final SOURCE = '''
            import static com.wystar.app.payroll.util.DataMaintenanceUtil.*
            import static com.wystar.app.payroll.util.PayrollProcessingConstants.*
            import static com.wystar.app.payroll.util.DataMaintenanceUtil.*
            import org.sample.MyClass
        '''
        assertSingleViolation(SOURCE, 4, 'import static com.wystar.app.payroll.util.DataMaintenanceUtil.*')
    }

    @Test
    void testApplyTo_DuplicateStaticImport_Violation() {
        final SOURCE = '''
            import static payroll.util.DataMaintenanceUtil.ONE
            import static payroll.util.DataMaintenanceUtil.TWO
            import static payroll.util.PayrollProcessingConstants.TWO
            import static payroll.util.DataMaintenanceUtil.ONE
            import org.sample.MyClass
        '''
        assertSingleViolation(SOURCE, 5, 'import static payroll.util.DataMaintenanceUtil.ONE')
    }

    @Test
    void testApplyTo_StaticImport_Comments_Violations() {
        final SOURCE = '''
            import static payroll.util.DataMaintenanceUtil.ONE//comment
            import static payroll.util.DataMaintenanceUtil.ONE         // comment
            import static payroll.util.DataMaintenanceUtil.TWO /* comment */
            import static payroll.util.DataMaintenanceUtil.TWO//comment
            import static payroll.util.Constants.*//comment
            import static payroll.util.Constants.*    /* comment */
            import org.sample.MyClass
        '''
        assertViolations(SOURCE,
                [line:3, source:'import static payroll.util.DataMaintenanceUtil.ONE         // comment'],
                [line:5, source:'import static payroll.util.DataMaintenanceUtil.TWO//comment'],
                [line:7, source:'import static payroll.util.Constants.*    /* comment */'])
    }

    @Test
    void testApplyTo_StaticImportWithWildcards_NoViolations() {
        final SOURCE = '''
            import static com.wystar.app.payroll.util.DataMaintenanceUtil.*
            import static com.wystar.app.payroll.util.PayrollProcessingConstants.*
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_CommentedOutDuplicateImport_NoViolations() {
        final SOURCE = '''
            import java.io.InputStream
            // import java.io.InputStream
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
            import java.io.InputStream
            import java.io.OutputStream
            import java.util.HashMap
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticImports_PolishLettersUsedInNames_NoViolations() {
        final SOURCE = '''
            import static other.PolishEnum.PÓŁ
            import static other.PolishEnum.DWA_I_PÓŁ
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected DuplicateImportRule createRule() {
        new DuplicateImportRule()
    }

}
