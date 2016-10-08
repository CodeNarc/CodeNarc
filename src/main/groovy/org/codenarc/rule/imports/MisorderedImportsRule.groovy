/*
 * Copyright 2016 the original author or authors.
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
package org.codenarc.rule.imports

import static org.codenarc.util.LongestCommonSubsequenceDiffUtil.computeDiff

import groovy.transform.Immutable
import org.codehaus.groovy.ast.ImportNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.LongestCommonSubsequenceDiffUtil

/**
 * <p>Imports should be sorted alphabetically and in groups. This is configured by the <code>patterns</code>.</p>
 *
 * <p><code>patterns</code> is a list of glob patterns describing import statements. The asterisk is only allowed
 * at the end. For a <code>patterns<code> to be valid, it must cover two very important cases:</p>
 * <ul>
 *     <li>import *</li>
 *     <li>import static *</li>
 * </ul>
 *
 * <p>Within each of these patterns, all the entries that match are sorted alphabetically.</p>
 *
 * <p>This is the default pattern for imports</p>
 * <pre>
 * [
 *    'import static *',
 *    '',
 *    'import *',
 *    '',
 *    'import javax.*',
 *    'import java.*',
 * ]</pre>
 *
 * <p>Any blank lines between import statements are ignored, including extra blank lines. The empty strings ('') are
 * separators between sections, but are not enforced.</p>
 *
 * <p>When you have the default pattern configured, this is a valid order of imports</p>
 * <pre>
 *  import static toons.looney.Coyote.*
 *  import static toons.looney.Roadrunner.legs
 *
 *  import com.acme.Device
 *  import com.acme.powders.*
 *
 *  import javax.acme.Switch
 *  import java.acme.Cord
 *  import java.toons.*
 * </pre>
 *
 * <ul>
 *     <li>The static imports are all on top, regardless of which package they belong to. And they're all sorted
 *          alphabetically</li>
 *     <li>Then there is a newline separating it from the next group of imports</li>
 *     <li>All unmatched imports go next</li>
 *     <li>Then there is another newline separating it from the next group of imports</li>
 *     <li>All <code>javax.*</code> imports come after this (sorted alphabetically of course)</li>
 *     <li>All <code>java.*</code> imports come after this. There is no newline separating them from the previous
 *          group</li>
 * </ul>
 *
 * @author Rahul Somasunderam
 */
class MisorderedImportsRule extends AbstractAstVisitorRule {

    public static final String SEPARATOR = ''
    public static final List<String> DEFAULT_PATTERN = [
            'import static *',
            SEPARATOR,
            'import *',
            SEPARATOR,
            'import javax.*',
            'import java.*',
    ]

    public static final List<String> STATIC_LAST_PATTERN = [
            'import *',
            SEPARATOR,
            'import javax.*',
            'import java.*',
            SEPARATOR,
            'import static *',
    ]

    String name = 'MisorderedImports'
    int priority = 3

    List<String> patterns = DEFAULT_PATTERN

    @Override
    void validate() {
        assert patterns.contains('import *')
        assert patterns.contains('import static *')
    }

    @Override
    void applyTo(SourceCode sourceCode, List violations) {
        List<Import> imports = getImports(sourceCode)

        int idx = 0
        def typedPatterns = patterns.
                collect { String it -> AbstractImportPattern.toRule(it, idx++) }.
                sort(false) { AbstractImportPattern it -> -it.priority }

        def groups = computeGroups(imports, typedPatterns)

        String expected = computeExpectedImports(typedPatterns, groups)

        def lineNumbers = imports*.lineNumber
        if (lineNumbers) {

            def importLines = lineNumbers.min()..lineNumbers.max()

            def actual = importLines.
                    collect { int ln -> imports.find { Import it -> it.lineNumber == ln }?.matcher ?: '' }.
                    join('\n')

            def misorderedImports = computeDiff(expected.split('\n'), actual.split('\n'))

            addAllViolations(misorderedImports, violations, lineNumbers)
        }

    }

    private Map<String, List<LongestCommonSubsequenceDiffUtil.Line>> addAllViolations(
            List<LongestCommonSubsequenceDiffUtil.Line> misorderedImports, List<Violation> violations,
            List<Integer> lineNumbers) {
        misorderedImports.
                findAll { it.text != '' }.
                groupBy { it.text }.
                each { String text, List<LongestCommonSubsequenceDiffUtil.Line> listOfImports ->
                    addViolation(listOfImports, text, violations, lineNumbers)
                }
    }

