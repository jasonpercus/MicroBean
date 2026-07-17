Feature: Utilisation de la bannière MicroBean

  Scenario: Affichage d'une bannière personnalisée
    Given une appClass nommée "B_ApplicationPersonnalized"
    And la classe "B_ApplicationPersonnalized" est annotée avec "@MicroBeanApplication"
    When j'exécute la classe "B_ApplicationPersonnalized"
    Then la bannière personnalisée de test s'affiche
    And le profil détecté et exécuté doit être "[unknown]"

  Scenario: Désactivation de la bannière
    Given une appClass nommée "B_ApplicationDisabled"
    And la classe "B_ApplicationDisabled" est annotée avec "@MicroBeanApplication"
    When j'exécute la classe "B_ApplicationDisabled"
    Then aucune sortie n'est affichée

  Scenario: Bannière introuvable
    Given une appClass nommée "B_ApplicationNotFound"
    And la classe "B_ApplicationNotFound" est annotée avec "@MicroBeanApplication"
    When j'exécute la classe "B_ApplicationNotFound"
    Then un message indique que la bannière est introuvable au chemin "banner-missing-test.txt"

  Scenario: Profil inconnu quand aucun profil n'est défini
    Given une appClass nommée "B_ApplicationUnknownProfile"
    And la classe "B_ApplicationUnknownProfile" est annotée avec "@MicroBeanApplication"
    When j'exécute la classe "B_ApplicationUnknownProfile"
    Then la bannière personnalisée de test s'affiche
    And le profil détecté et exécuté doit être "[unknown]"

  Scenario: Profil inconnu quand le profil actif est vide ou blanc
    Given une appClass nommée "B_ApplicationWhiteProfile"
    And la classe "B_ApplicationWhiteProfile" est annotée avec "@MicroBeanApplication"
    When j'exécute la classe "B_ApplicationWhiteProfile"
    Then la bannière personnalisée de test s'affiche
    And le profil détecté et exécuté doit être "[unknown]"
