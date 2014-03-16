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

import org.apache.log4j.Logger
import org.codenarc.util.io.DefaultResourceFactory
import org.codenarc.util.io.ResourceFactory

/**
 * A <code>RuleSet</code> implementation that parses Rule definitions from XML read from a
 * file. The filename passed into the constructor is interpreted relative to the classpath, by
 * default, but may be optionally prefixed by any of the valid java.net.URL prefixes, such as
 * "file:" (to load from a relative or absolute path on the filesystem), or "http:".
 * <p/>
 * Note that this class attempts to read the file and parse the XML from within the constructor.
 *
 * @author Chris Mair
  */
class XmlFileRuleSet implements RuleSet {

    private static final LOG = Logger.getLogger(XmlFileRuleSet)
    private final ResourceFactory resourceFactory = new DefaultResourceFactory()
    private List rules = []

    /**
     * Construct a new instance on the specified RuleSet file path
     * @param path - the path to the XML RuleSet definition file. The path is relative to the classpath,
     *      by default, but may be optionally prefixed by any of the valid java.net.URL prefixes, such
     *      as "file:" (to load from a relative or absolute path on the filesystem), or "http:". The
     *      path must not be empty or null.
     */
    XmlFileRuleSet(String path) {
        assert path
        LOG.info("Loading ruleset from [$path]")
        def inputStream = resourceFactory.getResource(path).inputStream
        inputStream.withReader { reader ->
            def xmlReaderRuleSet = new XmlReaderRuleSet(reader)
            this.rules = xmlReaderRuleSet.rules
        }
    }

    /**
     * @return a List of Rule objects
     */
    List getRules() {
        rules
    }
}
