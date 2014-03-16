ruleset {
    description 'A custom Groovy RuleSet (see CodeNarcTask_CustomRuleSetTest)'

    CyclomaticComplexity {
        maxMethodComplexity = 1
    }

    ClassName

    MethodName

    ConfusingTernary(priority:3)

    StatelessClass {
        name = 'StatelessDao'
        applyToClassNames = '*Dao'
    }

    // Old style
    rule(org.codenarc.rule.basic.ThrowExceptionFromFinallyBlockRule) {
        priority = 3
    }
    ruleset('rulesets/dry.xml')

}
