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

import org.codenarc.rule.Rule
import org.codenarc.util.io.DefaultResourceFactory
import org.codenarc.util.io.ResourceFactory

/**
 * A private utility class for the <code>RuleSet</code> classes. All methods are static.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
  */
class RuleSetUtil {

    protected static final String CLASS_LOADER_SYS_PROP = 'codenarc.useCurrentThreadContextClassLoader'
    private static final ResourceFactory RESOURCE_FACTORY = new DefaultResourceFactory()

    protected static void assertClassImplementsRuleInterface(Class ruleClass) {
        assert ruleClass
        assert Rule.isAssignableFrom(ruleClass), "The rule class [${ruleClass.name}] does not implement the Rule interface"
    }

    static RuleSet loadRuleSetFile(String path) {
        isXmlFile(path) ? new XmlFileRuleSet(path) : new GroovyDslRuleSet(path)
    }

    protected static Rule loadRuleScriptFile(String path) {
        def inputStream = RESOURCE_FACTORY.getResource(path).inputStream
        Class ruleClass
        inputStream.withStream { input ->
            ClassLoader parentClassLoader = (System.getProperty(CLASS_LOADER_SYS_PROP) == 'true') ?
                Thread.currentThread().getContextClassLoader() :
                getClass().classLoader
            GroovyClassLoader gcl = new GroovyClassLoader(parentClassLoader)
            ruleClass = gcl.parseClass(input.text)
        }
        assertClassImplementsRuleInterface(ruleClass)
        ruleClass.newInstance()
    }

    private static boolean isXmlFile(String path) {
        path && path.endsWith('.xml')
    }

    private RuleSetUtil() { }
}
