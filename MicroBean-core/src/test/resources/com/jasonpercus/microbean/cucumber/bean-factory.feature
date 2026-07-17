Feature: Création des beans via BeanFactory

  Scenario: Créer un bean depuis une méthode @Bean avec injection de dépendance
    Given un contexte BeanFactory initialisé
    When je crée un bean BeanFactory depuis une méthode @Bean
    Then le bean BeanFactory issu d'une méthode contient une dépendance injectée

  Scenario: Utiliser le constructeur avec le plus de paramètres
    Given un contexte BeanFactory initialisé
    When je crée un bean BeanFactory depuis une classe avec constructeur prioritaire
    Then le bean BeanFactory utilise le constructeur avec le plus de paramètres

  Scenario: Résoudre une dépendance nommée avec @Named
    Given un contexte BeanFactory initialisé
    When je crée un bean BeanFactory depuis une classe avec dépendance nommée
    Then le bean BeanFactory résout correctement la dépendance nommée

  Scenario: Exécuter les PostConstruct de la classe, de la superclasse et de l'interface
    Given un contexte BeanFactory initialisé
    When je crée un bean BeanFactory avec des PostConstruct hérités
    Then les PostConstruct BeanFactory de la classe, de la superclasse et de l'interface sont exécutés

  Scenario: Remonter une erreur quand un PostConstruct échoue
    Given un contexte BeanFactory initialisé
    When je crée un bean BeanFactory dont le PostConstruct échoue
    Then une erreur BeanFactory est levée contenant "Failed to call"
