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

/**
 * A <code>RuleSet</code> implementation that parses a Groovy DSL of RuleSet definitions.
 * The filename passed into the constructor is interpreted relative to the classpath.
 * Note that this class attempts to read the file and parse the Groovy from within the constructor.
 *
 * @author Chris Mair
 * @version $Revision: 7 $ - $Date: 2009-01-21 21:52:00 -0500 (Wed, 21 Jan 2009) $
 */
class GroovyDslRuleSet implements RuleSet {
    private static final LOG = Logger.getLogger(XmlFileRuleSet)
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

        def callRuleSet = { closure -> ruleSetBuilder.ruleset(closure) }
        Binding binding = new Binding(ruleset:callRuleSet)

        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(inputStream);

        rules = ruleSetBuilder.ruleSet.rules
    }

    /**
     * @return a List of Rule objects
     */
    List getRules() {
        return rules
    }
}