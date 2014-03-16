/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.grails

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AstVisitor

/**
 * Forbids usage of SQL reserved keywords as class or field names in Grails domain classes.
 * Naming a domain class (or its field) with such a keyword causes SQL schema creation errors and/or redundant
 * table/column name mappings.<br/>
 *
 * Note: due to limited type information available during CodeNarc's operation, this rule will report fields
 * of type {@link java.io.Serializable}, but not of its implementations. Please specify any implementations
 * used as domain properties in {@code #additionalHibernateBasicTypes}.
 *
 * @author Artur Gajowy
 */
class GrailsDomainReservedSqlKeywordNameRule extends AbstractAstVisitorRule {

    String name = 'GrailsDomainReservedSqlKeywordName'
    int priority = 2
    Class astVisitorClass = GrailsDomainReservedSqlKeywordNameAstVisitor
    String applyToFilesMatching = GrailsUtil.DOMAIN_FILES

    //based on http://developer.mimer.com/validator/sql-reserved-words.tml
    private final reservedSqlKeywords = ['ABSOLUTE', 'ACTION', 'ADD', 'AFTER', 'ALL', 'ALLOCATE', 'ALTER', 'AND', 'ANY',
        'ARE', 'ARRAY', 'AS', 'ASC', 'ASENSITIVE', 'ASSERTION', 'ASYMMETRIC', 'AT', 'ATOMIC', 'AUTHORIZATION', 'AVG',
        'BEFORE', 'BEGIN', 'BETWEEN', 'BIGINT', 'BINARY', 'BIT', 'BIT_LENGTH', 'BLOB', 'BOOLEAN', 'BOTH', 'BREADTH',
        'BY', 'CALL', 'CALLED', 'CASCADE', 'CASCADED', 'CASE', 'CAST', 'CATALOG', 'CHAR', 'CHARACTER',
        'CHARACTER_LENGTH', 'CHAR_LENGTH', 'CHECK', 'CLOB', 'CLOSE', 'COALESCE', 'COLLATE', 'COLLATION', 'COLUMN',
        'COMMIT', 'CONDITION', 'CONNECT', 'CONNECTION', 'CONSTRAINT', 'CONSTRAINTS', 'CONSTRUCTOR', 'CONTAINS',
        'CONTINUE', 'CONVERT', 'CORRESPONDING', 'COUNT', 'CREATE', 'CROSS', 'CUBE', 'CURRENT', 'CURRENT_DATE',
        'CURRENT_DEFAULT_TRANSFORM_GROUP', 'CURRENT_PATH', 'CURRENT_ROLE', 'CURRENT_TIME', 'CURRENT_TIMESTAMP',
        'CURRENT_TRANSFORM_GROUP_FOR_TYPE', 'CURRENT_USER', 'CURSOR', 'CYCLE', 'DATA', 'DATE', 'DAY', 'DEALLOCATE',
        'DEC', 'DECIMAL', 'DECLARE', 'DEFAULT', 'DEFERRABLE', 'DEFERRED', 'DELETE', 'DEPTH', 'DEREF', 'DESC',
        'DESCRIBE', 'DESCRIPTOR', 'DETERMINISTIC', 'DIAGNOSTICS', 'DISCONNECT', 'DISTINCT', 'DO', 'DOMAIN', 'DOUBLE',
        'DROP', 'DYNAMIC', 'EACH', 'ELEMENT', 'ELSE', 'ELSEIF', 'END', 'EQUALS', 'ESCAPE', 'EXCEPT', 'EXCEPTION',
        'EXEC', 'EXECUTE', 'EXISTS', 'EXIT', 'EXTERNAL', 'EXTRACT', 'FALSE', 'FETCH', 'FILTER', 'FIRST', 'FLOAT', 'FOR',
        'FOREIGN', 'FOUND', 'FREE', 'FROM', 'FULL', 'FUNCTION', 'GENERAL', 'GET', 'GLOBAL', 'GO', 'GOTO', 'GRANT',
        'GROUP', 'GROUPING', 'HANDLER', 'HAVING', 'HOLD', 'HOUR', 'IDENTITY', 'IF', 'IMMEDIATE', 'IN', 'INDICATOR',
        'INITIALLY', 'INNER', 'INOUT', 'INPUT', 'INSENSITIVE', 'INSERT', 'INT', 'INTEGER', 'INTERSECT', 'INTERVAL',
        'INTO', 'IS', 'ISOLATION', 'ITERATE', 'JOIN', 'KEY', 'LANGUAGE', 'LARGE', 'LAST', 'LATERAL', 'LEADING', 'LEAVE',
        'LEFT', 'LEVEL', 'LIKE', 'LOCAL', 'LOCALTIME', 'LOCALTIMESTAMP', 'LOCATOR', 'LOOP', 'LOWER', 'MAP', 'MATCH',
        'MAX', 'MEMBER', 'MERGE', 'METHOD', 'MIN', 'MINUTE', 'MODIFIES', 'MODULE', 'MONTH', 'MULTISET', 'NAMES',
        'NATIONAL', 'NATURAL', 'NCHAR', 'NCLOB', 'NEW', 'NEXT', 'NO', 'NONE', 'NOT', 'NULL', 'NULLIF', 'NUMERIC',
        'OBJECT', 'OCTET_LENGTH', 'OF', 'OLD', 'ON', 'ONLY', 'OPEN', 'OPTION', 'OR', 'ORDER', 'ORDINALITY', 'OUT',
        'OUTER', 'OUTPUT', 'OVER', 'OVERLAPS', 'PAD', 'PARAMETER', 'PARTIAL', 'PARTITION', 'PATH', 'POSITION',
        'PRECISION', 'PREPARE', 'PRESERVE', 'PRIMARY', 'PRIOR', 'PRIVILEGES', 'PROCEDURE', 'PUBLIC', 'RANGE', 'READ',
        'READS', 'REAL', 'RECURSIVE', 'REF', 'REFERENCES', 'REFERENCING', 'RELATIVE', 'RELEASE', 'REPEAT', 'RESIGNAL',
        'RESTRICT', 'RESULT', 'RETURN', 'RETURNS', 'REVOKE', 'RIGHT', 'ROLE', 'ROLLBACK', 'ROLLUP', 'ROUTINE', 'ROW',
        'ROWS', 'SAVEPOINT', 'SCHEMA', 'SCOPE', 'SCROLL', 'SEARCH', 'SECOND', 'SECTION', 'SELECT', 'SENSITIVE',
        'SESSION', 'SESSION_USER', 'SET', 'SETS', 'SIGNAL', 'SIMILAR', 'SIZE', 'SMALLINT', 'SOME', 'SPACE', 'SPECIFIC',
        'SPECIFICTYPE', 'SQL', 'SQLCODE', 'SQLERROR', 'SQLEXCEPTION', 'SQLSTATE', 'SQLWARNING', 'START', 'STATE',
        'STATIC', 'SUBMULTISET', 'SUBSTRING', 'SUM', 'SYMMETRIC', 'SYSTEM', 'SYSTEM_USER', 'TABLE', 'TABLESAMPLE',
        'TEMPORARY', 'THEN', 'TIME', 'TIMESTAMP', 'TIMEZONE_HOUR', 'TIMEZONE_MINUTE', 'TO', 'TRAILING', 'TRANSACTION',
        'TRANSLATE', 'TRANSLATION', 'TREAT', 'TRIGGER', 'TRIM', 'TRUE', 'UNDER', 'UNDO', 'UNION', 'UNIQUE', 'UNKNOWN',
        'UNNEST', 'UNTIL', 'UPDATE', 'UPPER', 'USAGE', 'USER', 'USING', 'VALUE', 'VALUES', 'VARCHAR', 'VARYING', 'VIEW',
        'WHEN', 'WHENEVER', 'WHERE', 'WHILE', 'WINDOW', 'WITH', 'WITHIN', 'WITHOUT', 'WORK', 'WRITE', 'YEAR', 'ZONE'
    ] as Set

