package org.codenarc.rule.concurrency

import java.lang.reflect.Modifier
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * This rule reports long or double fields which are declared as volatile. Java
 * specifies that reads and writes from such fields are atomic, but many JVM's
 * have violated this specification. Unless you are certain of your JVM, it is
 * better to synchronize access to such fields rather than declare them volatile.
 */
class VolatileLongOrDoubleFieldRule extends AbstractAstVisitorRule {

    String name = 'VolatileLongOrDoubleField'
    int priority = 2
    Class astVisitorClass = VolatileLongOrDoubleFieldVisitor
}

class VolatileLongOrDoubleFieldVisitor extends AbstractAstVisitor  {

    def void visitField(FieldNode node) {
        if (node?.type == ClassHelper.double_TYPE ||
                node?.type == ClassHelper.long_TYPE ||
                node?.type?.name == "Long" ||
                node?.type?.name == "Double") {
            if ((node.modifiers & Modifier.VOLATILE) == Modifier.VOLATILE) {
                addViolation(node)
            }
        }
        super.visitField(node);    
    }
}
