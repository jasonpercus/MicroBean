# 📄 @EntryPointService [🧩 Annotation]

## 🎯 Description

L'annotation `@EntryPointService` permet de déclarer une classe de service comme point d'entrée dans une application MicroBean. Elle indique au framework que la classe annotée doit être considérée comme un point d'entrée lors du démarrage de l'application, selon le cycle de vie spécifié par l'attribut `lifecycle`. Elle est essentielle pour orchestrer le lancement des services principaux ou de fond dans l'application.

## 🧠 Rôle dans l'architecture

`@EntryPointService` structure la gestion des points d'entrée applicatifs dans MicroBean. Elle permet au conteneur d'identifier, d'instancier et de lancer les services principaux selon des stratégies de cycle de vie adaptées (exécution unique ou longue durée). Elle s'intègre avec le système de gestion des threads et de supervision du framework, et permet de séparer explicitement la logique de démarrage des autres composants métier.

## 🔗 Relations

- Dépend de :
  - [`LifecycleEntryPoint`](./LifecycleEntryPoint.md) : définit le mode d'exécution du service (ONE_SHOT, LONG_RUNNING).
- Utilisé par :
  - Le conteneur MicroBean pour l'orchestration du démarrage applicatif.
- Concepts liés :
  - [`@Service`](./Service.md) : stéréotype de service métier.
  - [`@MicroBeanApplication`](./MicroBeanApplication.md) : déclaration de l'application principale.

## ⚙️ Attributs

| Nom       | Type                | Valeur par défaut | Rôle / Impact à l'exécution                                   |
|-----------|---------------------|-------------------|---------------------------------------------------------------|
| lifecycle | LifecycleEntryPoint | (obligatoire)     | Définit le cycle de vie du service (ONE_SHOT ou LONG_RUNNING) |

## 💡 Exemples d'utilisation

```java
@EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
public class MainService implements ApplicationEntryPoint {
    // Service exécuté sur le thread courant
}
```

---

```java
@EntryPointService(lifecycle = LifecycleEntryPoint.LONG_RUNNING)
public class BackgroundService implements ApplicationEntryPoint {
    // Service exécuté sur un thread dédié
}
```

## 🔄 Comportement du cycle de vie

1. Découverte lors du scan des classes annotées dans le classpath.
2. Instanciation de la classe de service par le conteneur.
3. Démarrage du service selon le mode défini par `lifecycle` :
   - `ONE_SHOT` : exécution sur le thread principal (un seul service de ce type autorisé).
   - `LONG_RUNNING` : exécution sur un thread dédié, adapté aux services de fond ou de longue durée.
4. Supervision et gestion du cycle de vie par le conteneur.

## ⚠️ Limitations / cas particuliers

- Un seul service de type `ONE_SHOT` peut exister dans l'application.
- Les services de type `LONG_RUNNING` doivent être conçus pour supporter une exécution continue ou asynchrone.
- L'annotation ne gère pas la terminaison automatique des services : la gestion de l'arrêt doit être prévue dans l'implémentation.

## 📍 Notes internes MicroBean

- L'annotation `@EntryPointService` favorise la séparation explicite entre logique de démarrage et logique métier.
- Elle permet une extension du conteneur pour supporter des stratégies avancées de gestion du cycle de vie applicatif.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`LifecycleEntryPoint`](./LifecycleEntryPoint.md) – modes de cycle de vie des points d'entrée
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@MicroBeanApplication`](./MicroBeanApplication.md) – déclaration de l'application principale
- [`@Profile`](./Profile.md) – activation selon le profil d'exécution
- [`@Condition`](./Condition.md) – activation conditionnelle personnalisée
