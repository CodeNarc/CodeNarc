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
package org.codenarc.rule.exceptions

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AstVisitor

/**
 * Rule that checks for throwing an exception from within a finally block
 *
 * @author Chris Mair
 * @version $Revision: 24 $ - $Date: 2009-01-31 07:47:09 -0500 (Sat, 31 Jan 2009) $
 */
class ThrowThrowableRule extends AbstractAstVisitorRule {
    String name = 'ThrowThrowable'
    int priority = 2

    AstVisitor getAstVisitor() {
        return new CommonThrowAstVisitor(['Throwable', 'java.lang.Throwable'])
    }
}