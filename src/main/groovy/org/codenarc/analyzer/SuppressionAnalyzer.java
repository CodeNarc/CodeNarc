package org.codenarc.analyzer;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codenarc.rule.AbstractAstVisitor;
import org.codenarc.rule.Rule;
import org.codenarc.rule.Violation;
import org.codenarc.source.SourceCode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class encapsulates all of the logic for determining if an rule is suppressed or not.
 */
public class SuppressionAnalyzer {

    private static final String ALL = "all";
    private static final String CODE_NARC = "CodeNarc";
    private static final ClassNode SUPPRESS_WARNINGS = ClassHelper.make(SuppressWarnings.class);

    private final SourceCode source;
    private boolean initialized = false;
    private final Object initializationLock = new Object();
    private final Set<String> suppressedRuleNames = Collections.synchronizedSet(new HashSet<String>());
    private final Map<String, BitSet> suppressionsByLineNumber = new ConcurrentHashMap<String, BitSet>();

    public SuppressionAnalyzer(SourceCode source) {
        this.source = source;
    }

    public boolean isRuleSuppressed(Rule rule) {
        init();
        return suppressedRuleNames.contains(rule.getName())
            || suppressedRuleNames.contains(CODE_NARC + "." + rule.getName())
            || suppressedRuleNames.contains(ALL)
            || suppressedRuleNames.contains(CODE_NARC);
    }

    public List<Violation> filterSuppressedViolations(Iterable<Violation> violations) {
        List<Violation> result = new ArrayList<Violation>();
        if (violations == null) return result;
        for (Violation v : violations) {
            if (!isViolationSuppressed(v)) {
                result.add(v);
            }
        }
        return result;
    }

    public boolean isViolationSuppressed(Violation violation) {
        if (violation == null) return false;
        if (violation.getRule() == null) return false;
        if (violation.getRule().getName() == null) return false;
        if (violation.getLineNumber() == null) return false;
        if (violation.getLineNumber() < 0) return false;

        init();

        String ruleName = violation.getRule().getName();
        int lineNumber = violation.getLineNumber();

        BitSet linesForAll = suppressionsByLineNumber.get(ALL);
        if (linesForAll != null && linesForAll.get(lineNumber)) {   // only if "all" applies to this line
            return true;
        }

        BitSet linesForCodeNarc = suppressionsByLineNumber.get(CODE_NARC);
        if (linesForCodeNarc != null && linesForCodeNarc.get(lineNumber)) {   // only if "CodeNarc" applies to this line
            return true;
        }

        BitSet lines = new BitSet();

        BitSet linesWithoutPrefix = suppressionsByLineNumber.get(ruleName);
        if (linesWithoutPrefix != null) {
            lines.or(linesWithoutPrefix);
        }

        BitSet linesWithPrefix = suppressionsByLineNumber.get(CODE_NARC + "." + ruleName);
        if (linesWithPrefix != null) {
            lines.or(linesWithPrefix);
        }

        return lines.get(lineNumber);
    }

