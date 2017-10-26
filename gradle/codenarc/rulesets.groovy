ruleset {
    ruleset('rulesets/basic.xml') {
        ThrowExceptionFromFinallyBlock {
            enabled = false
        }
        EmptyCatchBlock {
            enabled = false
        }
        EmptyClass {
            doNotApplyToFileNames = 'GebTest.groovy, GebReportingTest.groovy'
        }
    }
    ruleset('rulesets/braces.xml')
    ruleset('rulesets/convention.xml') {
        InvertedIfElse {
            enabled = false
        }
        NoDef {
            enabled = false
        }
        TrailingComma {
            enabled = false
        }
    }
    ruleset('rulesets/dry.xml') {
        DuplicateStringLiteral {
            doNotApplyToFileNames = '*Spec.groovy, RemoteWebDriverWithExpectations.groovy, Configuration.groovy, Page.groovy, BindingUpdater.groovy, TextMatchingSupport.groovy, NonEmptyNavigator.groovy, EmptyNavigator.groovy'
        }
        DuplicateNumberLiteral {
            doNotApplyToFileNames = '*Spec.groovy, Crawler.groovy'
        }
        DuplicateListLiteral {
            doNotApplyToFileNames = '*Spec.groovy, Page.groovy'
        }
        DuplicateMapLiteral {
            doNotApplyToFileNames = '*Spec.groovy'
        }
    }
    ruleset('rulesets/formatting.xml') {
        ClassJavadoc {
            enabled = false
        }
        SpaceAroundMapEntryColon {
            characterAfterColonRegex = /\s/
            characterBeforeColonRegex = /.*/
        }
        FileEndsWithoutNewline {
            enabled = false
        }
        LineLength {
            length = 200
            doNotApplyToFileNames = 'TemplateOptionsSpec.groovy'
        }
        SpaceAfterOpeningBrace {
            ignoreEmptyBlock = true
        }
        SpaceBeforeClosingBrace {
            ignoreEmptyBlock = true
        }
        SpaceBeforeOpeningBrace {
            doNotApplyToFileNames = 'InteractionsSupportSpec.groovy, WaitingSupportSpec.groovy'
        }
        MissingBlankLineAfterImports {
            doNotApplyToFileNames = 'ManualsMenuModule.groovy'
        }
    }
    ruleset('rulesets/generic.xml') {
        RequiredString {
            string = 'Copyright'
            violationMessage = 'Copyright header not found'
        }
    }
    ruleset('rulesets/groovyism.xml') {
        ExplicitHashSetInstantiation {
            enabled = false
        }
        GetterMethodCouldBeProperty {
            enabled = false
        }
        ExplicitCallToDivMethod {
            enabled = false
        }
        ExplicitCallToModMethod {
            enabled = false
        }
    }
    ruleset('rulesets/imports.xml') {
        MisorderedStaticImports {
            comesBefore = false
        }
        NoWildcardImports {
            enabled = false
        }
        UnnecessaryGroovyImport {
            doNotApplyToFileNames = 'ExceptionToPngConverter.groovy'
        }
    }
    ruleset('rulesets/naming.xml') {
        MethodName {
            doNotApplyToFileNames = '*Spec.groovy'
            regex = /([a-z]\w*|\$)/
        }
        ConfusingMethodName {
            enabled = false
        }
        FactoryMethodName {
            enabled = false
        }
        ClassNameSameAsSuperclass {
            enabled = false
        }
    }
    ruleset('rulesets/unnecessary.xml') {
        UnnecessaryGetter {
            enabled = false
        }
        UnnecessaryGString {
            enabled = false
        }
        UnnecessarySubstring {
            enabled = false
        }
        UnnecessaryObjectReferences {
            enabled = false
        }
        UnnecessaryPackageReference {
            enabled = false
        }
        UnnecessarySemicolon {
            doNotApplyToFileNames = 'PageOrientedSpec.groovy, StrongTypingSpec.groovy'
        }
    }
    ruleset('rulesets/unused.xml')
}