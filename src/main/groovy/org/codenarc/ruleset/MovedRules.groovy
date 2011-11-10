/*
 * Copyright 2011 the original author or authors.
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
 package org.codenarc.ruleset

/**
 * Helper class to manage information for moved or renamed rules, and provide helpful error message.
 *
 * @author Chris Mair
 */
class MovedRules {

    private static final CONVENTION_RULESET = 'rulesets/convention.xml'
    private static final DESIGN_RULESET = 'rulesets/design.xml'
    private static final UNNECESSARY_RULESET = 'rulesets/unnecessary.xml'
    private static final GROOVYISM_RULESET = 'rulesets/groovyism.xml'
    private static final SERIALIZATION_RULESET = 'rulesets/serialization.xml'

    private static class MovedToRuleSet {
        String ruleSetName
        String messageFor(String name) {
            "The \"$name\" rule has been moved to the \"$ruleSetName\" ruleset."
        }
    }

    private static class Renamed {
        String newRuleName
        String messageFor(String name) {
            "The \"$name\" rule has been renamed to \"$newRuleName\"."
        }
    }

    private static final RULES = [
        HardcodedWindowsRootDirectory: renamedTo('HardCodedWindowsRootDirectory'),

        AddEmptyString: movedTo(UNNECESSARY_RULESET),
        AssignCollectionSort: movedTo(GROOVYISM_RULESET),
        AssignCollectionUnique: movedTo(GROOVYISM_RULESET),
        BooleanMethodReturnsNull: movedTo(DESIGN_RULESET),
        CloneableWithoutClone: movedTo(DESIGN_RULESET),
        ClosureAsLastMethodParameter: movedTo(GROOVYISM_RULESET),
        CollectAllIsDeprecated: movedTo(GROOVYISM_RULESET),
        CompareToWithoutComparable: movedTo(DESIGN_RULESET),
        ConfusingMultipleReturns: movedTo(GROOVYISM_RULESET),
        ConfusingTernary: movedTo(CONVENTION_RULESET),
        ConsecutiveLiteralAppends: movedTo(UNNECESSARY_RULESET),
        ConsecutiveStringConcatenation: movedTo(UNNECESSARY_RULESET),
        CouldBeElvis: movedTo(CONVENTION_RULESET),
        ExplicitArrayListInstantiation: movedTo(GROOVYISM_RULESET),
        ExplicitCallToAndMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToCompareToMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToDivMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToEqualsMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToGetAtMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToLeftShiftMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToMinusMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToModMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToMultiplyMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToOrMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToPlusMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToPowerMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToRightShiftMethod: movedTo(GROOVYISM_RULESET),
        ExplicitCallToXorMethod: movedTo(GROOVYISM_RULESET),
        ExplicitHashMapInstantiation: movedTo(GROOVYISM_RULESET),
        ExplicitHashSetInstantiation: movedTo(GROOVYISM_RULESET),
        ExplicitLinkedHashMapInstantiation: movedTo(GROOVYISM_RULESET),
        ExplicitLinkedListInstantiation: movedTo(GROOVYISM_RULESET),
        ExplicitStackInstantiation: movedTo(GROOVYISM_RULESET),
        ExplicitTreeSetInstantiation: movedTo(GROOVYISM_RULESET),
        GStringAsMapKey: movedTo(GROOVYISM_RULESET),
        GroovyLangImmutable: movedTo(GROOVYISM_RULESET),
        InvertedIfElse: movedTo(CONVENTION_RULESET),
        LongLiteralWithLowerCaseL: movedTo(CONVENTION_RULESET),
        ReturnsNullInsteadOfEmptyArray: movedTo(DESIGN_RULESET),
        ReturnsNullInsteadOfEmptyCollection: movedTo(DESIGN_RULESET),
        SimpleDateFormatMissingLocale: movedTo(DESIGN_RULESET),
        UseCollectMany: movedTo(GROOVYISM_RULESET),
        UseCollectNested: movedTo(GROOVYISM_RULESET),

        SerialVersionUID: movedTo(SERIALIZATION_RULESET),                               // 0.14
        SerializableClassMustDefineSerialVersionUID: movedTo(SERIALIZATION_RULESET),    // 0.14

        StringInstantiation: movedTo(UNNECESSARY_RULESET),                  // 0.12
        BooleanInstantiation: movedTo(UNNECESSARY_RULESET),                 // 0.12

        UnnecessaryBooleanExpression: movedTo(UNNECESSARY_RULESET),     // 0.11
        UnnecessaryIfStatement: movedTo(UNNECESSARY_RULESET),           // 0.11
        UnnecessaryTernaryExpression: movedTo(UNNECESSARY_RULESET)      // 0.11
    ]

    static String getMovedOrRenamedMessageForRuleName(String name) {
        def message = RULES[name]?.messageFor(name)
        return message ?: ''
    }

    private static Renamed renamedTo(String newRuleName) {
        return new Renamed(newRuleName:newRuleName)
    }

    private static MovedToRuleSet movedTo(String ruleSetName) {
        return new MovedToRuleSet(ruleSetName:ruleSetName)
    }

    // Private constructor to prevent instantiation. All methods are static.
    private MovedRules() { }
}
