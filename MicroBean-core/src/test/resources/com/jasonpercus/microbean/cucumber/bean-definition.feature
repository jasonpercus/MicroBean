Feature: Construction des définitions de bean

  Scenario: Construire une définition depuis une méthode @Bean
    Given un contexte BeanDefinition initialisé
    When je construis une BeanDefinition depuis la méthode bean
    Then la BeanDefinition de méthode est construite avec les métadonnées attendues

  Scenario: Construire une définition depuis une classe service
    Given un contexte BeanDefinition initialisé
    When je construis une BeanDefinition depuis une classe service
    Then la BeanDefinition de service est construite avec les métadonnées attendues

  Scenario: Construire une définition depuis une classe entrypoint
    Given un contexte BeanDefinition initialisé
    When je construis une BeanDefinition depuis une classe entrypoint
    Then la BeanDefinition d'entrypoint est construite avec les valeurs par défaut

  Scenario: Échouer quand la méthode n'est pas annotée @Bean
    Given un contexte BeanDefinition initialisé
    When je construis une BeanDefinition depuis une méthode non bean
    Then une erreur BeanDefinition est levée contenant "not annotated"

  Scenario: Échouer quand la classe n'est pas un composant supporté
    Given un contexte BeanDefinition initialisé
    When je construis une BeanDefinition depuis une classe non composant
    Then une erreur BeanDefinition est levée contenant "component"
    