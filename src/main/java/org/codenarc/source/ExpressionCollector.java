package org.codenarc.source;


import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.control.SourceUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ExpressionCollector {

    Map<ClassNode, List<MethodCallExpression>> getMethodCalls(ModuleNode module) {

        Map<ClassNode, List<MethodCallExpression>> result = new HashMap<ClassNode, List<MethodCallExpression>>();

        if (module != null && module.getClasses() != null) {
            for (ClassNode classNode : module.getClasses()) {
                ExpressionCollectorVisitor collector = new ExpressionCollectorVisitor();
                collector.visitClass(classNode);
                result.put(classNode, collector.methodCalls);
            }
        }
        return result; 
    }

    private static class ExpressionCollectorVisitor extends ClassCodeVisitorSupport {
        
        private final List<MethodCallExpression> methodCalls = new ArrayList<MethodCallExpression>();

        @Override
        public void visitMethodCallExpression(MethodCallExpression call) {
            if (!methodCalls.contains(call)) {
                methodCalls.add(call);
            }
            super.visitMethodCallExpression(call);
        }

        @Override
        protected SourceUnit getSourceUnit() {
            throw new UnsupportedOperationException();
        }

    }
}