package org.codenarc

/**
 * Tests that the "site" documentation is up to date.
 *
 * @author Hamlet D'Arcy
 */
class SiteDocumentationTest extends GroovyTestCase {

    public void testDocumentation() {


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
}
