@config-properties
Feature: Chargement des fichiers de configuration dans Initializer

  Scenario: Chargement nominal de fichiers YAML et JSON
    Given une appClass nommée "AppWithConfigurationPropertiesInitializer"
    And il n'y a pas d'arguments
    And un consumer de Context est défini avec l'action "PRINT_CONFIG_PROPERTIES"
    And les entryPoints de l'application sont:
      | ValidEntryPointInitializer |
    When j'exécute le framework MicroBean
    Then la sortie contient la ligne "CONFIG:server.host-name=localhost"
    And la sortie contient la ligne "CONFIG:server.port=8080"
    And la sortie contient la ligne "CONFIG:database.max-pool-size=10"

  Scenario: Échec quand l'extension de fichier de configuration est invalide
    Given une appClass nommée "AppWithInvalidConfigurationExtensionInitializer"
    And il n'y a pas d'arguments
    And un consumer de Context n'est pas défini
    And les entryPoints de l'application sont:
      | ValidEntryPointInitializer |
    When j'exécute le framework MicroBean
    Then une exception "MicroBeanException" est levée contenant "Invalid configuration properties file"

  Scenario: Échec quand le fichier de configuration est introuvable
    Given une appClass nommée "AppWithMissingConfigurationInitializer"
    And il n'y a pas d'arguments
    And un consumer de Context n'est pas défini
    And les entryPoints de l'application sont:
      | ValidEntryPointInitializer |
    When j'exécute le framework MicroBean
    Then une exception "MicroBeanException" est levée contenant "Configuration properties file not found"

  Scenario: Échec quand le fichier JSON de configuration est invalide
    Given une appClass nommée "AppWithInvalidJsonConfigurationInitializer"
    And il n'y a pas d'arguments
    And un consumer de Context n'est pas défini
    And les entryPoints de l'application sont:
      | ValidEntryPointInitializer |
    When j'exécute le framework MicroBean
    Then une exception "MicroBeanException" est levée contenant "Failed to load configuration properties"

