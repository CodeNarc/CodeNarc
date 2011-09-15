package org.codenarc.analyzer;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codenarc.rule.Rule;
import org.codenarc.rule.Violation;
import org.codenarc.source.SourceCode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class encapsulates all of the logic for determining if an rule is suppressed or not. 
 */
public class SuppressionAnalyzer {

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
        return suppressedRuleNames.contains(rule.getName());
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
        BitSet lines = suppressionsByLineNumber.get(ruleName);
        if (lines != null) {
            return lines.get(lineNumber);             
        }
        return false;
    }

    private void init() {
        synchronized (initializationLock) {
            if (!initialized) {
                ModuleNode ast = source.getAst();
                if (ast != null) {
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getPackage()));
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getImports()));
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getStaticStarImports().values()));
                    suppressedRuleNames.addAll(getSuppressedRuleNames(ast.getStarImports()));
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

        Map<String, BitSet> result = new HashMap<String, BitSet>();
        int numLines = getLineCount(ast);
        
        for (ClassNode classNode : ast.getClasses()) {
            for (String ruleName : getSuppressedRuleNames(classNode)) {
                populateLineNumbers(classNode, result, numLines, ruleName);
            }
            for (AnnotatedNode fieldNode : from(classNode.getFields())) {
                for (String ruleName : getSuppressedRuleNames(fieldNode)) {
                    populateLineNumbers(fieldNode, result, numLines, ruleName);
                }
            }
            for (AnnotatedNode methodNode : from(classNode.getMethods())) {
                for (String ruleName : getSuppressedRuleNames(methodNode)) {
                    populateLineNumbers(methodNode, result, numLines, ruleName);
                }
            }
        }
        return result; 
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
