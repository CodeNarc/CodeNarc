package org.codenarc.report

import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.test.AbstractTestCase

import java.text.DateFormat

abstract class AbstractReportWriterTestCase extends AbstractTestCase {

    // NOTE: These values are used across multiple tests

    protected static final int LINE1 = 111
    protected static final int LINE2 = 222
    protected static final int LINE3 = 333
    protected static final SOURCE_LINE1 = 'if (count < 23 && index <= 99 && name.contains(\'\u0000\')) {'
    protected static final SOURCE_LINE3 = 'throw new Exception("cdata=<![CDATA[whatever]]>") // c:\\\\data - Some very long message 1234567890123456789012345678901234567890'
    protected static final MESSAGE2 = 'bad stuff: !@#$%^&*()_+<>'
    protected static final MESSAGE3 = 'Other info c:\\\\data'
    protected static final VIOLATION1 = new Violation(rule:new StubRule(name:'Rule1', priority:1), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    protected static final VIOLATION2 = new Violation(rule:new StubRule(name:'Rule2', priority:3), lineNumber:LINE2, message:MESSAGE2)
    protected static final VIOLATION3 = new Violation(rule:new StubRule(name:'Rule3', priority:3), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3)

    protected static final TIMESTAMP_DATE = new Date(1262361072497)
    protected static final FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)

    protected static final String TITLE = 'My Cool Project'
    protected static final String SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    protected static final String SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    protected static final String VERSION_FILE = 'src/main/resources/codenarc-version.txt'
    protected static final String VERSION = new File(VERSION_FILE).text

    protected static final String CODENARC_URL = 'https://codenarc.org'

}
