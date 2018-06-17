
# CodeNarc Change Log

TODO: Version 1.2 (??? 2018)
--------------------------------------
New Rules
  - #336: **StaticFieldsBeforeInstanceFields** rule (convention) - Enforce that all static fields are above all instance fields within a class.
  - #337: **StaticMethodsBeforeInstanceMethods** rule (convention) - Enforce that all static methods within each visibility level (public, protected, private) are above all instance methods within that same visibility level. 
  - #340: **PublicMethodsBeforeNonPublic** rule (convention) - Enforce that all public methods are above protected and private methods.
  - #344: **GrailsDomainStringPropertyMaxSize** rule (grails) - String properties in Grails domain classes have to define maximum size otherwise the property is mapped to VARCHAR(255) causing runtime exceptions to occur. (Vladimir Orany)

Updated/Enhanced Rules and Bug Fixes
   - #315: **Indentation** rule: Fix Indentation Rule to work with spock block labels. (Russell Sanborn)
   - #307: **TrailingWhitespace**: Fix java.util.regex.PatternSyntaxException from TrailingWhitespaceRule; because of \R (Java 1.8 dependency).
   - #322: **UnsafeArrayDeclaration**: Fix a NullPointerException thrown by UnsafeArrayDeclarationRule when it encounters a field initialized with null value. (Marcin Erdmann)
   - #318: **SpaceAroundOperator** rule: Allow newline after elvis operator. (Russell Sanborn)
   - #310: **Indentation** rule: Incorrect level when outer class is None (i.e. script).
   - #313: **Indentation** rule: Nested classes declared within a condition report incorrect indentation.
   - #332: **SpaceBeforeOpeningBrace**: Ignore opening brace preceded by opening ‘[’.
   - #332: **SpaceAfterClosingBrace**: Ignore closing brace followed by closing ‘]’. Deprecate  and ignore *checkClosureMapEntryValue* property. 
   - #314: **VariableTypeRequired** rule: Add *ignoreVariableNames* property.
   - #314: **FieldTypeRequired** rule: Add *ignoreFieldNames* property.
   - #235: **UnnecessaryGetter** rule: Ignore getters within calls to Spock `Stub()`/`Mock()`. 
   - #157 **SpaceAroundOperator** rule: Check for space around equals for declaration expressions in variables and fields.
   - #157 **SpaceAroundOperator** rule: Check for space around equals for method/constructor parameters. Add *ignoreParameterDefaultValueAssignments* flag.
   - #346: **UnnecessarySetter** rule: Ignore setter calls if they are part of an expression.

Framework and Infrastructure
   - #311: Add equivalent linux command line example. (Wilfred Hughes)
   - #334: Update Gradle wrapper to 4.6. (Frieder Bluemle)
   - #325: Perform general cleanup across project. (Russell Sanborn)
   - #324: Add missing Violation type to List parameter in applyTo methods. (Russell Sanborn)
   - #323: Add terminating semicolons to keep consistency in js sort functions. (Russell Sanborn)
   - #347: Add "spec" to DEFAULT_TEST_FILES and DEFAULT_TEST_CLASS_NAMES. (Russell Sanborn)


Version 1.1 (Jan 2018)
--------------------------------------
New Rules
   - #247: **MissingOverrideAnnotation** rule (enhanced) - Checks for methods that override a method in a super class or implement a method in an interface but are not annotated with @Override. (Marcin Erdmann)
   - #271: **Indentation** rule (formatting) - Check indentation for class, method and field declarations and initial statements.
   - #283: **InvertedCondition** rule (convention) - An inverted condition is one where a constant expression is used on the left hand side of the equals comparision. Such conditions can be confusing especially when used in assertions where the expected value is by convention placed on the right hand side of the comparision. (Marcin Erdmann)
   - #285: **MethodReturnTypeRequired** rule (convention) - Checks that method return types are not dynamic, that is they are explicitly stated and different than def. (Marcin Erdmann)
   - #288: **MethodParameterTypeRequired** rule (convention) - Checks that method parameters are not dynamically typed, that is they are explicitly stated and different than def. (Marcin Erdmann)
   - #290: **FieldTypeRequired** rule (convention) - Checks that field types are explicitly specified (and not using def)
   - #290: **VariableTypeRequired** rule (convention) - Checks that variable types are explicitly specified in declarations (and not using def)
   - #291: **BlockStartsWithBlankLine** rule (formatting) - Checks that code blocks such as method bodies, closures and control structure bodies do not start with an empty line. (Marcin Erdmann)
   - #293: **BlockEndsWithBlankLine** rule (formatting) - Checks that code blocks such as method bodies, closures and control structure bodies do not end with an empty line. (Marcin Erdmann)

Updated/Enhanced Rules and Bug Fixes
   - #239: Ignore super calls in UnnecessarySetterRule. (Marcin Erdmann)
   - #258: Add enhanced mode for **CloseWithoutCloseableRule**. (Marcin Erdmann)
   - #263: **SpaceAfterOpeningBraceRule**: Fix false positive when an annotation has a brace. (Marcus Rosenow)
   - #243: **TrailingComma** rule: Fix for inline Lists in annotations. (Russell Sanborn)
   - #260: **BlankLineBeforePackageRule**, **UnnecessarySemicolonRule**: Update to ignore semicolons in multiline comments (Russell Sanborn).
   - #254: Removed @SuppressWarnings that were not needed. (Russel Sanborn)
   - #268: Enable @SuppressWarnings on imports.
   - #246: CodeNarc command-line runner: Only set *title* property on *ReportWriters* that support it.
   - #276: Fix minor spelling typos throughout project. (Russell Sanborn)
   - #275: Remove todos that were copied over from the Test.groovy template. (Russell Sanborn)
   - #264: **SpaceAfterOpeningBrace** rule: One-line constructors with braces in the body are marked as violations
   - #279: **TrailingWhitespace**: Fix to also support Windows line endings.
   - #241:**UnnecessaryConstructor**: Ignore protected constructors.
   - #281: **NoWildcardImports**: Add *ignoreStaticImports* property to ignore static imports. Redesign rule to be AST-based rather than file/string-based.
   - #244: JUnit* rules are not compatible with JUnit5. (Yuriy Chulovskyy)
   - #303: UnnecessaryGString rule not ignored in Baseline. Fix encoding for characters within baseline report.
   - #304: BaselineXmlReport: Fix incorrect filtering out (removing) of violations in new/different files.
   - #305: BaselineXmlReport: Format (pretty-print) the XMl report.

Framework and Infrastructure
   - #297: Add generic test type to AbstractRuleTestCase
   - #232: Update link to the gradle maven plugin. (Jenn Strater)
   - #233: Change dependency subst. to exclusions in integration-test module. (Marcin Erdmann)
   - #238: Update Gradle to 4.2. (Marcin Erdmann)
   - #242: Removing duplicate gradle property settings. (Russell Sanborn)
   - #255: Apply **MissingOverrideAnnotation** rule to project source code. (Marcin Erdmann)
   - #262: Update readme to describe how to test locally.  (Russell Sanborn)
   - #270: Move build configuration out of project "build.properties".
   - #277: Fix online docs to remove references to obsolete createViolation() and addViolation() methods.
   - #278: Clean up unnecessary whitespace in source files.
   - #301: Update Gradle wrapper to version 4.4.1. (Christian Murphy)


Version 1.0 (Sep 2017)
--------------------------------------
New Rules
   - #199: **CouldBeSwitchStatement** rule (convention)  - Checks for multiple if statements that could be converted to a switch (Thanks to Jenn Strater)
   - #215: **UnnecessarySetter** rule (unnecessary)  - Checks for explicit calls to setter methods which can be replaced by assignment to property (Thanks to Yuriy Chulovskyy)

Updated/Enhanced Rules and Bug Fixes
   - #196: Fix #194: **SpaceAfterClosingBrace**: false positive that occurs when a closure is the last item in a map entry (Thanks to Jenn Strater)
   - #201: Remove tabs from test template. (Thanks to Jenn Strater)
   - #200: Fix the error message in the logs from the space after closing brace rule. (Thanks to Jenn Strater)
   - #198: Fix false positive for trailing comma when sending method parameters by name. (Thanks to Jenn Strater)
   - #202: Update copyright year in rule templates. (Thanks to Jenn Strater)
   - #205: Using AST rule to check def usages. Now ignores def within comments. (Thanks Sargis Harutyunyan)
   - #206: fix for issue #197. Allow suppress warnings on local variables and constructors. (Thanks to Andrey Adamovich)
   - #213: Don't run **BracesForClass** against script ClassNodes. (Thanks to Simon St John-Green)
   - #207: **ParameterCount** ignores methods with @Override annotation; *ignoreOverriddenMethods* defaults to true. (Thanks to Yuriy Chulovskyy)
   - #223: **UnusedVariable**: Don't count variable assignment as a reference (usage).
   - #230: **NoDef**: ClassCastException: ArgumentListExpression cannot be cast to VariableExpression.
   - #226: **UnusedPrivateField** rule should ignore fields annotated with `groovy.lang.Delegate`.

Framework and Infrastructure
   - #228: [BREAKING CHANGE] Upgrade to Groovy 2.3.
   - #203: [BREAKING CHANGE] Switch from Log4J to SLF4J 1.7.25.
   - #217: [BREAKING CHANGE] Upgrade to GMetrics 1.0.
   - #227: [BREAKING CHANGE] Remove *deprecated* rules, classes and methods:
      * The **AbcComplexity** rule (use the **AbcMetric** rule instead)
      * The **GrailsSessionReference** rule
      * `DirectorySourceAnalyzer`
      * `AbstractAstVisitor.addViolation(ASTNode)`
      * `AbstractRule.createViolation(Integer)`
      * `AbstractRule.createViolation(Integer, String)`
      * `AbstractRule.createViolation(SourceCode, ASTNode)`
      * `AbstractRule.createViolationForImport(SourceCode, ImportNode)`
      * CodeNarc Ant Task - \<report\> element: *toFile* and *title* attributes (remove *title* and *toFile* properties from the `org.codenarc.ant.Report` class)
   - #209: build: update junit (4.8 -> 4.12) and commons cli (1.2 -> 1.4). (Thanks to Dominik Broj).
   - #210: build: upgrade gradle (2.4 -> 4.0). (Thanks to Dominik Broj).
   - #212: ci: enable gradle caching in travis (Thanks to Dominik Broj).
   - #219: Use the right classloader for loading AST transformations when compiling analysed code. (Thanks to Marcin Erdmann)
   - #222: Make JUnit jar a *compileOnly* dependency.
   - #231: Do not use every available processor. Switch to Runtime.availableProcessors() - 1.


Version 0.27.0 (Mar 2017)
--------------------------------------
Updated/Enhanced Rules and Bug Fixes
   - #180: **UnusedPrivateMethod** rule skip method with annotations. (Thanks to Yuriy Chulovskyy)
   - #184: Fix NPE in **MissingBlankLineAfterPackage** rule for source containing only a package. (Thanks to René Scheibe)
   - #186: Fix #185: **SpaceAfterOpeningBrace** does not honour *ignoreEmptyBlock* for constructors. (Thanks to Jenn Strater)
   - #182: **UnusedVariable**: Ignore variables in Scripts annotated with `@Field`.
   - #188: **GStringExpressionWithinString**: Fix false positive for an escaped $ character within a GString.
   - #191: Add ability to specify compile classpath for analysed sources when using CodeNarc's Ant task. (Thanks to Marcin Erdmann)
   - #183: Online docs  - Added note and link to common rule config properties from each rule where *applyToClassNames* and *doNotApplyToClassNames* properties are not available.
   - #193: Change `EmptyClassRule` to subclass `AbstractAstVisitorRule`.


Version 0.26.0 (Oct 2016)
--------------------------------------
Updated/Enhanced Rules and Bug Fixes
   - #158: UnnecessaryConstructor: Add ignoreAnnotations to optionally ignore constructors with annotations. (Thanks to Daniel Spilker)
   - #162: UnnecessaryPublicModifierRule: Ignore generics methods. (Thanks to norbson)
   - #166: GroovyLangImmutable: Exclude `javax.annotation.concurrent.Immutable`. (Thanks to Mike Kobit)
   - #168: GetterMethodCouldBeProperty: Fixed inconsistency in handling anonymous inner classes, and explicit return statements.
   - #169: GetterMethodCouldBeProperty: Add ignoreMethodsWithOverrideAnnotation flag.
   - #174: DuplicateNumberLiteral: Fixed false violations for enums. This rule now ignores Long/long values within enums.
   - #154: DuplicateListLiteral: ignore lists within annotations.
   - #177: JUnitSetUpCallsSuper: ignore methods annotated with @BeforeClass; JUnitTearDownCallsSuper: ignore methods with @AfterClass.
   - #165: UnnecessaryGetter: Fix false positive violation when a field already exists.

Framework and Infrastructure
   - #170: Remove TestUtil class from the CodeNarc jar; move it into test source folder.
   - #178: Add Badge to README for latest version. (Thanks to Mike Kobit)


Version 0.25.2 (Mar 2016)
--------------------------------------
   - #150: BracesForIfElseRule can't process enum files.
   - #152: VariableNameRule: Variables with annotations can cause ArrayIndexOutOfBoundsException.
   - #149: BracesForForLoop, BracesForIfElseRule: Improve validation if there are GString curly braces as part of expression.
        BracesForTryCatchFinally: Improve validation if there are curly braces in comments.


Version 0.25.1 (Feb 2016)
--------------------------------------
  - #147: UnusedImport: misses import reference within GString.
  - Change NoTabCharacter rule priority to 3.


Version 0.25.0 (Feb 2016)
--------------------------------------
New Rules
  - #135: NoTabCharacter rule (convention)  - Check that all source files do not contain the tab character (Yuriy Chulovskyy)
  - #136: TrailingComma rule (convention). (Yuriy Chulovskyy)

Updated/Enhanced Rules and Bug Fixes
  - #146: Validate regular expressions when setting rule properties applyToFilesMatching and doNotApplyToFilesMatching.
  - #145: Reduce number of concurrent threads to number of available processors.
  - #144: LineLength rule: Add ignoreLineRegex.
  - #143: EmptyClass rule: also ignore abstract classes.
  - #124: UnusedVariable rule: Ignore variables annotated with @BaseScript.
  - #141: UnnecessaryDefInFieldDeclaration rule: false violation if comment includes the word "def".
  - #139: IdeTextReportWriter: Default line number to 0 so that IDE links are valid.
  - #131: UnusedImport rule does not handle Scala. Fix for #115. (Yuriy Chulovskyy)
  - #133: Updated documentation for BracesForIfElse rule. (Yuriy Chulovskyy)
  - #130: Fixed issue #119 BracesForIfElse with curly braces inside GString. (Yuriy Chulovskyy)
  - #129: Fixed issue #121 False positive for UnusedPrivateField. (Yuriy Chulovskyy)
  - #127: Fixed issue #126 False positive UnnecessaryPublicModifier for groovy (Yuriy Chulovskyy)
  - #122: Fix typo: contant -> constant (Dominik Przybysz)


