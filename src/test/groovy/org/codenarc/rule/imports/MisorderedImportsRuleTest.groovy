/*
 * Copyright 2016 the original author or authors.
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

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for MisorderedImportsRule
 *
 * @author Rahul Somasunderam
 */
class MisorderedImportsRuleTest extends AbstractRuleTestCase {

    private static final List<String> STATIC_LAST_PATTERN = MisorderedImportsRule.STATIC_LAST_PATTERN
    private static final List<String> STATIC_LAST_PATTERN_NO_SEPARATORS = MisorderedImportsRule.STATIC_LAST_PATTERN.findAll { it != '' }
    private static final List<String> DEFAULT_PATTERN_NO_SEPARATORS = MisorderedImportsRule.DEFAULT_PATTERN.findAll { it != '' }

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MisorderedImports'
    }

    @Test
    void testApplyTo_NoImports_NoViolations() {
        final SOURCE = '''\
            |class Foo {}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Imports_NoViolations() {
        final SOURCE = '''\
            |import static toons.looney.Coyote.*
            |import static toons.looney.Roadrunner.legs
            |
            |import com.acme.Device
            |import com.acme.powders.*
            |
            |import javax.acme.Switch
            |import java.acme.Cord
            |import java.toons.*
            |
            |class Foo {}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SingleImportOutOfOrder_Violation() {
        final SOURCE = '''\
            |import com.a.Thing1
            |import com.c.Thing2
            |import com.b.Thing3
            |import com.e.Thing as E
            |import com.f.Thing as F
        '''.stripMargin()
        assertSingleViolation(SOURCE, 2, 'import com.c.Thing2',
                "Expected 'import com.c.Thing2' on line 3. Found on line 2")
    }

    @Test
    void testApplyTo_MultipleImportsOutOfOrder_Violations() {
        final SOURCE = '''\
            |import static toons.looney.Roadrunner.legs
            |import static toons.looney.Coyote.*
            |
            |import com.acme.Device
            |import com.acme.powders.*
            |
            |import java.toons.*
            |import javax.acme.Switch
            |import java.acme.Cord
            |
            |class Foo {}
        '''.stripMargin()
        assertViolations(SOURCE,
                [lineNumber : 1, sourceLineText: 'import static toons.looney.Roadrunner.legs',
                 messageText: "Expected 'import static toons.looney.Roadrunner.legs' on line 2. Found on line 1"],
                [lineNumber : 7, sourceLineText: 'import java.toons.*', // TODO Fix
                 messageText: "Expected 'import java.toons.*' on line 10. Found on line 7"])
    }

    @Test
    void testApplyTo_PatternWithNoSeparators_BlankLines_NoViolations() {
        final SOURCE = '''\
            |import static toons.looney.Coyote.*
            |import static toons.looney.Roadrunner.legs
            |
            |
            |import com.acme.Device
            |import com.acme.powders.*
            |
            |import javax.acme.Switch
            |import java.acme.Cord
            |import java.toons.*
            |
            |class Foo {}
        '''.stripMargin()
        rule.patterns = DEFAULT_PATTERN_NO_SEPARATORS
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ImportsWithoutBlankLines_NoViolations() {
        final SOURCE = '''\
            |import static toons.looney.Coyote.*
            |import static toons.looney.Roadrunner.legs
            |import com.acme.Device
            |import com.acme.powders.*
            |import javax.acme.Switch
            |import java.acme.Cord
            |import java.toons.*
            |
            |class Foo {}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticLastPattern_MultipleViolations() {
        final SOURCE = '''\
            |import static toons.looney.Roadrunner.legs
            |import static toons.looney.Coyote.*
            |
            |import com.acme.Device
            |import com.acme.powders.*
            |
            |import java.toons.*
            |import javax.acme.Switch
            |import java.acme.Cord
            |
            |class Foo {}
        '''.stripMargin()
        rule.patterns = STATIC_LAST_PATTERN
        assertViolations(SOURCE,
                [lineNumber : 1, sourceLineText: 'import static toons.looney.Roadrunner.legs',
                 messageText: "Expected 'import static toons.looney.Roadrunner.legs' on line 10. Found on line 1"],
                [lineNumber : 2, sourceLineText: 'import static toons.looney.Coyote.*',
                 messageText: "Expected 'import static toons.looney.Coyote.*' on line 9. Found on line 2"],
                [lineNumber : 7, sourceLineText: 'import java.toons.*',
                 messageText: "Expected 'import java.toons.*' on line 7. Found on line 7"]
        )
    }

    @Test
    void testApplyTo_StaticLastPattern_NoViolations() {
        final SOURCE = '''\
            |import com.acme.Device
            |import com.acme.powders.*
            |
            |import javax.acme.Switch
            |import java.acme.Cord
            |import java.toons.*
            |
            |import static toons.looney.Coyote.*
            |import static toons.looney.Roadrunner.legs
            |
            |class Foo {}
        '''.stripMargin()
        rule.patterns = STATIC_LAST_PATTERN
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticLastPatternWithNoSeparators_BlankLines_NoViolations() {
        final SOURCE = '''\
            |import com.acme.Device
            |import com.acme.powders.*
            |
            |import javax.acme.Switch
            |import java.acme.Cord
            |import java.toons.*
            |
            |
            |import static toons.looney.Coyote.*
            |import static toons.looney.Roadrunner.legs
            |
            |class Foo {}
        '''.stripMargin()
        rule.patterns = STATIC_LAST_PATTERN_NO_SEPARATORS
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticLastPatternWithNoSeparators_NoBlankLines_Violations() {
        final SOURCE = '''\
            |import static toons.looney.Roadrunner.legs
            |import static toons.looney.Coyote.*
            |import com.acme.Device
            |import com.acme.powders.*
            |import java.toons.*
            |import javax.acme.Switch
            |import java.acme.Cord
            |
            |class Foo {}
        '''.stripMargin()
        rule.patterns = STATIC_LAST_PATTERN_NO_SEPARATORS
        assertViolations(SOURCE,
                [lineNumber : 1, sourceLineText: 'import static toons.looney.Roadrunner.legs',
                 messageText: "Expected 'import static toons.looney.Roadrunner.legs' on line 10. Found on line 1"],
                [lineNumber: 2, sourceLineText: 'import static toons.looney.Coyote.*',
                 messageText: "Expected 'import static toons.looney.Coyote.*' on line 9. Found on line 2"],
                [lineNumber: 5, sourceLineText: 'import java.toons.*',
                 messageText: "Expected 'import java.toons.*' on line 7. Found on line 5"]
        )
    }

    @Test
    void testApplyTo_StaticLastPatternWithNoSeparators_NoBlankLines_NoViolations() {
        final SOURCE = '''\
            |import com.acme.Device
            |import com.acme.powders.*
            |import javax.acme.Switch
            |import java.acme.Cord
            |import java.toons.*
            |import static toons.looney.Coyote.*
            |import static toons.looney.Roadrunner.legs
            |
            |class Foo {}
        '''.stripMargin()
        rule.patterns = STATIC_LAST_PATTERN_NO_SEPARATORS
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PatternMissingImport_ThrowsException() {
        final SOURCE = '''\
            |class Foo {}
        '''.stripMargin()
        rule.patterns = [
                'import static *',
                'import java.*',
        ]
        shouldFailWithMessageContaining('import *') { applyRuleTo(SOURCE) }
    }

    @Test
    void testApplyTo_PatternMissingImportStatic_ThrowsException() {
        final SOURCE = '''\
            |class Foo {}
        '''.stripMargin()
        rule.patterns = [
                'import *',
        ]
        shouldFailWithMessageContaining('import static *') { applyRuleTo(SOURCE) }
    }

    protected Rule createRule() {
        new MisorderedImportsRule()
    }
}