    private void addViolation(List<LongestCommonSubsequenceDiffUtil.Line> listOfImports, String text,
                              List<Violation> violations, List<Integer> lineNumbers) {
        assert listOfImports.size() == 2

        def startingLine = lineNumbers.min()
        def expectedLine = listOfImports.
                find { it.mode == LongestCommonSubsequenceDiffUtil.Line.Mode.REMOVE }.lineNumber + startingLine
        def actualLine = listOfImports.
                find { it.mode == LongestCommonSubsequenceDiffUtil.Line.Mode.ADD }.lineNumber + startingLine

        violations.add(new Violation(
                rule: this,
                lineNumber: actualLine,
                sourceLine: text,
                message: "Expected '${text}' on line ${expectedLine}. Found on line ${actualLine}"
        ))
    }

    /**
     * Extracts a list of all imports from the sourcecode
     */
    private static List<Import> getImports(SourceCode sourceCode) {
        List<Import> imports = []
        sourceCode.ast?.imports?.each { ImportNode importNode ->
            addImport(importNode, imports, false)
        }
        sourceCode.ast?.starImports?.each { ImportNode importNode ->
            addImport(importNode, imports, true)
        }
        sourceCode.ast?.staticImports?.each { Map.Entry<String, ImportNode> importNode ->
            addStaticImport(importNode, imports)
        }
        sourceCode.ast?.staticStarImports?.each { Map.Entry<String, ImportNode> importNode ->
            addStaticImport(importNode, imports)
        }
        imports
    }

    private static String computeExpectedImports(
            List<AbstractImportPattern> typedPatterns, Map<AbstractImportPattern, List<Import>> groups) {
        def expected = typedPatterns.
                sort(false) { AbstractImportPattern it -> it.position }.
                collect { AbstractImportPattern rule ->
                    if (rule instanceof NewlinePattern) {
                        '\n'
                    } else {
                        def texts = groups[rule]*.matcher ?: []
                        (texts.sort().join('\n') + '\n') ?: ''
                    }
                }.
                findAll { it != '' }.
                join('\n').
                replaceAll('\n\n+', '\n\n').
                trim()
        expected
    }

    private static Map<AbstractImportPattern, List<Import>> computeGroups(
            List<Import> imports, List<AbstractImportPattern> typedPatterns) {
        def groups = [:]
        for (def importStatement : imports) {
            def matchingPattern = typedPatterns.find { AbstractImportPattern it ->
                it instanceof ImportPattern && importStatement.matcher.startsWith(it.pattern.replace('*', ''))
            } as ImportPattern
            if (!groups.containsKey(matchingPattern)) {
                groups[matchingPattern] = []
            }
            (groups[matchingPattern] as List<Import>) << importStatement
        }
        groups
    }

    private static void addImport(ImportNode importNode, List<Import> imports, boolean star) {
        def theImport = importNode.className ?: importNode.packageName
        imports << new Import(importNode.lineNumber, false, "${theImport}${star ? '*' : ''}", importNode.alias)
    }

    private static void addStaticImport(Map.Entry<String, ImportNode> entry, List<Import> imports) {
        ImportNode importNode = entry.value
        def theImport = (importNode.className ?: importNode.packageName) + '.' + (importNode.fieldName ?: '*')
        imports << new Import(importNode.lineNumber, true, theImport, importNode.alias)
    }

    @Immutable
    private static class Import {
        int lineNumber
        boolean isStatic
        String theImport
        String alias

        String getMatcher() {
            (alias && !theImport.endsWith(".${alias}")) ?
                    "import ${isStatic ? 'static ' : ''}${theImport} as ${alias}" :
                    "import ${isStatic ? 'static ' : ''}${theImport}"
        }

        @Override
        String toString() {
            "${lineNumber.toString().padLeft(3)}|${matcher}"
        }
    }

    private static abstract class AbstractImportPattern {
        int position

        static AbstractImportPattern toRule(String pattern, int position) {
            pattern ? new ImportPattern(pattern: pattern, position: position) : new NewlinePattern(position: position)
        }

        abstract int getPriority()
    }

    private static class NewlinePattern extends AbstractImportPattern {

        final int priority = -1

        @Override
        String toString() {
            "NewlinePattern($position, $priority)"
        }
    }

    private static class ImportPattern extends AbstractImportPattern {
        String pattern

        @Override
        int getPriority() { pattern.length() }

        @Override
        String toString() { "ImportPattern($pattern, $position, $priority)" }
    }

}
