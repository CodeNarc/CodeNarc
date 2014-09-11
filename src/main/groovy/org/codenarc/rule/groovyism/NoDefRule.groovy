package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

import java.util.regex.Pattern

/**
 * Def keyword is overused and should be replaced with specific type.
 * <p/>
 * The <code>excludePattern</code> property optionally specifies regex
 * to find text which could occur immediately after def.
 *
 * @author Dominik Przybysz
 */
class NoDefRule extends AbstractRule {
    String name = "No def"
    int priority = 3
    protected static final String MESSAGE = 'def should not be used'
    String excludePattern

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        Pattern excludeFilter = excludePattern ? ~/.*def\s+$excludePattern.*/ : null
        sourceCode.lines.eachWithIndex {
            String line, int idx ->
                if (line.contains("def ") && (!excludeFilter || !(line ==~ excludeFilter))){
                    Violation violation = new Violation();
                    violation.setRule(this);
                    violation.setLineNumber(idx + 1);
                    violation.setSourceLine(line.trim());
                    violation.setMessage(MESSAGE);
                    violations.add(violation);
                }
        }
    }
}