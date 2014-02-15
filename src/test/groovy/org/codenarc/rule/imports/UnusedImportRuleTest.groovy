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
 * Tests for UnusedImportRule
 *
 * @author Chris Mair
  */
class UnusedImportRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnusedImport'
    }

    @Test
    void testApplyTo_OneViolation() {
        final SOURCE = '''
            import java.io.InputStream
            import java.io.OutputStream
            class ABC {
                InputStream input
            }
        '''
        assertSingleViolation(SOURCE, 3, 'import java.io.OutputStream', 'The [java.io.OutputStream] import is never referenced')
    }

    @Test
    void testApplyTo_TwoViolations() {
        final SOURCE = '''
            import java.io.InputStream
            import java.util.Map
            import java.io.OutputStream
            class ABC {
                Map map
            }
        '''
        assertTwoViolations(SOURCE,
            2, 'import java.io.InputStream', 'The [java.io.InputStream] import is never referenced',
            4, 'import java.io.OutputStream', 'The [java.io.OutputStream] import is never referenced')
    }

    @Test
    void testApplyTo_OnlyFullyQualifiedClassNameReferenced() {
        final SOURCE = '''
            import a.b.SomeClass
            import d.e.OtherClass as OC
            import f.g.ThirdClass as TC
            class ABC extends a.b.SomeClass {
                def name = d.e.OtherClass.name
                def info = f.g.ThirdClass.name + ":" + TC.metaClass.name
            }
        '''
        assertTwoViolations(SOURCE,
            2, 'import a.b.SomeClass', 'The [a.b.SomeClass] import is never referenced',
            3, 'import d.e.OtherClass as OC', 'The [d.e.OtherClass] import is never referenced')
    }

    @Test
    void testApplyTo_UnusedStaticImportConstant() {
        final SOURCE = '''
            import static Math.PI
            class ABC {
                def name
            }
        '''
        assertSingleViolation(SOURCE, 2, 'import static Math.PI', 'The [Math] import is never referenced')
    }

    @Test
    void testApplyTo_UnusedImport_WithSemicolon() {
        final SOURCE = '''
            import com.example.MyService;
            import com.example.test.AbstractTestCase;

            abstract class AbstractMyProfileTestCase extends AbstractTestCase {

                protected void assertEmailAddress(EmailPreference emailPref) {
                    assert emailPref.emailAddress
                }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'import com.example.MyService', 'The [com.example.MyService] import is never referenced')
    }

    @Test
    void testApplyTo_SimilarlyNamedImports() {
        final SOURCE = '''
            import static com.example.FaultCode.*
            import com.example.Fault

            class MyResourceTest {
    @Test
                void testUpdateUserWidget_UpdateFails() {
                    useStubFormatter(false, [UPDATE_FAILED])
                    def response = resource.getRecords()
                    assert response.status == 404
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'import com.example.Fault', 'The [com.example.Fault] import is never referenced')
    }

    @Test
    void testApplyTo_UnusedImport_SubstringOccurrence() {
        final SOURCE = '''
            import com.example.Service
            class ABC {
                def getService() { }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'import com.example.Service', 'The [com.example.Service] import is never referenced')
    }

    @Test
    void testApplyTo_UnusedImportWildcard() {
        final SOURCE = '''
            import org.codenarc.*
            class ABC {
                def name
            }
        '''
        // Can't know whether any of the classes within org.codenarc package were ever referenced,
        // since we don't know what they are
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_UnusedStaticImportWildcard() {
        final SOURCE = '''
            import static Math.*
            class ABC {
                def name
            }
        '''
        // Can't know whether any of the static members of the Math class were ever referenced,
        // since we don't know what they are
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
            import java.io.InputStream
            import java.io.OutputStream
            import static Math.PI
            class ABC {
                def run() {
                    String fff
                    InputStream input
                    OutputStream output
                    def value = PI
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnusedImportRule()
    }

}
