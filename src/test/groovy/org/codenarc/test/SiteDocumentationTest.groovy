/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.test

import org.junit.Test

/**
 * Tests that the "site" documentation is up to date.
 *
 * @author Hamlet D'Arcy
  */
class SiteDocumentationTest extends AbstractTestCase {

    @Test
    void testDocumentation() {

        new File('src/main/resources/rulesets').eachFileMatch(~/.*\.xml/) { File ruleset ->
            def ruleSetName = ruleset.name[0..-5]
            def docFile = "src/site/apt/codenarc-rules-${ruleSetName}.apt"
            def documentation = new File(docFile).text

            def violations = new XmlSlurper().parse(ruleset).rule.collect {
                it.@class.text()
            }.collect { String className ->
                def ruleInstance = this.class.getClassLoader().loadClass(className).newInstance()
                ruleInstance.name
            }.collect { ruleName ->
                documentation.contains(ruleName) ? null : ruleName
            }.removeAll { } {
                it == null
            }

            if (violations) {
                fail("""
It's really great that you wrote a new rule for the $ruleSetName ruleset.
The universe AND the CodeNarc team thanks you.
But you still have a little documentation to write.
Open this file: $docFile
And document the following rules: $violations
                """)
            }
        }
    }
}
