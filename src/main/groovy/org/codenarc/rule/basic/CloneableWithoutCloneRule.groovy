package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ClassHelper

/**
 * A class that implements Cloneable should define a clone() method.
 * 
 * @author Hamlet D'Arcy & René Gröschke
 * @version $Revision$ - $Date$
 */
class CloneableWithoutCloneRule extends AbstractAstVisitorRule {

    String name = 'CloneableWithoutClone'
    int priority = 2
    Class astVisitorClass = CloneableWithoutCloneAstVisitor
}


class CloneableWithoutCloneAstVisitor extends AbstractAstVisitor  {

    private boolean hasCloneMethod
    
    def void visitClass(ClassNode node) {
        // is this class a Clonable?
        def cloneableClassNode = ClassHelper.make(Cloneable)
        def isCloneable = node.interfaces.find {
            it == cloneableClassNode || it.name == "Cloneable"
        }
        if (isCloneable) {
            super.visitClass(node);
            if (!hasCloneMethod) {
                addViolation(node)
            }
        } else {
            super.visitClass(node);
        }
    }

    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        // is this method a clone method?
        if ((node.name == "clone") && (!node.parameters)) {
            hasCloneMethod = true
        }
        super.visitConstructorOrMethod(node, isConstructor);
    }
}
