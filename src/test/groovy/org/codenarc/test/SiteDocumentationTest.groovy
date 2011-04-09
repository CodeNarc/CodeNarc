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

/**
 * Tests that the "site" documentation is up to date.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class SiteDocumentationTest extends GroovyTestCase {

    void testDocumentation() {


        new File('src/main/resources/rulesets').eachFileMatch(~/.*\.xml/) { File ruleset ->
            def ruleSetName = ruleset.name[0..-5]
            def docFile = "src/site/apt/codenarc-rules-${ruleSetName}.apt"
            def documentation = new File(docFile).text

            def violations = new XmlSlurper().parse(ruleset).rule.collect {
                it.@class.text()
            }.collect { String className ->
                def ruleInstance = Class.forName(className).newInstance()
                ruleInstance.name
            }.collect { ruleName ->
                documentation.contains(ruleName) ? null : ruleName
            }.removeAll {} {
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

    /**
     * This unit test regenerates the site index.
     */
    void testRegenerateSiteIndex() {

        def categories = []
        new File('src/main/resources/rulesets/').eachFileMatch(~/.*\.xml/) { File it ->
            categories.add(it.name[0..-5])
        }
        categories.sort() // apply default sort order

        StringBuilder output = new StringBuilder()
        
        categories.each { String it ->
            output.append "\n\n\n* {{{codenarc-rules-${it}.html}${it.capitalize()}}}\n\n"
            GString file = "src/main/resources/rulesets/${it}.xml"
            def y = new XmlSlurper().parse(file).rule.collect {
                String fqc = it.@class.text()
                int lindex = fqc.lastIndexOf('.') + 1
                fqc.substring(lindex)
            }.collect {
                if (it.endsWith('Rule')) {
                    it[0..-5]
                } else {
                    it
                }
            }
            y.sort()
            y.each {
                output.append """    * $it\n\n"""
            }
        }

        def header = """		--------------------------------------------------
                      CodeNarc - Rule Index
        --------------------------------------------------

Rule Index
~~~~~~~~~~
  <<CodeNarc>> includes 206 rules.

"""

        new File('src/site/apt/codenarc-rule-index.apt').text = header + output

    }

}
