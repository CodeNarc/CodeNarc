package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.ClassHelper
import java.lang.reflect.Modifier

/**
 * ThreadLocal fields should be static and final. In the most common case a java.lang.ThreadLocal 
 * instance associates state with a thread. A non-static non-final java.lang.ThreadLocal field
 * associates state with an instance-thread combination. This is seldom necessary and often a
 * bug which can cause memory leaks and possibly incorrect behavior.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class ThreadLocalNotStaticFinalRule extends AbstractAstVisitorRule {

     String name = 'ThreadLocalNotStaticFinal'
     int priority = 2
     Class astVisitorClass = ThreadLocalNotStaticFinalAstVisitor
}

class ThreadLocalNotStaticFinalAstVisitor extends AbstractAstVisitor {

    def void visitField(FieldNode node) {
        if (node?.type?.name == "ThreadLocal") {
            if ((node.modifiers & Modifier.STATIC) != Modifier.STATIC ||
                    (node.modifiers & Modifier.FINAL) != Modifier.FINAL) {
                addViolation(node)
            }
        }
        super.visitField(node);
    }
}
