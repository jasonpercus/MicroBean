Feature: Exécution de la classe Processor

  Scenario: Enregistrer les composants nominaux via Processor
    Given un contexte Processor initialisé
    And les classes Processor suivantes:
      | ConfigurationNominale |
      | ServiceNominal |
    When j'exécute Processor sans argument
    Then le bean "BeanNominal" est résolvable
    And le bean "ServiceNominal" est résolvable

  Scenario: Ignorer un service quand son profil ne correspond pas
    Given un contexte Processor initialisé
    And le profil actif est "prod"
    And les classes Processor suivantes:
      | ServiceProfileDev |
    When j'exécute Processor sans argument
    Then le bean "ServiceProfileDev" n'est pas résolvable

  Scenario: Enregistrer un adaptateur quand l'OS est compatible
    Given un contexte Processor initialisé
    And le système d'exploitation est "WINDOWS"
    And les classes Processor suivantes:
      | AdapterWindows |
    When j'exécute Processor sans argument
    Then le bean "AdapterWindows" est résolvable

  Scenario: Ignorer un bean de configuration quand la condition est négatée
    Given un contexte Processor initialisé
    And les classes Processor suivantes:
      | ConfigurationConditionNegatee |
    When j'exécute Processor sans argument
    Then le bean "BeanConditionNegatee" n'est pas résolvable

  Scenario: Lever une erreur quand une méthode @Bean n'est pas publique
    Given un contexte Processor initialisé
    And les classes Processor suivantes:
      | ConfigurationBeanPrive |
    When j'exécute Processor sans argument
    Then une exception Processor est levée contenant "method must be public"
