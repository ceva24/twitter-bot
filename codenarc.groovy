ruleset {
    ruleset 'rulesets/basic.xml'
    ruleset('rulesets/braces.xml') { IfStatementBraces(enabled: false) }
    ruleset 'rulesets/concurrency.xml'
    ruleset('rulesets/convention.xml') { NoDef(enabled: false)  }
    ruleset 'rulesets/design.xml'
    ruleset 'rulesets/dry.xml'
    ruleset 'rulesets/exceptions.xml'
    ruleset 'rulesets/groovyism.xml'
    ruleset 'rulesets/imports.xml'
    ruleset 'rulesets/jdbc.xml'
    ruleset 'rulesets/junit.xml'
    ruleset 'rulesets/logging.xml'
    ruleset 'rulesets/naming.xml'
    ruleset 'rulesets/security.xml'
    ruleset('rulesets/size.xml') { CrapMetric(enabled: false) }
    ruleset 'rulesets/serialization.xml'
    ruleset('rulesets/unnecessary.xml') { UnnecessaryReturnKeyword(enabled: false); UnnecessaryDefInMethodDeclaration(enabled: false)  }
    ruleset 'rulesets/unused.xml'
}