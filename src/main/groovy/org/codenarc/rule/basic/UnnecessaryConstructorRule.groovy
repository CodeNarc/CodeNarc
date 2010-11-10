/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.basic

import java.lang.reflect.Modifier
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * UnnecessaryConstructor
 *
 * @author Tomasz Bujok
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class UnnecessaryConstructorRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryConstructor'
    int priority = 2
    Class astVisitorClass = UnnecessaryConstructorAstVisitor
}

class UnnecessaryConstructorAstVisitor extends AbstractAstVisitor {

  def void visitClassEx(ClassNode node) {
    if (node.constructors?.size() == 1) {
        analyzeConstructor node.constructors[0]
    }
    super.visitClassEx(node);
  }

  private void analyzeConstructor(ConstructorNode node) {
     if(node.code?.isEmpty() && !Modifier.isPrivate(node.modifiers) && node.parameters?.size() == 0) {
        addViolation node
     }
  }
  
}
