/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCall
import org.codenarc.util.AstUtil

/**
 * @author Daniel Zänker
 */
class JenkinsUtil {
    static boolean isCpsMethod(MethodNode methodNode, boolean isConstructor) {
        return !isConstructor && !AstUtil.hasAnnotation(methodNode, 'com.cloudbees.groovy.cps.NonCPS')
    }

    static ClassNode getReceiverType(MethodCall call) {
        if (call.receiver instanceof Expression) {
            Expression expression = (Expression) call.receiver
            return expression.type
        } else if (call.receiver instanceof ClassNode) {
            return (ClassNode) call.receiver
        }
        throw new IllegalStateException("Unsupported receiver type ${call.receiver}")
    }
}
