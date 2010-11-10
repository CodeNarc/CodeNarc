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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitorRule

/**
 * This rule detects when the compareTo(Object) method is called directly in code instead of using the <=>, >, >=, <, and <= operators. A groovier way to express this: a.compareTo(b) is this: a <=> b, or using the other operators. 
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class ExplicitCallToCompareToMethodRule extends AbstractAstVisitorRule {
    String name = 'ExplicitCallToCompareToMethod'
    int priority = 2
    Class astVisitorClass = ExplicitCallToCompareToMethodAstVisitor
    boolean ignoreThisReference = false
}

class ExplicitCallToCompareToMethodAstVisitor extends ExplicitCallToMethodAstVisitor {

    def ExplicitCallToCompareToMethodAstVisitor() {
        super('compareTo');
    }
}