    private void init() {
        synchronized (initializationLock) {
            if (!initialized) {
                ModuleNode ast = source.getAst();
                if (ast != null) {
                    // These rule names are suppressed for the entire AST
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getPackage()));
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getImports()));
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getStarImports()));
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getStaticImports().values()));
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getStaticStarImports().values()));
                    // if it is the only class in the file, then a @SuppressWarnings applies to everything
                    if (ast.getClasses() != null && ast.getClasses().size() == 1) {
                        suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getClasses()));
                    }

                    // build up suppressions by line number
                    suppressionsByLineNumber.putAll(getSuppressionsByLineNumber(ast));
                }
                initialized = true;
            }
        }
    }

    private Map<String, BitSet> getSuppressionsByLineNumber(ModuleNode ast) {

        final Map<String, BitSet> result = new HashMap<String, BitSet>();
        final int numLines = getLineCount(ast);

        for (ImportNode importNode : getImportNodes(ast)) {
            for (String ruleName : getSuppressedRuleNames(importNode)) {
                populateLineNumbers(importNode, result, numLines, ruleName);
            }
        }

        for (ClassNode classNode : ast.getClasses()) {
            for (String ruleName : getSuppressedRuleNames(classNode)) {
                populateLineNumbers(classNode, result, numLines, ruleName);
            }
            for (AnnotatedNode fieldNode : from(classNode.getFields())) {
                for (String ruleName : getSuppressedRuleNames(fieldNode)) {
                    populateLineNumbers(fieldNode, result, numLines, ruleName);
                }
            }
            AbstractAstVisitor declarationVisitor = new AbstractAstVisitor() {
                public void visitDeclarationExpression(DeclarationExpression expression) {
                    for (String ruleName : getSuppressedRuleNames(expression)) {
                        populateLineNumbers(expression, result, numLines, ruleName);
                    }
                    super.visitDeclarationExpression(expression);
                }
            };
            for (MethodNode methodNode : from(classNode.getMethods())) {
                for (String ruleName : getSuppressedRuleNames(methodNode)) {
                    populateLineNumbers(methodNode, result, numLines, ruleName);
                }
                declarationVisitor.visitMethod(methodNode);
            }
            for (ConstructorNode constructorNode : from(classNode.getDeclaredConstructors())) {
                for (String ruleName : getSuppressedRuleNames(constructorNode)) {
                    populateLineNumbers(constructorNode, result, numLines, ruleName);
                }
                declarationVisitor.visitConstructor(constructorNode);
            }
        }
        return result;
    }

    private List<ImportNode> getImportNodes(ModuleNode ast) {
        List<ImportNode> importNodes = new ArrayList<ImportNode>();
        importNodes.addAll(ast.getImports());
        importNodes.addAll(ast.getStarImports());
        importNodes.addAll(ast.getStaticImports().values());
        importNodes.addAll(ast.getStaticStarImports().values());
        return importNodes;
    }

    @SuppressWarnings({"unchecked"})
    private static <T extends Collection> T from(T from) {
        if (from != null) return from;
        return (T) Collections.emptyList();
    }

    private static void populateLineNumbers(AnnotatedNode node, Map<String, BitSet> result, int numLines, String ruleName) {
        final BitSet bits;
        if (result.containsKey(ruleName)) {
            bits = result.get(ruleName);
        } else {
            bits = new BitSet(numLines);
        }
        bits.set(node.getLineNumber(), node.getLastLineNumber() + 1);
        result.put(ruleName, bits);
    }

    private static int getLineCount(ModuleNode ast) {
        int highest = 0;
        for (AnnotatedNode classNode : ast.getClasses()) {
            if (classNode.getLastLineNumber() > highest) {
                highest = classNode.getLastLineNumber();
            }
        }
        return highest;
    }

    private static Collection<String> getSuppressedRuleNames(Collection<? extends AnnotatedNode> imports) {
        List<String> result = new ArrayList<String>();
        if (imports != null) {
            for (AnnotatedNode node : imports) {
                result.addAll(getSuppressedRuleNames(node));
            }
        }
        return result;
    }

    private static Collection<String> getSuppressedRuleNames(AnnotatedNode node) {
        List<String> result = new ArrayList<String>();
        if (node == null) return result;

        Set<AnnotationNode> annos = getSuppressWarningsAnnotations(node);
        for (AnnotationNode annotation : annos) {

            Map<String, Expression> members = annotation.getMembers();
            if (members != null) {
                Collection<Expression> values = members.values();
                if (values != null) {
                    for (Expression exp : values) {
                        if (exp instanceof ConstantExpression && ((ConstantExpression) exp).getValue() instanceof String) {
                            result.add((String) ((ConstantExpression) exp).getValue());
                        } else if (exp instanceof ListExpression) {
                            List<Expression> expressions = ((ListExpression) exp).getExpressions();
                            if (expressions != null) {
                                for (Expression entry : expressions) {
                                    if (entry instanceof ConstantExpression && ((ConstantExpression) entry).getValue() instanceof String) {
                                        result.add((String) ((ConstantExpression) entry).getValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static Set<AnnotationNode> getSuppressWarningsAnnotations(AnnotatedNode node) {
        Set<AnnotationNode> result = new HashSet<AnnotationNode>();
        result.addAll(node.getAnnotations(SUPPRESS_WARNINGS));

        List<AnnotationNode> annots = node.getAnnotations();
        if (annots != null) {
            for (AnnotationNode n : annots) {
                ClassNode classNode = n.getClassNode();
                if (classNode != null) {
                    String name = classNode.getName();
                    if ("SuppressWarnings".equals(name)) {
                        result.add(n);
                    } else if ("java.lang.SuppressWarnings".equals(name)) {
                        result.add(n);
                    }
                }
            }
        }
        return result;
    }
}
