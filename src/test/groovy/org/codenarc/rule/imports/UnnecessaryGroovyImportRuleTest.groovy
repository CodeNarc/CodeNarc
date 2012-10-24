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
 * Tests for UnnecessaryGroovyImportRule
 *
 * @author Chris Mair
  */
class UnnecessaryGroovyImportRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryGroovyImport'
    }

    /*
     * no violations
     */

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
            import java.text.SimpleDateFormat
            import com.xxx.MyClass
            import MyClassFromSamePackage
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticImport() {
        final SOURCE = '''
            import static java.math.BigDecimal.*
            import static java.math.BigInteger.*

            import static java.io.InputStream.*
            import static java.lang.Integer.*
            import static java.net.Socket.*
            import static java.util.Map.*

            import static groovy.lang.GString.*
            import static groovy.util.Expando.*
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MixtureOfStaticAndRegularImports_NoViolations() {
        final SOURCE = '''
            import static java.net.HttpURLConnection.*
            import org.junit.*

            class Test1 {
                static me() {
                   println HTTP_OK
                }
            }
         '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testImportAliases_NoViolations() {
        final SOURCE = '''
            import groovy.lang.MetaClass as Foo
            import com.xxx.MyClass
            import groovy.lang.GString as Bar
        '''
        assertNoViolations(SOURCE)
    }

    /*
     * violations - misc.
     */

    @Test
    void testApplyTo_ImportStar_Violations() {
        final SOURCE = '''
            import java.io.*
        '''
        assertSingleViolation(SOURCE, 2, 'import java.io.*')
    }

    /*
     * violations - Java imports
     */

    @Test
    void testApplyTo_ImportJavaIo() {
        final SOURCE = '''
            import com.xxx.MyClass
            import java.io.InputStream
            import java.io.OutputStream
        '''
        assertTwoViolations(SOURCE, 3, 'java.io.InputStream', 4, 'java.io.OutputStream')
    }

    @Test
    void testApplyTo_ImportJavaLang() {
        final SOURCE = '''
            import java.lang.String
            import com.xxx.MyClass
            import java.lang.reflect.Field
            import java.lang.Integer
        '''
        assertTwoViolations(SOURCE, 2, 'java.lang.String', 5, 'java.lang.Integer')
    }

    @Test
    void testApplyTo_ImportJavaMath() {
        final SOURCE = '''
            import java.math.BigDecimal
            import com.xxx.MyClass
            import java.math.RoundingMode
            import java.math.BigInteger
        '''
        assertTwoViolations(SOURCE, 2, 'java.math.BigDecimal', 5, 'java.math.BigInteger')
    }

    @Test
    void testApplyTo_ImportJavaNet() {
        final SOURCE = '''
            import java.net.URL
            import com.xxx.MyClass
            import java.net.Socket
        '''
        assertTwoViolations(SOURCE, 2, 'java.net.URL', 4, 'java.net.Socket')
    }

    @Test
    void testApplyTo_ImportJavaUtil() {
        final SOURCE = '''
            import java.util.List
            import com.xxx.MyClass
            import java.util.Map
        '''
        assertTwoViolations(SOURCE, 2, 'java.util.List', 4, 'java.util.Map')
    }

    /*
     * violations - Groovy imports
     */

    @Test
    void testApplyTo_ImportGroovyLang() {
        final SOURCE = '''
            import groovy.lang.MetaClass
            import com.xxx.MyClass
            import groovy.lang.GString
        '''
        assertTwoViolations(SOURCE, 2, 'groovy.lang.MetaClass', 4, 'groovy.lang.GString')
    }

    @Test
    void testApplyTo_ImportGroovyUtil() {
        final SOURCE = '''
            import groovy.util.Eval
            import com.xxx.MyClass
            import groovy.util.Expando
        '''
        assertTwoViolations(SOURCE, 2, 'groovy.util.Eval', 4, 'groovy.util.Expando')
    }

    protected Rule createRule() {
        new UnnecessaryGroovyImportRule()
    }
}
