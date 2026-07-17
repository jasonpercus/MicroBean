Feature: Scan des composants avec ClassScanner

  Scenario: Scan nominal des composants annotés valides
    Given une appClass nommée "CS_ApplicationNominal"
    When j'exécute la classe "CS_ApplicationNominal"
    Then le scanner retourne la classe "ServiceValid"
    And le scanner retourne la classe "AdapterValid"
    And le scanner retourne la classe "ConfigurationValid"
    And le scanner retourne la classe "EntryPointValid"
    And le scanner ne retourne pas la classe "ClasseNotAnnotated"
    And le scanner retourne 4 classes

  Scenario: Exclusion des interfaces, classes abstraites et annotations
    Given une appClass nommée "CS_ApplicationExcluded"
    When j'exécute la classe "CS_ApplicationExcluded"
    Then le scanner retourne la classe "ValidConcreteServiceWithPackageExcluded"
    And le scanner ne retourne pas la classe "ServiceInterfaceAnnotated"
    And le scanner ne retourne pas la classe "AbstractServiceAnnotated"
    And le scanner ne retourne pas la classe "AnnotationAnnotatedService"
    And le scanner retourne 1 classes

  Scenario: Exclusion d'une classe invalidée par ScanningValidator
    Given une appClass nommée "CS_ApplicationInvalidated"
    When j'exécute la classe "CS_ApplicationInvalidated"
    Then le scanner ne retourne pas la classe "ServiceProfiledInvalid"
    And le scanner retourne 0 classes
