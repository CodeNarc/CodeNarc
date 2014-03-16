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
package org.codenarc.tool

/**
 * Runs CodeNarc against the Grails source code.
 *
 * You must set the "grails.home" system property to the Grails installation directory, containing the Grails source.
 *
 * @author Chris Mair
  */
class RunCodeNarcAgainstGrails {

    private static final RULESET_FILES = 'RunCodeNarcAgainstGrails.ruleset'

    static void main(String[] args) {
        runCodeNarc()
    }

    private static void runCodeNarc() {
        def baseDir = System.getProperty('grails.home')
        assert baseDir, 'The "grails.home" system property must be set to the location of the Grails installation.'

        System.setProperty('codenarc.properties.file', 'Ignore')
        def ant = new AntBuilder()

        ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')

        ant.codenarc(ruleSetFiles:RULESET_FILES) {

           fileset(dir:baseDir) {
               include(name:'src/**/*.groovy')
               include(name:'scripts/**/*.groovy')
               exclude(name:'**/templates/**')
           }

           report(type:'html') {
               option(name:'title', value:"Grails ($baseDir)")
               option(name:'outputFile', value:'CodeNarc-Grails-Report.html')
           }
        }
    }

}
