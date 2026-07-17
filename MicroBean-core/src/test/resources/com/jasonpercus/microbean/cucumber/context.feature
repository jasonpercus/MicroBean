Feature: Résolution des beans dans le Context

  Scenario: Résoudre un bean simple par type
    Given un contexte Context initialisé
    And les classes Context suivantes sont enregistrées:
      | ContextSimpleService |
    When je demande le bean Context par type "ContextSimpleService"
    Then le bean Context retourné est de type "ContextSimpleService"
    And le bean Context retourne la même instance pour "ContextSimpleService"

  Scenario: Résoudre un bean prototype par type
    Given un contexte Context initialisé
    And les classes Context suivantes sont enregistrées:
      | ContextPrototypeService |
    When je demande le bean Context par type "ContextPrototypeService"
    Then le bean Context retourné est de type "ContextPrototypeService"
    And le bean Context retourne deux instances différentes pour "ContextPrototypeService"

  Scenario: Résoudre le bean primaire quand plusieurs candidats implémentent le même contrat
    Given un contexte Context initialisé
    And les classes Context suivantes sont enregistrées:
      | ContextSecondaryService |
      | ContextPrimaryService |
    When je demande le bean Context par type "ContextContract"
    Then le bean Context retourné est de type "ContextPrimaryService"

  Scenario: Lever une erreur quand plusieurs candidats existent sans primaire
    Given un contexte Context initialisé
    And les classes Context suivantes sont enregistrées:
      | ContextNoPrimaryOneService |
      | ContextNoPrimaryTwoService |
    When je demande le bean Context par type "ContextContract"
    Then une erreur Context est levée contenant "Multiple beans found for type"

  Scenario: Lever une erreur quand le bean nommé n'existe pas
    Given un contexte Context initialisé
    And les classes Context suivantes sont enregistrées:
      | ContextSimpleService |
    When je demande le bean Context nommé "inconnu" avec le type "ContextSimpleService"
    Then une erreur Context est levée contenant "No bean found for name"
