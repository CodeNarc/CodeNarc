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
package org.codenarc.ruleset

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.codenarc.test.AbstractTestCase

/**
 * Load all predefined RuleSet files using XmlFileRuleSet
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class LoadAllPredefinedRuleSetsTest extends AbstractTestCase {

    private static final BASE_MESSAGES_BUNDLE = 'codenarc-base-messages'
    private messages

    void testPredefinedRulesHaveDescriptions() {

        forEachRule { rule ->
            assert messages.getString(rule.name + '.description')
            assert messages.getString(rule.name + '.description.html')
        }
    }

    @SuppressWarnings('CatchThrowable')
    void testPredefinedRulesHaveValidHtmlDescriptions() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        factory.validating = false
        factory.namespaceAware = true
        DocumentBuilder builder = factory.newDocumentBuilder()


        def errors = []
        forEachRule { rule ->
            String propertyName = rule.name + '.description.html'

            def htmlSnippet = messages.getString(propertyName)

            //builder.setErrorHandler(new SimpleErrorHandler());
            ByteArrayInputStream bs = new ByteArrayInputStream(('<root>' + htmlSnippet + '</root>').bytes)
            try {
                builder.parse(bs)
            } catch (Throwable t) {
                errors.add("""An error occurred parsing the property $propertyName
Value: $htmlSnippet
Error: $t.message

""")
            }
        }
        if (errors) {
            fail(errors.join('\n'))
        }
    }

    private forEachRule(Closure assertion) {
        RuleSets.ALL_RULESET_FILES.each { ruleSetPath ->
            def ruleSet = new XmlFileRuleSet(ruleSetPath)
            def rules = ruleSet.rules
            log("[$ruleSetPath] rules=$rules")
            assert rules

            rules.each(assertion)
        }
    }

    void setUp() {
        super.setUp()
        messages = ResourceBundle.getBundle(BASE_MESSAGES_BUNDLE)
    }

}