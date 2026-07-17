# 📄 @Adapter [🧩 Annotation]

## 🎯 Description

L'annotation `@Adapter` permet de déclarer une classe comme adaptateur géré par le conteneur d'injection de dépendances MicroBean. Elle facilite l'instanciation, l'injection et la gestion du cycle de vie de la classe, tout en permettant de restreindre son activation à un ou plusieurs systèmes d'exploitation spécifiques via l'attribut `os`. Un adaptateur représente un composant d'intégration ou de pont entre le domaine applicatif et des systèmes externes ou des couches techniques.

## 🧠 Rôle dans l'architecture

`@Adapter` existe pour formaliser la déclaration de composants d'adaptation dans un système IoC. Elle permet au conteneur MicroBean de détecter, instancier et injecter automatiquement ces classes, tout en contrôlant leur cycle de vie (`Scope`) et leur activation conditionnelle selon l'environnement d'exécution (`OS`). Elle s'inscrit dans la phase de scan, d'enregistrement et d'injection des composants, et interagit avec les mécanismes de profil, de condition et de nommage du framework.

## 🔗 Relations

- Dépend de :
  - [`Scope`](./Scope.md) : gestion du cycle de vie de l'adaptateur (singleton, prototype).
  - [`OS`](./OS.md) : activation conditionnelle selon le(s) système(s) d'exploitation.
- Utilisé par :
  - Le conteneur MicroBean pour l'enregistrement et l'injection des adaptateurs.
- Concepts liés :
  - [`@Service`](./Service.md), [`@Bean`](./Bean.md) : autres stéréotypes de composants gérés.
  - [`@Profile`](./Profile.md), [`@Condition`](./Condition.md) : activation conditionnelle avancée.
  - [`@Primary`](./Primary.md), [`@Named`](./Named.md) : gestion des conflits et du nommage.

## ⚙️ Attributs

| Nom         | Type    | Valeur par défaut | Rôle / Impact à l'exécution                                                     |
|-------------|---------|-------------------|---------------------------------------------------------------------------------|
| scope       | Scope   | Scope.SINGLETON   | Définit le cycle de vie de l'adaptateur (singleton, prototype, etc.)            |
| name        | String  | ""                | Nom explicite de l'adaptateur. Si vide, le nom est déduit automatiquement       |
| os          | OS      | OS.ALL            | Spécifie le ou les systèmes d'exploitation pour lesquels l'adaptateur est actif |

## 💡 Exemples d'utilisation

```java
@Adapter(name = "fileAdapter", scope = Scope.PROTOTYPE, os = OS.WINDOWS)
public class FileWindowsAdapter {
    // Adaptateur spécifique à Windows
}
```

---

```java
@Adapter(scope = Scope.SINGLETON)
public class UniversalAdapter {
    // Adaptateur actif sur tous les OS, singleton par défaut
}
```

## 🔄 Comportement du cycle de vie

1. Découverte lors du scan des classes par le conteneur MicroBean.
2. Instanciation selon le `scope` défini (singleton ou prototype).
3. Injection automatique dans les composants consommateurs.
4. Activation conditionnelle selon l'OS courant (filtrage via l'attribut `os`).
5. Initialisation post-construction si une méthode annotée [`@PostConstruct`](./PostConstruct.md) existe.
6. Destruction (si applicable) gérée par le conteneur pour les prototypes.

## ⚠️ Limitations / cas particuliers

- L'activation dépend strictement de la valeur de l'attribut `os` : un adaptateur non compatible avec l'OS courant ne sera pas instancié.
- Si plusieurs adaptateurs du même type existent, la résolution peut nécessiter [`@Primary`](./Primary.md) ou [`@Named`](./Named.md).
- Les adaptateurs ne doivent pas contenir de logique métier : ils servent de pont technique.
- L'utilisation de `scope = Scope.PROTOTYPE` implique une gestion attentive des ressources et de l'état.

## 📍 Notes internes MicroBean

- L'annotation `@Adapter` favorise la séparation des préoccupations entre logique métier et adaptation technique.
- Elle permet une extension du conteneur pour supporter des stratégies d'activation conditionnelle avancées (OS, profil, condition personnalisée).
- L'intégration avec les autres stéréotypes (`@Service`, `@Bean`) assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`Scope`](./Scope.md) – gestion du cycle de vie des composants
- [`OS`](./OS.md) – activation conditionnelle selon le(s) système(s) d'exploitation
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Profile`](./Profile.md) – activation selon le profil d'exécution
- [`@Condition`](./Condition.md) – activation conditionnelle personnalisée
- [`@Primary`](./Primary.md) – résolution de conflit de candidats
- [`@Named`](./Named.md) – nommage explicite des composants
- [`@PostConstruct`](./PostConstruct.md) – initialisation post-construction
