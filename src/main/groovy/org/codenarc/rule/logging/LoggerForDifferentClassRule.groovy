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
package org.codenarc.rule.logging

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that checks for instantiating a logger for a class other than the current class. Supports logger
 * instantiations for Log4J, Logback, SLF4J, Apache Commons Logging, Java Logging API (java.util.logging).
 *
 * Limitations:
 * <ul>
 *   <li>Only checks Loggers instantiated within a class field or property (not variables or expressions within a method)</li>
 *   <li>For Log4J: Does not catch Logger instantiations if you specify the full package name for the Logger class:
 *      e.g.  org.apache.log4.Logger.getLogger(..)</li>
 *   <li>For SLF4J and Logback: Does not catch Log instantiations if you specify the full package name for the LoggerFactory
 *      class: e.g.  org.slf4j.LoggerFactory.getLogger(..)</li>
 *   <li>For Commons Logging: Does not catch Log instantiations if you specify the full package name for the LogFactory
 *      class: e.g.  org.apache.commons.logging.LogFactory.getLog(..)</li>
 *   <li>For Java Logging API: Does not catch Logger instantiations if you specify the full package name for the Logger
 *      class: e.g.  java.util.logging.Logger.getLogger(..)</li>
 * </ul>
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class LoggerForDifferentClassRule extends AbstractAstVisitorRule {
    String name = 'LoggerForDifferentClass'
    int priority = 2
    Class astVisitorClass = LoggerForDifferentClassAstVisitor
}

class LoggerForDifferentClassAstVisitor extends AbstractAstVisitor  {

    void visitFieldEx(FieldNode fieldNode) {
        def expression = fieldNode.getInitialExpression()
        if (LogUtils.isMatchingLoggerDefinition(expression)) {
            def classNameWithoutPackage = fieldNode.declaringClass.nameWithoutPackage
            def firstArg = expression.arguments?.expressions?.get(0)
            def argText = firstArg.text
            if (isCapitalized(argText) && !isEqualToCurrentClassOrClassName(argText, classNameWithoutPackage)) {
                addViolation(fieldNode)
            }
        }
        super.visitFieldEx(fieldNode)
    }

    private static boolean isEqualToCurrentClassOrClassName(String argText, classNameWithoutPackage) {
        return isEqualToCurrentClass(argText, classNameWithoutPackage) || isEqualToCurrentClassName(argText, classNameWithoutPackage)
    }

    private static boolean isEqualToCurrentClass(String argText, classNameWithoutPackage) {
        return (argText == classNameWithoutPackage) || (argText == classNameWithoutPackage + ".class")
    }

    private static boolean isEqualToCurrentClassName(String argText, classNameWithoutPackage) {
        def classNameOptions = [
            classNameWithoutPackage + '.getClass().getName()',
            classNameWithoutPackage + '.getClass().name',
            classNameWithoutPackage + '.class.getName()',
            classNameWithoutPackage + '.class.name',
        ]
        return argText in classNameOptions
    }

    private static boolean isCapitalized(String text) {
        def firstCharacter = text[0]
        return firstCharacter.toUpperCase() == firstCharacter
    }

}