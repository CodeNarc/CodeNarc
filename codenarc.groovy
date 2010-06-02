#!/usr/bin/env groovy

import groovy.text.SimpleTemplateEngine

if (!args) {
	println usage()
	return
}


if (args[0] == 'create-rule') {
		
	print "Enter the rule name:"
	def ruleName = getUserInput()
	def ruleCategory = getRuleCategory() 

	makeRule(ruleName, ruleCategory) 
	makeRuleTest(ruleName, ruleCategory) 
	updatePropertiesFile(ruleName)
	updateRuleList(ruleName, ruleCategory) 
	
	println "\tFinished"
} else {
	println usage()
}


/*
* The rest of the code is here to support create-rule.
*/ 
def makeRule(ruleName, ruleCategory) {

	makeFromTemplate(
		ruleName, ruleCategory, 'Rule.groovy', 
		"./src/main/groovy/org/codenarc/rule/${ruleCategory}/${ruleName}Rule.groovy")
}

def makeRuleTest(ruleName, ruleCategory) {
	makeFromTemplate(
		ruleName, ruleCategory, 'Test.groovy', 
		"./src/test/groovy/org/codenarc/rule/${ruleCategory}/${ruleName}RuleTest.groovy")
}

def makeFromTemplate(ruleName, ruleCategory, templateName, targetPath) {
	def file = new File(targetPath)
	file.createNewFile()
	def ruleTemplate = new File("./templates/$templateName").text
	
	def binding = ['ruleName':ruleName, 'ruleCategory':ruleCategory]
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

def updatePropertiesFile(ruleName) {
	def path = './src/main/resources/codenarc-base-messages.properties'
	def file = new File(path)
	file.text = """
# todo: manually sort your messages into the correct location
${ruleName}.description=add a text description of your rule
${ruleName}.description.html=add an html description of your rule

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

