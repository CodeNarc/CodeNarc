/*
 * Copyright 2019 the original author or authors.
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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * etects when the map.putAt(k, v) method is called directly rather than using map[k] = v.
 *
 * @author Chris Mair
 */
class ExplicitCallToPutAtMethodRule extends AbstractAstVisitorRule {

    String name = 'ExplicitCallToPutAtMethod'
    int priority = 2
    Class astVisitorClass = ExplicitCallToPutAtMethodAstVisitor
    boolean ignoreThisReference = false
}

class ExplicitCallToPutAtMethodAstVisitor extends ExplicitCallToMethodAstVisitor {
    ExplicitCallToPutAtMethodAstVisitor() {
        super('putAt', 2)
    }

    @Override
    protected String getViolationMessage(MethodCallExpression call) {
        "Explicit call to ${call.text} method can be rewritten as ${call.objectExpression.text}[${call.arguments[0].text}] = ${call.arguments[1].text}"
    }
}
