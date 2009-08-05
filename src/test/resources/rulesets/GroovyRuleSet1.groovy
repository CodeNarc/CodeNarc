// Example Groovy RuleSet for testing

import org.codenarc.rule.basic.ThrowExceptionFromFinallyBlockRule

ruleset {
    ruleset('rulesets/RuleSet4.xml') {
        'CatchThrowable' {
            priority = 1
            enabled = false
        }
        include 'CatchThrowable'
    }

    rule(ThrowExceptionFromFinallyBlockRule) {
        priority = 3        
    }
}