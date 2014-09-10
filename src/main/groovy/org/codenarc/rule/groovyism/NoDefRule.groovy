package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

import java.util.regex.Pattern

/**
 * Created by alien on 10.09.14.
 */
class NoDefRule extends AbstractRule {
    String name = "No def"
    int priority = 3
    protected static final String MESSAGE = 'def should not be used'
    String excludePattern = ""

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        Pattern excludeFilter = excludePattern ? ~/.*$excludePattern.*/ : null
        sourceCode.lines.eachWithIndex {
            String line, int idx ->
                if (line.contains("def ") && (!excludeFilter || !(line ==~ excludeFilter))){
                    Violation violation = new Violation();
                    violation.setRule(this);
                    violation.setLineNumber(idx);
                    violation.setSourceLine(line.trim());
                    violation.setMessage(MESSAGE);
                    violations.add(violation);
                }
        }
    }
}