    //based on http://docs.jboss.org/hibernate/orm/3.6/reference/en-US/html/types.html#types-value-basic
    private final hibernateBasicTypes = ['String', 'Character', 'boolean', 'Boolean', 'byte', 'Byte', 'short', 'Short',
        'int', 'Integer', 'long', 'Long', 'float', 'Float', 'double', 'Double', 'BigInteger', 'BigDecimal', 'Timestamp',
        'Time', 'Date', 'Calendar', 'Currency', 'Locale', 'TimeZone', 'URL', 'Class', 'Blob', 'Clob', '[B', 'Byte[]',
        '[C', 'Character[]', 'UUID', 'Serializable'
    ] as Set

    String additionalReservedSqlKeywords = ''
    String additionalHibernateBasicTypes = ''

    @Override
    AstVisitor getAstVisitor() {
        return new GrailsDomainReservedSqlKeywordNameAstVisitor(
            reservedSqlKeywords + toSet(additionalReservedSqlKeywords),
            hibernateBasicTypes + toSet(additionalHibernateBasicTypes)
        )
    }

    private Set<String> toSet(String listInString) {
        listInString.split(/,\s*/).findAll { !it.isEmpty() } as Set<String>
    }
}

class GrailsDomainReservedSqlKeywordNameAstVisitor extends AbstractAstVisitor {

    private final reservedSqlKeywords
    private final hibernateBasicTypes
    private final instanceFields = []
    private transients = []

    GrailsDomainReservedSqlKeywordNameAstVisitor(Set<String> reservedSqlKeywords, Set<String> hibernateBasicTypes) {
        this.reservedSqlKeywords = reservedSqlKeywords*.toUpperCase()
        this.hibernateBasicTypes = hibernateBasicTypes
    }

    @Override
    protected void visitClassEx(ClassNode node) {
        if (node.name.toUpperCase() in reservedSqlKeywords) {
            addViolation(node, violationMessage(node.name, 'domain class name'))
        }
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        addViolationsForInstanceFields()
    }

    @Override
    void visitField(FieldNode node) {
        if (node.inStaticContext) {
            if (node.name == 'transients') {
                extractTransients(node)
            }
        } else {
            instanceFields << node
        }
    }

    @SuppressWarnings('EmptyCatchBlock')
    private void extractTransients(FieldNode transientsNode) {
        try {
            assert transientsNode.initialExpression instanceof ListExpression
            def listExpression = transientsNode.initialExpression as ListExpression
            assert listExpression.expressions.every { it instanceof ConstantExpression }
            def expressions = listExpression.expressions as List<ConstantExpression>
            assert expressions.every { it.type.name == String.name }
            transients = expressions*.value
        } catch (AssertionError e) {
        }
    }

    private addViolationsForInstanceFields() {
        instanceFields.each { FieldNode field ->
            if (hibernateWouldCreateColumnFor(field) && field.name.toUpperCase() in reservedSqlKeywords) {
                addViolation(field, violationMessage(field.name, 'domain class\' field name'))
            }
        }
    }

    private boolean hibernateWouldCreateColumnFor(FieldNode field) {
        !(field.dynamicTyped || field.name in transients) && field.type.name in hibernateBasicTypes
    }

    private String violationMessage(String identifier, String kindOfName) {
        "'$identifier' is a reserved SQL keyword and - as such - a problematic $kindOfName."
    }
}
