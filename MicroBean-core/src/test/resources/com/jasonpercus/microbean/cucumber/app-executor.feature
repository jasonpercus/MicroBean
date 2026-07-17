Feature: AppExecutor via le flux public MicroBean

  Scenario: AppExecutor exécute un entry point ONE_SHOT au premier-plan et transmet les arguments
    Given une appClass nommée "AE_Launcher"
    And la classe "AE_Launcher" est annotée avec "@MicroBeanApplication"
    And les arguments de l'application sont:
      | alpha |
      | beta  |
    And un consumer de Context n'est pas défini
    And les entryPoints de l'application sont:
      | AE_OneShotService |
    When j'exécute le framework MicroBean
    Then l'entrypoint "AE_OneShotService" est bien exécutée au premier-plan
    And la sortie contient la ligne "AE_OneShotService args=[alpha, beta]"

  Scenario: AppExecutor exécute un entry point LONG_RUNNING en arrière-plan
    Given une appClass nommée "AE_Launcher"
    And la classe "AE_Launcher" est annotée avec "@MicroBeanApplication"
    And les arguments de l'application sont:
      | async |
    And un consumer de Context n'est pas défini
    And les entryPoints de l'application sont:
      | AE_LongRunningService |
    When j'exécute le framework MicroBean
    Then l'entrypoint "AE_LongRunningService" est bien exécutée en arrière-plan
    And la sortie contient la ligne "AE_LongRunningService args=[async]"

  Scenario: AppExecutor exécute le consumer de contexte une fois par entry point
    Given une appClass nommée "AE_Launcher"
    And la classe "AE_Launcher" est annotée avec "@MicroBeanApplication"
    And il n'y a pas d'arguments
    And un consumer de Context est défini avec l'action "EXECUTED"
    And les entryPoints de l'application sont:
      | AE_OneShotService     |
      | AE_LongRunningService |
    When j'exécute le framework MicroBean
    Then l'entrypoint "AE_OneShotService" est bien exécutée au premier-plan
    And l'entrypoint "AE_LongRunningService" est bien exécutée en arrière-plan
    And la sortie contient 2 fois la ligne "Consumer de Context exécuté"

  Scenario: AppExecutor lève une erreur si plusieurs entry points sont ONE_SHOT
    Given une appClass nommée "AE_Launcher"
    And la classe "AE_Launcher" est annotée avec "@MicroBeanApplication"
    And il n'y a pas d'arguments
    And un consumer de Context n'est pas défini
    And les entryPoints de l'application sont:
      | AE_OneShotService       |
      | AE_SecondOneShotService |
    When j'exécute la classe "AE_Launcher"
    Then une exception "MicroBeanException" est levée contenant "Only one ApplicationEntryPoint can be ONE_SHOT"
