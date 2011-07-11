/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.jdbc

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AstVisitor
import org.codenarc.rule.ClassReferenceAstVisitor

/**
 * Check for direct use of java.sql.ResultSet, which is not necessary if using the Groovy Sql facility or an
 * ORM framework such as Hibernate.
 *
 * Known limitation: Does not catch references as Anonymous Inner class: def x = new java.sql.ResultSet() { .. }
 *
 * @author Chris Mair
 */
class JdbcResultSetReferenceRule extends AbstractAstVisitorRule {
    String name = 'JdbcResultSetReference'
    int priority = 2

    @Override
    AstVisitor getAstVisitor() {
        new ClassReferenceAstVisitor('java.sql.ResultSet')
    }
}
