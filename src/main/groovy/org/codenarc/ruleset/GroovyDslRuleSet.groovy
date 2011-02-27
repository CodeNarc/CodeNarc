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

import org.apache.log4j.Logger
import org.codenarc.util.io.ResourceFactory
import org.codenarc.util.io.DefaultResourceFactory
import org.codehaus.groovy.control.MultipleCompilationErrorsException

/**
 * A <code>RuleSet</code> implementation that parses a Groovy DSL of RuleSet definitions.
 * The filename passed into the constructor is interpreted relative to the classpath.
 * Note that this class attempts to read the file and parse the Groovy from within the constructor.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class GroovyDslRuleSet implements RuleSet {
    private static final LOG = Logger.getLogger(GroovyDslRuleSet)
    private ResourceFactory resourceFactory = new DefaultResourceFactory()
    private rules

    /**
     * Construct a new instance on the specified Groovy DSL RuleSet file path
     * @param path - the path to the Groovy DSL RuleSet definition file, relative to the classpath; must not be empty or null
     */
    GroovyDslRuleSet(String path) {
        assert path
        LOG.info("Loading ruleset from [$path]")
        def inputStream = resourceFactory.getResource(path).inputStream 

        def ruleSetBuilder = new RuleSetBuilder()

        def callRuleSet = { Closure closure -> 
            closure.resolveStrategy = Closure.DELEGATE_ONLY    // fail if access non-existent properties
            ruleSetBuilder.ruleset(closure)
        }
        Binding binding = new Binding(ruleset:callRuleSet)

        GroovyShell shell = new GroovyShell(binding)

        try {
            shell.evaluate(inputStream)
        } catch (MultipleCompilationErrorsException compileError) {
            LOG.error("An error occurred compiling the configuration file $path", compileError)
            throw new IllegalStateException("An error occurred compiling the configuration file $path\n$compileError.message")
        }

        rules = ruleSetBuilder.ruleSet.rules
    }

    /**
     * @return a List of Rule objects
     */
    List getRules() {
        rules
    }
}