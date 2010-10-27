#!/usr/bin/env groovy

import groovy.text.SimpleTemplateEngine

if (!args) {
	println usage()
	return
}


if (args[0] == 'create-rule') {
		
    print "Enter your name:"
    def authorName = getUserInput()

	print "Enter the rule name:"
	def ruleName = getUserInput()
	def ruleCategory = getRuleCategory() 

    print "Enter the rule description:"
    def ruleDescription = getUserInput()

    def binding = ['ruleName':ruleName, 'ruleCategory':ruleCategory, 'authorName': authorName, 'ruleDescription': ruleDescription]
	makeRule(binding)
	makeRuleTest(binding) 
	updatePropertiesFile(ruleName, ruleDescription)
	updateRuleList(ruleName, ruleCategory) 
	
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
}

def updateRuleList(ruleName, ruleCategory) {
	def path = "./src/main/resources/rulesets/${ruleCategory}.xml"
	def file = new File(path)
	file.text = file.text.replaceAll(
			'</ruleset>', 
			"    <rule class='org.codenarc.rule.${ruleCategory}.${ruleName}Rule'/>\n</ruleset>")
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
    return input.nextLine()

}

def getValidRuleCategories() {
	def categories = []
	new File("./src/main/groovy/org/codenarc/rule/").eachDir() { dir->
	    if (dir.name != '.svn') categories << dir.name
	}
	categories
}

def usage() {
"""
Usage: codenarc [OPTION]
Valid Options: 
    create-rule - Creates a new CodeNarc rule
"""
}
