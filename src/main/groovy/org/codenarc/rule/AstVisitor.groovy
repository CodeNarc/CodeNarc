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
package org.codenarc.rule

import org.codehaus.groovy.ast.GroovyClassVisitor
import org.codenarc.source.SourceCode

/**
 * Interface for Groovy AST Visitors used with Rules
 *
 * @author Chris Mair
 */
interface AstVisitor extends GroovyClassVisitor {

    /**
     * Set the Rule associated with this visitor
     * @param rule - the Rule
     */
    void setRule(Rule rule)

    /**
     * Set the SourceCode associated with this visitor
     * @param sourceCode - the SourceCode
     */
    void setSourceCode(SourceCode sourceCode)

    /**
     * Retrieve the List of Violations resulting from applying this visitor
     * @return the List of Violations; may be empty
     */
    List<Violation> getViolations()
}