Version 0.24.1 (Aug 2015)
--------------------------------------
  - #114: Excluding baseline violations (excludeBaseline) does not match/ignore all baseline violations.
    * Fixed BaselineResultsProcessor to properly handle encoded messages.
    * Fixed BaselineResultsProcessor to properly filter out multiple violations of the same rule per file.
    * Fixed BaselineResultsProcessor to properly handle violations with null/empty messages.
    * Added logging when loading baseline violations file and count of ignored violations.
  - #117: Enums should not trigger check for SerialVersionUID.


Version 0.24 (July 2015)
--------------------------------------
New Rules
  - #110: AssignmentToStaticFieldFromInstanceMethod rule (design)  - Checks for assignment to a static field from an instance method.
  - #111: ClassNameSameAsSuperclass rule (naming)  - Checks for any class that has an identical name to its superclass, other than the package. This can be very confusing.
  - #111: InterfaceNameSameAsSuperInterface rule (naming)  - Checks for any interface that has an identical name to its super-interface, other than the package. This can be very confusing.

Updated/Enhanced Rules and Bug Fixes
  - #91: ClassJavadoc rule: Allow regular comments or annotations before Javadoc section. (Thanks to Sébastien Launay)
  - #90: UnnecessarySafeNavigationOperator rule: Fix false positive on spread operator.
  - #95: Some tests incompatible with Java 8. (Thanks to Artur Gajowy)
  - #107: FileEndsWithoutNewlineRule to handle empty source. (Thanks to René Scheibe)
  - #108: Fix NPE when analyzing file with trait; fix #93. (Thanks to Dominik Przybysz)

Framework and Infrastructure
  - #92: New SortableHtmlReportWriter, report type "sortable". Sort by rules (with the most violations) and files (with the most violations), as well as rule name and rule priority.
  - #89: Provide mechanism for excluding a baseline violations file. Add "baseline" report type and associated "excludeBaseline" property to Ant Task.
  - #39: Convert project to Gradle. (Thanks very much to Tomasz Przybysz, Marcin Erdmann and Kyle Boon)
  - #100: Remove IDE project files from source control. Use Gradle IDEA Plugin.
  - #106: HtmlReportWriter add includeRuleDescriptions property.
  - #109: Enable @SuppressWarnings to work on constructors.
  - #112: Fix file encoding for CHANGELOG.txt in codenarc.groovy command-line script


Version 0.23 (Feb 2015)
--------------------------------------
New Rules
  - #73: NestedForLoop rule (design)  - Checks for nested for loops. (Thanks to Maciej Ziarko)
  - #80: ParameterCount rule (size)  - Checks if the number of parameters in method/constructor exceeds the number of parameters specified by the maxParameters property. (Thanks to Maciej Ziarko)

Updated/Enhanced Rules and Bug Fixes
  - #69: PrivateFieldCouldBeFinal rule support for JPA entities. (Thanks to Maciej Ziarko)
  - #70: GStringExpressionWithinString rule should ignore quasi GString expressions inside annotations. (Thanks to Maciej Ziarko)
  - #76: SpaceAfterClosingBrace and SpaceBeforeOpeningBrace rules: GStrings with embedded closures incorrectly cause violations. (Thanks to Maciej Ziarko)
  - #59: SpaceAfterClosingBrace false violation if string contains closing brace. (fixed by #76)
  - #79: JUnitTestMethodWithoutAssert support for JUnit's ExpectedException way to verify exceptions. (Thanks to Maciej Ziarko)
  - #66: SpaceAfterComma rule doesn't check constructor calls or constructor declarations.
  - #82: IdeTextReportWriter violation link (loc=..) does not work in Eclipse.
  - #81: XML Report: Escape illegal XML characters within the source line or message.
  - #84: XML Report: Remove illegal (non-escapable) XML characters from the source line or message.
  - #88: UseAssertTrueInsteadOfAssertEquals: Only check assert statements if checkAssertStatements property is true; defaults to false.

Framework and Infrastructure
  - #86: Upgrade to Groovy 2.x. (>= 2.1.0)    [BREAKING CHANGE]
  - #67: Don't depend on groovy-all jar, but only on the specifically required groovy artifacts.
  - #68: Cleaned up and fixed HtmlReportWriterTest.
  - #74: Improved codenarc.groovy to remove Rule suffix from rule name. (Thanks to Maciej Ziarko)
  - #87: Switch pom.xml to use Groovy-Eclipse Compiler.
  - #83: HtmlReportWriter: Optionally skip violation summary by package. Add includeSummaryByPackage property; defaults to true.


Version 0.22 (Oct 2014)
--------------------------------------
New Rules
  - #56: PackageMatchesFilePath rule (naming)  - A package source file's path should match the package itself. (Thanks to Simon Tost)
  - #150: Instanceof rule (design)  - Checks for use of the instanceof operator. Use ignoreTypeNames property to configure ignored type names.
  - #423: UnnecessarySafeNavigationOperator rule (unnecessary)  - Check for the safe navigation operator (?.) applied to constants and literals, which can never be null.
  - #63: NoDef rule (convention)  - Check for all uses of the def keyword. (Thanks to Dominik Przybysz)

Updated/Enhanced Rules and Bug Fixes
  - #157: Enable optionally using Thread Context classpath for loading rule scripts. Introduce "codenarc.useCurrentThreadContextClassLoader" system property. See https://jira.grails.org/browse/GPCODENARC-32.
  - #55: FieldName rule  - Add private static final field name regex. (Thanks to Dominik Przybysz)
  - #54: Invalid Cast to BigDecimal. In an XML ruleset or "codenarc.properties", specifying a BigDecimal property value throws an exception.
  - #51: CodeNarc command-line runner always returns 0, even for failure/error. Now return exit status of 1 for all errors.
  - #58: SpaceBeforeOpeningBrace rule: Allow for opening parenthesis before opening brace. (Thanks to Dominik Przybysz)
  - #158 SpaceAfterClosingBrace rule: allow semicolons following closing brace.
  - #155: SpaceAroundMapEntryColon rule: Using spread map operator (*:) causes to fail with ArrayIndexOutOfBoundsException
  - #154: Line numbers for violations of TrailingWhitespace rule are 0-based. Same for IllegalRegex rule. Fix AbstractSourceCode.getLineNumberForCharacterIndex() to be 1-based.
  - #159: ToStringReturnsNullRule: throws "No signature of method handleClosure()" exception if toString() contains a Closure.
  - #53: SpockIgnoreRestUsed not working in tests extending a class that descends from Specification. Add specificationSuperclassNames and specificationClassNames properties.
  - #160: PackageName rule: Fix to allow package name containing numbers in first part, e.g. "t3". Change package name regex to /[a-z]+[a-z0-9]*(\.[a-z0-9]+)*/.
  - #161: ReturnNullFromCatchBlock: Reports return statement from within a void method. Add boolean shouldVisitMethod(MethodNode node) to AbstractAstVisitor.
  - #62: GrailsDuplicateConstraint: consider importFrom includes: constraints when checking for duplicate constraint violations. (Thanks to Dan Tanner)
  - #64: SpaceAfterOpeningBrace and SpaceBeforeClosingBrace rules: Add ignoreEmptyBlock property to allow no spaces between braces for empty blocks. (Thanks to Dominik Przybysz)
  - #65: HtmlReportWriter and TextReportWriter do not properly parse the maxPriority report option.
  - #34: BooleanMethodReturnsNullRule should not flag methods that return other non-null and non-boolean types.

Framework and Infrastructure
  - #435: IdeTextReportWriter: Text report formatter that includes automatic IDE (Eclipse/Idea) hyperlinks to source code. Provide new "ide" report type in ReportWriterFactory.
  - #157: Enable optionally using Thread Context classloader. Also see GPCODENARC-32. Introduce codenarc.useCurrentThreadContextClassLoader system property.


Version 0.21 (April 2014)
-------------------------------------------
New Rules
  - #30: GrailsMassAssignment rule (grails)  - Checks for mass assignment from a params Map within Grails domain classes. (Thanks to Brian Soby)
  - #38: NoWildcardImports rule (imports)  - Wildcard imports, static or otherwise, should not be used. (Thanks to Kyle Boon)
  - #41: ConsecutiveBlankLines rule (formatting)  - Makes sure there are no consecutive lines that are either blank or whitespace only. (Thanks to Joe Sondow)
  - #42: BlankLineBeforePackage rule (formatting)  - Makes sure there are no blank lines before the package declaration of a source code file. (Thanks to Joe Sondow)
  - #43: FileEndsWithoutNewline rule (formatting)  - Makes sure the source code file ends with a newline character. (Thanks to Joe Sondow)
  - #44: MissingBlankLineAfterImports rule (formatting)  - Makes sure there is a blank line after the imports of a source code file. (Thanks to Joe Sondow)
  - #46: MissingBlankLineAfterPackage rule (formatting)  - Makes sure there is a blank line after the package statement of a source code file. (Thanks to Joe Sondow)
  - #47: TrailingWhitespaceRule (formatting)  - Checks that no lines of source code end with whitespace characters. (Thanks to Joe Sondow)
  - #411: ExceptionExtendsThrowable rule (exceptions)  - Checks for classes that extend Throwable. Custom exception classes should subclass Exception or one of its descendants.
  - #341: UnnecessaryCast rule (unnecessary)  - Checks for unnecessary cast operations.
  - #149: IllegalSubclass rule (generic)  - Checks for classes that extend one of the specified set of illegal superclasses.
  - #157: UnnecessaryToString rule (unnecessary)  - Checks for unnecessary calls to toString().
  - #152: JUnitPublicProperty rule (junit)  - Checks for public properties defined on JUnit test classes.
  - #422: ToStringReturnsNull rule (design)  - Checks for toString() methods that return null.
  - #143: MultipleUnaryOperators rule (basic)  - Checks for multiple consecutive unary operators.

Updated/Enhanced Rules and Bug Fixes
  - #31: UnusedImportRule: Extended to now also detect cases where the imported class name occurs as a substring in the source code. (Thanks to René Scheibe)
  - #36: UnnecessaryDefInMethodDeclaration: Prevent false positives from modifiers within quoted method name. (Thanks to René Scheibe)
  - #37: LineLengthRule: Flags to ignore import and package statements line length. (Thanks to Kyle Boon)
  - #35: UnnecessaryPackageReferenceRule: raises confusing violation for Script with package.
  - #48: Fix Method chaining breaks SpaceAfterComma.
  - #49: Fix SpaceBeforeOpeningBrace doesn't work on switch statements.
  - #50: Fix UnnecessaryDotClass doesn't work if class is qualified
  - #153: Fix ClosureStatementOnOpeningLineOfMultipleLineClosure does not catch multiline closure with only a single statement.


Version 0.20 (Dec 2013)
-------------------------------------------
New Rules
  - #425: LocaleSetDefault rule (design)  - Checks for calls to Locale.setDefault(), which sets the Locale across the entire JVM. (Thanks to Ming Huang and Rob Patrick)
  - #114: IllegalString rule (generic)  - Checks for a specified illegal string within the source code.
  - #430: SpaceAroundMapEntryColon rule (formatting)  - Check for proper formatting of whitespace around ':' for literal Map entries.
  - #427: ClosureStatementOnOpeningLineOfMultipleLineClosure rule (formatting)  - Checks for closure logic on first line (after ->) for a multi-line closure.

Updated/Enhanced Rules
  - #426: Disable GrailsPublicControllerMethod by default.
  - #424: Expand SpaceAroundOperator rule to also check for "as" operator.
  - #432: Add checkLastStatementImplicitElse property on IfStatementCouldBeTernary and UnnecessaryIfStatement.

Bug Fixes
  - #409: Fix bug ClassNameSameAsFilenameRule when sourceCode.name is null.
  - #22: Fix DuplicateStringLiteral: Allow empty strings within ignoreStrings. (Thanks to Ei Kageyama)
  - #21: Fix ClassJavadoc rule has no effect. Also ignore blank lines between javadoc and class declaration.
  - #143: Fix UnsafeImplementationAsMap  - duplicate violation.
  - #135: Fix CoupledTestCase incorrectly thrown when referencing the current class.
  - #24: Enable Groovy ruleset to find custom rules (pass the current ClassLoader to the GroovyShell). (Thanks John Engelman)
  - #26: Fix MissingMethodException error on custom rule with recent Groovy. (Thanks to Joe Sondow)
  - #146: Fix SpaceAroundOperator: Does not catch some violations with elvis operator (?:)
  - #139: Fix JUnitPublicField to ignore interfaces.

Framework and Infrastructure
  - #23: Inline violations support within rule tests. (Thanks to Artur Gajowy)
  - #428: Use ClassNode if present in ImportNode to get line number and possibly source text
  - #431: Remove unused "samples" folder.
  - #434: Remove duplicate logging of CodeNarc results.
  - #433: Rename BaseSourceAnalyzer to AbstractSourceAnalyzer.


Version 0.19 (Jul 2013)
-------------------------------------------
  - #349: New EmptyClass rule (basic)  - Reports classes without methods, fields or properties. Why would you need a class like this?
  - #409: New ClassNameSameAsFilename rule (naming)  - Reports files containing only one top level class / enum / interface which is named differently than the file.
  - #214: New ThisReferenceEscapesConstructor rule (concurrency)  - Reports constructors passing the 'this' reference to other methods. Thanks to Artur Gajowy. (pull request #15)
  - #340: New GrailsDomainReservedSqlKeywordName rule (grails)  - Check for Grails domain class with class or field name which is a reserved SQL keyword. Thanks to Artur Gajowy. (pull request #13)
  - #339: New GrailsDomainWithServiceReference rule (grails)  - Check for Grails domain class with reference to service class (field). Thanks to Artur Gajowy. (pull request #12)
  - #382: New JUnitPublicField rule (junit)  - There is usually no reason to have a public field (even a constant) on a test class.
  - #407: New GStringExpressionWithinString rule (groovyism)  - Check for regular (single quote) strings containing a GString-type expression (${..}).
  - #417: New IllegalClassMember rule (generic)  - Checks for classes containing fields/properties/methods matching configured illegal member modifiers or not matching any of the configured allowed member modifiers.
  - #412: New EnumCustomSerializationIgnored rule (serialization)  - Checks for enums that define writeObject() or writeReplace() methods, or declare serialPersistentFields or serialVersionUID fields, all of which are ignored for enums.
  - #421: SpaceAroundClosureArrow rule (formatting)  - Checks that there is whitespace around the closure arrow (->) symbol

New "Enhanced Classpath" Ruleset: These rules require application classes on CodeNarc's classpath:
  - #329: New UnsafeImplementationAsMap rule  - Reports incomplete interface implementations created by map-to-interface coercions. By default, this rule does not apply to test files. Thanks to Artur Gajowy. (pull request #14)
  - #364: New CloneWithoutCloneable rule  - The method clone() should only be declared if the class implements the Cloneable interface. Thanks to Artur Gajowy. (pull request #14)
  - #278: New JUnitAssertEqualsConstantActualValue rule  - Reports usages of org.junit.Assert.assertEquals([message,] expected, actual) where the 'actual' parameter is a constant or a literal. Most likely it was intended to be the 'expected' value.

Updated/Enhanced Rules
  - #371: CouldBeElvis rule. Added support for IF statements without braces.
  - #408: SerialVersionUID rule: Also check that serialVersionUID field is declared private.
  - #414: Extend UnnecessaryIfStatement rule: if (condition){ return true }; return false.
  - #415: Extend IfStatementCouldBeTernary rule: if (condition) { return 1 }; return 2.
  - #416: Enhance JUnitAssertAlwaysSucceeds and JUnitAssertAlwaysFails to catch String or number constant or List or Map literal passed in as condition parameter; and add support for checking assertNotNull().

