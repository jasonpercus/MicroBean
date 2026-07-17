Feature: Éxécution du framework MicroBean

  Scenario: Lorsque l'annotation @MicroBeanApplication est absente
    Given une appClass nommée "Application2"
    And la classe "Application2" n'est pas annotée avec "@MicroBeanApplication"
    When j'exécute la classe "Application2"
    Then une exception "MicroBeanException" est levée disant que la classe "Application2" n'est pas annotée avec @MicroBeanApplication

  Scenario: Lorsqu'aucun entryPoint n'est défini
    Given une appClass nommée "Application3"
    And la classe "Application3" est annotée avec "@MicroBeanApplication"
    And il n'y a pas d'arguments
    And un consumer de Context n'est pas défini
    And aucun entryPoint n'est défini
    When j'exécute la classe "Application3"
    Then une exception "MicroBeanException" est levée disant qu'aucun entryPoint n'est défini dans la classe "Application3"

  Scenario: Lorsque la classe principale du projet est également annotée avec @EntryPointService
    Given une appClass nommée "Application4"
    And la classe "Application4" est annotée avec @MicroBeanApplication et @EntryPointService
    And il n'y a pas d'arguments
    And un consumer de Context n'est pas défini
    When j'exécute la classe "Application4"
    Then une exception "MicroBeanException" est levée disant que la classe principale "Application4" ne doit pas être annotée avec @EntryPointService

  Scenario: Lorsqu'un entryPoint n'est pas annoté avec @EntryPointService
    Given une appClass nommée "Application3"
    And la classe "Application3" est annotée avec "@MicroBeanApplication"
    And il n'y a pas d'arguments
    And un consumer de Context n'est pas défini
    And les entryPoints de l'application sont:
      | EntryPointNotAnnotated |
    When j'exécute la classe "Application3"
    Then une exception "MicroBeanException" est levée disant que la classe "EntryPointNotAnnotated" n'est pas annotée avec @EntryPointService

  Scenario: Cas nominal d'exécution du framework MicroBean
    Given une appClass nommée "Application"
    And la classe "Application" est annotée avec "@MicroBeanApplication"
    And les arguments de l'application sont:
      | -Dapp.profile=test |
      | 2026 |
    And un consumer de Context est défini avec l'action "EXECUTED"
    And les entryPoints de l'application sont:
      | MainService |
      | BackgroundService |
    And le système d'exploitation est "Linux"
    When j'exécute le framework MicroBean
    Then la bannière de démarrage s'affiche
    And le profil détecté et exécuté doit être "test"
    And le consumer de Context doit être exécuté avec l'action "EXECUTED"
    And la classe "ConfigProfiled" est bien détectée comme "@Configuration"
    And la classe "ConfigProfiledKept" est bien détectée comme "@Configuration"
    And la classe "ConfigProfiledNoKept" est bien détectée comme "@Configuration" mais skippée à cause de son profil
    And la classe "ConfigConditioned" est bien détectée comme "@Configuration"
    And la classe "ConfigConditionedKept" est bien détectée comme "@Configuration"
    And la classe "ConfigConditionedNoKept" est bien détectée comme "@Configuration" mais skippée à cause de sa condition
    And la classe "ConfigPrimary" est bien détectée comme "@Configuration"
    And la classe "ConfigNamed" est bien détectée comme "@Configuration"
    And la classe "ConfigScoped" est bien détectée comme "@Configuration"
    And la méthode "ConfigProfiled#createObject1" est bien détectée comme "@Bean"
    And la méthode "ConfigProfiled#createObject2" n'est pas détectée comme "@Bean" valide
    And la méthode "ConfigProfiledKept#createObject3" est bien détectée comme "@Bean"
    And la méthode "ConfigProfiledKept#createObject4" est bien détectée comme "@Bean"
    And la méthode "ConfigProfiledNoKept#createObject5" n'est pas détectée comme "@Bean" valide
    And la méthode "ConfigProfiledNoKept#createObject6" n'est pas détectée comme "@Bean" valide
    And la méthode "ConfigConditioned#createObject7" est bien détectée comme "@Bean"
    And la méthode "ConfigConditioned#createObject8" n'est pas détectée comme "@Bean" valide
    And la méthode "ConfigConditionedKept#createObject9" est bien détectée comme "@Bean"
    And la méthode "ConfigConditionedKept#createObject10" est bien détectée comme "@Bean"
    And la méthode "ConfigConditionedNoKept#createObject11" n'est pas détectée comme "@Bean" valide
    And la méthode "ConfigConditionedNoKept#createObject12" n'est pas détectée comme "@Bean" valide
    And la méthode "ConfigPrimary#createObject13_1" est bien détectée comme "@Bean"
    And la méthode "ConfigPrimary#createObject13_2" est bien détectée comme "@Bean"
    And la méthode "ConfigPrimary#createObject13_3" est bien détectée comme "@Bean"
    And la méthode "ConfigNamed#createObject14_1" est bien détectée comme "@Bean"
    And la méthode "ConfigNamed#createObject14_2" est bien détectée comme "@Bean"
    And la méthode "ConfigNamed#createObject14_3" est bien détectée comme "@Bean"
    And la méthode "ConfigScoped#createObject15" est bien détectée comme "@Bean"
    And la méthode "ConfigScoped#createObject16" est bien détectée comme "@Bean"
    And l'objet "Object13" retenu et injecté est bien annoté avec @Primary
    And le premier objet "Object14" retenu et injecté est bien annoté avec @Named
    And le second objet "Object14" retenu et injecté est bien annoté avec @Named + @Primary
    And l'objet "Object15" retenu et injecté est bien un singleton
    And l'objet "Object16" retenu et injecté est bien un prototype
    And la classe "MainService" est bien détectée comme "@EntryPointService"
    And la classe "MainService" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object4 |
      | Object7 |
      | Object9 |
      | Object10 |
      | Object13 |
      | Object14 |
      | Object14 |
      | Object15 |
      | Object15 |
      | Object16 |
      | Object16 |
      | ServiceConditionedKept |
      | ServiceNamedByClass |
      | ServiceNamedByBean |
      | ServicePrimaryByClass |
      | ServicePrimaryByBean |
      | ServiceProfiledKept |
      | ServiceScopedSingleton |
      | ServiceScopedSingleton |
      | ServiceScopedPrototype |
      | ServiceScopedPrototype |
      | AdapterConditionedKept |
      | AdapterNamedByClass |
      | AdapterNamedByBean |
      | AdapterPrimaryByClass |
      | AdapterPrimaryByBean |
      | AdapterProfiledKept |
      | AdapterScopedSingleton |
      | AdapterScopedSingleton |
      | AdapterScopedPrototype |
      | AdapterScopedPrototype |
      | AdapterOSLinux |
      | AdapterOSAll |
    And l'entrypoint "MainService" est bien exécutée au premier-plan
    And les 2 méthodes de @PostConstruct de "MainService" sont bien exécutées
    And l'entrypoint "BackgroundService" est bien exécutée en arrière-plan
    And la classe "ServiceConditionedKept" est bien détectée comme "@Service"
    And la classe "ServiceConditionedNoKept" est bien détectée comme "@Service" mais skippée à cause de sa condition
    And la classe "ServiceConditionedKept" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "ServiceConditionedKept" a bien été injectée
    And la classe "ServiceNamedByClass" est bien détectée comme "@Service"
    And la classe "ServiceNamedByClass" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "ServiceNamedByClass" a bien été injectée
    And la classe "ServiceNamedByBean" est bien détectée comme "@Service"
    And la classe "ServiceNamedByBean" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "ServiceNamedByBean" a bien été injectée
    And la classe "ServicePrimaryByClass" est bien détectée comme "@Service"
    And la classe "ServicePrimaryByClass" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "ServicePrimaryByClass" a bien été injectée
    And la classe "ServicePrimaryByBean" est bien détectée comme "@Service"
    And la classe "ServicePrimaryByBean" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "ServicePrimaryByBean" a bien été injectée
    And la classe "ServiceProfiledKept" est bien détectée comme "@Service"
    And la classe "ServiceProfiledNoKept" est bien détectée comme "@Service" mais skippée à cause de son profil
    And la classe "ServiceProfiledKept" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "ServiceProfiledKept" a bien été injectée
    And la classe "ServiceScopedSingleton" est bien détectée comme "@Service"
    And la classe "ServiceScopedPrototype" est bien détectée comme "@Service"
    And la classe "ServiceScopedSingleton" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "ServiceScopedPrototype" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And l'objet "ServiceScopedSingleton" retenu et injecté est bien un singleton
    And l'objet "ServiceScopedPrototype" retenu et injecté est bien un prototype
    And la classe "AdapterConditionedKept" est bien détectée comme "@Adapter"
    And la classe "AdapterConditionedNoKept" est bien détectée comme "@Adapter" mais skippée à cause de sa condition
    And la classe "AdapterConditionedKept" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterConditionedKept" a bien été injectée
    And la classe "AdapterNamedByClass" est bien détectée comme "@Adapter"
    And la classe "AdapterNamedByClass" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterNamedByClass" a bien été injectée
    And la classe "AdapterNamedByBean" est bien détectée comme "@Adapter"
    And la classe "AdapterNamedByBean" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterNamedByBean" a bien été injectée
    And la classe "AdapterPrimaryByClass" est bien détectée comme "@Adapter"
    And la classe "AdapterPrimaryByClass" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterPrimaryByClass" a bien été injectée
    And la classe "AdapterPrimaryByBean" est bien détectée comme "@Adapter"
    And la classe "AdapterPrimaryByBean" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterPrimaryByBean" a bien été injectée
    And la classe "AdapterProfiledKept" est bien détectée comme "@Adapter"
    And la classe "AdapterProfiledNoKept" est bien détectée comme "@Adapter" mais skippée à cause de son profil
    And la classe "AdapterProfiledKept" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterProfiledKept" a bien été injectée
    And la classe "AdapterScopedSingleton" est bien détectée comme "@Adapter"
    And la classe "AdapterScopedPrototype" est bien détectée comme "@Adapter"
    And la classe "AdapterScopedSingleton" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterScopedPrototype" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And l'objet "AdapterScopedSingleton" retenu et injecté est bien un singleton
    And l'objet "AdapterScopedPrototype" retenu et injecté est bien un prototype
    And la classe "AdapterOSLinux" est bien détectée comme "@Adapter"
    And la classe "AdapterOSLinux" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterOSLinux" a bien été injectée
    And la classe "AdapterOSWindows" est bien détectée comme "@Adapter" mais skippée à cause de son os
    And la classe "AdapterOSMac" est bien détectée comme "@Adapter" mais skippée à cause de son os
    And la classe "AdapterOSAll" est bien détectée comme "@Adapter"
    And la classe "AdapterOSAll" a bien son bean de créé et injecté avec:
      | Object1 |
      | Object3 |
      | Object14 |
      | Object14 |
    And la classe "AdapterOSAll" a bien été injectée
