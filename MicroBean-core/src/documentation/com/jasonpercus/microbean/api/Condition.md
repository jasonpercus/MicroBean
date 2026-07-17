# 📄 @Condition [🧩 Annotation]

## 🎯 Description

L'annotation `@Condition` permet de conditionner l'activation d'un bean, d'un service, d'un adaptateur ou d'une configuration à une évaluation personnalisée. Elle s'utilise sur une méthode annotée [`@Bean`](./Bean.md) ou sur une classe annotée [`@Configuration`](./Configuration.md), [`@Service`](./Service.md) ou [`@Adapter`](./Adapter.md). Elle permet d'activer dynamiquement des composants selon le contexte d'exécution, les arguments, l'environnement ou toute logique personnalisée implémentée via un [`ConditionEvaluator`](./ConditionEvaluator.md).

## 🧠 Rôle dans l'architecture

`@Condition` introduit un mécanisme d'activation conditionnelle dans le cycle de vie des composants MicroBean. Elle permet au conteneur d'évaluer dynamiquement, lors de l'enregistrement ou de l'instanciation, si un composant doit être activé ou non, en s'appuyant sur une logique métier ou technique personnalisée. Elle s'intègre avec les autres mécanismes d'activation conditionnelle comme [`@Profile`](./Profile.md) et permet une grande flexibilité dans la configuration des applications.

## 🔗 Relations

- Dépend de :
  - [`ConditionEvaluator`](./ConditionEvaluator.md) : interface d'évaluation de la condition.
- Utilisé par :
  - Le conteneur MicroBean pour l'activation conditionnelle des composants.
- Concepts liés :
  - [`@Profile`](./Profile.md) : activation selon le profil d'exécution.
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md), [`@Configuration`](./Configuration.md) : cibles possibles de l'annotation.

## ⚙️ Attributs

| Nom    | Type                                | Valeur par défaut | Rôle / Impact à l'exécution                                                      |
|--------|-------------------------------------|-------------------|----------------------------------------------------------------------------------|
| value  | Class<? extends ConditionEvaluator> | (obligatoire)     | Classe d'évaluateur de condition à utiliser                                      |
| negate | boolean                             | false             | Inverse le résultat de l'évaluation (active le composant si la condition échoue) |

## 💡 Exemples d'utilisation

```java
@Configuration
public class AppConfig {
    
    @Bean
    @Condition(value = MaConditionEvaluator.class)
    public MainService mainService() {
        return new MainService();
    }
    
}
```

---

```java
@Service
@Condition(value = MaConditionEvaluator.class)
public class MonService {
    // ...
}
```

## 🔄 Comportement du cycle de vie

1. Découverte de l'annotation lors du scan des composants.
2. Évaluation de la condition via la classe [`ConditionEvaluator`](./ConditionEvaluator.md) spécifiée.
3. Si la condition est validée (ou échouée si `negate = true`), le composant est activé et enregistré dans le contexte.
4. Sinon, le composant est ignoré par le conteneur.

## ⚠️ Limitations / cas particuliers

- L'évaluation dépend de l'implémentation de [`ConditionEvaluator`](./ConditionEvaluator.md) : une logique incorrecte peut empêcher l'activation attendue.
- L'attribut `negate` permet d'inverser la logique, mais peut rendre la configuration moins lisible.
- L'annotation ne gère pas la désactivation dynamique après l'initialisation du contexte.
- Peut être combinée avec [`@Profile`](./Profile.md) pour des scénarios avancés.

## 📍 Notes internes MicroBean

- Le mécanisme de condition permet d'adapter dynamiquement la configuration sans modifier le code métier.
- Il favorise la factorisation des règles d'activation et la réutilisation de logique conditionnelle.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`ConditionEvaluator`](./ConditionEvaluator.md) – interface d'évaluation de condition
- [`@Configuration`](./Configuration.md) – déclaration de classes de configuration
- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
- [`@Profile`](./Profile.md) – activation selon le profil d'exécution
