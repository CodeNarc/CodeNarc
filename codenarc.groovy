#!/usr/bin/env groovy

import groovy.text.SimpleTemplateEngine

import static java.nio.charset.StandardCharsets.UTF_8

if (!args) {
    println usage()
    return
}


if (args[0] == 'create-rule') {

    print "Enter your name:"
    def authorName = getUserInput()

    print "Enter the rule name:"
    def ruleName = removeRuleSuffix(getUserInput())
    def ruleCategory = getRuleCategory()

    print "Enter the rule description:"
    def ruleDescription = getUserInput()

    def binding = ['ruleName':ruleName, 'ruleCategory':ruleCategory, 'authorName': authorName, 'ruleDescription': ruleDescription]
    def ruleFile = makeRule(binding)
    def testFile = makeRuleTest(binding)
    updatePropertiesFile(ruleName, ruleDescription)
    updateRuleList(ruleName, ruleCategory)
    updateSiteDocumentation(ruleName, ruleCategory, ruleDescription)
    updateChangelog(ruleName, ruleCategory, ruleDescription)
    print "\tadding to git... "
    print "git add -v $ruleFile".execute().text
    print "\tadding to git... "
    print "git add -v $testFile".execute().text
    println "\tFinished"
} else {
    println usage()
}


/*
* The rest of the code is here to support create-rule.
*/
def makeRule(binding) {

    makeFromTemplate(
        binding, 'Rule.groovy',
        "./src/main/groovy/org/codenarc/rule/${binding.ruleCategory}/${binding.ruleName}Rule.groovy")
}

def makeRuleTest(binding) {
    makeFromTemplate(
        binding,
        'Test.groovy',
        "./src/test/groovy/org/codenarc/rule/${binding.ruleCategory}/${binding.ruleName}RuleTest.groovy")
}

def makeFromTemplate(binding, templateName, targetPath) {
    def file = new File(targetPath)
    file.createNewFile()
    def ruleTemplate = new File("./templates/$templateName").text

    def engine = new SimpleTemplateEngine()
    def rule = engine.createTemplate(ruleTemplate).make(binding)

    file.text = rule.toString()

    println "\tCreated $targetPath"
    targetPath
}

def updateRuleList(ruleName, ruleCategory) {
    def path = "./src/main/resources/rulesets/${ruleCategory}.xml"
    def file = new File(path)
    file.text = file.text.replaceAll(
            '</ruleset>',
            "    <rule class='org.codenarc.rule.${ruleCategory}.${ruleName}Rule'/>\n</ruleset>")
    println "\tUpdated $path"
}

def updateSiteDocumentation(ruleName, ruleCategory, ruleDescription) {
    def path = "./docs/codenarc-rules-${ruleCategory}.md"
    new File(path).append """
## $ruleName Rule

<Since CodeNarc 2.0.0>

$ruleDescription

Example of violations:

```
    // TODO: Add examples
```
"""
    println "\tUpdated $path"
}

def updateChangelog(ruleName, ruleCategory, ruleDescription) {
    def path = './CHANGELOG.md'
    File file = new File(path)
    String original = file.getText(UTF_8.name())
    file.setText("""
#TODO: Sort the following line into the file
- $ruleName rule ($ruleCategory) - $ruleDescription

$original""", UTF_8.name())
    println "\tUpdated $path"
}

def updatePropertiesFile(ruleName, ruleDescription) {
    def path = './src/main/resources/codenarc-base-messages.properties'
    def file = new File(path)
    file.text = """
# todo: manually sort your messages into the correct location
${ruleName}.description=$ruleDescription
${ruleName}.description.html=$ruleDescription

""" + file.text
    println "\tUpdated $path"
}

def getRuleCategory() {
    def categories = getValidRuleCategories()
    println "Enter the rule category. Valid categories are:\n  ${categories.sort().join("\n  ")}"

    while(true) {
        def input = getUserInput()
        if (categories.contains(input)) {
            return input
        }
        println "Invalid category. Valid categories are:\n  ${categories.sort().join("\n  ")}"
    }
}

def getUserInput() {
    def input = new java.util.Scanner(System.in)
    input.nextLine()

}

def getValidRuleCategories() {
    def categories = []
    new File("./src/main/groovy/org/codenarc/rule/").eachDir() { dir->
        if (dir.name != '.svn') categories << dir.name
    }
    categories
}

String removeRuleSuffix(String initialRuleName) {
    removeSuffix(initialRuleName, 'Rule')
}

String removeSuffix(String input, String suffix) {
    if (input.endsWith(suffix)) {
        return input.substring(0, input.lastIndexOf(suffix))
    }
    return input
}

def usage() {
"""
Usage: codenarc [OPTION]
Valid Options:
    create-rule - Creates a new CodeNarc rule
"""
}
