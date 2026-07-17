# 📄 ConditionEvaluator [🧩 Interface]

## 🎯 Description

L'interface `ConditionEvaluator` permet d'implémenter des règles de validation conditionnelle sur un objet d'instance, en utilisant un tableau d'arguments (généralement ceux de l'application) comme données additionnelles. Elle hérite de [`Validator<String[]>`](../infrastructure/validator/Validator.md) et est conçue pour valider un objet selon des conditions dynamiques transmises sous forme de tableau de chaînes (par exemple, les arguments de la ligne de commande).

## 🧠 Rôle dans l'architecture

`ConditionEvaluator` introduit un mécanisme d'évaluation conditionnelle dans le cycle de vie des composants MicroBean. Elle permet de factoriser la logique de validation contextuelle, d'activer ou non des composants selon des règles dynamiques, et de rendre la configuration applicative plus flexible et modulaire. Elle s'intègre avec l'annotation [`@Condition`](./Condition.md) pour l'activation conditionnelle des beans, services, adaptateurs, etc.

## 🔗 Relations

- Utilisé par :
  - [`@Condition`](./Condition.md) pour l'activation conditionnelle des composants.
- Concepts liés :
  - [`Validator`](../infrastructure/validator/Validator.md) : interface de validation générique.

## 🔧 Méthodes

| Signature                       | Description                                           | Comportement IoC / Effets de bord              |
|---------------------------------|-------------------------------------------------------|------------------------------------------------|
| boolean validate(String[] args) | Valide l'objet d'instance selon les arguments fournis | Invoquée par le conteneur lors de l'évaluation |

## 💡 Exemple d'implémentation

```java
public class MaConditionEvaluator implements ConditionEvaluator {
    
    private final MonObjet objet;
    
    public MaConditionEvaluator(MonObjet objet) {
        this.objet = objet;
    }
    
    @Override
    public boolean validate(String[] args) {
        // Logique de validation conditionnelle sur objet
        return ...;
    }
}
```

## 🔄 Comportement du cycle de vie

1. Instanciation de la classe implémentant `ConditionEvaluator` avec l'objet à valider.
2. Appel de la méthode `validate(String[] args)` lors de l'évaluation conditionnelle par le conteneur.
3. Activation ou non du composant cible selon le résultat de la validation.

## ⚠️ Limitations / cas particuliers

- L'objet à valider doit être passé dans le constructeur de l'implémentation.
- Le tableau d'arguments doit être correctement interprété pour éviter des comportements inattendus.
- Peut être combinée avec d'autres stratégies de validation ou d'activation conditionnelle.

## 📍 Notes internes MicroBean

- L'interface `ConditionEvaluator` favorise la factorisation et la réutilisabilité de la logique conditionnelle.
- Elle permet une extension du framework pour supporter des stratégies avancées d'activation contextuelle.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`Validator`](../infrastructure/validator/Validator.md) – interface de validation générique
- [`@Condition`](./Condition.md) – annotation d'activation conditionnelle
