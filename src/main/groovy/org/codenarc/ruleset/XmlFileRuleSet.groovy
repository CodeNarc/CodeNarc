/*
 * Copyright 2008 the original author or authors.
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

/**
 * A <code>RuleSet</code> implementation that parses Rule definitions from XML read from a
 * file. The filename passed into the constructor is interpreted relative to the classpath.
 * Note that this class attempts to read the file and parse the XML from within the constructor.
 *
 * @author Chris Mair
 * @version $Revision: 190 $ - $Date: 2009-01-13 20:52:35 -0500 (Tue, 13 Jan 2009) $
 */
class XmlFileRuleSet implements RuleSet {

    private List rules = []

    /**
     * Construct a new instance on the specified RuleSet file path
     * @param path - the path to the XML RuleSet definition file, relative to the classpath; must not be empty or null
     */
    XmlFileRuleSet(String path) {
        assert path
        def inputStream = getClass().classLoader.getResourceAsStream(path)
        assert inputStream, "File [$path] does not exist or is not accessible"
        inputStream.withReader { reader ->
            def xmlReaderRuleSet = new XmlReaderRuleSet(reader)
            this.rules = xmlReaderRuleSet.rules
        }
    }

    /**
     * @return a List of Rule objects
     */
    List getRules() {
        return rules
    }
}