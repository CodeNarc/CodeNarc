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
 * Tests for UnnecessaryGroovyImportRule
 *
 * @author Chris Mair
 * @version $Revision: 193 $ - $Date: 2009-01-13 21:04:52 -0500 (Tue, 13 Jan 2009) $
 */
class UnnecessaryGroovyImportRuleTest extends AbstractImportRuleTest {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.id == 'UnnecessaryGroovyImport'
    }

    void testApplyTo_ImportJavaMath() {
        final SOURCE = '''
            import java.math.BigDecimal
            import com.xxx.MyClass
            import java.math.RoundingMode
            import java.math.BigInteger
        '''
        assertImportViolations(SOURCE, ['java.math.BigDecimal', 'java.math.BigInteger'])
    }

    void testApplyTo_ImportGroovyLang() {
        final SOURCE = '''
            import groovy.lang.MetaClass
            import com.xxx.MyClass
            import groovy.lang.GString
        '''
        assertImportViolations(SOURCE, ['groovy.lang.MetaClass', 'groovy.lang.GString'])
    }

    void testApplyTo_ImportGroovyUtil() {
        final SOURCE = '''
            import groovy.util.Eval
            import com.xxx.MyClass
            import groovy.util.Expando
        '''
        assertImportViolations(SOURCE, ['groovy.util.Eval', 'groovy.util.Expando'])
    }

    void testApplyTo_ImportJavaLang() {
        final SOURCE = '''
            import java.lang.String
            import com.xxx.MyClass
            import java.lang.reflect.Field
            import java.lang.Integer
        '''
        assertImportViolations(SOURCE, ['java.lang.String', 'java.lang.Integer'])
    }

    void testApplyTo_ImportJavaUtil() {
        final SOURCE = '''
            import java.util.List
            import com.xxx.MyClass
            import java.util.Map
        '''
        assertImportViolations(SOURCE, ['java.util.List', 'java.util.Map'])
    }

    void testApplyTo_ImportJavaIo() {
        final SOURCE = '''
            import com.xxx.MyClass
            import java.io.InputStream
            import java.io.OutputStream
        '''
        assertImportViolations(SOURCE, ['java.io.InputStream', 'java.io.OutputStream'])
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''
            import java.text.SimpleDateFormat
            import com.xxx.MyClass
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new UnnecessaryGroovyImportRule()
    }

}