Bug Fixes
 - Fix bug in UnnecessaryDefInFieldDeclarationRule making it report false positives with 'def' on right hand side of assignment
  - #135: CoupledTestCase incorrect violation when referencing the current class.
  - #19: Fix BracesForMethod rule for single-line methods.
  - #20: HTML Report Anchor Fix. Thanks to Jeff Beck.
  - #137: Mistake in ExplicitHashSetInstantiationRule violation message.
  - #138: SpaceAroundOperators: Add violation for isEcpr?processRecords(records[0]):''

Framework and Infrastructure
 - Added support for per-rule custom compilation phase, allowing for more type information in AST when required.
  NOTE: New Rules that use a later compilation phase require that CodeNarc have the application classes being
  analyzed on the classpath. This adds a new int getCompilerPhase() method to Rule. Thanks to Artur Gajowy. (pull request #14)
  - #419: Add maxPriority property to HTML and Text ReportWriters  - filter out lower priority violations.


Version 0.18.1 (Feb 2013)
-------------------------------------------
Bug Fixes
  - Fix #3596256: SpaceAroundOperatorRule: False positive if source line contains unicode chars.
      Also affects (known limitation): SpaceAfterClosingBrace, SpaceBeforeOpeningBrace, SpaceBeforeClosingBrace   - Known limitation: will not catch violations on same line following unicode char literal.
  - Fix #3598154: SpaceAroundOperator: bad/duplicate violation for ?: after closure. Ignore standalone elvis expression statements (known limitation).
  - Fix #3598717: BracesForClassRule failing on annotation types. (Known limitation: ignore for @interface). Thanks to Marcin Gryszko.
  - Fix #3598732: BracesForMethodRule fixed for methods with closure parameters. Thanks to Marcin Gryszko.
  - Fix #3596323: BracesForClassRule ignored for enums and Groovy 1.7. Thanks to Marcin Gryszko.
  - Fix for CodeNarc GitHub issue #10: No such property: maxMethodComplexity for class: org.codenarc.rule.size.AbcMetricRule.
  - Fix #3604095: AssignCollectionSort ignore calls that pass in mutate=false; add checks for two-arg calls.
  - Fix #3603257: UnusedVariable reported for variable used in (more than one) for loop.


Version 0.18 (Dec 2012 )
-------------------------------------------
New Rules
  - #3531554: New JUnitLostTest rule. Checks for classes that import JUnit 4 classes and contain a public, instance, void, no-arg method named test* that is not annotated with @Test.
  - #3509530: New SpaceAfterComma rule. Checks that there is at least one space or whitespace following each comma. That includes checks for method and closure declaration parameter lists, method call parameter lists, Map literals and List literals.
  - #3583257: New SpaceAfterSemicolon rule. Checks that there is at least one space or whitespace following a semicolon that separates classic for loop clauses and also multiple statements per line.
  - #3509532: New SpaceAroundOperator rule. Check that there is at least one space (blank) or whitespace around each binary operator, including: +, -, *, /, >>, <<, &&, ||, &, |, ?:, =.
  - #3583262: New SpaceBeforeOpeningBrace rule. Check that there is at least one space (blank) or whitespace before each opening brace ("{"). This checks method/class/interface declarations, closure expressions and block statements.
  - #3583262: New SpaceAfterOpeningBrace rule. Check that there is at least one space (blank) or whitespace after each opening brace ("{"). This checks method/class/interface declarations, closure expressions and block statements.
  - #3583262: New SpaceAfterClosingBrace rule. Check that there is at least one space (blank) or whitespace after each closing brace ("}"). This checks method/class/interface declarations, closure expressions and block statements.
  - #3583262: New SpaceBeforeClosingBrace rule. Check that there is at least one space (blank) or whitespace before each closing brace ("}"). This checks method/class/interface declarations, closure expressions and block statements.
  - #3589701: New SpaceAfterIf rule. Check that there is exactly one space (blank) after the if keyword and before the opening parenthesis.
  - #3589701: New SpaceAfterWhile rule. Check that there is exactly one space (blank) after the while keyword and before the opening parenthesis.
  - #3589701: New SpaceAfterFor rule. Check that there is exactly one space (blank) after the for keyword and before the opening parenthesis.
  - #3589701: New SpaceAfterSwitch rule. Check that there is exactly one space (blank) after the switch keyword and before the opening parenthesis.
  - #3589701: New SpaceAfterCatch rule. Check that there is exactly one space (blank) after the catch keyword and before the opening parenthesis.
  - #3581377: New JUnitUnnecessaryThrowsException rule. Check for throws clauses on JUnit test methods. That is not necessary in Groovy.
  - #3575859: New GrailsDuplicateMapping rule. Check for duplicate entry in a domain class mapping.
  - #3575861: New GrailsDuplicateConstraint rule. Check for duplicate constraints entry
  - #3592678: New IfStatementCouldBeTernary rule  - Checks for if statements where both the if and else blocks contain only a single return statement with a value
  - #3581378: New ExceptionNotThrown rule  - Checks for an exception constructor call without a throw as the last statement within a catch block.

Updated/Enhanced Rules
  - Pull request #2: Adding support for catch, finally, and else brace placement validation. Thanks to Matias Bjarland.
  - #3521130: PrintStackTrace: Also check for StackTraceUtils.printSanitizedStackTrace().
  - #3589971: Add threshold for max class metric value for CyclomaticComplexity, AbcMetric and CrapMetric.
  - #3574257: Rename AbcComplexity rule to AbcMetric. Deprecate old AbcComplexity rule and set enabled=false. Rename properties, e.g. maxMethodAbcScore.
  - Pull request #5: Enhanced UnusedVariableRule to enable ignoring some variables (ignoreVariableNames). Thanks to René Scheibe.

Bug Fixes
  - Fix #3555096: UnusedPrivateMethod  - StringIndexOutOfBoundsException for zero-length method name.
  - Fix #3524882: False positive UnnecessaryPackageReference violation for Enums.
  - Fix #3558623: UnnecessarySemicolon  - violations inside classes were ignored. Thanks to Marcin Erdmann.
  - Fix #3574259: CyclomaticComplexity, CrapMetric and AbcComplexity: Do not check class-level metric value if maxClassAverageMethodXX value is null or 0.
  - Fix #3526749: The FieldName rule should ignore serialVersionUID fields by default.
  - Fix #3543848: Online docs; formatting on Naming rules doc.
  - Fix #3441842: Online docs; UnnecessarySubstring documentation is misleading.
  - Fix #3511004: PrivateFieldCouldBeFinal false positive (- - and ++ operators).
  - Fix Pull request #4: parseReport doesn't work with absolute paths on windows. Thanks to Gavin Matthews.

Framework and Infrastructure
  - #3546737: Migrate source code from Subversion to GitHub. Many thanks to Marcin Erdmann.
  - #3578372: Add notes to rule index and sample rule sets that ABC/CC rules require the GMetrics jar.
  - #3578909: Move helper methods from AbstractTestCase into new TestUtil class (to enable use by other test frameworks, e.g. Spock)
  - #3578909: Upgrade tests and test framework to JUnit 4.
  - #3578909: Move test framework classes into src/main/groovy, so that they are included in the CodeNarc jar:
            AbstractTestCase, InMemoryAppender, TestUtil, AbstractRuleTestCase, StubRule
  - Switch to Sonatype OSS Maven Repository


Version 0.17 (March 2012)
-------------------------------------------
New Rules
  - #3433042: New PrivateFieldCouldBeFinal rule: Checks for private fields that are only set within a constructor or field initializer. Such fields can safely be made final.
  - #3432991: New ParameterReassignment rule: Checks for a method or closure parameter being reassigned to a new value within the body of the method/closure, which is a confusing, bad practice. Use a temporary variable instead.
  - #3108331: New TernaryCouldBeElvis rule: Checks for ternary with boolean and true expressions are the same; can be simplified to an Elvis expression.
  - #3489801: New AssertWithinFinallyBlock rule: Checks for assert statements within a finally block. An assert can throw an exception, hiding the original exception, if there is one.
  - #3489800: New ConstantAssertExpression rule: Checks for assert statements where the assert condition expressions is a constant value or literal value.
  - #3161693: New BrokenNullCheck rule: Looks for faulty checks for null that can cause a NullPointerException.
  - #3495466: New VectorIsObsolete rule: Checks for references to the (effectively) obsolete java.util.Vector class. Use the Java Collections Framework classes instead including ArrayList or Collections.synchronizedList().
  - #3495466: New HashtableIsObsolete rule: Checks for references to the (effectively) obsolete java.util.Hashtable class. Use the Java Collections Framework classes instead including HashMap or ConcurrentHashMap.
  - #3485545: New CrapMetric rule: Checks the CRAP metric score for methods. This metric is based on the cyclomatic complexity and test coverage for individual methods. Requires a Cobertura XML coverage file and GMetrics 0.5.

Updated/Enhanced Rules
  - #3476844: Extend GetterMethodCouldBeProperty to also check static getter methods.
  - #3477351: UnnecessaryConstructor: Also catch constructor containing only call to super().
  - #3460463: StatelessClassRule and GrailsStatelessServiceRule: Ignore fields annotated with @Inject.
  - #3460463: GrailsStatelessServiceRule: Ignore non-static properties (i.e., no visibility modifier specified) declared with "def".
  - #3485544: AssignmentInConditional: Also catch nested binary expressions, e.g. if (x==1 || x=3)
  - #3488705: GrailsDomainHasToString: Ignore classes annotated with @ToString or @Canonical.
  - #3488704: GrailsDomainHasEquals: Ignore classes annotated with @EqualsAndHashCode or @Canonical.
  - #3501349: UnnecessaryPackageReference: Also check for explicitly-imported classes.
  - #3509542: UnnecessaryPackageReference: Also check for package references in "as <Class>".

Bug Fixes
  - #3463408: Remove dependency on Java 1.6 (String.isEmpty()).
  - #3475170: Fix duplicate violations for SimpleDateFormatMissingLocale.
  - #3477085. Fix UnusedImport violations missing line numbers for imports with semicolons.
  - #3477162: Fix duplicate violations for UnnecessaryDotClass.
  - #3487448: Fix UnnecessaryNullCheckBeforeInstanceOf should also check standalone binary (boolean) expressions, e.g. boolean ready = x != null && x instanceof Integer
  - #3496557: Fix UseCollectNestedRule: GroovyCastException: Cannot cast object VariableExpression to class ClosureExpression
  - #3496696:	Fix UnusedPrivateFieldRule: GroovyCastException: Cannot cast object with class 'org.codehaus.groovy.ast.FieldNode' to class 'groovy.lang.MetaClass'.

Framework and Infrastructure
  - #3495841: Support Groovy 2.x.
  - #3496463: Support GMetrics 0.5.
  - #3476394: Include PMD report in project reports; Fix/resolve violations.
  - Introduce AbstractSharedAstVisitorRule. Refactor UnusedPrivateFieldRule and UnusedPrivateMethodRule to use it.
  - Add exists() method to org.codenarc.util.io.Resource and implementations


Version 0.16.1 (November 2011)
-------------------------------------------
  - Fix #3436461: StackOverflowError when running CodeNarc with a Groovy 1.8 runtime. Use ClassCodeVisitorSupportHack for all AstVisitor classes.
  This is a workaround for a known groovy issue: http://jira.codehaus.org/browse/GROOVY-4922


Version 0.16 (November 2011)
-------------------------------------------
New Feaures
 - Performance Improvements (3394481)  - There are big performance improvements in this release.
 - Upgrade to GMetrics 0.4. (3424121)  - This upgrade is optional for users, but may provide performance improvements.
 - Rule Index page: Rule name is now a link to the rule description web page. (3434063)

New and Updated Rules
 - @SuppressWarnings Support  - The support for @SuppressWarnings was redesigned so that it is more reliable. @SuppressWarnings no works on *all* rules at the Class, Field, and Method level.
 - CouldBeElvis rule (convention)  - Catch an if block that could be written as an elvis expression.
 - LongLiteralWithLowerCaseL rule (convention)  - In Java and Groovy, you can specify long literals with the L or l character, for instance 55L or 24l. It is best practice to always use an uppercase L and never a lowercase l. This is because 11l rendered in some fonts may look like 111 instead of 11L.
 - ConfusingMultipleReturns rule (groovyism)  - Multiple return values can be used to set several variables at once. To use multiple return values, the left hand side of the assignment must be enclosed in parenthesis. If not, then you are not using multiple return values, you're only assigning the last element.
 - GetterMethodCouldBeProperty rule (groovyism)  - If a class defines a public method that follows the Java getter notation, and returns a constant, then it is cleaner to provide a Groovy property for the value rather than a Groovy method.
 - UnnecessaryDefInMethodDeclaration rule (unnecessary)  - 3176230  - Rule now catches when you try to add the def keyword to constructor declarations. Also expanded to catch more instances of in method declarations with explicit return types.
 - UnnecessaryDefInFieldDeclaration rule (unnecessary)  - If a field has a visibility modifier or a type declaration, then the def keyword is unneeded. For instance, 'static def constraints = {}' is redundant and can be simplified to 'static constraints = {}.
 - UnnecessaryDefInVariableDeclaration rule (unnecessary)  - Expanded rule to catch more instances of unneeded defs.
 - UnusedMethodParameter rule (unused)  - This rule finds instances of method parameters not being used. It does not analyze private methods (that is done by the UnusedPrivateMethodParameter rule) or methods marked @Override.
 - BuilderMethodWithSideEffects rule (design)  - 3408045  - A builder method is defined as one that creates objects. As such, they should never be of void return type. If a method is named build, create, or make, then it should always return a value.
 - MisorderedStaticImportRule  - 3392892  - The rule now allows you to specify that static imports come after the other imports, not just before.  This rule has one property comesBefore, which defaults to true. If you like your static imports to come after the others, then set this property to false.
 - FactoryMethodName rule (naming)  - A factory method is a method that creates objects, and they are typically named either buildFoo(), makeFoo(), or createFoo(). This rule enforces that only one naming convention is used. It defaults to makeFoo(), but that can be changed using the property 'regex'.
 - UseCollectMany rule (groovyism)  - 3411722  - In many case collectMany() yields the same result as collect{}.flatten. It is easier to understand and more clearly conveys the intent.
 - CollectAllIsDeprecated rule (groovyism)  - 3411724  - collectAll is deprecated since Groovy 1.8.1. Use collectNested instead
 - UseCollectNested rule (groovyism)  - 3411724  - Instead of nested collect{}-calls use collectNested{}
 - DuplicateMapLiteral (dry)  - 3413600  - Check for multiple instances of the same Map literal; limited to Maps where the keys and values are all constants or literals.
 - DuplicateListLiteral (dry)  - 3413601  - Check for multiple instances of the same List literal; limited to Lists where the values are all constants or literals.

Bug Fixes
  - #3393179: Fix for JUnitPublicNonTestMethod reporting violations for public non-test methods that are annotated @Override
  - #3394313: Fixed UnusedPrivateField to honor the @SuppressWarnings annotation.
  - #3397468: Fixed CyclomaticComplexity rule to honor the @SuppressWarnings annotation.
  - #3392768: The UnnecessaryDefInVariableDeclaration no longer checks fields. That work is done in the UnnecessaryDefInFieldDeclaration rule.
  - #3401516: Fixed FinalClassWithProtectedMember to ignore methods with @Override
  - #3408106: Fixed UnnecessaryDefInMethod to ignore parameters that have the def keyword
  - #3393184: Fixed ExplicitCallToEqualsMethod to suggest a better rewrite, which works better with negation
  - #3408108: Fixed UnnecessaryDefInMethodDeclaration to not flag generic methods with a required def as an error
  - #3410261: Fixed UnusedImport  - confuses import of similar class names
  - #3408440: Fixed UnnecessaryObjectReferences rule to not track references across methods.
  - #3393144: Fixed unnecessaryObjectReferences rule to not track references across fields.
  - #3394312: Fixed UnusedPrivateField rule to search for usages based on super class references.
  - #3423987: BracesFor* rules should not produce violations if there are no braces.
  - #3429658: False positive for UnusedPrivateMethod rule
  - #3387422: ClosureAsLastMethodParameter  - false positive

Breaking Changes
 * The HardcodedWindowsRootDirectory has been renamed to HardCodedWindowsRootDirectory. (#3433741)
 * Major Reorganization of the "Basic" RuleSet. (#3432475)
  This included moving several rules from the "Basic" ruleset to other rulesets, as well as adding two New Rulesets:
      1. "Groovyism"  - Groovy idiomatic usage, and Groovy-specific bad practices
      2. "Convention"  - Coding conventions; not typically errors.

  The following rules were moved out of the "Basic" ruleset:
   - AddEmptyString                      (unnecessary)
   - AssignCollectionSort                (groovyism)
   - AssignCollectionUnique              (groovyism)
   - BooleanMethodReturnsNull            (design)
   - CloneableWithoutClone               (design)
   - ClosureAsLastMethodParameter        (groovyism)
   - CollectAllIsDeprecated              (groovyism)
   - CompareToWithoutComparable          (design)
   - ConfusingMultipleReturns            (groovyism)
   - ConfusingTernary                    (convention)
   - ConsecutiveLiteralAppends           (unnecessary)
   - ConsecutiveStringConcatenation      (unnecessary)
   - CouldBeElvis                        (convention)
   - ExplicitArrayListInstantiation      (groovyism)
   - ExplicitCallToAndMethod             (groovyism)
   - ExplicitCallToCompareToMethod       (groovyism)
   - ExplicitCallToDivMethod             (groovyism)
   - ExplicitCallToEqualsMethod          (groovyism)
   - ExplicitCallToGetAtMethod           (groovyism)
   - ExplicitCallToLeftShiftMethod       (groovyism)
   - ExplicitCallToMinusMethod           (groovyism)
   - ExplicitCallToModMethod             (groovyism)
   - ExplicitCallToMultiplyMethod        (groovyism)
   - ExplicitCallToOrMethod              (groovyism)
   - ExplicitCallToPlusMethod            (groovyism)
   - ExplicitCallToPowerMethod           (groovyism)
   - ExplicitCallToRightShiftMethod      (groovyism)
   - ExplicitCallToXorMethod             (groovyism)
   - ExplicitHashMapInstantiation        (groovyism)
   - ExplicitHashSetInstantiation        (groovyism)
   - ExplicitLinkedHashMapInstantiation  (groovyism)
   - ExplicitLinkedListInstantiation     (groovyism)
   - ExplicitStackInstantiation          (groovyism)
   - ExplicitTreeSetInstantiation        (groovyism)
   - GStringAsMapKey                     (groovyism)
   - GroovyLangImmutable                 (groovyism)
   - InvertedIfElse                      (convention)
   - LongLiteralWithLowerCaseL           (convention)
   - ReturnsNullInsteadOfEmptyArray      (design)
   - ReturnsNullInsteadOfEmptyCollection (design)
   - SimpleDateFormatMissingLocale       (design)
   - UseCollectMany                      (groovyism)
   - UseCollectNested                    (groovyism)

  The ruleset parser classes have been modified to print a helpful error message for moved and renamed rules (see MovedRules helper class).
 * In a previous version, method names on AbstractAstVisitor were changed to add @SuppressWarnings support. visitField became visitFieldEx, visitProperty became visitPropertyEx, and visitConstructor became visitConstructorEx. These were changed back to the default names used by Groovy visitors.

Thanks
 - Thanks to the Groovy Users of Minnesota for the CouldBeElvis rule. Thanks Jeff Beck, Doug Sabers, Ryan Applegate, and Mike Minner.
 - Thanks to Joachim Baumann for UseCollectMany, CollectAllIsDeprecated, UseCollectNested.


Version 0.15 (August 2011)
-------------------------------------------
New and Updated Rules
 - UnnecessaryDefInVariableDeclaration rule (unnecessary)  - If a variable has a visibility modifier or a type declaration, then the def keyword is unneeded. For instance 'def private n = 2' is redundant and can be simplified to 'private n = 2'.
 - UnnecessaryDefInMethodDeclaration rule (unnecessary)  - Added more checked modifiers: final, synchronized, abstract, strictfp. Multiple method declarations in a single line are handled correctly.
 - AssignCollectionUnique rule (basic)  - The Collections.unique() method mutates the list and returns the list as a value. If you are assigning the result of unique() to a variable, then you probably don't realize that you're also modifying the original list as well. This is frequently the cause of subtle bugs.
 - AssignCollectionSort rule (basic)  - The Collections.sort() method mutates the list and returns the list as a value. If you are assigning the result of sort() to a variable, then you probably don't realize that you're also modifying the original list as well. This is frequently the cause of subtle bugs.
 - UnnecessaryDotClass rule (unnecessary)  - To make a reference to a class, it is unnecessary to specify the '.class' identifier. For instance String.class can be shortened to String.
 - BitwiseOperatorInConditional rule (basic)  - Checks for bitwise operations in conditionals, if you need to do a bitwise operation then it is best practive to extract a temp variable.
 - UnnecessaryInstanceOfCheck rule (unnecessary)  - This rule finds instanceof checks that cannot possibly evaluate to true. For instance, checking that (!variable instanceof String) will never be true because the result of a not expression is always a boolean.
 - UnnecessarySubstring rule (unnecessary)  - This rule finds usages of String.substring(int) and String.substring(int, int) that can be replaced by use of the subscript operator. For instance, var.substring(5) can be replaced with var[5..-1].
 - HardcodedWindowsRootDirectory rule (basic)  - This rule find cases where a File object is constructed with a windows-based path. This is not portable, and using the File.listRoots() method is a better alternative.
 - HardCodedWindowsFileSeparator rule (basic)  - This rule finds usages of a Windows file separator within the constructor call of a File object. It is better to use the Unix file separator or use the File.separator constant.
 - RandomDoubleCoercedToZero rule (basic)  - The Math.random() method returns a double result greater than or equal to 0.0 and less than 1.0. If you coerce this result into an Integer or int, then it is coerced to zero. Casting the result to int, or assigning it to an int field is probably a bug.
 - BracesForClass rule (formatting)  - Checks the location of the opening brace ({) for classes. By default, requires them on the same line, but the sameLine property can be set to false to override this.
 - LineLength rule (formatting)  - Checks the maximum length for each line of source code. It checks for number of characters, so lines that include tabs may appear longer than the allowed number when viewing the file. The maximum line length can be configured by setting the length property, which defaults to 120.
 - GrailsDomainHasToString rule (grails)  - Checks that Grails domain classes redefine toString()
 - GrailsDomainHasEquals rule (grails)  - Checks that Grails domain classes redefine equals().
 - BracesForForLoop rule (formatting)  - Checks the location of the opening brace ({) for for loops. By default, requires them on the same line, but the sameLine property can be set to false to override this.
 - BracesForIfElse rule (formatting)  - Checks the location of the opening brace ({) for if statements. By default, requires them on the same line, but the sameLine property can be set to false to override this.
 - BracesForMethod rule (formatting)  - Checks the location of the opening brace ({) for constructors and methods. By default, requires them on the same line, but the sameLine property can be set to false to override this.
 - BracesForTryCatchFinally rule (formatting)  - Checks the location of the opening brace ({) for try statements. By default, requires them on the line, but the sameLine property can be set to false to override this.
 - ClassJavadoc rule (formatting)  - Makes sure each class and interface definition is preceded by javadoc. Enum definitions are not checked, due to strange behavior in the Groovy AST.
 - JdbcConnectionReference rule (jdbc)  - Check for direct use of java.sql.Connection, which is discouraged and almost never necessary in application code.
 - JdbcResultSetReference rule (jdbc)  - Check for direct use of java.sql.ResultSet, which is not necessary if using the Groovy Sql facility or an ORM framework such as Hibernate.
 - JdbcStatementReference rule (jdbc)  - Check for direct use of java.sql.Statement, java.sql.PreparedStatement, or java.sql.CallableStatement, which is not necessary if using the Groovy Sql facility or an ORM framework such as Hibernate.
 - IllegalClassReference rule (generic)  - Checks for reference to any of the classes configured in classNames.

Bug Fixes and New Features
  - #3325147: Fix for running CodeNarc with a Groovy 1.8 runtime. There should no longer be StackOverflowExceptions.
  - #3317632: CloneableWithoutClone  - false positive.
  - #3315990: StaticXxxField false positive on initializer: StaticSimpleDateFormatField, StaticDateFormatField, StaticCalendarField.
  - #3314773: UnnecessaryGroovyImportRule: false positive on static imports
  - #3315992: ClosureAsLastMethodParameter  - false positive, when method call surrounded by parentheses.
  - #3307699: Fixed a typo and made some "Violation" strings lowercase, so the log messages are consistent. (Fixed by René Scheibe)
  - #3315946: Cmdline runner does not respect -includes and -excludes.  (Fixed by René Scheibe)
  - #3314576: UnnecessaryPublicModifierRule: MissingPropertyException.  (Fixed by René Scheibe)
  - #3322395: JUnitTestMethodWithoutAssert  - Added support for parameters in the @Test annotation. E.g.: @Test(expected = IllegalArgumentException) and @Test(timeout = 1000).   (Fixed by René Scheibe)
  - #3310381: Added test for all rules (in AbstractRuleTestCase) to verify that any values specified for (doNot)applyToFilesMatching are valid regular expressions.
  - #3325049: Change StatelessClassRule (generic) to require applyToClassNames, applyToFileNames or applyToFilesMatching to be configured.
  - #3361263: IllegalPackageReferenceRule: Also check constructor parameter types and type coercion (x as Type).
  - #3315991: Unnecessary*Instantiation (including UnnecessaryBigDecimalInstantiation): Duplicate violations.
  - #3351964: Include rules w/ priority > 4 in HTML report. Add getViolations() to Results interface.
  - #3375718: UnusedPrivateField: Recognize references to static fields through the class name.
  - #3380494: Automatically create report output folders.
  - #3376518: UnnecessaryBigDecimalInstantiation should not produce violations for new BigDecimal(42), new BigDecimal(42L) or new BigDecimal("42")  - i.e., when the parameter evaluates to an integer/long.
  - #3376576: UnnecessaryParenthesesForMethodCallWithClosureRule: IllegalArgumentException: Start and end indexes are one based and have to be greater than zero.
  - #3384056: Unnecessary* rules should be priority 3.

Thanks
 - Thanks to René Scheibe for the UnnecessaryDefInVariableDeclarationRule and enhancements to UnnecessaryDefInMethodDeclarationRule;
  as well as the many Bug Fixes.
 - Thanks to Dean Del Ponte for the UnnecessaryDotClass rule.
 - Thanks to Nick Larson, Juan Vazquez, and Jon DeJong for the AssignCollectionUnique rule.
 - Thanks to Jeff Beck for the BitwiseOperatorInConditional rule.
 - Thanks to Geli Crick and Rafael Luque for the BracesForClass, LineLength, GrailsDomainHasToString,GrailsDomainHasEquals, BracesForIfElseRule, BracesForMethod, BracesForTryCatchFinally and ClassJavadoc rules.


Version 0.14 (June 2011)
-------------------------------------------
New and Updated Rules
 - ExplicitLinkedHashMapInstantiation rule (basic)  - This rule checks for the explicit instantiation of a LinkedHashMap using the no-arg constructor. In Groovy, it is best to write "new LinkedHashMap()" as "[:]", which creates the same object.
 - DuplicateMapKey rule (basic)  - A map literal is created with duplicated key. The map entry will be overwritten.
 - DuplicateSetValue rule (basic)  - A Set literal is created with duplicate constant value. A set cannot contain two elements with the same value.
 - EqualsOverloaded rule (basic)  - The class has an equals method, but the parameter of the method is not of type Object. It is not overriding equals but instead overloading it.
 - ForLoopShouldBeWhileLoop (basic)  - A for-loop without an init and an update statement can be simplified to a while loop.
 - NonFinalSubclassOfSensitiveInterface rule (security)  - The permissions classes such as java.security.Permission and java.security.BasicPermission are designed to be extended. Classes that derive from these permissions classes, however, must prohibit extension. This prohibition ensures that malicious subclasses cannot change the properties of the derived class. Classes that implement sensitive interfaces such as java.security.PrivilegedAction and java.security.PrivilegedActionException must also be declared final for analogous reasons.
 - ImportFromSunPackages rule (imports)  - Avoid importing anything from the 'sun.*' packages. These packages are not portable and are likely to change.
 - UnnecessaryFinalOnPrivateMethod rule (unnecessary)  - A private method is marked final. Private methods cannot be overridden, so marking it final is unnecessary.
 - InsecureRandom rule (security)  - Reports usages of java.util.Random, which can produce very predictable results. If two instances of Random are created with the same seed and sequence of method calls, they will generate the exact same results. Use java.security.SecureRandom instead, which provides a cryptographically strong random number generator. SecureRandom uses PRNG, which means they are using a deterministic algorithm to produce a pseudo-random number from a true random seed. SecureRandom produces non-deterministic output.
 - DirectConnectionManagement rule (jdbc)  - The J2EE standard requires that applications use the container's resource management facilities to obtain connections to resources. Every major web application container provides pooled database connection management as part of its resource management framework. Duplicating this functionality in an application is difficult and error prone, which is part of the reason it is forbidden under the J2EE standard.
 - ComparisonWithSelf (basic)  - Checks for using a comparison operator or equals() or compareTo() to compare a variable to itself, e.g.: x == x, x != x, x <=> x, x < x, x > x, x <= x, x >= x, x.equals(x), or x.compareTo(x), where x is a variable.
 - ComparisonOfTwoConstants (basic)  - Checks for using a comparison operator or equals() or compareTo() to compare two constants to each other or two literals that contain only constant values.
 - FileCreateTempFile rule (security)  - The File.createTempFile() method is insecure, and has been deprecated by the ESAPI secure coding library. It has been replaced by the ESAPI Randomizer.getRandomFilename(String) method.
 - SystemExit rule (security)  - Web applications should never call System.exit(). A call to System.exit() is probably part of leftover debug code or code imported from a non-J2EE application.
 - ObjectFinalize rule (security)  - The finalize() method should only be called by the JVM after the object has been garbage collected.
 - JavaIoPackageAccess rule (security)  - This rule reports violations of the Enterprise JavaBeans specification by using the java.io package to access files or the file system.
 - UnsafeArrayDeclaration rule (security)  - Triggers a violation when an array is declared public, final, and static. Secure coding principles state that, in most cases, an array declared public, final and static is a bug because arrays are mutable objects.
 - PublicFinalizeMethod rule (security)  - Creates a violation when the program violates secure coding principles by declaring a finalize() method public.
 - NonFinalPublicField rule (security)  - Finds code that violates secure coding principles for mobile code by declaring a member variable public but not final.
 - UnnecessaryElseStatement rule (unnecessary)  - When an if statement block ends with a return statement the else is unnecessary. The logic in the else branch can be run without being in a new scope.
 - StaticConnection rule (concurrency)  - Creates violations when a java.sql.Connection object is used as a static field. Database connections stored in static fields will be shared between threads, which is unsafe and can lead to race conditions.
 - UnnecessaryPackageReference (unnecessary)  - Checks for explicit package reference for classes that Groovy imports by default, such as java.lang.String, java.util.Map and groovy.lang.Closure.
 - AbstractClassWithPublicConstructor (design)  - Checks for abstract classes that define a public constructor, which is useless and confusing.
 - StaticSimpleDateFormatField (concurrency)  - Checks for static SimpleDateFormat fields. SimpleDateFormat objects are not threadsafe, and should not be shared across threads.
 - IllegalPackageReference (generic)  - Checks for reference to any of the packages configured in packageNames.
 - SpockIgnoreRestUsed rule (junit)  - If Spock's @IgnoreRest appears on any method, then all non-annotated test methods are not executed. This behaviour is almost always unintended. It's fine to use @IgnoreRest locally during development, but when committing code, it should be removed.
 - SwallowThreadDeath rule (exceptions)  - Detects code that catches java.lang.ThreadDeath without rethrowing it
 - MisorderedStaticImports rule (imports)  - Static imports should never be declared after nonstatic imports.
 - ConfusingMethodName (naming)  - Existing rule updated to analyze fields and field names as well as just methods.
 - PublicInstanceField rule (design)  - Using public fields is considered to be a bad design. Use properties instead.
 - UnnecessaryNullCheck (unnecessary)  - Updated rule to flag null and not-null checks against the this reference. The this reference can never be null.
 - ClassForName rule (basic)  - Using Class.forName(...) is a common way to add dynamic behavior to a system. However, using this method can cause resource leaks because the classes can be pinned in memory for long periods of time.
 - StatelessSingleton rule (design)  - Rule finds occurrences of the Singleton pattern where the object has no state (mutable or otherwise). There is no point in creating a stateless Singleton, just make a new instance with the new keyword instead.
 - InconsistentPropertySynchronization rule (concurrency)  - The rule is a little smarter now and flags code that defines a synchronized getter without any setter and vice versa. Those methods will be generated in unsynchronized from by the Groovy compiler later.
 - ClosureAsLastMethodParameter rule (basic)  - If a method is called and the last parameter is an inline closure then it can be declared outside of the method call brackets.
 - SerialPersistentFields rule (serialization)  - To use a Serializable object's serialPersistentFields correctly, it must be declared private, static, and final.
 - UnnecessaryParenthesesForMethodCallWithClosure rule (unnecessary)  - If a method is called and the only parameter to that method is an inline closure then the brackets of the method call can be omitted.

Bug Fixes and New Features
 - Groovy 1.8 Support  - CodeNarc continues to be built with Groovy 1.7, but should be able to be run with a Groovy 1.8 Runtime.
  - #3290486  - AbstractClassWithoutAbstractMethod no longer flags marker interfaces as abstract classes that do not define a method.
  - #3202691  - ClassNameRule rule is changed to handle $ in the class name, which is in Inner and Nested classes by default.
  - #3206167  - VariableNameRule now has a violation message that states the name of the variable and the regex it did not match.
  - #3206667  - FieldName, VariableName, MethodName, ParameterName, UnusedVariable, PropertyName, and UnusedPrivateField violations message now contains the class name of the enclosing class. This helps you configure an exclude.
  - #3206258  - LoggerForDifferentClass rule now accepts MyClass.name as a valid name for the logger.
  - #3206238  - The DuplicateNumberLiteral rule now allows you to ignore literals in the 1.2d, 1.2f, and 1.2G format.
  - #3207628  - The UnusedPrivateMethodParameter rule now allows you to ignore parameters by name. By default, parameters named 'ignore' and 'ignored' do not trigger a violation. You can configure this with the 'ignoreRegex' property on the rule.
  - #3205696  - The ConsecutiveStringConcatenation rule no longer suggesting that you join numbers together, it only suggests joining strings together.
  - #3206150  - Fixed UnusedGroovyImport rule so that imports with aliases are ignored and do not cause violations.
  - #3207605  - Fixed UnusedPrivateMethod rule to recognize static method references.
  - #3207607  - Fixed UnusedPrivateMethod rule to recognize access of privately defined getters and setters as properties.
  - #3205697  - Fixed bug where the source line displayed for annotated nodes was sometimes showing just the annotation and not the node.
  - #3288895  - Expand default test file regex pattern to include *TestCase
  - #3291474  - Enhance StaticDateFormatFieldRule to also check for static fields initialized to a DateFormat, e.g. static final DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG)
  - #3291559  - Enhance StaticCalendarFieldRule to also check for untyped static fields that are initialized to a Calendar, e.g. static final CALENDAR = Calendar.getInstance()
  - #3293429  - Fix UnnecessaryNullCheck duplicate violations.
  - #3295887  - Fix AddEmptyString duplicate violations.
  - #3299713  - Fix ImportFromSamePackage, UnnecessaryGroovyImport: Star import.
  - #3300225  - Fix UnnecessaryGroovyImport: Check static imports.
  - #3290324  - Fix UnnecessarySemicolon: the contents of multiline strings and GStrings are no longer checked, just the strings themselves. For example, embedding JavaScript or Java within a Multi-line String would previously trigger the violation.
  - #3305896  - Fix LoggerForDifferentClass: The rule now correctly handles inner classes (that are not static)
  - #3305019  - Updated the EmptyCatchBlockRule so that exceptions named ignore or ignored will not trigger errors. The name is configurable.
  - #3309062  - UnnecessaryGroovyImportRule handles static imports incorrectly
  - Fixed the Explicit[Collection]Instantiation rules so that the error messages are more descriptive.
  - Fixed the InconsistentPropertySynchronization rule so that it recognizes the new @Synchronized annotation.
  - #3308930  - LoggerWithWrongModifiersRule now contains a parameter 'allowProtectedLogger' so that loggers can also be instantiated as 'protected final LOG = Logger.getLogger(this.class)'. Also, it has a 'allowNonStatic' logger property, that allows you to create a non static logger.
  - #3308930  - LoggerForDifferentClassRule now contains a parameter 'allowDerivedClasses'. When set, a logger may be created about this.getClass().
  - #3309748  - FieldName:Do not treat non-static final fields as constants
  - #3310413  - Fix UnnecessaryPublicModifier does not catch public on constructors.
  - #3310521  - For all ExplicitCallToXxxMethod rules: Ignore calls to super.Xxx(). Patch from René Scheibe
  - #3313550  - Some HTML violation descriptions in the properties files were not well-formed HTML. This has been fixed.
  - #3205688  - Fixed false positives in UseAssertEqualsInsteadOfAssertTrue when using the JUnit assertFalse method.

Breaking Changes
 * Moved the following rules out of the "basic" ruleset into the new "serialization" ruleset:
    - SerialVersionUID
    - SerializableClassMustDefineSerialVersionUID
 * Moved import-specific helper methods out of AbstractRule into ImportUtil.
 * The ExplicitTypeInstantiationAstVisitor is now an abstract class that requires you to specify a custom violation message. This should affect no one, but it is a backwards breaking change. ppp

Thanks
  - Thank you to Victor Savkin for sending in a patch with the ForLoopShouldBeWhileLoop, UnnecessaryElse, StatelessSingleton, PublicInstanceField, and EmptyCatchBlock rules.
  - Thank you to Jan Ahrens and Stefan Armbruster for the SpockIgnoreRestUsed rule.
  - Thank you to Rob Fletcher and Klaus Baumecker for the SwallowThreadDeath rule.
  - Thank you to Erik Pragt for the MisorderedStaticImports rule.
  - Thank you to Marcin Erdmann for the MisorderedStaticImports, ClosureAsLastMethodParameterInBracketsRule, and UnnecessaryBracketsForMethodWithClosureCall rules.
  - Thank you to Hubert 'Mr. Haki' Klein Ikkink for updating the ConfusingMethodName rule.
  - Thank you to René Scheibe for the ExplicitLinkedHashMapInstantiation rule and UnnecessaryGroovyImportRule and ExplicitCallToXxxMethod patches.

Version 0.13 (February 2011)
-------------------------------------------
New Features
 - New and improved syntax for defining custom rulesets. Just list the rule names you want. No need to specify
  ruleset filename or rule class. No more tweaking your custom rulesets with every CodeNarc release when we
  add New Rules to the "basic" ruleset or one of the others. Feature #3193684.
 - Better support for command line runners. You can now specify the report type "console" as a command line parameter,
  and the CodeNarc text report is output to the console instead of a file. Feature #3162487
 - All rules now provide a nicely formatted message. This makes console tools and the CodeNarc Web Console much more
  user friendly. Feature #3162847
 - Greatly improved styling of the HTML report. Feature #3192593
 - Improved error messages when CodeNarc is mis-configured and configuration file does not compile. Feature #3193468
 - Improved println rule. If a class defines a println method, then it is OK to call this method because it won't dispatch to System.out.println. Feature #3194121
 - Improved NestedBlockDepth rule. Method calls on builder objects are now ignored, so you can nest builder calls arbitrarily deep. What constitutes a builder is exposed in the ignoreRegex property, which defaults to '.*(b|B)uilder'.

New Rules
  Dead/Unnecessary Code
 - EmptyInstanceInitializer rule (basic)  - Feature #3163464  - An empty instance initializer. It is safe to remove it.
 - EmptyStaticInitializer rule (basic)  - Feature #3161685  - An empty static initializer was found. It is safe to remove it.
 - EmptyMethod rule (basic)  - Feature #3163806  - A method was found without an implementation. If the method is overriding or implementing a parent method, then mark it with the @Override annotation.
 - UnnecessaryCallToSubstring rule (unnecessary)  - Feature #3164688  - Calling String.substring(0) always returns the original string. This code is meaningless.
 - UnnecessaryModOne rule (unnecessary)  - Feature #3164684  - Any expression mod 1 (exp % 1) is guaranteed to always return zero. This code is probably an error, and should be either (exp & 1) or (exp % 2).
 - UnnecessarySelfAssignment rule (unnecessary)  - Feature #3164674  - Method contains a pointless self-assignment to a variable or property.
 - UnnecessaryTransientModifier rule (unnecessary)  - Feature #3164672  - The field is marked as transient, but the class isn't Serializable, so marking it as transient has no effect.
 - UnnecessaryFail rule (junit)  - Feature #3105925  - In a unit test, catching an exception and immediately calling Assert.fail() is pointless and hides the stack trace. It is better to rethrow the exception or not catch the exception at all.
 - UnnecessaryPublicModifier rule (unnecessary)  - Feature #3166320  - The 'public' modifier is not required on methods or classes.
 - UnnecessaryDefInMethodDeclaration rule (unnecessary)  - Feature #3165469  - If a method has a visibility modifier, then the def keyword is unneeded. For instance 'def private method() {}' is redundant and can be simplified to 'private method() {}'.
 - UnnecessarySemicolon rule (unnecessary)  - Feature #3176207  - Semicolons as line terminators are not required in Groovy: remove them. Do not use a semicolon as a replacement for empty braces on for and while loops; this is a confusing practice.
 - UnnecessaryGString rule (unnecessary)  - Feature #3183521  - String objects should be created with single quotes, and GString objects created with double quotes. Creating normal String objects with double quotes is confusing to readers.

  Bugs/Bad Practices
 - AssignmentInConditional rule (basic)  - An assignment operator (=) was used in a conditional test. This is usually a typo, and the comparison operator (==) was intended.
 - SerializableClassMustDefineSerialVersionUID rule (basic)  - Feature #3161076  - Classes that implement Serializable should define a serialVersionUID.
 - ConsecutiveStringConcatenation rule (basic)  - Feature #3163826  - Catches concatenation of two string literals on the same line. These can safely by joined.
 - ConsecutiveLiteralAppends rule (basic)  - Feature #3161779  - Violations occur when method calls to append(Object) are chained together with literals as parameters. The chained calls can be joined into one invocation.
 - AddEmptyString rule (basic)  - Feature #3161763  - Finds empty string literals which are being added. This is an inefficient way to convert any type to a String.
 - BrokenOddnessCheck rule (basic)  - Feature #3164687  - The code uses x % 2 == 1 to check to see if a value is odd, but this won't work for negative numbers (e.g., (-5) % 2 == -1). If this code is intending to check for oddness, consider using x & 1 == 1, or x % 2 != 0.
 - ExceptionExtendsError rule (exceptions)  - Feature #3161749  - Errors are system exceptions. Do not extend them.
 - MissingNewInThrowStatementRule (exceptions)  - Feature #3166115  - Improved existing rule to catch throwing a class literal, which always causes a RuntimeException.
 - UseAssertTrueInsteadOfAssertEquals (junit)  - Feature #3172959  - This rule was expanded to handle assert statements as well as JUnit style assertions.
 - GroovyLangImmutable rule (basic)  - Feature #3175158  - The groovy.lang.Immutable annotation has been deprecated and replaced by groovy.transform.Immutable. Do not use the Immutable in groovy.lang.
 - IntegerGetInteger rule (basic)  - Feature #3174771  - This rule catches usages of java.lang.Integer.getInteger(String, ...) which reads an Integer from the System properties. It is often mistakenly used to attempt to read user input or parse a String into an Integer. It is a poor piece of API to use; replace it with System.properties['prop'].
 - BooleanGetBoolean rule (basic)  - Feature #3174770  - This rule catches usages of java.lang.Boolean.getBoolean(String) which reads a boolean from the System properties. It is often mistakenly used to attempt to read user input or parse a String into a boolean. It is a poor piece of API to use; replace it with System.properties['prop?'].
 - ChainedTest rule (junit)  - Feature #3175647  - A test methodency that invokes another test method is a chained test; the methods are dependent on one another. Tests should be isolated, and not be dependent on one another.
 - CoupledTestCase rule (junit)  - Feature #3175645  - This rule finds test cases that are coupled to other test cases, either by invoking static methods on another test case or by creating instances of another test case. If you require shared logic in test cases then extract that logic to a new class where it can properly be reused.

  Concurrency Problems
 - BusyWait rule (concurrency)  - Feature #3170271  - Busy waiting (forcing a Thread.sleep() while waiting on a condition) should be avoided. Prefer using the gate and barrier objects in the java.util.concurrent package.
 - DoubleCheckedLocking rule (concurrency)  - Feature #3170263  - This rule detects double checked locking, where a 'lock hint' is tested for null before initializing an object within a synchronized block. Double checked locking does not guarantee correctness and is an anti-pattern.
 - InconsistentPropertyLocking rule (concurrency)  - Feature #3164700  - Class contains similarly-named get and set methods where one method of the pair is marked either @WithReadLock or @WithWriteLock and the other is not locked at all.
 - InconsistentPropertySynchronization rule (concurrency)  - Feature #3164699  - Class contains similarly-named get and set methods where the set method is synchronized and the get method is not, or the get method is synchronized and the set method is not.
 - StaticCalendarField rule (concurrency)  - Feature #3164702  - Calendar objects should not be used as static fields. Calendars are inherently unsafe for multithreaded use. Sharing a single instance across thread boundaries without proper synchronization will result in erratic behavior of the application.
 - StaticDateFormatField rule (concurrency)  - DateFormat objects should not be used as static fields. DateFormat are inherently unsafe for multithreaded use. Sharing a single instance across thread boundaries without proper synchronization will result in erratic behavior of the application.
 - StaticMatcherField rule (concurrency)  - Feature #3170276  - Matcher objects should not be used as static fields. Calendars are inherently unsafe for multithreaded use. Sharing a single instance across thread boundaries without proper synchronization will result in erratic behavior of the application.
 - SynchronizedOnBoxedPrimitive rule (concurrency)  - Feature #3164708  - The code synchronizes on a boxed primitive constant, such as an Integer. Since Integer objects can be cached and shared, this code could be synchronizing on the same object as other, unrelated code, leading to unresponsiveness and possible deadlock
 - SynchronizedOnReentrantLock rule (concurrency)  - Feature #3170262  - Synchronizing on a ReentrantLock field is almost never the intended usage. A ReentrantLock should be obtained using the lock() method and released in a finally block using the unlock() method.
 - SynchronizedOnString rule (concurrency)  - Feature #3164707  - Synchronization on a String field can lead to deadlock because Strings are interned by the JVM and can be shared.
 - SynchronizedReadObjectMethod rule (concurrency)  - Feature #3164704  - Catches Serializable classes that define a synchronized readObject method. By definition, an object created by deserialization is only reachable by one thread, and thus there is no need for readObject() to be synchronized. If the readObject() method itself is causing the object to become visible to another thread, that is an example of very dubious coding style.
 - ThreadGroup rule (concurrency)  - Feature #3161692  - Avoid using ThreadGroup; although it is intended to be used in a threaded environment it contains methods that are not thread safe.
 - VolatileArrayField rule (concurrency)  - Feature #3170265  - Volatile array fields are unsafe because the contents of the array are not treated as volatile. Changing the entire array reference is visible to other threads, but changing an array element is not.
 - WaitOutsideOfWhileLoop rule (concurrency)  - Feature #3127703  - Calls to Object.wait() must be within a while loop. This ensures that the awaited condition has not already been satisfied by another thread before the wait() is invoked. It also ensures that the proper thread was resumed and guards against incorrect notification.

Bug Fixes
 - Fix Bug #3162499 UnnecessaryObjectReferences: This rule was not ignoring 'this' references, leading to false positives.
 - Fix Bug #3164150 UnnecessaryReturnKeyword: Rule is no longer triggered if the return is for a closure.
 - Fix Bug #3165139 LoggingSwallowsStacktrace: No longer reports multiple logger errors.
 - Fix bug #3166311: Error on numeric/boolean property values if they contain leading or trailing whitespace in "codenarc.properties" or in XML ruleset file.
 - Fix bug #3170295: @SuppressWarnings does not work with GMetrics-based rules (CC and ABC).
 - Fix bug #3190483: The UnusedPrivateField rule now ignores fields named serialVersionUID. The rule has a property called ignoreFieldNames that can be used to add other ignores as well.
 - Fix bug #3162975: The UnusedPrivate* rules now correctly read anonymous classes.
 - Fix bug #3155974: The UnusedPrivate* rules now correctly read inner classes.
 - Fix bug #3183443: Restricted the ConfusingTernary rule to reduce false positives. The rule looks for inverted conditionals in ternary statements. It did not account for Groovy Truth, and suggested 'x != null' be inverted to 'x', but that is not the inverse. So the rule was relaxed.
 - Fix bug #3195027: The violation message for "Replace Getter with Property Access" suggests converting ".getURLs()" to ".uRLs". This has been corrected to ".URLs".
 - Fix bug #3196019: Compiling files with unresolved Grapes dependencies no longer causes an error.

Potential Breaking Changes
 - addViolation(ASTNode) is deprecated. Instead use addViolation(ASTNode, String) when adding violations to the output.

Thanks
  - Many thanks to Mathilde Lemée, Guillaume Laforge, and Tim Yates for helping us redesign our HTML report.
  - Big thanks to Evgeny Goldin and Cédric Champeau for reporting bugs which helps us improve the product so much faster.

Version 0.12 (January 2011)
-------------------------------------------
New Features
 - Improved performance of the CodeNarc Ant Task (by multi-threading source code analysis)  - Feature #3150044
 - Add support for message parameter substitution in Rule descriptions  - Feature #3133988

New Rules
 - AbstractClassWithoutAbstractMethod rule (design)  - Feature #3160204  - Adds a violation when an abstract class does not contain any abstract methods
 - CompareToWithoutComparable rule (basic)  - Adds violation if you implement a compare method without implementing the Comparable interface
 - ExplicitGarbageCollection rule (basic)  - Feature #3106689  - Adds a violation if you try to manually trigger a garbage collection run
 - CloseWithoutCloseable rule (design)  - Feature #3138445  - Adds a violation if you implement a close method without imlpementing the Closeable interface
 - FinalClassWithProtectedMember rule (design)  - Feature #3137650  - Adds violation if you have a protected member in a final class
 - CatchArrayIndexOutOfBoundsException rule (exceptions)   - Feature #3122979  - Adds violation if you explicitly catch ArrayIndexOutOfBoundsException
 - CatchIndexOutOfBoundsException rule (exceptions)  - Feature #3122979  - Adds violation if you explicitly catch IndexOutOfBoundsException
 - ConfusingTernary rule (basic)  - Feature #3158945  - Adds violation if you use unnecessary negation within a ternary expression
 - ConstantsOnlyInterface rule (design)  - Feature #3159134  - Adds violation if an interface contains constants but no methods
 - EmptyMethodInAbstractClass rule (design)  - Feature #3159180  - Adds violation if an abstract class contains an empty method
 - RequiredString rule (generic)  - Feature #3122980  - Adds violation if a user-defined String is not found. For example, a copyright notice may be required.
 - UseAssertFalseInsteadOfNegation rule (junit)  - Feature #3114728  - Adds violation if JUnit's assertEquals is used with the boolean literal 'false'
 - UseAssertTrueInsteadOfNegation rule (junit)  - Feature #3114728  - Adds violation if JUnit's assertEquals is used with the boolean literal 'true'
 - JUnitTestMethodWithoutAssert. rule (junit)  - Feature #3111443  - Adds violation if a JUnit test method contains no assert statements
 - LoggerForDifferentClass rule (logging)  - Feature #3114736  - Adds violation if a Logger is created based on a Class that is not the enclosing class
 - LoggerWithWrongModifiers rule (logging)  - Adds violation if a Logger is not private, final, and static.
 - LoggingSwallowsStacktrace rule (logging)  - Adds violation if a log statement logs an exception without the accompanying stack trace
 - MultipleLoggers rule (logging)  - Adds a violation if a class declares more than one logger field
 - MissingNewInThrowStatement (exceptions)  - Feature #3140762  -Adds a violation if a throw statement is used to throw a class literal
 - SimpleDateFormatMissingLocale rule (basic)  - Feature #3160227  - Adds a violation if SimpleDateFormat is used without a Locale.
 - UnnecessaryBigDecimalInstantiation rule (unnecessary)  - Adds a violation for explicit instantiation of BigDecimal
 - UnnecessaryBigIntegerInstantiation rule (unnecessary)  - Adds a violation for explicit instantiation of BigInteger
 - UnnecessaryCatchBlock rule (unnecessary)  - Feature #3107168  - Adds violation if catch block does nothing but throw original exception
 - UnnecessaryDoubleInstantiation rule (unnecessary)  - Adds a violation for explicit instantiation of Double
 - UnnecessaryFloatInstantiation rule (unnecessary)  - Adds a violation for explicit instantiation of Float
 - UnnecessaryInstantiationToGetClass (unnecessary)  - Feature #3158935  - Adds a violation if a new instance is created in order to invoke getClass()
 - UnnecessaryIntegerInstantiation rule (unnecessary)  - Adds a violation for explicit instantiation of Integer
 - UnnecessaryLongInstantiation rule (unnecessary)  - Adds a violation for explicit instantiation of Long
 - UnnecessaryNullCheck Rule (unnecessary)  - Feature #3160184  - Adds violation when a null-safe dereference can be used instead of an explicit null check
 - UnnecessaryNullCheckBeforeInstanceOf rule (unnecessary)  - Feature #3159274  - Adds a violation for an unnecessary null check before using the instanceof operator
 - UnnecessaryObjectReferences rule (unnecessary)  - Feature #3107838  - Adds a violation for a chain of setters being invoked, which can be converted into a with or identity block
 - UnnecessaryCallForLastElement rule (unnecessary)  - Feature #3107884  - Adds violation if accessing the last element of a list without using the .last() method or -1th index
 - UnnecessaryCollectCall rule (unnecessary)  - Feature #3110317  - Adds a violation if a collect method call can be replaced with the spread operator
 - UnnecessaryCatchBlock rule  (unnecessary) - Feature #3110318  - Adds a violation if a catch block does nothing but throw the original exception
 - UnnecessaryGetter rule  (unnecessary) - Feature #3110321  - Adds a violation if a a getter method is called explicitly instead of using property syntax
 - UnusedPrivateMethodParameter rule (unused)  - Feature #3151598  - Adds a violation if the parameter to a private method is never used within that method

Bug Fixes
 - Fix Bug #3127967: DuplicateImportRule: Fix for Groovy 1.7. Resort to parsing source code.
 - Fix Bug #3146678: UnnecessaryGetter false positive, e.g. def allPaths = resultsMap.keySet() ??? keySet() can probably be rewritten as set.
 - Fix Bug #3111189: "StatelessClassRule  - Honor @Immutable".
 - Fix Bug #3111181: "UnnecessaryIfStatement  - misses if without explicit return".
 - Fix Bug #3108126: "assertNoViolations still passes when compile fails"
 - Fix Bug #3111439: "Synchronized on getClass() rule gives double warnings".
 - Fix Bug #3111438: "Lines with annotations have the wrong source line".
 - Fix Bug #3109909: DuplicateStringLiteralRule: Add "" as default ignoreStringLiteral.
 - Fix Bug #3110308: "Rules to catch null returns ignore ternary expressions".
 - Fix Bug #3110303: "ExplicitCallToXMethod rules should ignore spread statements".
 - Fix Bug #3109917: "DuplicateStringLiteralRule  - enum names".
 - Fix Bug #3109628: "Exceptions are thrown with CodeNarc v0.11" ? "GString as map key checking classes that do not exist yet?? and ?unnecessary ctor rule breaks for anonymous classes".
 - Fix Bug #3190754: Don't use default ClassLoader for loading rule scripts.

Potential Breaking Changes
 - Moved the existing StringInstantiation and BooleanInstantiation rules from the Basic ruleset into the Unnecessary ruleset.
  Also renamed the rules to UnnecessaryStringInstantiationRule and UnnecessaryBooleanInstantiation).
  Likewise, the rule classes were moved from the org.codenarc.rule.basic package into org.codenarc.rule.unnecessary.
  NOTE: This should only affect users if
    * You configured one of these rules specifically by rule name or by class name within a custom ruleset
    * You configured one of these rules (by rule name) within "codenarc.properties"
    * You configured one of these rules (by rule name) within a @SuppressWarnings annotation
    * You configured one of these rules (by rule name) within "codenarc-message.properties".
 - Removed deprecated applyToFilenames and doNotApplyToFilenames properties of AbstractRule.

Other Changes
 - UnnecessaryIfStatementRule: Expand the rule to also catch if/else statements that contain only single constant/literal expressions for the if and/or else blocks, if the if is not the last statement in the block.
 - Feature #3108153: Expand ReturnNull* rules to catch elvis operators
 - Feature #3111442: "add better error messages to the ReturnsNull*Rules"
 - Feature #3111440: "added better violation messages to BigDecimalInstantiationRule, BooleanInstantiationRule, and StringInstantiationRule".
 - Add isValid() to SourceCode (and AbstractSourceCode).


Version 0.11 (November 2010)
-------------------------------------------
New Features
 - @SuppressWarnings support. All rules now recognize the java.lang.SuppressWarnings annotation on fields, methods, and classes. If this annotation is added then there will be no violations produced. Just as in Java, the annotation requires a String or List<String> parameter. For example, annotating a class with @SuppressWarnings('UnusedPrivateField') will ignore the rule for that class. Annotating a method with @SuppressWarnings(['UnusedPrivateField', 'UnnecessaryIfStatementRule']) will ignore both rules for the annotated method.
 - Better "codenarc create-rule" support. You can create a new rule by running "codenarc create-rule" from the root of the CodeNarc codebase. This script has been updated to properly format Javadoc, prompt for the author name, and update the Maven Site .apt documentation.

New Rules
 - BooleanMethodReturnsNull Rule (basic) : Checks for a method with Boolean return type that returns an explicit null.
 - DeadCode Rule (basic) :  Checks for dead code that appears after a return statement or after an exception is thrown.
 - DoubleNegative Rule (basic) :  Checks for using a double negative, which is always positive.
 - DuplicateCaseStatement Rule (basic) :  Check for duplicate case statements in a switch block, such as two equal integers or strings.
 - ExplicitCallToAndMethod Rule (basic) :  This rule detects when the and(Object) method is called directly in code instead of using the & operator.
 - ExplicitCallToCompareToMethod Rule (basic) :  This rule detects when the compareTo(Object) method is called directly in code instead of using the <=>, >, >=, <, and <= operators.
 - ExplicitCallToDivMethod Rule (basic) :  This rule detects when the div(Object) method is called directly in code instead of using the / operator.
 - ExplicitCallToEqualsMethod Rule (basic) :  This rule detects when the equals(Object) method is called directly in code instead of using the == or != operator.
 - ExplicitCallToGetAtMethod Rule (basic) :  This rule detects when the getAt(Object) method is called directly in code instead of using the [] index operator.
 - ExplicitCallToLeftShiftMethod Rule (basic) :  This rule detects when the leftShift(Object) method is called directly in code instead of using the \<\< operator.
 - ExplicitCallToMinusMethod Rule (basic) :  This rule detects when the minus(Object) method is called directly in code instead of using the  - operator.
 - ExplicitCallToMultiplyMethod Rule (basic) :  This rule detects when the multiply(Object) method is called directly in code instead of using the * operator.
 - ExplicitCallToModMethod Rule (basic) :  This rule detects when the mod(Object) method is called directly in code instead of using the % operator.
 - ExplicitCallToOrMethod Rule (basic) :  This rule detects when the or(Object) method is called directly in code instead of using the | operator.
 - ExplicitCallToPlusMethod Rule (basic) :  This rule detects when the plus(Object) method is called directly in code instead of using the + operator.
 - ExplicitCallToPowerMethod Rule (basic) :  This rule detects when the power(Object) method is called directly in code instead of using the ** operator.
 - ExplicitCallToRightShiftMethod Rule (basic) :  This rule detects when the rightShift(Object) method is called directly in code instead of using the \>\> operator.
 - ExplicitCallToXorMethod Rule (basic) :  This rule detects when the xor(Object) method is called directly in code instead of using the ^ operator.
 - ExplicitArrayListInstantiation Rule (basic) :  This rule checks for the explicit instantiation of an ArrayList. In Groovy, it is best to write new ArrayList() as [], which creates the same object.
 - ExplicitHashMapInstantiation Rule (basic) :  This rule checks for the explicit instantiation of a HashMap. In Groovy, it is best to write new HashMap() as [:], which creates the same object.
 - ExplicitHashSetInstantiation Rule (basic) :  This rule checks for the explicit instantiation of a HashSet. In Groovy, it is best to write new HashSet() as [] as Set, which creates the same object.
 - ExplicitLinkedListInstantiation Rule (basic) :  This rule checks for the explicit instantiation of a LinkedList. In Groovy, it is best to write new LinkedList() as [] as Queue, which creates the same object.
 - ExplicitStackInstantiation Rule (basic) :  This rule checks for the explicit instantiation of a Stack. In Groovy, it is best to write new Stack() as [] as Stack, which creates the same object.
 - ExplicitTreeSetInstantiation Rule (basic) :  This rule checks for the explicit instantiation of a TreeSet. In Groovy, it is best to write new TreeSet()>> as [] as SortedSet, which creates the same object.
 - GStringAsMapKey Rule  A GString should not be used as a map key since its hashcode is not guaranteed to be stable.
 - InvertedIfElse Rule (basic) :  An inverted if-else statement is one in which there is a single if statement with a single else branch and the boolean test of the if is negated.
 - RemoveAllOnSelf Rule (basic) : Don't use removeAll to clear a collection.
 - ReturnsNullInsteadOfEmptyArray Rule (basic) : If you have a method or closure that returns an array, then when there are no results return a zero-length (empty) array rather than null.
 - ReturnsNullInsteadOfEmptyCollection Rule (basic) : If you have a method or closure that returns a collection, then when there are no results return a zero-length (empty) collection rather than null.
 - SerialVersionUID Rule (basic) : A serialVersionUID is normally intended to be used with Serialization. It needs to be of type long, static, and final.
 - UnnecessaryConstructor Rule (unnecessary) : This rule detects when a constructor is not necessary; i.e., when there's only one constructor, it's public, has an empty body, and takes no arguments.
 - UnnecessaryCollectionCall Rule (unnecessary) : Checks for useless calls to collections.
 - UnnecessaryOverridingMethod Rule (unnecessary) : Checks for an overriding method that merely calls the same method defined in a superclass.
 - UnnecessaryReturnKeyword Rule (unnecessary) :  In Groovy, the return keyword is often optional.
 - SynchronizedOnGetClass Rule (concurrency) : Checks for synchronization on getClass() rather than class literal.
 - UseOfNotifyMethod Rule (concurrency) : Checks for code that calls notify() rather than notifyAll().
 - DuplicateNumberLiteral Rule (dry) : This rule checks for duplicate number literals within the current class.
 - DuplicateStringLiteral Rule (dry) : This rule checks for duplicate String literals within the current class.
 - CatchIllegalMonitorStateException Rule (exceptions) : Dubious catching of IllegalMonitorStateException.
 - ConfusingClassNamedException Rule (exceptions) : This class is not derived from another exception, but ends with 'Exception'.
 - ReturnNullFromCatchBlock Rule (exceptions) : Returning null from a catch block often masks errors and requires the client to handle error codes.
 - UseAssertEqualsInsteadOfAssertTrue Rule (junit) : This rule detects JUnit assertions in object equality.
 - UseAssertTrueInsteadOfAssertEqualsRule Rule (junit) : This rule detects JUnit calling assertEquals where the first parameter is a boolean.
 - UseAssertNullInsteadOfAssertEquals Rule (junit) : This rule detects JUnit calling assertEquals where the first or second parameter is null.
 - UseAssertSameInsteadOfAssertTrue Rule (junit) : This rule detects JUnit calling assertTrue or assertFalse where the first or second parameter is an Object#is() call testing for reference equality.
 - JUnitFailWithoutMessage Rule (junit) : This rule detects JUnit calling the fail() method without an argument.
 - JUnitStyleAssertions Rule (junit) : This rule detects calling JUnit style assertions like assertEquals, assertTrue, assertFalse, assertNull, assertNotNull.
 - ConfusingMethodName Rule (naming) : Checks for very confusing method names. The referenced methods have names that differ only by capitalization.
 - ObjectOverrideMisspelledMethodName Rule (naming) : Verifies that the names of the most commonly overridden methods of Object: equals, hashCode and toString, are correct.
 - MethodCount Rule (size) : Checks if the number of methods within a class exceeds the number of lines specified by the maxMethod property.

Bug Fixes
 - None reported, none fixed!

Potential Breaking Changes
 - AbstractAstVisitor  - If you implemented your own rule by subclassing AbstractAstVisitor then your code might break. It is a compile error and the breakage will be clear and obvious. In order to support @SuppressWarnings, we needed to change AbstractAstVisitor. The method visitMethodNode became visitMethodNodeEx, visitClass became visitClassEx, visitConstructorOrMethod became visitConstructorOrMethodEx, visitField became visitFieldEx, visitProperty became visitPropertyEx, and visitConstructor became visitConstructorEx. From within these new methods there is no need to call super.visitX. In the rare case that the simple renaming does not fix your problem then please contact the mailing list.
 - The following three classes were moved from the org.codenarc.rule.basic package into org.codenarc.rule.unnecessary.
  Likewise, the associated rules were moved from the "basic" ruleset into the new "unnecessary" ruleset.
  You will need to adjust your ruleset configuration if you included one of these rules specifically in a custom ruleset, or configured them as part of the "basic" ruleset.
    * UnnecessaryBooleanExpressionRule
    * UnnecessaryIfStatementRule
    * UnnecessaryTernaryExpressionRule

Other Changes
 - CodeNarc now requires Groovy 1.7 to run. Keep in mind that it can still run against (analyze) older Groovy code.
 - There is a new rule set (category) called "dry", meaning "do not repeat yourself".
 - There is a new "unnecessary" rule set (category).
 - AstUtil enhancements  - For writers of rules: there are new utility methods in the AstUtil class. See the javadoc for more details.

Version 0.10 (26 September 2010)
-------------------------------------------
Bug Fixes
 - Fix Bug #3071531: "Unused rules don't recognized method closures". https://sourceforge.net/tracker/?func=detail&atid=1126573&aid=3071531&group_id=250145. e.g. private def bar() { }  .. return this.&bar;
 - Fix: UnusedPrivateField: Don't produce violation if field is a Closure field and is executed.
 - Fix: ImportFromSamePackage: Fix for (ignore) alias imports within same package.   package org.xyz;  import org.xyz.MyBigClass as MBC.

New Rules
 - New UnnecessaryIfStatementRule (basic):  Checks for if statements where the if and else blocks are only returning true and false constants. if(x) { return true } else { return false }.
 - New UnnecessaryBooleanExpressionRule (basic): Check for unnecessary boolean expressions, including ANDing (&&) or ORing (||) with true, false, null, or Map/List/String/Number literal. Also checks for negation of true, false, null, or Map/List/String/Number literal.
 - New BigDecimalInstantiationRule (basic): Avoid creating BigDecimal with a decimal (float/double) literal. Use a String literal. From PMD. See http://pmd.sourceforge.net/rules/basic.html#AvoidDecimalLiteralsInBigDecimalConstructor. Note that println new BigDecimal(0.1) prints out 0.1000000000000000055511151231257827021181583404541015625.
 - UnusedObjectRule (unused): Checks for object allocations that are not assigned or used, unless it is the last statement within a block (because it may be the intentional return value).
 - New UnusedArrayRule (unused): This inspection reports any instances of Groovy array allocation where the array allocated is ignored. Such allocation expressions are legal Groovy, but are usually either inadvertent, or evidence of a very odd object initialization strategy. (IntelliJ). When an ExpressionStatement has as its expression an ArrayExpression.
 - Implement ImplementationAsTypeRule (design): Checks that particular classes are never used as types in variable declarations, return values or parameters. GregorianCalendar, HashMap, HashSet, Hashtable, LinkedList, LinkedHashMap, LinkedHashSet, TreeSet, TreeMap, Vector, ArrayList, CopyOnWriteArrayList, CopyOnWriteArraySet, ConcurrentHashMap, ArrayBlockingQueue, ConcurrentLinkedQueue, DelayQueue, LinkedBlockingQueue, PriorityBlockingQueue, PriorityQueue, SynchronousQueue (see Checkstyle). (design)
 - New JUnitUnnecessaryTearDownRule (junit). Checks for tearDown() methods that only call super.tearDown().
 - New JUnitUnnecessarySetUpRule (junit). Checks for setUp() methods that only call super.setUp().

New Report Writer
 - New InlineXmlReportWriter (from Robin Bramley) for improved integration with the Hudson Violations plugin. See http://wiki.hudson-ci.org/display/HUDSON/Violations.

Other Changes
 - CodeNarc now requires Groovy 1.6 to run. Keep in mind that it can still run against (analyze) older Groovy code.
 - Upgrade to GMetrics 0.3 for AbcComplexityRule and CyclomaticComplexityRule. Violations now include line number and source line.
 - All JUnit rules (JUnit*Rule): Also apply to classes with names ending in *TestCase.
 - Add codenarc create-rule script to create a new rule and associated classes/tests. https://sourceforge.net/tracker/?func=detail&aid=3005873&group_id=250145&atid=1126575. (Hamlet D'Arcy)
 - ConstantTernaryExpressionRule: Also flag constant Map and List expressions.
 - ConstantIfExpressionRule: Also flag constant Map and List expressions.
 - GrailsPublicControllerMethod: add ignoreMethodNames property.
 - Add reference to Sonar plugin. http://docs.codehaus.org/display/SONAR/Groovy+Plugin to web site.
 - Add reference to Hudson Violations Plugin. http://wiki.hudson-ci.org/display/HUDSON/Violations to web site.


Version 0.9 (10 May 2010)
------------------------------------
Bug Fixes
 - Fix bug #2985592: "MissingPropertyException: No such property: W3C_XML_SCHEMA_NS_URI".
 - XML RuleSet: Allow wildcards (*) in <include> and <exclude>.
 - WildcardPattern: Escape plus sign ('+').
 - NestedBlockDepthRule: Ignore first level of closure for Closure Fields (since they are really equivalent to methods).

New Size/Complexity Rules
 - AbcComplexityRule  - Check ABC size/complexity score against threshold for method and class average (size)
 - CyclomaticComplexityRule  - Check Cyclomatic Complexity against threshold for method and class average (size)

New Concurrency Rules
 - NestedSynchronizationRule (concurrency  - Hamlet D'Arcy)
 - RunFinalizersOnExitRule (concurrency  - Hamlet D'Arcy)
 - SynchronizedMethodRule (concurrency  - Hamlet D'Arcy)
 - SynchronizedOnThisRule (concurrency  - Hamlet D'Arcy)
 - ThreadLocalNotStaticFinalRule (concurrency  - Hamlet D'Arcy)
 - ThreadYieldRule: (concurrency  - Hamlet D'Arcy)
 - VolatileLongOrDoubleFieldRule: (concurrency  - Hamlet D'Arcy)

New Basic Rules
 - CloneableWithoutCloneRule (basic  - Hamlet D'Arcy & René Groeschke)
 - ConstantIfExpressionRule: if(true) or if(false). Or literal constant. (basic)
 - ConstantTernaryExpressionRule: true ? x : y or false, null or literal.(basic)
 - UnnecessaryTernaryExpressionRule: x ? true : false. Or Boolean.TRUE. Or where both expressions are the same. (basic)

Other Changes
 - Deprecate GrailsSessionReferenceRule. Default enabled to false. Note in online docs.
 - StatelessClassRule: Add setAddToIgnoreFieldNames() method (adds to ignoreFieldNames).
 - Add new <rule>.description.html property for each rule. Change the HtmlReportWriter to look for *.description.html first; if not defined, use *.description. Continue to use *.description in XML report. Potential BREAKAGE: If you have overridden the "<rule>.description" message for a predefined rule in a "codenarc-messages.properties" file, you will have to change those message keys to "<rule>.description.html".
 - Do not include disabled rules in list of rules at bottom of HTML/XML report.
 - HtmlReportWriter: Don?t log warning if "codenarc-messages.properties" contains *.description but not *.description.html.
 - UnusedVariableRule: Fix limitation: Does not recognize variable references on the same line as the variable declaration.
 - GroovyDslRuleSet: Throw MissingPropertyException if a non-existent property is set within a Groovy RuleSet DSL.
 - CodeNarcRunner: Add System.out println of summary counts.
 - MethodSizeRule: Apply to constructors as well.
 - WildcardPattern: Trim pattern values. This includes property values such as common rule properties applyToFileNames/doNotApplyToFileNames, applyToClassNames/doNotApplyToClassNames. Or many rule-specific properties such as ignoreMethodNames for the MethodSizeRule.
 - PropertiesFileRuleSetConfigurer: Log warning if rule name not found.
 - TESTS: AbstractRuleTestCase: Add assertViolations(String source, Map[] violationMaps) helper method.
 - TESTS: Fix tests broken on Linux.


Version 0.8.1 (2 Feb 2010)
------------------------------------
Bug Fixes
 - Fix Bug #2943025: "NestedBlockDepthRule: Produces erroneous results on Groovy 1.6.x." https://sourceforge.net/tracker/?func=detail&aid=2943025&group_id=250145&atid=1126573
 - Fix Bug #2943028: "PackageNameRule may show incorrect violations for classes within the default package when running in Groovy 1.6.x." https://sourceforge.net/tracker/?func=detail&aid=2943028&group_id=250145&atid=1126573
 - Fix Bug #2935587 "Building CodeNarc 0.8 fails."  - Problem from Joern Huxhorn (Jan 18, 2010) ? Unable to build from the downloaded CodeNarc-0.8-bin.tar.gz.  http://sourceforge.net/tracker/?func=detail&aid=2935587&group_id=250145&atid=1126573. See CodeNarc  - Unable to Build From TarGZip.doc. Remove platform/locale dependencies: AbstractReportWriterTest, UrlResourceTest, GrailsSessionReferenceRuleTest, GrailsPublicControllerMethodRuleTest, GrailsServletContextReferenceRuleTest, GrailsStatelessServiceRuleTest. [Jan 24]
 - Fix StackOverflow in Groovy 1.7.0 for some rules: All rules that implement the visitVariableExpression(VariableExpression expression) visitor method: UnusedVariableRule, UnusedPrivateFieldRule, GrailsSessionReferenceRule, GrailsServletContextReferenceRule ? Removed call to super.visitVariableExpression(expression) since that seems to cause problems (StackOverflow) in Groovy 1.7.0.
 - Fix tests broken when running in Groovy 1.6 or Groovy 1.7.
 - DuplicateImportRule: Document that this rule does not work when running under Groovy 1.7 (i.e., will not produce any violations), and does not distinguish between multiple duplicate imports for the same class.


Version 0.8 (17 Jan 2010)
------------------------------------
Bug Fixes
 - Fix Bug #2930886: "Cannot load rules when groovy is in different classloader".  XmlReaderRuleSet, ReportWriterFactory: Replace calls to Class.forName() with getClass().classLoader.loadClass(). https://sourceforge.net/tracker/?func=detail&atid=1126573&aid=2930886&group_id=250145.
 - Fix Bug #2847520: "UnusedVariableRule: Closure variable must be invoked". UnusedVariableRule: Referencing an (explicit) Closure variable without invoking it is not recognized as a valid reference. e.g., final CLOSURE = { .. }; return CLOSURE. https://sourceforge.net/tracker/?func=detail&aid=2847520&group_id=250145&atid=1126573
 - Fix false positive: Local closures: If a closure is assigned to a local variable, then used later in the method, CodeNarc report the variable unused. [I think this has already been resolved, perhaps as part of Bug #2847520]. (reported by Donal Murtagh)
 - Fix false positive: Default arguments: If a constant is only used as the value of a default argument, CodeNarc reports this constant unused. (reported by Donal Murtagh)

Reports
 - Create XmlReportWriter for wrting out an XML report ("xml"). (beta)
 - Create TextReportWriter. Writes out text report with violations by file and priority and summary counts.
 - Enable configuring all provided ReportWriters (HtmlReportWriter, XmlReportWriter, TextReportWriter) to write to the stdout (console).
 - Enable specifying the full class name of a custom ReportWriter class.
 - CodeNarcTask: Add support for <option> nested elements under the <report> element.
 - HtmlReportWriter: Externalize strings to resource bundle.
 - Remove setTitle() and setOutputFile() from ReportWriter interface.
 - ReportWriter: Rename writeOutReport() method to writeReport().
 - Create AbstractReportWriter.

Rules
 - Create new NestedBlockDepthRule. Checks for nesting of all types of block statements (and closures), including for, if, while, try and switch statements. (design)
 - MethodSizeRule: Add support for "ignoreMethodNames" property to enable filtering.
 - AbstractRule: Enhance applyToFileNames to handle optional path (e.g. "abc/def/MyClass.groovy").
 - AbstractRule: The (previously deprecated) applyToFilenames and doNotApplyToFilenames properties now throw an UnsupportedOperationException.
 - AbstractRule: Add optional description property; Use it in report if specified.

Other Changes
 - Allow spaces within comma-separated list of ruleset files. Applies when specifying ruleset files for the CodeNarc Ant Task or the CodeNarc script.
 - CodeNarcRunner: Don't require that reportWriters is non-empty.
 - AntFileSetSourceAnalyzer: Specify sourceDirectories relative to project base directory.
 - Reorganize web site. Add Reports section. Add pages for HTML, XML and and text reports.
 - Add support and documentation for running CodeNarc as part of an automated test suite (e.g. JUnit).
 - Add List getSourceDirectories() to SourceAnalyzer interface and implementation classes.
 - Change "codenarc-version.txt" to contain only the version number.
 - Rename AbstractTest to AbstractTestCase.


Version 0.7 (25 Aug 2009)
------------------------------------
Bug Fixes
 - Fix Bug #2825698: "UnusedVariableRule: stops after 1st unused variable per name". https://sourceforge.net/tracker/?func=detail&aid=2825698&group_id=250145&atid=1126573.
 - Fix Bug #2828696: "UnusedImport rule with fully qualified class reference"   https://sourceforge.net/tracker/?func=detail&atid=1126573&aid=2828696&group_id=250145.
 - UnusedImportRule: Add support for static imports. Document known limitation: will not work on imports with wildcards.
 - UnnecessaryGroovyImportRule: Add java.net as another automatically included package.

New Features and Infrastructure
 - Groovy DSL for defining RuleSets. (GroovyDslRuleSet and RuleSetBuilder).
 - Enable optional prefix of "file:", "http:" or any other standard URL prefix for resource files, including ruleset files, "codenarc.properties" and rule scripts.
    * Addresses Tracker Issue #2828616: "Allow ruleset file to be specified as a file or url" https://sourceforge.net/tracker/?func=detail&atid=1126575&aid=2828616&group_id=250145.
 - CodeNarcTask and AntFileSetSourceAnalyzer: Allow more than one FileSet to be added.
    * Addresses Tracker Issue #2831255: "Ant task should accept any ResourceCollection for source"  https://sourceforge.net/tracker/?func=detail&aid=2831255&group_id=250145&atid=1126575.
 - HtmlReportWriter: Show rule name in color according to priority.
 - CompositeRuleSet: Rename add(RuleSet) to addRuleSet(RuleSet). Add addRule(Rule).

Rules
 - Create new PropertyNameRule. (naming)
 - FieldNameRule: Do not apply to property fields.


Version 0.6 (17 Jul 2009)
------------------------------------
Bug Fixes
 - Fix BUG #2798845 : "StringIndexOutOfBoundsException" https://sourceforge.net/tracker/?func=detail&atid=1126573&aid=2798845&group_id=250145
 - Fix BUG #2799752: GrailsPublicControllerMethodRule  - should only apply itself to methods within classes that have suffix "Controller". (see email from Jason Anderson, May 26, 2009) https://sourceforge.net/tracker/?func=detail&aid=2799752&group_id=250145&atid=1126573
 - Fix BUG #2796953: StatelessClassRule requires applyToFileNames or applyToFilesMatching.  https://sourceforge.net/tracker/?func=detail&atid=1126573&aid=2796953&group_id=250145
 - Fix BUG #2811213: "FieldNameRule: Names for final fields  - not be all caps". FieldNameRule: Field names for final instance fields should not default to be all caps like static final fields. For instance: final List sentEmails = []. Rather, "If a property is declared final the private field is created final and no setter is generated." (http://groovy.codehaus.org/Groovy+Beans). So, they should be named with "normal" naming conventions. Re-purpose the finalRegex property to just apply to final instance fields and default to null so that final field names use same convention as non-final. https://sourceforge.net/tracker/?func=detail&aid=2811213&group_id=250145&atid=1126573.
 - Fix: GrailsStatelessServiceRule: Should only apply to *Service classes.

Rules
 - Implement UnusedVariableRule. (unused)
 - Implement UnusedPrivateMethodRule. (unused)
 - Implement UnusedPrivateFieldRule. (unused).
 - MethodNameRule: Add support for ignoreMethodNames.
 - ParameterNameRule: Add support for ignoreParameterNames.
 - VariableNameRule: Add support for ignoreVariableNames.
 - FieldNameRule: Add support for ignoreFieldNames.
 - IllegalRegexRule: Include line numbers in rule violations.

Infrastructure
 - Allow setting custom name and path for "codenarc.properties" file using "codenarc.properties.file" system property.
 - WildcardPattern: Add optional defaultMatches constructor parameter to specify return value of matches() when the pattern string is null or empty.
 - HtmlReportWriter: Show rules with priority 4. This enables configuring rules to be included in the report but not fail the build.
 - SourceCode: Add int getLineNumberForCharacterIndex(int charIndex).
 - CodeNarcRunner: Don't mandate that reportWriters is non-empty.

Other
 - Publish CodeNarc to Maven repository.


Version 0.5 (24 May 2009)
------------------------------------
Bug Fixes
 - FIX: IllegalRegexRule: Don't stop after first violation.
 - FIX: VariableNameRule processing enums: "ArrayIndexOutOfBoundsException: Negative array index [-3] too large for array size 1".
 - FIX: BooleanInstantiationRule, VariableNameRule: Error parsing code with Enums.
 - FIX: Rules sometimes produce two violations for a single line. (e.g. EmptyElseBlockRule, PrintlnRule)

Potential Breaking Changes
 - Normalize all path separators (/,\) in SourceCode paths.
 - SourceCodeCriteria: Change applyToFilenames and doNotApplyToFilenames to applyToFileNames and doNotApplyToFileNames.
 - DirectorySourceAnalyzer: Change applyToFilenames and doNotApplyToFilenames to applyToFileNames and doNotApplyToFileNames.

Rules
 - Created new "grails" RuleSet and nine New Rules:
   * GrailsStatelessServiceRule (Specially-configured StatelessClassRule) (grails)
   * GrailsPublicControllerMethodRule (grails)
   * GrailsServletContextReferenceRule (grails)
   * GrailsSessionReferenceRule (grails)
   * StatelessClassRule (generic)
   * EmptySynchronizedStatementRule (basic).
   * EmptySwitchStatementRule (basic).
   * EqualsAndHashCodeRule (basic)
   * JUnitPublicNonTestMethodRule (junit).
 - Change JUnit rules to use applyToClassNames=*Test,*Tests.
 - ClassSizeRule: Don't include package name in violation message.

Infrastructure
 - Support running CodeNarc as a command-line application (org.codenarc.CodeNarc).
 - Add support for Groovy rule scripts.
 - AbstractAstVisitorRule: Add applyToClassNames and doNotApplyToClassNames. Filter rules based on class and/or package.
 - AbstractRule. Add applyToFileNames and doNotApplyToFileNames (deprecate applyToFilenames and doNotApplyToFilenames).
 - Normalize all separators (/,\) in SourceCode paths to '/'.
 - AbstractRule: Enable setting violationMessage to empty string to turn it off.
 - HtmlReportWriter: Log output report filename.
 - Include number (index) of rule in Rule Descriptions section of HTML report.
 - Create CodeNarcRunner class; refactor CodeNarc and CodeNarcTask to use it.
 - HtmlReportWriter: Shrink font size of the rule descriptions at the end of the HTML report.
 - HtmlReportWriter: Remove "Source Directories" section.
 - Create FilesystemSourceAnalyzer.
 - Refactor WildcardPattern to (optionally) support comma-separated list of patterns.
 - Change WildcardPattern to support Ant-style ?**? wildcards.
 - Refactor AbstractRule and AbstractAstVisitor add/create Violation and source line helper methods.
 - Add createViolation(SourceCode, ASTNode) method to AbstractRule.
 - Add line(int) method to SourceCode to return trimmed source line.
 - Add createViolation() convenience method(s) to AbstractRule.
 - AbstractAstVisitor: Replace isAlreadyVisited() and registerAsVisited() with isFirstVisit().

Deprecations
 - AbstractRule: Deprecated applyToFilenames and doNotApplyToFilenames (replace with applyToFileNames and doNotApplyToFileNames).
 - Deprecated DirectorySourceAnalyzer.

Documentation
 - Create "Create Rule" document.
 - Add section in "Configuring Rules" on Turning Off a Rule.


Version 0.4 (31 Mar 2009)
------------------------------------
Infrastructure
 - Support for wildcards (*,?) in RuleSet <include> and <exclude>, and in Rule.applyToFilenames.
 - Fix for configuration from properties files  - allow setting superclass fields.
 - Add better logging, including stacktrace, when an error occurs during processing.
 - Format (and truncate) source line within the HtmlReportWriter.
 - Improve violation information for imports  - line number and actual source line.
 - Make version at the bottom of the HTML report a link to the web site.
 - Refactor SourceCodeUtil to a SourceCodeCriteria class.
 - CodeNarcTask: Log elapsed time after analysis is complete.
 - CodeNarcTask: Don't fail build until after reports are generated.
 - Create WildcardPattern class.
 - Create AstUtil. Move isBlock() and isEmptyBlock() from AbstractAstVisitor.

Rules
 - Created new "junit" and "logging" RuleSets and a bunch of New Rules:
   * JUnitAssertAlwaysSuccedsRule (junit)
   * JUnitAssertAlwaysFailsRule (junit)
   * JUnitTearDownCallsSuperRule (junit)
   * JUnitSetUpCallsSuperRule (junit)
   * SystemErrPrintRule. (logging)
   * SystemOutPrintRule. (logging)
   * PrintlnRule. (logging)
   * PrintStackTraceRule. (logging)
   * CatchNullPointerExceptionRule. (exceptions)
   * CatchExceptionRule. (exceptions)
   * CatchRuntimeExceptionRule. (exceptions)
   * CatchErrorRule. (exceptions)
   * ThrowThrowableRule (exceptions)
   * ThrowExceptionRule (exceptions)
   * ThrowNullPointerExceptionRule (exceptions)
   * ThrowRuntimeExceptionRule (exceptions)
   * ThrowErrorRule (exceptions)
 - BooleanInstantiationRule: Also check for Boolean.valueOf(true/false).

Documentation
 - Add example reports for open source Groovy projects: Grails, Griffon and Gradle.


Version 0.3 (02 Mar 2009)
------------------------------------
Infrastructure
 - Read rules configuration from optional "codenarc.properties" file. (PropertiesRuleSetConfigurer).
 - CodeNarcTask: Add support for maxPriority3Violations property, etc.
 - AbstractRule: Add applyToFilenames and doNotApplyToFilenames  - available to all rules (subclasses).
 - HtmlReportWriter: Display sourceLine and message on separate lines with named bullets.

Rules
 - Created new "naming" RuleSets and New Rules:
   * AbstractClassNameRule. (naming)
   * ClassNameRule. (naming)
   * FieldNameRule. (naming)
   * InterfaceNameRule. (naming)
   * MethodNameRule. (naming)
   * PackageNameRule. (naming)
   * ParameterNameRule. (naming)
   * VariableNameRule. (naming)

Minor Fixes/Enhancements
 - Fix NullPointerException for compiler errors from import rules.
 - AbstractRule: Introduce violationMessage property. Overrides default message if set.
 - Rename Violation description property to message.
 - Rename HtmlReportWriter CSS file to 'codenarc-htmlreport.css'.
 - Change SourceCodeUtil.shouldApplyTo() to take a Map of criteria.
 - Add setName() and setPriority() to AbstractRule.

Documentation
 - Add info to online docs re: standard rule properties.
 - Reorganize docs  - separate doc for each RuleSet; section for each rule; table for properties.
 - Update RuleSets doc with info about configuring using properties file.
 - Create "Configuring Rules" document (adapt existing "custom-rule-descriptions.apt").


Version 0.2 (07 Feb 2009)
------------------------------------
 - Create XML Schema Definition (XSD) for XML RuleSet file.
    * NOTE: RuleSet files MUST declare this schema
    * NOTE: RuleSet files are validated against this schema
 - More powerful and flexible RuleSet definition, including:
    * Nested RuleSets
    * Use <include> and <exclude> to filter rules from nested RuleSets
    * Use <rule-config> to configure rules from nested RuleSets

 - Created new "generic", "braces", and "size" RuleSets
 - Created New Rules:
   * IllegalRegexRule. (generic)
   * RequiredRegexRule. (generic)
   * ElseBlockBracesRule. (braces)
   * ForStatementBracesRule. (braces)
   * IfStatementBracesRule. (braces)
   * WhileStatementBracesRule. (braces)
   * MethodSizeRule. (size)
   * ClassSizeRule. (size)

 - Rule: Rename "id" property to "name".
    * NOTE: This is a potential breakage if you have defined custom Rules.
 - Flexible customization and localization of rule descriptions. ("codenarc-messages.properties")
 - HtmlReportWriter: Add setTitle(), setOutputFile() to ReportWriter interface.


Version 0.1 (24 Jan 2009)
------------------------------------
 - Initial release. Includes 16 Rules; HtmlReportWriter; CodeNarcAntTask, DirectorySourceAnalyzer; XML RuleSets, etc..


<http://www.codenarc.org>