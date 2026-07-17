# 📄 @Service [🧩 Annotation]

## 🎯 Description

L'annotation `@Service` permet de déclarer une classe comme un service métier géré par le conteneur d'injection de dépendances MicroBean. Elle facilite la détection automatique, l'instanciation et l'injection de la classe là où elle est requise. Un service représente un composant métier ou technique réutilisable, central dans l'architecture applicative.

## 🧠 Rôle dans l'architecture

`@Service` formalise la déclaration des services métier dans un système IoC. Elle permet au conteneur MicroBean de détecter, instancier et injecter automatiquement les classes annotées, en gérant leur cycle de vie (`Scope`) et leur nommage (`name`). Elle s'inscrit dans la phase de scan, d'enregistrement et d'injection des composants, et favorise la séparation des préoccupations et la réutilisabilité du code métier.

## 🔗 Relations

- Dépend de :
  - [`Scope`](./Scope.md) : gestion du cycle de vie du service (singleton, prototype).
- Utilisé par :
  - Le conteneur MicroBean pour l'enregistrement et l'injection des services.
- Concepts liés :
  - [`@Bean`](./Bean.md), [`@Adapter`](./Adapter.md) : autres stéréotypes de composants gérés.
  - [`@Profile`](./Profile.md), [`@Condition`](./Condition.md) : activation conditionnelle avancée.
  - [`@Primary`](./Primary.md), [`@Named`](./Named.md) : gestion des conflits et du nommage.

## ⚙️ Attributs

| Nom   | Type   | Valeur par défaut | Rôle / Impact à l'exécution                                          |
|-------|--------|-------------------|----------------------------------------------------------------------|
| scope | Scope  | Scope.SINGLETON   | Définit le cycle de vie du service (singleton, prototype, etc.)      |
| name  | String | ""                | Nom explicite du service. Si vide, le nom est déduit automatiquement |

## 💡 Exemples d'utilisation

```java
@Service(name = "monService", scope = Scope.PROTOTYPE)
public class MonService {
    // Service métier réutilisable, prototype
}
```

---

```java
@Service
public class MainService {
    // Service singleton par défaut
}
```

## 🔄 Comportement du cycle de vie

1. Découverte lors du scan des classes annotées dans le classpath.
2. Instanciation de la classe de service selon le `scope` défini (singleton ou prototype).
3. Injection automatique dans les composants consommateurs.
4. Initialisation post-construction si une méthode annotée [`@PostConstruct`](./PostConstruct.md) existe.
5. Destruction (si applicable) gérée par le conteneur pour les prototypes.

## ⚠️ Limitations / cas particuliers

- Si plusieurs services du même type existent, la résolution peut nécessiter [`@Primary`](./Primary.md) ou [`@Named`](./Named.md).
- L'utilisation de `scope = Scope.PROTOTYPE` implique une gestion attentive des ressources et de l'état.
- Les services doivent être conçus pour être réutilisables et découplés de la logique de présentation.
- Peut être combinée avec [`@Profile`](./Profile.md) ou [`@Condition`](./Condition.md) pour une activation conditionnelle.

## 📍 Notes internes MicroBean

- L'annotation `@Service` favorise la structuration du code métier et la réutilisabilité des composants.
- Elle permet une extension du conteneur pour supporter des stratégies avancées de gestion du cycle de vie.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`Scope`](./Scope.md) – gestion du cycle de vie des composants
- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
- [`@Profile`](./Profile.md) – activation selon le profil d'exécution
- [`@Condition`](./Condition.md) – activation conditionnelle personnalisée
- [`@Primary`](./Primary.md) – résolution de conflit de candidats
- [`@Named`](./Named.md) – nommage explicite des composants
- [`@PostConstruct`](./PostConstruct.md) – initialisation post-construction
