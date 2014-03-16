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
 * Rule that checks for throwing an instance of java.lang.Error
 *
 * @author Chris Mair
  */
class ThrowErrorRule extends AbstractAstVisitorRule {
    String name = 'ThrowError'
    int priority = 2

    AstVisitor getAstVisitor() {
        new CommonThrowAstVisitor(['Error', 'java.lang.Error'])
    }
}
