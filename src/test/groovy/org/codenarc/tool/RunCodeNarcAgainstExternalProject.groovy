/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.tool

/**
 * Runs CodeNarc against the another project's source code.
 *
 * You must set the "basedir" system property to the base directory of the external project.
 * You must set the "title" system property to the title of the external project.
 *
 * @author Chris Mair
 */
class RunCodeNarcAgainstExternalProject {

    private static final DEFAULT_RULESET_FILES = 'RunCodeNarcAgainstExternalProject.ruleset'

    static void main(String[] args) {
        runCodeNarc()
    }

    private static void runCodeNarc() {
        def baseDir = System.getProperty('basedir')
        def title = System.getProperty('title')
        assert baseDir, 'The "basedir" system property must be set to the base directory of the external project.'
        assert title, 'The "title" system property must be set to the title of the external project.'

        def rulesetfiles = System.getProperty('rulesetfiles') ?: DEFAULT_RULESET_FILES

        System.setProperty('codenarc.properties.file', 'Ignore')
        def ant = new AntBuilder()

        ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')

        ant.codenarc(ruleSetFiles:rulesetfiles) {

           fileset(dir:baseDir) {
               include(name:'**/*.groovy')
           }

           report(type:'html') {
               option(name:'title', value:title)
               option(name:'outputFile', value:'target/' + title + '-CodeNarcReport.html')
           }
            report(type:'xml') {
                option(name:'title', value:title)
                option(name:'outputFile', value:'target/' + title + '-CodeNarcReport.xml')
            }
            report(type:'text') {
                option(name:'writeToStandardOut', value:true)
            }
        }
    }

}
